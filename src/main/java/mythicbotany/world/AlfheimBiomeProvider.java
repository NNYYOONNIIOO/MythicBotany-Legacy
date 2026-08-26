package mythicbotany.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/** Continuous, domain-warped biome source for Alfheim. */
public final class AlfheimBiomeProvider extends BiomeProvider {
    private final long seed;
    private final BiomeProvider vanillaProvider;

    public AlfheimBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);
        seed = worldInfo.getSeed();
        vanillaProvider = new BiomeProvider(worldInfo);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return getBiomeAt(pos.getX(), pos.getZ());
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

    private Biome[] fill(Biome[] reuse, int x, int z, int width, int height) {
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
        // Reuse vanilla 1.12's multi-layer biome climate map. It provides the
        // irregular, scale-varied boundaries seen in the Overworld instead of
        // the rounded contours produced by one custom threshold field.
        Biome vanillaBiome = vanillaProvider.getBiome(new BlockPos(x, 0, z));
        return mapVanillaBiome(vanillaBiome);
    }

    private Biome mapVanillaBiome(Biome vanillaBiome) {
        String name = vanillaBiome.getRegistryName() == null
                ? vanillaBiome.getBiomeName()
                : vanillaBiome.getRegistryName().getPath();
        name = name.toLowerCase(Locale.ROOT);

        if (name.contains("ocean") || name.contains("river")
                || name.contains("beach") || name.contains("shore")
                || name.contains("swamp")) {
            return AlfheimBiomes.ALFHEIM_LAKES;
        }
        if (name.contains("desert") || name.contains("savanna")
                || name.contains("mesa")) {
            return AlfheimBiomes.GOLDEN_FIELDS;
        }
        if (name.contains("hill") || name.contains("mountain")
                || name.contains("extreme")) {
            return AlfheimBiomes.ALFHEIM_HILLS;
        }
        if (name.contains("forest") || name.contains("taiga")
                || name.contains("jungle")) {
            return AlfheimBiomes.DREAMWOOD_FOREST;
        }
        return AlfheimBiomes.ALFHEIM_PLAINS;
    }

    private double fractalNoise(double x, double z, long salt) {
        double value = 0.0D;
        double amplitude = 1.0D;
        double amplitudeSum = 0.0D;
        double frequency = 1.0D;
        for (int octave = 0; octave < 3; octave++) {
            value += gradientNoise(x * frequency, z * frequency,
                    salt + octave * 0x632BE59BD9B4E019L) * amplitude;
            amplitudeSum += amplitude;
            amplitude *= 0.45D;
            frequency *= 2.0D;
        }
        return value / amplitudeSum;
    }

    private double gradientNoise(double x, double z, long salt) {
        int x0 = (int) Math.floor(x);
        int z0 = (int) Math.floor(z);
        double tx = fade(x - x0);
        double tz = fade(z - z0);
        double n00 = gradientDot(x0, z0, x - x0, z - z0, salt);
        double n10 = gradientDot(x0 + 1, z0, x - x0 - 1.0D, z - z0, salt);
        double n01 = gradientDot(x0, z0 + 1, x - x0, z - z0 - 1.0D, salt);
        double n11 = gradientDot(x0 + 1, z0 + 1,
                x - x0 - 1.0D, z - z0 - 1.0D, salt);
        return lerp(lerp(n00, n10, tx), lerp(n01, n11, tx), tz) * 1.6D;
    }

    private double gradientDot(int x, int z, double dx, double dz, long salt) {
        long value = seed ^ salt;
        value ^= (long) x * 341873128712L;
        value ^= (long) z * 132897987541L;
        value = (value ^ (value >>> 33)) * 0xff51afd7ed558ccdL;
        value = (value ^ (value >>> 33)) * 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        switch ((int) (value & 7L)) {
            case 0: return dx + dz;
            case 1: return -dx + dz;
            case 2: return dx - dz;
            case 3: return -dx - dz;
            case 4: return dx;
            case 5: return -dx;
            case 6: return dz;
            default: return -dz;
        }
    }

    private static double fade(double value) {
        return value * value * value * (value * (value * 6.0D - 15.0D) + 10.0D);
    }

    private static double lerp(double first, double second, double amount) {
        return first + (second - first) * amount;
    }
}
