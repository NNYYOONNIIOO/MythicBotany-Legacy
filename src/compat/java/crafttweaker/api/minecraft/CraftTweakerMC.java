package crafttweaker.api.minecraft;

import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;

/** Compile-time compatibility surface; the real class is supplied by CraftTweaker at runtime. */
public final class CraftTweakerMC {
    private CraftTweakerMC() {
    }

    public static ItemStack getItemStack(IItemStack stack) {
        return ItemStack.EMPTY;
    }
}
