package mythicbotany.item;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/** The filled horn broadcasts Mimir's call to nearby creatures. */
public class ItemGjallarHornFull extends Item {
    public ItemGjallarHornFull() {
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack horn = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            AxisAlignedBB area = new AxisAlignedBB(playerIn.getPosition()).grow(8.0D);
            for (EntityLivingBase entity : worldIn.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                if (entity instanceof EntityPlayer) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 240, 0));
                    entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 240, 0));
                    entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 240, 0));
                } else {
                    entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 160, 1));
                    entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 160, 0));
                }
            }
            worldIn.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_ENDERDRAGON_GROWL,
                    SoundCategory.PLAYERS, 0.8F, 1.25F);
            horn.shrink(1);
            ItemStack emptyHorn = new ItemStack(ModItems.gjallarHornEmpty);
            if (!playerIn.addItemStackToInventory(emptyHorn)) {
                playerIn.dropItem(emptyHorn, false);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, horn);
    }
}
