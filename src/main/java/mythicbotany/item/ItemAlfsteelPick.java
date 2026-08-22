package mythicbotany.item;

import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;

/** Alfsteel's TerraPick-like shatterer with a mana bar and a toggleable 3x3 mode. */
public class ItemAlfsteelPick extends ItemPickaxe implements IManaItem, IManaTooltipDisplay {
    private static final String TAG_MANA = "mana";
    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_TIPPED = "tipped";
    private static final int MAX_MANA = 4000000;
    private static final int MANA_PER_BLOCK = 100;

    public ItemAlfsteelPick(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("mythicbotany", "enabled"), (stack, world, entity) -> isEnabled(stack) ? 1.0F : 0.0F);
        addPropertyOverride(new ResourceLocation("mythicbotany", "tipped"), (stack, world, entity) -> isTipped(stack) ? 1.0F : 0.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            ItemNBTHelper.setBoolean(stack, TAG_ENABLED, !isEnabled(stack));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (!isEnabled(stack) || player.world.isRemote) {
            return false;
        }
        EnumFacing facing = horizontalFacing(player);
        int cost = 0;
        BlockPos[] targets = new BlockPos[8];
        int index = 0;
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) continue;
                BlockPos target = offset(pos, facing, a, b);
                IBlockState state = player.world.getBlockState(target);
                if (state.getBlockHardness(player.world, target) >= 0.0F
                        && stack.getDestroySpeed(state) > 0.0F
                        && stack.canHarvestBlock(state)) {
                    targets[index++] = target;
                    cost += MANA_PER_BLOCK;
                }
            }
        }
        if (index == 0 || !ManaItemHandler.requestManaExactForTool(stack, player, cost, true)) {
            return false;
        }
        for (int i = 0; i < index; i++) {
            BlockPos target = targets[i];
            IBlockState state = player.world.getBlockState(target);
            if (player.world.destroyBlock(target, !(isTipped(stack) && isGarbage(state)))) {
                stack.damageItem(1, player);
            }
        }
        return false;
    }

    private static BlockPos offset(BlockPos origin, EnumFacing facing, int a, int b) {
        if (facing.getAxis() == EnumFacing.Axis.X) return origin.add(0, a, b);
        return origin.add(a, b, 0);
    }

    private static EnumFacing horizontalFacing(EntityPlayer player) {
        Vec3d look = player.getLookVec();
        if (Math.abs(look.x) >= Math.abs(look.z)) return look.x >= 0 ? EnumFacing.EAST : EnumFacing.WEST;
        return look.z >= 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
    }

    private static boolean isGarbage(IBlockState state) {
        return state.getBlock() == Blocks.COBBLESTONE || state.getBlock() == Blocks.DIRT
                || state.getBlock() == Blocks.GRAVEL || state.getBlock() == Blocks.NETHERRACK
                || state.getBlock() == Blocks.SAND || state.getBlock() == Blocks.SANDSTONE;
    }

    public static boolean isEnabled(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, TAG_ENABLED, false); }
    public static boolean isTipped(ItemStack stack) { return ItemNBTHelper.getBoolean(stack, TAG_TIPPED, false); }
    public static void setTipped(ItemStack stack, boolean tipped) { ItemNBTHelper.setBoolean(stack, TAG_TIPPED, tipped); }

    @Override public int getMana(ItemStack stack) { return ItemNBTHelper.getInt(stack, TAG_MANA, 0); }
    @Override public int getMaxMana(ItemStack stack) { return MAX_MANA; }
    @Override public void addMana(ItemStack stack, int mana) {
        int value = Math.max(0, Math.min(MAX_MANA, getMana(stack) + mana));
        ItemNBTHelper.setInt(stack, TAG_MANA, value);
    }
    @Override public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean canExportManaToPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean isNoExport(ItemStack stack) { return false; }
    @Override public float getManaFractionForDisplay(ItemStack stack) { return (float) getMana(stack) / (float) MAX_MANA; }
    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - getManaFractionForDisplay(stack); }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) {
        return net.minecraft.util.math.MathHelper.hsvToRGB(getManaFractionForDisplay(stack) / 3.0F, 1.0F, 1.0F);
    }
}
