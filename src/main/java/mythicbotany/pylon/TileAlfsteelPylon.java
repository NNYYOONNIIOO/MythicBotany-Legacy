package mythicbotany.pylon;

import com.google.common.base.Predicates;
import java.util.List;

import mythicbotany.tile.ManaTileEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Enchantments;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.core.handler.ManaNetworkHandler;

/**
 * Alfsteel pylon for the Botania 1.12 mana API.
 *
 * Botania 1.12 exposes mana receivers but not the later spark attachment
 * interfaces, so this tile uses the stable receiver contract and forwards
 * stored mana to adjacent MythicBotany mana tiles.
 */
public class TileAlfsteelPylon extends ManaTileEntity implements IManaPool, ISparkAttachable, IWandBindable {
    private static final int ALFSTEEL_TOOL_MANA_PER_POINT = 100;
    private static final int ALFSTEEL_ARMOR_MANA_PER_POINT = 70;
    private static final int MENDING_MANA_PER_POINT = 200;
    private static final int MAX_PYLON_MANA = 1000;

    @Override
    public int getMaxMana() {
        return MAX_PYLON_MANA;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        registerToManaNetwork();
    }

    @Override
    public void invalidate() {
        removeFromManaNetwork();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        removeFromManaNetwork();
        super.onChunkUnload();
    }

    private void registerToManaNetwork() {
        if (world != null && !world.isRemote && !isInvalid()
                && !ManaNetworkHandler.instance.isPoolIn(this)) {
            ManaNetworkEvent.addPool(this);
        }
    }

    private void removeFromManaNetwork() {
        if (world != null && !world.isRemote
                && ManaNetworkHandler.instance.isPoolIn(this)) {
            ManaNetworkEvent.removePool(this);
        }
    }

    @Override
    public void recieveMana(int amount) {
        int oldMana = mana;
        super.recieveMana(amount);
        if (oldMana != mana && world != null && !world.isRemote) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return !isFull();
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public EnumDyeColor getColor() {
        return EnumDyeColor.ORANGE;
    }

    @Override
    public void setColor(EnumDyeColor color) {
        // The Alfsteel pylon has a fixed color.
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {
        // Spark attachment is discovered from the Spark entity above this tile.
    }

    @Override
    public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
        return world != null && !isInvalid();
    }

    @Override
    public boolean bindTo(EntityPlayer player, ItemStack wand, BlockPos clickedPos, EnumFacing side) {
        if (world == null || player == null || clickedPos == null) {
            return false;
        }
        TileEntity clickedTile = world.getTileEntity(clickedPos);
        if (clickedTile instanceof TileSpreader) {
            // Let Botania's spreader perform the normal binding and receiver
            // scan on both logical sides.  The Forest Wand then dispatches the
            // spreader's updated rotation and receiver state as usual.
            TileSpreader spreader = (TileSpreader) clickedTile;
            boolean bound = spreader.bindTo(player, wand, getPos(), side);
            if (bound && !world.isRemote) {
                spreader.markDirty();
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(spreader);
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
            }
            return bound;
        }
        return false;
    }

    @Override
    public BlockPos getBinding() {
        if (world == null) {
            return null;
        }
        for (TileEntity tile : world.loadedTileEntityList) {
            if (tile instanceof TileSpreader && getPos().equals(((TileSpreader) tile).getBinding())) {
                return tile.getPos();
            }
        }
        return null;
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(0, getMaxMana() - getCurrentMana());
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        if (world == null) {
            return null;
        }
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class,
                new AxisAlignedBB(pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                        pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D),
                Predicates.instanceOf(ISparkEntity.class));
        return sparks.size() == 1 ? (ISparkEntity) sparks.get(0) : null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    /** Kept as a block-break hook for parity with later pylon implementations. */
    public void detachSpark() {
    }

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        if (world.isRemote) {
            return;
        }
        registerToManaNetwork();
        repairTopItem();
    }

    /** Repairs alfsteel equipment and Mending equipment dropped on the pylon. */
    private void repairTopItem() {
        if (mana <= 0) {
            return;
        }
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        boolean repaired = false;
        for (EntityItem entity : items) {
            ItemStack stack = entity.getItem();
            int manaCost = getRepairManaPerPoint(stack);
            int damage = stack.getItemDamage();
            if (stack.getCount() != 1 || manaCost <= 0 || damage <= 0) {
                continue;
            }
            int repairPoints = Math.min(damage, mana / manaCost);
            if (repairPoints <= 0) {
                continue;
            }
            stack.setItemDamage(damage - repairPoints);
            entity.setItem(stack);
            mana -= repairPoints * manaCost;
            repaired = true;
            if (mana <= 0) {
                break;
            }
        }
        if (repaired) {
            markDirty();
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        }
    }

    public static int getRepairManaPerPoint(ItemStack stack) {
        if (stack.isEmpty() || stack.getCount() != 1 || !stack.getItem().isDamageable()
                || !stack.isItemDamaged()) {
            return 0;
        }
        if (isAlfsteel(stack)) {
            return stack.getItem() instanceof ItemArmor
                    ? ALFSTEEL_ARMOR_MANA_PER_POINT : ALFSTEEL_TOOL_MANA_PER_POINT;
        }
        return isMendingRepairable(stack) ? MENDING_MANA_PER_POINT : 0;
    }

    private static boolean isMendingRepairable(ItemStack stack) {
        return stack.getItem().isDamageable()
                && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0;
    }

    private static boolean isAlfsteel(ItemStack stack) {
        ResourceLocation id = stack.getItem().getRegistryName();
        return id != null && "mythicbotany".equals(id.getNamespace())
                && id.getPath().startsWith("alfsteel_");
    }
}
