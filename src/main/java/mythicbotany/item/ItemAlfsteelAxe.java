package mythicbotany.item;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import baubles.api.BaublesApi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ItemTemperanceStone;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import mythicbotany.MythicBotany;

/** Alfsteel axe with active tree chaining and Loki-compatible inactive mode. */
public class ItemAlfsteelAxe extends ItemAxe implements ISequentialBreaker {
    private static final int MANA_PER_DAMAGE = AlfsteelRepairHelper.MANA_PER_DURABILITY;
    private static final int LOG_RANGE = 31;
    private static final int LEAF_RANGE = 3;
    private static final String LOKI_CURSOR_LIST = "cursorList";
    private static final String LOKI_CURSOR_COUNT = "cursorCount";
    private static final String LOKI_CURSOR_PREFIX = "cursor";
    private static final String LOKI_X_OFFSET = "xOffset";
    private static final String LOKI_Y_OFFSET = "yOffset";
    private static final String LOKI_Z_OFFSET = "zOffset";

    public ItemAlfsteelAxe(Item.ToolMaterial material, float attackDamage, float attackSpeed) {
        super(material, attackDamage, attackSpeed);
        setMaxStackSize(1);
        setMaxDamage(4600);
        addPropertyOverride(new ResourceLocation(MythicBotany.MODID, "active"),
                (stack, world, entity) -> entity == null || !(entity instanceof EntityPlayer)
                        || isActive((EntityPlayer) entity) ? 1.0F : 0.0F);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (slot == EntityEquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(Item.ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.8D, 0));
            builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 5.0D, 0));
            return builder.build();
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            AlfsteelRepairHelper.repair(stack, (EntityPlayer) entity, world.getTotalWorldTime());
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (player.world.isRemote) return false;
        IBlockState state = player.world.getBlockState(pos);
        if (!isTreeBlock(state)) return false;
        if (isActive(player)) {
            breakConnected(stack, player, pos, isLeaves(state) ? LEAF_RANGE : LOG_RANGE);
        } else {
            breakLokiCursors(stack, player, pos);
        }
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state,
                                    BlockPos pos, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer
                && !state.getBlock().isAir(state, world, pos)) {
            ToolCommons.damageItem(stack, 1, entity, MANA_PER_DAMAGE);
        }
        return true;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        ToolCommons.damageItem(stack, 1, attacker, MANA_PER_DAMAGE);
        return true;
    }

    @Override
    public void breakOtherBlock(EntityPlayer player, ItemStack stack, BlockPos pos,
                                BlockPos originPos, EnumFacing side) {
        if (player.world.isRemote || pos.equals(originPos) || !isTreeBlock(player.world.getBlockState(pos))) return;
        removeAndDamage(stack, player, pos);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking() && facing == EnumFacing.UP
                && (world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.DIRT
                || world.getBlockState(pos).getBlock() == net.minecraft.init.Blocks.GRASS)) {
            ItemStack sapling = findSapling(player);
            if (!sapling.isEmpty() && world.isAirBlock(pos.up())) {
                return plantSapling(sapling, player, world, pos, hand, facing, hitX, hitY, hitZ);
            }
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    /** Place a sapling from the inventory without changing the axe held in the player's hand. */
    private static EnumActionResult plantSapling(ItemStack sapling, EntityPlayer player, World world,
                                                 BlockPos soilPos, EnumHand hand, EnumFacing facing,
                                                 float hitX, float hitY, float hitZ) {
        BlockPos target = soilPos.up();
        Block block = ((ItemBlock) sapling.getItem()).getBlock();
        if (!player.canPlayerEdit(target, facing, sapling)
                || !world.mayPlace(block, target, false, facing, player)) {
            return EnumActionResult.FAIL;
        }
        IBlockState state = block.getStateFromMeta(sapling.getMetadata());
        if (!world.setBlockState(target, state, 11)) {
            return EnumActionResult.FAIL;
        }
        block.onBlockPlacedBy(world, target, world.getBlockState(target), player, sapling);
        if (!player.capabilities.isCreativeMode) {
            sapling.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    private static boolean isActive(EntityPlayer player) {
        return !player.isSneaking() && !ItemTemperanceStone.hasTemperanceActive(player);
    }

    private static boolean isTreeBlock(IBlockState state) {
        return state != null && (state.getBlock() instanceof BlockLog || state.getBlock() instanceof BlockLeaves);
    }

    private static boolean isLeaves(IBlockState state) {
        return state != null && state.getBlock() instanceof BlockLeaves;
    }

    private static void breakConnected(ItemStack stack, EntityPlayer player, BlockPos origin, int range) {
        World world = player.world;
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        visited.add(origin);
        for (EnumFacing facing : EnumFacing.values()) queue.add(origin.offset(facing));

        while (!queue.isEmpty() && !stack.isEmpty()) {
            BlockPos current = queue.remove();
            if (!visited.add(current)
                    || Math.abs(current.getX() - origin.getX()) > range
                    || Math.abs(current.getY() - origin.getY()) > range
                    || Math.abs(current.getZ() - origin.getZ()) > range) {
                continue;
            }
            IBlockState state = world.getBlockState(current);
            if (!isTreeBlock(state)) continue;
            if (canBreak(world, current, state, player)) {
                removeAndDamage(stack, player, current);
            }
            for (EnumFacing facing : EnumFacing.values()) queue.add(current.offset(facing));
        }
    }

    private static void breakLokiCursors(ItemStack stack, EntityPlayer player, BlockPos origin) {
        int slot = BaublesApi.isBaubleEquipped(player, ModItems.lokiRing);
        if (slot < 0) return;
        ItemStack loki = BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
        NBTTagCompound list = ItemNBTHelper.getCompound(loki, LOKI_CURSOR_LIST, false);
        int count = list.getInteger(LOKI_CURSOR_COUNT);
        for (int i = 0; i < count && !stack.isEmpty(); i++) {
            NBTTagCompound cursor = list.getCompoundTag(LOKI_CURSOR_PREFIX + i);
            BlockPos target = origin.add(cursor.getInteger(LOKI_X_OFFSET), cursor.getInteger(LOKI_Y_OFFSET),
                    cursor.getInteger(LOKI_Z_OFFSET));
            if (!target.equals(origin) && isTreeBlock(player.world.getBlockState(target))) {
                removeAndDamage(stack, player, target);
            }
        }
    }

    private static void removeAndDamage(ItemStack stack, EntityPlayer player, BlockPos pos) {
        if (!(player instanceof EntityPlayerMP) || stack.isEmpty()) return;
        World world = player.world;
        IBlockState state = world.getBlockState(pos);
        if (!canBreak(world, pos, state, player)) return;

        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        int exp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.interactionManager.getGameType(),
                serverPlayer, pos);
        if (exp == -1) return;
        Block block = state.getBlock();
        if (player.capabilities.isCreativeMode) {
            world.setBlockToAir(pos);
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (block.removedByPlayer(state, world, pos, player, true)) {
            block.onPlayerDestroy(world, pos, state);
            block.harvestBlock(world, player, pos, state, tile, stack);
            block.dropXpOnBlockBreak(world, pos, exp);
            world.playEvent(2001, pos, Block.getStateId(state));
            ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE);
        }
    }

    private static boolean canBreak(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return world.isBlockLoaded(pos) && isTreeBlock(state)
                && !state.getBlock().isAir(state, world, pos)
                && state.getPlayerRelativeBlockHardness(player, world, pos) > 0.0F
                && state.getBlock().canHarvestBlock(world, pos, player);
    }

    private static ItemStack findSapling(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock
                    && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockSapling) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean disposeOfTrashBlocks(ItemStack stack) {
        return false;
    }

    @Override
    public int getEntityLifespan(ItemStack stack, World world) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (entityItem.isBurning()) entityItem.extinguish();
        return false;
    }
}
