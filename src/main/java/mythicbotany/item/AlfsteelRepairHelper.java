package mythicbotany.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.ManaItemHandler;

/** Shared Botania 1.12 mana repair behavior for alfsteel equipment. */
public final class AlfsteelRepairHelper {
    public static final int MANA_PER_DURABILITY = 200;
    public static final int ARMOR_MANA_PER_DURABILITY = 140;

    private AlfsteelRepairHelper() { }

    public static void repair(ItemStack stack, EntityPlayer player, long worldTime) {
        repair(stack, player, worldTime, MANA_PER_DURABILITY, true);
    }

    public static void repairArmor(ItemStack stack, EntityPlayer player, long worldTime) {
        repair(stack, player, worldTime, ARMOR_MANA_PER_DURABILITY, false);
    }

    private static void repair(ItemStack stack, EntityPlayer player, long worldTime,
                               int manaPerDurability, boolean applyToolDiscount) {
        if (stack == null || stack.isEmpty() || player == null || stack.getItemDamage() <= 0
                || worldTime % 20L != 0L) {
            return;
        }
        boolean supplied = applyToolDiscount
                ? ManaItemHandler.requestManaExactForTool(stack, player, manaPerDurability, true)
                : ManaItemHandler.requestManaExact(stack, player, manaPerDurability, true);
        if (supplied) {
            stack.setItemDamage(stack.getItemDamage() - 1);
        }
    }
}
