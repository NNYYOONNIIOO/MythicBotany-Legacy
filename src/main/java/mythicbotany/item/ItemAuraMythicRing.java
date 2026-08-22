package mythicbotany.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.ManaItemHandler;

/** The upstream Greatest Aura Ring gives mana and does not store mana itself. */
public class ItemAuraMythicRing extends ItemMythicRing implements IManaGivingItem {
    public ItemAuraMythicRing() { super(Effect.AURA); }
    @Override public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote && player instanceof EntityPlayer && player.ticksExisted % 50 == 0)
            ManaItemHandler.dispatchManaExact(stack, (EntityPlayer) player, 1, true);
    }
}
