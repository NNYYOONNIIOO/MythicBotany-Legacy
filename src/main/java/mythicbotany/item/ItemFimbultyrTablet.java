package mythicbotany.item;

import mythicbotany.config.MythicBotanyConfig;
import mythicbotany.dimension.ModDimensions;
import mythicbotany.dimension.TeleporterAlfheim;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemFimbultyrTablet extends Item {
    public ItemFimbultyrTablet() {
        setMaxStackSize(1);
        setMaxDamage(16);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!MythicBotanyConfig.allowFimbultyrTabletAlfheimTravel) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP && worldIn.getMinecraftServer() != null) {
            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            int targetDimension = player.dimension == ModDimensions.ALFHEIM_DIMENSION_ID
                    ? 0 : ModDimensions.ALFHEIM_DIMENSION_ID;
            WorldServer targetWorld = worldIn.getMinecraftServer().getWorld(targetDimension);
            if (targetWorld != null) {
                player.changeDimension(targetDimension, new TeleporterAlfheim(targetWorld));
                if (!player.capabilities.isCreativeMode) {
                    stack.damageItem(1, player);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
