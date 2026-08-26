package mythicbotany.world;

import mythicbotany.MythicBotany;
import mythicbotany.entity.EntityAlfPixie;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.AltGrassVariant;
import vazkii.botania.common.block.ModBlocks;

/** The large-scale biome palette used by the Alfheim dimension. */
public final class AlfheimBiomes {
    public static final Biome ALFHEIM_LAKES = create(
            "alfheim_lakes", -0.55F, 0.08F, 0.7F, 0.9F,
            Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState());
    public static final Biome ALFHEIM_PLAINS = create(
            "alfheim_plains", 0.1F, 0.05F, 0.8F, 0.7F,
            altGrass(AltGrassVariant.VIVID), Blocks.DIRT.getDefaultState());
    public static final Biome DREAMWOOD_FOREST = create(
            "dreamwood_forest", 0.3F, 0.25F, 0.75F, 0.8F,
            altGrass(AltGrassVariant.INFUSED), Blocks.DIRT.getDefaultState());
    public static final Biome GOLDEN_FIELDS = create(
            "golden_fields", 0.05F, 0.04F, 0.9F, 0.5F,
            altGrass(AltGrassVariant.GOLDEN), Blocks.DIRT.getDefaultState());
    public static final Biome ALFHEIM_HILLS = create(
            "alfheim_hills", 0.9F, 0.65F, 0.65F, 0.7F,
            altGrass(AltGrassVariant.MUTATED), Blocks.DIRT.getDefaultState());

    public static final Biome[] ALL = {
            ALFHEIM_LAKES, ALFHEIM_PLAINS, DREAMWOOD_FOREST,
            GOLDEN_FIELDS, ALFHEIM_HILLS
    };

    private AlfheimBiomes() {
    }

    private static Biome create(String name, float baseHeight,
                                       float heightVariation, float temperature,
                                       float rainfall, IBlockState top,
                                       IBlockState filler) {
        Biome biome = new Biome(new Biome.BiomeProperties(
                "mythicbotany." + name)
                .setBaseHeight(baseHeight)
                .setHeightVariation(heightVariation)
                .setTemperature(temperature)
                .setRainfall(rainfall)) {
        };
        biome.topBlock = top;
        biome.fillerBlock = filler;
        biome.getSpawnableList(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(
                EntityAlfPixie.class, 5, 4, 10));
        biome.getSpawnableList(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(
                EntityCow.class, 8, 4, 4));
        biome.getSpawnableList(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(
                EntityPig.class, 8, 4, 4));
        biome.getSpawnableList(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(
                EntitySheep.class, 8, 4, 4));
        biome.getSpawnableList(EnumCreatureType.CREATURE).add(new Biome.SpawnListEntry(
                EntityChicken.class, 6, 4, 4));
        biome.getSpawnableList(EnumCreatureType.MONSTER).add(new Biome.SpawnListEntry(
                EntityWitch.class, 2, 1, 2));
        biome.getSpawnableList(EnumCreatureType.MONSTER).add(new Biome.SpawnListEntry(
                EntityIllusionIllager.class, 1, 1, 1));
        biome.setRegistryName(new ResourceLocation(MythicBotany.MODID, name));
        return biome;
    }

    private static IBlockState altGrass(AltGrassVariant variant) {
        return ModBlocks.altGrass.getDefaultState()
                .withProperty(BotaniaStateProps.ALTGRASS_VARIANT, variant);
    }
}
