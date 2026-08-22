package mythicbotany.registry;

import mythicbotany.MythicBotany;
import mythicbotany.block.BlockManaCollector;
import mythicbotany.block.BlockManaInfuser;
import mythicbotany.block.BlockCentralRuneHolder;
import mythicbotany.block.BlockRuneHolder;
import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.block.BlockAlfsteelPylon;
import mythicbotany.block.BlockDreamwoodLeaves;
import mythicbotany.block.BlockDragonstoneOre;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public final class ModBlocks {
    public static final Block manaInfuser = named(new BlockManaInfuser(), "mana_infuser");
    public static final Block manaCollector = named(new BlockManaCollector(), "mana_collector");
    public static final Block alfsteelBlock = named(rockBlock(5.0F, 10.0F, SoundType.METAL), "alfsteel_block");
    public static final Block alfsteelPylon = named(new BlockAlfsteelPylon(), "alfsteel_pylon");
    public static final Block yggdrasilBranch = named(new BlockYggdrasilBranch(), "yggdrasil_branch");
    public static final Block runeHolder = named(new BlockRuneHolder(), "rune_holder");
    public static final Block centralRuneHolder = named(new BlockCentralRuneHolder(), "central_rune_holder");
    public static final Block dreamwoodLeaves = named(new BlockDreamwoodLeaves(), "dreamwood_leaves");
    public static final Block elementiumOre = named(rockBlock(4.0F, 5.0F, SoundType.STONE), "elementium_ore");
    public static final Block dragonstoneOre = named(new BlockDragonstoneOre(), "dragonstone_ore");
    public static final Block goldOre = named(rockBlock(3.0F, 5.0F, SoundType.STONE), "gold_ore");

    public static final Block[] ALL = {
            manaInfuser, manaCollector, alfsteelBlock, alfsteelPylon,
            yggdrasilBranch, runeHolder, centralRuneHolder, dreamwoodLeaves,
            elementiumOre, dragonstoneOre, goldOre
    };

    private ModBlocks() {
    }

    private static Block rockBlock(float hardness, float resistance, SoundType sound) {
        return new SoundBlock(Material.ROCK, sound).setHardness(hardness).setResistance(resistance);
    }

    private static <T extends Block> T named(T block, String name) {
        block.setRegistryName(new ResourceLocation(MythicBotany.MODID, name));
        block.setTranslationKey(MythicBotany.MODID + ":" + name);
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
