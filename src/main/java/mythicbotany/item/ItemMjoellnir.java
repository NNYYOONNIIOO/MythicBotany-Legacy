package mythicbotany.item;

import mythicbotany.entity.EntityMjoellnir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

public class ItemMjoellnir extends ItemSword {
    public ItemMjoellnir(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int damage) {
        // Durability is not an item-model variant.
        return 0;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!worldIn.isRemote) {
            ItemStack thrownStack = stack.copy();
            ToolCommons.damageItem(thrownStack, 4, playerIn, AlfsteelRepairHelper.MANA_PER_DURABILITY);
            EntityMjoellnir thrown = new EntityMjoellnir(worldIn, playerIn, thrownStack);
            thrown.setCreativeThrow(playerIn.capabilities.isCreativeMode);
            thrown.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(thrown);
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.8F, 0.8F);
        }
        // The server and client both remove the held stack while the projectile is in flight.
        // The projectile returns that exact stack to the owner when it reaches them.
        if (!playerIn.capabilities.isCreativeMode) {
            stack.shrink(1);
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
        world.addWeatherEffect(new EntityLightningBolt(world, x, y, z, true));
    }
}
