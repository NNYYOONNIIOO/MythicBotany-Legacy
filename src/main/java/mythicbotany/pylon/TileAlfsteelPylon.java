package mythicbotany.pylon;

import java.util.List;

import mythicbotany.tile.ManaTileEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Alfsteel pylon for the Botania 1.12 mana API.
 *
 * Botania 1.12 exposes mana receivers but not the later spark attachment
 * interfaces, so this tile uses the stable receiver contract and forwards
 * stored mana to adjacent MythicBotany mana tiles.
 */
public class TileAlfsteelPylon extends ManaTileEntity {
    private static final int TRANSFER_PER_TICK = 2000;
    private static final int ALFSTEEL_TOOL_MANA_PER_POINT = 100;
    private static final int ALFSTEEL_ARMOR_MANA_PER_POINT = 70;
    private static final int MENDING_MANA_PER_POINT = 200;

    /** Kept as a block-break hook for parity with later pylon implementations. */
    public void detachSpark() {
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        repairTopItem();
        if (mana <= 0) {
            return;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if (tile instanceof ManaTileEntity && tile != this) {
                ManaTileEntity receiver = (ManaTileEntity) tile;
                int space = receiver.getMaxMana() - receiver.getCurrentMana();
                if (space > 0) {
                    int amount = Math.min(Math.min(mana, space), TRANSFER_PER_TICK);
                    receiver.recieveMana(amount);
                    mana -= amount;
                    markDirty();
                    return;
                }
            }
        }
    }

    /** Repairs alfsteel equipment and Mending equipment dropped on the pylon. */
    private void repairTopItem() {
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        for (EntityItem entity : items) {
            ItemStack stack = entity.getItem();
            int manaCost = getRepairManaPerPoint(stack);
            if (manaCost <= 0 || mana < manaCost) {
                continue;
            }
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
            mana -= manaCost;
            markDirty();
            return;
        }
    }

    public static int getRepairManaPerPoint(ItemStack stack) {
        if (stack.isEmpty() || !stack.isItemDamaged()) {
            return 0;
        }
        if (isAlfsteel(stack)) {
            if (!(stack.getItem() instanceof ItemArmor
                    || stack.getItem() instanceof ItemTool
                    || stack.getItem() instanceof ItemSword)) {
                return 0;
            }
            return stack.getItem() instanceof ItemArmor
                    ? ALFSTEEL_ARMOR_MANA_PER_POINT : ALFSTEEL_TOOL_MANA_PER_POINT;
        }
        return isMendingRepairable(stack) ? MENDING_MANA_PER_POINT : 0;
    }

    private static boolean isMendingRepairable(ItemStack stack) {
        return (stack.getItem() instanceof ItemArmor
                || stack.getItem() instanceof ItemTool
                || stack.getItem() instanceof ItemSword)
                && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0;
    }

    private static boolean isAlfsteel(ItemStack stack) {
        ResourceLocation id = stack.getItem().getRegistryName();
        return id != null && "mythicbotany".equals(id.getNamespace())
                && id.getPath().startsWith("alfsteel_");
    }
}
