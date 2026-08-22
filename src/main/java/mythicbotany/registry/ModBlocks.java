package mythicbotany.registry;

import mythicbotany.MythicBotany;
import mythicbotany.block.BlockFunctionalFlower;
import mythicbotany.block.BlockManaCollector;
import mythicbotany.block.BlockManaInfuser;
import mythicbotany.block.BlockCentralRuneHolder;
import mythicbotany.block.BlockRuneHolder;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public final class ModBlocks {
    public static final Block manaInfuser = named(new BlockManaInfuser(), "mana_infuser");
    public static final Block manaCollector = named(new BlockManaCollector(), "mana_collector");
    public static final Block alfsteelBlock = named(rockBlock(5.0F, 10.0F, SoundType.METAL), "alfsteel_block");
    public static final Block alfsteelPylon = named(rockBlock(3.0F, 10.0F, SoundType.METAL).setLightLevel(0.8F), "alfsteel_pylon");
    public static final Block exoblaze = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.EXOBLAZE), "exoblaze");
    public static final Block witherAconite = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.WITHER_ACONITE), "wither_aconite");
    public static final Block aquapanthus = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.AQUAPANTHUS), "aquapanthus");
    public static final Block hellebore = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.HELLEBORE), "hellebore");
    public static final Block raindeletia = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.RAINDELETIA), "raindeletia");
    public static final Block feysythia = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.FEYSYTHIA), "feysythia");
    public static final Block petrunia = named(new BlockFunctionalFlower(BlockFunctionalFlower.Mode.PETRUNIA), "petrunia");
    public static final Block yggdrasilBranch = named(new SoundBlock(Material.WOOD, SoundType.WOOD).setHardness(4.0F).setResistance(4.0F), "yggdrasil_branch");
    public static final Block runeHolder = named(new BlockRuneHolder(), "rune_holder");
    public static final Block centralRuneHolder = named(new BlockCentralRuneHolder(), "central_rune_holder");
    public static final Block mjoellnir = named(rockBlock(50.0F, 1200.0F, SoundType.METAL), "mjoellnir");
    public static final Block dreamwoodLeaves = named(new SoundBlock(Material.LEAVES, SoundType.PLANT).setHardness(0.2F).setLightOpacity(1), "dreamwood_leaves");
    public static final Block elementiumOre = named(rockBlock(4.0F, 5.0F, SoundType.STONE), "elementium_ore");
    public static final Block dragonstoneOre = named(rockBlock(5.0F, 8.0F, SoundType.STONE), "dragonstone_ore");
    public static final Block goldOre = named(rockBlock(3.0F, 5.0F, SoundType.STONE), "gold_ore");
    public static final Block rawElementiumBlock = named(rockBlock(5.0F, 8.0F, SoundType.METAL), "raw_elementium_block");

    public static final Block[] ALL = {
            manaInfuser, manaCollector, alfsteelBlock, alfsteelPylon,
            exoblaze, witherAconite, aquapanthus, hellebore, raindeletia, feysythia, petrunia,
            yggdrasilBranch, runeHolder, centralRuneHolder, mjoellnir, dreamwoodLeaves,
            elementiumOre, dragonstoneOre, goldOre, rawElementiumBlock
    };

    private ModBlocks() {
    }

    private static Block rockBlock(float hardness, float resistance, SoundType sound) {
        return new SoundBlock(Material.ROCK, sound).setHardness(hardness).setResistance(resistance);
    }

    private static <T extends Block> T named(T block, String name) {
        block.setRegistryName(new ResourceLocation(MythicBotany.MODID, name));
        block.setTranslationKey(MythicBotany.MODID + "." + name);
        block.setCreativeTab(MythicBotany.TAB);
        return block;
    }

    private static class SoundBlock extends Block {
        private SoundBlock(Material material, SoundType sound) {
            super(material);
            setSoundType(sound);
        }
    }
}
