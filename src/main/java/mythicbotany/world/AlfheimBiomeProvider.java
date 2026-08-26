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
        // Sample continuous value noise instead of floor-dividing coordinates. The
        // old implementation changed biome exactly at 192/256-block boundaries,
        // which made every boundary line up with a chunk edge.
        double continentalness = smoothNoise(x / 256.0D, z / 256.0D, 0x31A7L);
        double weirdness = smoothNoise(x / 192.0D, z / 192.0D, 0x9E37L);
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

    private double smoothNoise(double x, double z, long salt) {
        int x0 = (int) Math.floor(x);
        int z0 = (int) Math.floor(z);
        double tx = fade(x - x0);
        double tz = fade(z - z0);
        double n00 = hash(x0, z0, salt);
        double n10 = hash(x0 + 1, z0, salt);
        double n01 = hash(x0, z0 + 1, salt);
        double n11 = hash(x0 + 1, z0 + 1, salt);
        double nx0 = lerp(n00, n10, tx);
        double nx1 = lerp(n01, n11, tx);
        return lerp(nx0, nx1, tz);
    }

    private double hash(int x, int z, long salt) {
        long value = seed ^ salt;
        value ^= (long) x * 341873128712L;
        value ^= (long) z * 132897987541L;
        value = (value ^ (value >>> 33)) * 0xff51afd7ed558ccdL;
        value = (value ^ (value >>> 33)) * 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return ((value & 0x1FFFFFFFFFFFFFL) / (double) 0x20000000000000L) * 2.0D - 1.0D;
    }

    private static double fade(double value) {
        return value * value * (3.0D - 2.0D * value);
    }

    private static double lerp(double first, double second, double amount) {
        return first + (second - first) * amount;
    }
}
