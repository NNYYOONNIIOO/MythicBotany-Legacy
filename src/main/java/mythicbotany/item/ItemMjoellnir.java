package mythicbotany.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

public class ItemMjoellnir extends ItemSword {
    public ItemMjoellnir(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!worldIn.isRemote) {
            Vec3d look = playerIn.getLookVec();
            strike(worldIn, playerIn.posX + look.x * 8.0D,
                    playerIn.posY + playerIn.getEyeHeight() + look.y * 8.0D,
                    playerIn.posZ + look.z * 8.0D);
            ToolCommons.damageItem(stack, 4, playerIn, AlfsteelRepairHelper.MANA_PER_DURABILITY);
        }
        playerIn.getCooldownTracker().setCooldown(this, 20);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!attacker.world.isRemote) {
            strike(attacker.world, target.posX, target.posY, target.posZ);
        }
        ToolCommons.damageItem(stack, 1, attacker, AlfsteelRepairHelper.MANA_PER_DURABILITY);
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            AlfsteelRepairHelper.repair(stack, (EntityPlayer) entity, world.getTotalWorldTime());
        }
    }

    private void strike(World world, double x, double y, double z) {
        world.addWeatherEffect(new EntityLightningBolt(world, x, y, z, false));
    }
}
