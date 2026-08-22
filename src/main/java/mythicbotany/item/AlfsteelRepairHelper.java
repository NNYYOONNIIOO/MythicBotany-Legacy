package mythicbotany.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.ManaItemHandler;

/** Shared Botania 1.12 mana repair behavior for alfsteel equipment. */
public final class AlfsteelRepairHelper {
    public static final int MANA_PER_DURABILITY = 200;

    private AlfsteelRepairHelper() { }

    public static void repair(ItemStack stack, EntityPlayer player, long worldTime) {
        if (stack == null || stack.isEmpty() || player == null || stack.getItemDamage() <= 0
                || worldTime % 20L != 0L) {
            return;
        }
        if (ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_DURABILITY, true)) {
            stack.setItemDamage(stack.getItemDamage() - 1);
        }
    }
}

