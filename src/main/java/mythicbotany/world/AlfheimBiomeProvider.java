package mythicbotany.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.biome.BiomeProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Deterministic, low-frequency biome regions for Alfheim. */
public final class AlfheimBiomeProvider extends BiomeProvider {
    private final long seed;

    public AlfheimBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);
        seed = worldInfo.getSeed();
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return getBiomeAt(pos.getX(), pos.getZ());
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] reuse,
                                                  int x, int z,
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
                AlfheimBiomes.DREAMWOOD_FOREST, AlfheimBiomes.GOLDEN_FIELDS);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        if (allowed == null || allowed.isEmpty()) {
            return true;
        }
        int step = Math.max(4, radius / 4);
        for (int px = x - radius; px <= x + radius; px += step) {
            for (int pz = z - radius; pz <= z + radius; pz += step) {
                if (!allowed.contains(getBiomeAt(px, pz))) {
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
        int step = 4;
        for (int px = x - range; px <= x + range; px += step) {
            for (int pz = z - range; pz <= z + range; pz += step) {
                if (biomes.contains(getBiomeAt(px, pz))
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

    private Biome[] fill(Biome[] reuse, int x, int z,
                                int width, int height) {
        int size = width * height;
        if (reuse == null || reuse.length < size) {
            reuse = new Biome[size];
        }
        int index = 0;
        for (int dz = 0; dz < height; dz++) {
            for (int dx = 0; dx < width; dx++) {
                reuse[index++] = getBiomeAt(x + dx, z + dz);
            }
        }
        return reuse;
    }

    private Biome getBiomeAt(int x, int z) {
        double continentalness = noise(Math.floorDiv(x, 256), Math.floorDiv(z, 256), 0x31A7L);
        double weirdness = noise(Math.floorDiv(x, 192), Math.floorDiv(z, 192), 0x9E37L);
        if (continentalness < -0.35D) {
            return AlfheimBiomes.ALFHEIM_LAKES;
        }
        if (continentalness < -0.05D) {
            return AlfheimBiomes.ALFHEIM_PLAINS;
        }
        if (continentalness < 0.35D) {
            return weirdness < 0.0D
                    ? AlfheimBiomes.DREAMWOOD_FOREST : AlfheimBiomes.GOLDEN_FIELDS;
        }
        return AlfheimBiomes.ALFHEIM_HILLS;
    }

    private double noise(int x, int z, long salt) {
        long value = seed ^ salt;
        value ^= (long) x * 341873128712L;
        value ^= (long) z * 132897987541L;
        value = (value ^ (value >>> 33)) * 0xff51afd7ed558ccdL;
        value = (value ^ (value >>> 33)) * 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return ((value & 0x1FFFFFFFFFFFFFL) / (double) 0x20000000000000L) * 2.0D - 1.0D;
    }
}
