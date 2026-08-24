package mythicbotany.item;

import net.minecraft.item.Item;

/** A damageable Nether star with the full 1,200,000-point Wither Aconite charge. */
public class ItemFadedNetherStar extends Item {
    public static final int MAX_DAMAGE = 1_200_000;

    public ItemFadedNetherStar() {
        setMaxStackSize(1);
        setMaxDamage(MAX_DAMAGE);
    }

    @Override
    public int getMaxDamage() {
        return MAX_DAMAGE;
    }
}
