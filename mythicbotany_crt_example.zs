# MythicBotany CraftTweaker 1.12 example.
# Copy this file to the CraftTweaker scripts directory after installing CT.

# World Tree branch: one input, one output, mana.
mods.mythicbotany.addYggdrasilBranchRecipe(
    <minecraft:stick>,
    <minecraft:diamond>,
    6000
);

# Mana infuser: one to sixteen item inputs, one output, mana.
mods.mythicbotany.addManaInfuserRecipe(
    [<minecraft:iron_ingot>, <minecraft:gold_ingot>,
     <minecraft:redstone>, <minecraft:lapis_block>],
    <minecraft:diamond>,
    10000
);

# Rune ritual: center, extra item/entity inputs, output, positioned runes.
# Item inputs use CT brackets; entity inputs are written by registry name.
mods.mythicbotany.addRuneRitual(
    <minecraft:stone>,
    [<minecraft:stone:1>, mods.mythicbotany.entity("minecraft:zombie")],
    <minecraft:stone:1>,
    [[1, 1, <botania:rune>],
     [1, 0, <mythicbotany:helheim_rune>]],
    5000,
    200
);

# The four-argument form uses 0 mana and 200 ticks:
# mods.mythicbotany.addRuneRitual(<minecraft:stone>, [], <minecraft:diamond>, []);
