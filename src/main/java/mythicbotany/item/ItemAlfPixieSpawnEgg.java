package mythicbotany.item;

import mythicbotany.entity.EntityAlfPixie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAlfPixieSpawnEgg extends Item {
    public ItemAlfPixieSpawnEgg() {
        setMaxStackSize(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            BlockPos spawnPos = playerIn.getPosition().offset(playerIn.getHorizontalFacing());
            EntityAlfPixie pixie = new EntityAlfPixie(worldIn);
            pixie.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(),
                    spawnPos.getZ() + 0.5D, playerIn.rotationYaw, 0.0F);
            worldIn.spawnEntity(pixie);
            if (!playerIn.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
