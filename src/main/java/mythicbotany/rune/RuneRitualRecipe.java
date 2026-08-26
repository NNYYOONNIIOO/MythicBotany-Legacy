package mythicbotany.rune;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RuneRitualRecipe {
    public static final class InputRequirement {
        private final List<ItemStack> alternatives;

        private InputRequirement(List<ItemStack> alternatives) {
            List<ItemStack> copies = new ArrayList<>();
            for (ItemStack stack : alternatives) {
                if (stack != null && !stack.isEmpty()) {
                    copies.add(stack.copy());
                }
            }
            this.alternatives = Collections.unmodifiableList(copies);
        }

        public static InputRequirement of(ItemStack... stacks) {
            return new InputRequirement(Arrays.asList(stacks));
        }

        public boolean matches(ItemStack actual) {
            for (ItemStack expected : alternatives) {
                if (RuneRitualRecipe.matches(expected, actual)) {
                    return true;
                }
            }
            return false;
        }

        public int getRequiredCount() {
            return alternatives.isEmpty() ? 1 : Math.max(1, alternatives.get(0).getCount());
        }

        public List<ItemStack> getAlternatives() {
            List<ItemStack> copies = new ArrayList<>();
            for (ItemStack stack : alternatives) {
                copies.add(stack.copy());
            }
            return copies;
        }

        public ItemStack getDisplayStack() {
            return alternatives.isEmpty() ? ItemStack.EMPTY : alternatives.get(0).copy();
        }
    }

    public static final class RunePosition {
        private final int x;
        private final int z;
        private final ItemStack rune;
        private final boolean consume;

        private RunePosition(int x, int z, ItemStack rune, boolean consume) {
            this.x = x;
            this.z = z;
            this.rune = rune.copy();
            this.rune.setCount(1);
            this.consume = consume;
        }

        public int getX(int transform) {
            switch (transform & 7) {
                case 1: return -x;
                case 2: return x;
                case 3: return -x;
                case 4: return -z;
                case 5: return -z;
                case 6: return z;
                case 7: return z;
                default: return x;
            }
        }

        public int getZ(int transform) {
            switch (transform & 7) {
                case 1: return z;
                case 2: return -z;
                case 3: return -z;
                case 4: return x;
                case 5: return -x;
                case 6: return x;
                case 7: return -x;
                default: return z;
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

        public boolean isConsumed() {
            return consume;
        }
    }

    private final ItemStack center;
    private final List<ItemStack> outputs;
    private final int mana;
    private final int ticks;
    private final List<InputRequirement> inputs;
    private final List<RunePosition> runes;
    private final List<String> specialInputs;
    private final String specialInput;
    private final String specialOutput;

    public RuneRitualRecipe(ItemStack center, ItemStack output, int mana, int ticks,
                            RunePosition... runes) {
        this(center, output.isEmpty() ? Collections.<ItemStack>emptyList()
                        : Collections.singletonList(output), mana, ticks,
                Collections.<InputRequirement>emptyList(), Collections.<String>emptyList(), null, runes);
    }

    public RuneRitualRecipe(ItemStack center, List<ItemStack> outputs, int mana, int ticks,
                            List<InputRequirement> inputs, String specialInput, String specialOutput,
                            RunePosition... runes) {
        this(center, outputs, mana, ticks, inputs,
                specialInput == null ? Collections.<String>emptyList()
                        : Collections.singletonList(specialInput), specialOutput, runes);
    }

    public RuneRitualRecipe(ItemStack center, List<ItemStack> outputs, int mana, int ticks,
                            List<InputRequirement> inputs, List<String> specialInputs,
                            String specialOutput, RunePosition... runes) {
        this.center = center == null ? ItemStack.EMPTY : center.copy();
        this.center.setCount(1);
        List<ItemStack> outputCopies = new ArrayList<>();
        if (outputs != null) {
            for (ItemStack stack : outputs) {
                if (stack != null && !stack.isEmpty()) {
                    outputCopies.add(stack.copy());
                }
            }
        }
        this.outputs = Collections.unmodifiableList(outputCopies);
        this.mana = Math.max(0, mana);
        this.ticks = Math.max(1, ticks);
        this.inputs = Collections.unmodifiableList(new ArrayList<>(inputs == null
                ? Collections.<InputRequirement>emptyList() : inputs));
        this.runes = Collections.unmodifiableList(Arrays.asList(runes));
        List<String> specialInputCopies = new ArrayList<>();
        if (specialInputs != null) {
            for (String input : specialInputs) {
                if (input != null && !input.trim().isEmpty()) {
                    specialInputCopies.add(input.trim());
                }
            }
        }
        this.specialInputs = Collections.unmodifiableList(specialInputCopies);
        this.specialInput = this.specialInputs.isEmpty() ? null : this.specialInputs.get(0);
        this.specialOutput = specialOutput;
    }

    public static RunePosition rune(int x, int z, ItemStack rune) {
        return new RunePosition(x, z, rune, false);
    }

    public static RunePosition rune(int x, int z, ItemStack rune, boolean consume) {
        return new RunePosition(x, z, rune, consume || (x == 0 && z == 0));
    }

    public static boolean matches(ItemStack expected, ItemStack actual) {
        return expected != null && actual != null && !expected.isEmpty() && !actual.isEmpty()
                && expected.getItem() == actual.getItem()
                && (expected.getMetadata() == 32767 || expected.getMetadata() == actual.getMetadata())
                && (!expected.hasTagCompound() || ItemStack.areItemStackTagsEqual(expected, actual));
    }

    public boolean matchesCenter(ItemStack stack) {
        return matches(center, stack);
    }

    public ItemStack getCenter() {
        return center.copy();
    }

    public ItemStack getOutput() {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }

    public List<ItemStack> getOutputs() {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack stack : outputs) {
            copies.add(stack.copy());
        }
        return copies;
    }

    public int getMana() {
        return mana;
    }

    public int getTicks() {
        return ticks;
    }

    public List<InputRequirement> getInputs() {
        return inputs;
    }

    public List<RunePosition> getRunes() {
        return runes;
    }

    public String getSpecialInput() {
        return specialInput;
    }

    public List<String> getSpecialInputs() {
        return specialInputs;
    }

    public String getSpecialOutput() {
        return specialOutput;
    }
}
