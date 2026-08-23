package mythicbotany.rune;

import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RuneRitualRecipe {
    public static final class RunePosition {
        private final int x;
        private final int z;
        private final ItemStack rune;

        private RunePosition(int x, int z, ItemStack rune) {
            this.x = x;
            this.z = z;
            this.rune = rune.copy();
            this.rune.setCount(1);
        }

        public int getX(int rotation) {
            switch (rotation & 3) {
                case 1:
                    return -z;
                case 2:
                    return -x;
                case 3:
                    return z;
                default:
                    return x;
            }
        }

        public int getZ(int rotation) {
            switch (rotation & 3) {
                case 1:
                    return x;
                case 2:
                    return -z;
                case 3:
                    return -x;
                default:
                    return z;
            }
        }

        public ItemStack getRune() {
            return rune.copy();
        }

        public int getOriginalX() {
            return x;
        }

        public int getOriginalZ() {
            return z;
        }
    }

    private final ItemStack center;
    private final ItemStack output;
    private final int mana;
    private final int ticks;
    private final List<RunePosition> runes;

    public RuneRitualRecipe(ItemStack center, ItemStack output, int mana, int ticks, RunePosition... runes) {
        this.center = center.copy();
        this.center.setCount(1);
        this.output = output.copy();
        this.mana = Math.max(0, mana);
        this.ticks = Math.max(1, ticks);
        this.runes = Collections.unmodifiableList(Arrays.asList(runes));
    }

    public static RunePosition rune(int x, int z, ItemStack rune) {
        return new RunePosition(x, z, rune);
    }

    public static boolean matches(ItemStack expected, ItemStack actual) {
        return expected != null && actual != null && !expected.isEmpty() && !actual.isEmpty()
                && expected.getItem() == actual.getItem()
                && (expected.getMetadata() == 32767 || expected.getMetadata() == actual.getMetadata());
    }

    public boolean matchesCenter(ItemStack stack) {
        return matches(center, stack);
    }

    public ItemStack getCenter() {
        return center.copy();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getMana() {
        return mana;
    }

    public int getTicks() {
        return ticks;
    }

    public List<RunePosition> getRunes() {
        return runes;
    }
}
