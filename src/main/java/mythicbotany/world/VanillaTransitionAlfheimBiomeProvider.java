package mythicbotany.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Maps Minecraft 1.12's native multi-layer climate map to the Alfheim biome
 * palette. The native map supplies irregular, scale-varied boundaries while
 * Alfheim's generator still controls its own surfaces and features.
 */
public final class VanillaTransitionAlfheimBiomeProvider extends BiomeProvider {
    private final BiomeProvider vanillaProvider;

    public VanillaTransitionAlfheimBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);
        vanillaProvider = new BiomeProvider(worldInfo);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return mapBiome(vanillaProvider.getBiome(pos));
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] reuse, int x, int z,
                                          int width, int height) {
        return fill(reuse, x, z, width, height);
    }

    @Override
    public Biome[] getBiomes(Biome[] reuse, int x, int z,
                             int width, int height, boolean cacheFlag) {
        return fill(reuse, x, z, width, height);
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return Arrays.asList(AlfheimBiomes.ALFHEIM_PLAINS,
                AlfheimBiomes.DREAMWOOD_FOREST, AlfheimBiomes.GOLDEN_FIELDS,
                AlfheimBiomes.ALFHEIM_HILLS, AlfheimBiomes.ALFHEIM_LAKES);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        if (allowed == null || allowed.isEmpty()) {
            return true;
        }
        int step = Math.max(4, radius / 4);
        for (int px = x - radius; px <= x + radius; px += step) {
            for (int pz = z - radius; pz <= z + radius; pz += step) {
                if (!allowed.contains(mapBiome(vanillaProvider.getBiome(
                        new BlockPos(px, 0, pz))))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public BlockPos findBiomePosition(int x, int z, int range,
                                      List<Biome> biomes, Random random) {
        if (biomes == null || biomes.isEmpty()) {
            return null;
        }
        BlockPos result = null;
        for (int px = x - range; px <= x + range; px += 4) {
            for (int pz = z - range; pz <= z + range; pz += 4) {
                if (biomes.contains(mapBiome(vanillaProvider.getBiome(
                        new BlockPos(px, 0, pz))))
                        && (result == null || random.nextInt(2) == 0)) {
                    result = new BlockPos(px, 0, pz);
                }
            }
        }
        return result;
    }

    @Override
    public float getTemperatureAtHeight(float temperature, int height) {
        return temperature;
    }

    private Biome[] fill(Biome[] reuse, int x, int z, int width, int height) {
        int size = width * height;
        if (reuse == null || reuse.length < size) {
            reuse = new Biome[size];
        }
        int index = 0;
        for (int dz = 0; dz < height; dz++) {
            for (int dx = 0; dx < width; dx++) {
                reuse[index++] = mapBiome(vanillaProvider.getBiome(
                        new BlockPos(x + dx, 0, z + dz)));
            }
        }
        return reuse;
    }

    private Biome mapBiome(Biome vanillaBiome) {
        String name = vanillaBiome.getRegistryName() == null
                ? vanillaBiome.getBiomeName()
                : vanillaBiome.getRegistryName().getPath();
        name = name.toLowerCase(Locale.ROOT);
        if (name.contains("ocean") || name.contains("river")
                || name.contains("beach") || name.contains("shore")
                || name.contains("swamp") || name.contains("mushroom")) {
            return AlfheimBiomes.ALFHEIM_LAKES;
        }
        if (name.contains("desert") || name.contains("savanna")
                || name.contains("mesa")) {
            return AlfheimBiomes.GOLDEN_FIELDS;
        }
        if (name.contains("hill") || name.contains("mountain")
                || name.contains("extreme") || name.contains("ice")) {
            return AlfheimBiomes.ALFHEIM_HILLS;
        }
        if (name.contains("forest") || name.contains("taiga")
                || name.contains("jungle")) {
            return AlfheimBiomes.DREAMWOOD_FOREST;
        }
        return AlfheimBiomes.ALFHEIM_PLAINS;
    }
}
