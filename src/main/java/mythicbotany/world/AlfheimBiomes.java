package mythicbotany.world;

import mythicbotany.MythicBotany;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

/** The large-scale biome palette used by the Alfheim dimension. */
public final class AlfheimBiomes {
    public static final Biome ALFHEIM_LAKES = create(
            "alfheim_lakes", -0.4F, 0.08F, 0.7F, 0.9F);
    public static final Biome ALFHEIM_PLAINS = create(
            "alfheim_plains", 0.1F, 0.05F, 0.8F, 0.7F);
    public static final Biome DREAMWOOD_FOREST = create(
            "dreamwood_forest", 0.2F, 0.3F, 0.75F, 0.8F);
    public static final Biome GOLDEN_FIELDS = create(
            "golden_fields", 0.15F, 0.15F, 0.9F, 0.5F);
    public static final Biome ALFHEIM_HILLS = create(
            "alfheim_hills", 1.1F, 0.8F, 0.65F, 0.7F);

    public static final Biome[] ALL = {
            ALFHEIM_LAKES, ALFHEIM_PLAINS, DREAMWOOD_FOREST,
            GOLDEN_FIELDS, ALFHEIM_HILLS
    };

    private AlfheimBiomes() {
    }

    private static Biome create(String name, float baseHeight,
                                       float heightVariation, float temperature,
                                       float rainfall) {
        Biome biome = new Biome(new Biome.BiomeProperties(
                "mythicbotany." + name)
                .setBaseHeight(baseHeight)
                .setHeightVariation(heightVariation)
                .setTemperature(temperature)
                .setRainfall(rainfall)) {
        };
        biome.setRegistryName(new ResourceLocation(MythicBotany.MODID, name));
        return biome;
    }
}
