package mythicbotany.crafttweaker;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import mythicbotany.recipe.InfuserRecipe;
import mythicbotany.recipe.YggdrasilBranchRecipe;
import mythicbotany.rune.RuneRitualRecipe;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** CraftTweaker 1.12 recipe API for MythicBotany. */
@ModOnly("crafttweaker")
@ZenClass("mods.mythicbotany")
@ZenRegister
public final class MythicBotanyCraftTweaker {
    private MythicBotanyCraftTweaker() {
    }

    /** One input, one output and mana for a Yggdrasil branch. */
    @ZenMethod
    public static void addYggdrasilBranchRecipe(IItemStack input, IItemStack output, int mana) {
        ItemStack in = stack(input);
        ItemStack out = stack(output);
        if (in.isEmpty() || out.isEmpty()) {
            error("Yggdrasil branch recipes require a non-empty input and output");
            return;
        }
        YggdrasilBranchRecipe.register(in, out, mana);
    }

    /** One to sixteen item inputs, one output and mana for the mana infuser. */
    @ZenMethod
    public static void addManaInfuserRecipe(IItemStack[] inputs, IItemStack output, int mana) {
        if (inputs == null || inputs.length == 0 || inputs.length > InfuserRecipe.MAX_INPUTS) {
            error("Mana infuser recipes require between 1 and 16 inputs");
            return;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (IItemStack input : inputs) {
            ItemStack value = stack(input);
            if (value.isEmpty()) {
                error("Mana infuser inputs cannot be empty");
                return;
            }
            stacks.add(value);
        }
        ItemStack result = stack(output);
        if (result.isEmpty()) {
            error("Mana infuser recipes require a non-empty output");
            return;
        }
        InfuserRecipe.register(stacks, result, mana);
    }

    /** Four-argument ritual form: center, extra inputs, output and positioned runes. */
    @ZenMethod
    public static void addRuneRitual(IItemStack center, Object[] extras,
                                     IItemStack output, Object[] runes) {
        addRuneRitual(center, extras, output, runes, 0, 200);
    }

    /** Ritual form with explicit mana cost and duration in ticks. */
    @ZenMethod
    public static void addRuneRitual(IItemStack center, Object[] extras,
                                     IItemStack output, Object[] runes,
                                     int mana, int ticks) {
        ItemStack centerStack = stack(center);
        ItemStack outputStack = stack(output);
        if (centerStack.isEmpty() || outputStack.isEmpty()) {
            error("Rune rituals require a non-empty center and output");
            return;
        }

        List<RuneRitualRecipe.InputRequirement> itemInputs = new ArrayList<>();
        List<String> entityInputs = new ArrayList<>();
        if (extras != null) {
            for (Object extra : extras) {
                if (!readExtra(extra, itemInputs, entityInputs)) {
                    error("Invalid rune ritual extra input: " + extra);
                    return;
                }
            }
        }

        List<RuneRitualRecipe.RunePosition> positions = new ArrayList<>();
        if (!readRunes(runes, positions)) {
            error("Invalid rune ritual positions; use flat triples such as "
                    + "[1, 1, <botania:rune>, 1, 0, <mythicbotany:helheim_rune>]");
            return;
        }

        RuneRitualRegistry.register(new RuneRitualRecipe(centerStack,
                Collections.singletonList(outputStack), Math.max(0, mana), Math.max(1, ticks),
                itemInputs, entityInputs, null,
                positions.toArray(new RuneRitualRecipe.RunePosition[0])));
    }

    /** Marks a string as an entity registry name in a mixed input list. */
    @ZenMethod
    public static String entity(String registryName) {
        return registryName;
    }

    /**
     * Legacy helper for scripts that build rune entries programmatically.
     * Direct array syntax is also supported, so this helper is not required:
     * [[1, 1, <botania:rune>], [1, 0, <mythicbotany:helheim_rune>]]
     */
    @ZenMethod
    public static Object[] rune(int x, int z, IItemStack rune) {
        return new Object[]{x, z, rune};
    }

    private static ItemStack stack(IItemStack value) {
        if (value == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = CraftTweakerMC.getItemStack(value);
        return result == null ? ItemStack.EMPTY : result.copy();
    }

    private static boolean readExtra(Object value,
                                     List<RuneRitualRecipe.InputRequirement> itemInputs,
                                     List<String> entityInputs) {
        if (value instanceof IItemStack) {
            ItemStack item = stack((IItemStack) value);
            if (item.isEmpty()) {
                return false;
            }
            itemInputs.add(RuneRitualRecipe.InputRequirement.of(item));
            return true;
        }

        String descriptor = extractName(value);
        if (descriptor == null) {
            return false;
        }
        descriptor = normalize(descriptor);
        ItemStack item = parseItem(descriptor);
        if (!item.isEmpty()) {
            itemInputs.add(RuneRitualRecipe.InputRequirement.of(item));
        } else if (!descriptor.isEmpty()) {
            entityInputs.add(descriptor);
        } else {
            return false;
        }
        return true;
    }

    private static RuneRitualRecipe.RunePosition readRune(Object value) {
        Object[] entry = array(value);
        if (entry == null || entry.length < 2) {
            return null;
        }

        // ZenScript tuples such as (1, 0, <mythicbotany:helheim_rune>) arrive
        // as a flat three-element array. This is the preferred public form.
        if (entry.length >= 3 && entry[0] instanceof Number && entry[1] instanceof Number) {
            return makeRune(((Number) entry[0]).intValue(),
                    ((Number) entry[1]).intValue(), entry[2]);
        }

        // Keep accepting the original nested representation for old scripts.
        Object[] position = array(entry[0]);
        if (position != null && position.length >= 2
                && position[0] instanceof Number && position[1] instanceof Number) {
            return makeRune(((Number) position[0]).intValue(),
                    ((Number) position[1]).intValue(), entry[1]);
        }
        return null;
    }

    /**
     * Reads the rune list in a form that CraftTweaker 1.12 can actually pass
     * to Java. Nested any[] values and tuple literals are not supported by
     * that ZenScript runtime, so each position is represented by three
     * consecutive values: x, z and the rune item stack.
     */
    private static boolean readRunes(Object[] values,
                                     List<RuneRitualRecipe.RunePosition> positions) {
        if (values == null) {
            return true;
        }
        int index = 0;
        while (index < values.length) {
            RuneRitualRecipe.RunePosition position;
            if (index + 2 < values.length
                    && values[index] instanceof Number
                    && values[index + 1] instanceof Number) {
                position = makeRune(((Number) values[index]).intValue(),
                        ((Number) values[index + 1]).intValue(), values[index + 2]);
                index += 3;
            } else {
                position = readRune(values[index]);
                index++;
            }
            if (position == null) {
                return false;
            }
            positions.add(position);
        }
        return true;
    }

    private static RuneRitualRecipe.RunePosition makeRune(int x, int z, Object value) {
        if (!(value instanceof IItemStack)) {
            return null;
        }
        ItemStack rune = stack((IItemStack) value);
        return rune.isEmpty() ? null : RuneRitualRecipe.rune(x, z, rune);
    }

    private static Object[] array(Object value) {
        if (value instanceof Object[]) {
            return (Object[]) value;
        }
        if (value instanceof int[]) {
            int[] numbers = (int[]) value;
            Object[] result = new Object[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                result[i] = numbers[i];
            }
            return result;
        }
        if (value instanceof Iterable) {
            List<Object> values = new ArrayList<>();
            for (Object element : (Iterable<?>) value) {
                values.add(element);
            }
            return values.toArray(new Object[0]);
        }
        return null;
    }

    private static ItemStack parseItem(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) {
            return ItemStack.EMPTY;
        }
        String id = descriptor;
        int metadata = 0;
        int lastColon = id.lastIndexOf(':');
        int firstColon = id.indexOf(':');
        if (lastColon > firstColon) {
            try {
                metadata = Integer.parseInt(id.substring(lastColon + 1));
                id = id.substring(0, lastColon);
            } catch (NumberFormatException ignored) {
                // The last component belongs to the registry path.
            }
        }
        try {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            return item == null ? ItemStack.EMPTY : new ItemStack(item, 1, metadata);
        } catch (IllegalArgumentException exception) {
            return ItemStack.EMPTY;
        }
    }

    private static String extractName(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        for (String methodName : new String[]{"getName", "getId", "getRegistryName"}) {
            try {
                Method method = value.getClass().getMethod(methodName);
                Object result = method.invoke(value);
                if (result instanceof ResourceLocation) {
                    return result.toString();
                }
                if (result instanceof String && !((String) result).isEmpty()) {
                    return (String) result;
                }
            } catch (Exception ignored) {
                // Try the next representation exposed by the CT object.
            }
        }
        String text = value.toString();
        return text.contains(":") ? text : null;
    }

    private static String normalize(String value) {
        String result = value == null ? "" : value.trim();
        if (result.startsWith("<") && result.endsWith(">")) {
            result = result.substring(1, result.length() - 1);
        }
        if (result.startsWith("entity:")) {
            result = result.substring("entity:".length());
        }
        return result;
    }

    private static void error(String message) {
        System.err.println("[MythicBotany CraftTweaker] " + message);
    }
}
