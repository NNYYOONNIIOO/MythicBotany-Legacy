package mythicbotany.item;

import mythicbotany.entity.EntityAlfPixie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAlfPixieSpawnEgg extends Item {
    public ItemAlfPixieSpawnEgg() { setMaxStackSize(64); }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        BlockPos pos = player.getPosition().offset(player.getHorizontalFacing());
        return new ActionResult<>(spawn(world, player, stack, pos) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
    }
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return EnumActionResult.SUCCESS;
        return spawn(world, player, stack, pos.offset(facing)) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
    }
    private boolean spawn(World world, EntityPlayer player, ItemStack stack, BlockPos pos) {
        EntityAlfPixie pixie = new EntityAlfPixie(world);
        pixie.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.rotationYaw, 0.0F);
        if (!world.spawnEntity(pixie)) return false;
        if (!player.capabilities.isCreativeMode) stack.shrink(1);
        return true;
    }
}
