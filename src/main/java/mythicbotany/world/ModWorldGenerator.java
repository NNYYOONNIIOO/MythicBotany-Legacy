package mythicbotany.world;

import mythicbotany.registry.ModBlocks;
import mythicbotany.dimension.ModDimensions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/** Adds MythicBotany ores to the vanilla overworld generation pipeline. */
public class ModWorldGenerator implements IWorldGenerator {
    private static final IBlockState ELEMENTIUM = ModBlocks.elementiumOre.getDefaultState();
    private static final IBlockState DRAGONSTONE = ModBlocks.dragonstoneOre.getDefaultState();
    private static final IBlockState GOLD = ModBlocks.goldOre.getDefaultState();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        int dimension = world.provider.getDimension();
        if (dimension == 0) {
            generateOre(world, random, chunkX, chunkZ, ELEMENTIUM, 6, 8, 4, 40,
                    Blocks.STONE);
            generateOre(world, random, chunkX, chunkZ, DRAGONSTONE, 4, 5, 2, 24,
                    Blocks.STONE);
            return;
        }
        if (dimension != ModDimensions.ALFHEIM_DIMENSION_ID) {
            return;
        }

        Block host = vazkii.botania.common.block.ModBlocks.livingrock;
        generateOre(world, random, chunkX, chunkZ, ELEMENTIUM, 6, 8, 4, 40, host);
        generateOre(world, random, chunkX, chunkZ, DRAGONSTONE, 4, 5, 2, 24, host);
        generateOre(world, random, chunkX, chunkZ, GOLD, 8, 8, 8, 48, host);
        generateAlfheimFeatures(world, random, chunkX, chunkZ);
    }

    private void generateOre(World world, Random random, int chunkX, int chunkZ,
                             IBlockState state, int veinSize, int attempts,
                             int minHeight, int maxHeight, Block host) {
        WorldGenMinable generator = new WorldGenMinable(state, veinSize,
                blockState -> blockState.getBlock() == host);
        int heightRange = maxHeight - minHeight;
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(heightRange + 1);
            int z = chunkZ * 16 + random.nextInt(16);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }

    private void generateAlfheimFeatures(World world, Random random, int chunkX, int chunkZ) {
        Biome biome = world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8));
        int vanillaTreeCount = biome == AlfheimBiomes.DREAMWOOD_FOREST ? 2
                : biome == AlfheimBiomes.ALFHEIM_PLAINS ? 2
                : biome == AlfheimBiomes.ALFHEIM_HILLS ? 1 : 0;
        generateVanillaTrees(world, random, chunkX, chunkZ, biome, vanillaTreeCount);
        if (biome == AlfheimBiomes.DREAMWOOD_FOREST) {
            for (int i = 0; i < 4 + random.nextInt(4); i++) {
                generateDreamwoodTree(world, random, chunkX * 16 + random.nextInt(16),
                        chunkZ * 16 + random.nextInt(16));
            }
            generateFlowers(world, random, chunkX, chunkZ, 7);
        } else if (biome == AlfheimBiomes.ALFHEIM_PLAINS) {
            if (random.nextInt(5) == 0) {
                generateDreamwoodTree(world, random, chunkX * 16 + random.nextInt(16),
                        chunkZ * 16 + random.nextInt(16));
            }
            generateFlowers(world, random, chunkX, chunkZ, 4);
        } else if (biome == AlfheimBiomes.GOLDEN_FIELDS) {
            generateGoldenField(world, random, chunkX, chunkZ);
        } else if (biome == AlfheimBiomes.ALFHEIM_HILLS) {
            if (random.nextInt(8) == 0) {
                generateManaCrystal(world, random, chunkX * 16 + random.nextInt(16),
                        chunkZ * 16 + random.nextInt(16));
            }
            generateFlowers(world, random, chunkX, chunkZ, 3);
        } else if (biome == AlfheimBiomes.ALFHEIM_LAKES) {
            generateLakePlants(world, random, chunkX, chunkZ);
        }

        if (random.nextInt(biome == AlfheimBiomes.DREAMWOOD_FOREST ? 18 : 28) == 0) {
            generateAbandonedApothecary(world, random, chunkX * 16 + random.nextInt(16),
                    chunkZ * 16 + random.nextInt(16));
        }
    }

    private void generateVanillaTrees(World world, Random random, int chunkX, int chunkZ,
                                      Biome biome, int count) {
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            IBlockState ground = world.getBlockState(surface);
            if ((ground.getBlock() != Blocks.GRASS && ground.getBlock() != Blocks.DIRT)
                    || !world.isAirBlock(surface.up())) {
                continue;
            }
            WorldGenTrees tree = new WorldGenTrees(false);
            if (!tree.generate(world, random, surface.up())) {
                generateSimpleOakTree(world, surface.up(), random);
            }
        }
    }

    private void generateSimpleOakTree(World world, BlockPos base, Random random) {
        int height = 4 + random.nextInt(3);
        for (int y = 0; y < height; y++) {
            BlockPos log = base.up(y);
            if (!world.isAirBlock(log)) {
                return;
            }
            world.setBlockState(log, Blocks.LOG.getDefaultState(), 2);
        }
        for (int y = height - 2; y <= height; y++) {
            int radius = y == height ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= radius + 1) {
                        BlockPos leaves = base.add(dx, y, dz);
                        if (world.isAirBlock(leaves)) {
                            world.setBlockState(leaves, Blocks.LEAVES.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }

    private void generateFlowers(World world, Random random, int chunkX, int chunkZ, int count) {
        for (int i = 0; i < count; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            if (world.getBlockState(surface).getMaterial().isSolid()
                    && world.isAirBlock(surface.up())) {
                world.setBlockState(surface.up(), (i & 1) == 0
                        ? Blocks.RED_FLOWER.getDefaultState()
                        : Blocks.YELLOW_FLOWER.getDefaultState(), 2);
            }
        }
    }

    private void generateGoldenField(World world, Random random, int chunkX, int chunkZ) {
        if (random.nextInt(4) != 0) {
            return;
        }
        int centerX = chunkX * 16 + random.nextInt(12) + 2;
        int centerZ = chunkZ * 16 + random.nextInt(12) + 2;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos surface = world.getTopSolidOrLiquidBlock(
                        new BlockPos(centerX + dx, 0, centerZ + dz));
                if (world.getBlockState(surface).getMaterial().isSolid()
                        && world.isAirBlock(surface.up())) {
                    world.setBlockState(surface, Blocks.FARMLAND.getDefaultState(), 2);
                    world.setBlockState(surface.up(), Blocks.WHEAT.getDefaultState()
                            .withProperty(BlockCrops.AGE, 7), 2);
                }
            }
        }
    }

    private void generateManaCrystal(World world, Random random, int x, int z) {
        BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        if (!world.getBlockState(surface).getMaterial().isSolid()) {
            return;
        }
        int height = 2 + random.nextInt(3);
        IBlockState crystal = vazkii.botania.common.block.ModBlocks.bifrostPerm.getDefaultState();
        for (int y = 1; y <= height; y++) {
            BlockPos pos = surface.up(y);
            if (!world.isAirBlock(pos)) {
                return;
            }
            world.setBlockState(pos, crystal, 2);
        }
    }

    private void generateLakePlants(World world, Random random, int chunkX, int chunkZ) {
        for (int i = 0; i < 3; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            if (world.getBlockState(surface).getBlock() == Blocks.WATER
                    && world.isAirBlock(surface.up())) {
                world.setBlockState(surface.up(), Blocks.WATERLILY.getDefaultState(), 2);
            }
        }
    }

    private void generateDreamwoodTree(World world, Random random, int x, int z) {
        BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        if (!world.getBlockState(surface).getMaterial().isSolid()) {
            return;
        }
        int height = 4 + random.nextInt(4);
        IBlockState log = vazkii.botania.common.block.ModBlocks.dreamwood.getDefaultState();
        IBlockState leaves = ModBlocks.dreamwoodLeaves.getDefaultState();
        for (int y = 1; y <= height; y++) {
            BlockPos pos = surface.up(y);
            if (!world.isAirBlock(pos)) {
                return;
            }
            world.setBlockState(pos, log, 2);
        }
        for (int y = height - 2; y <= height + 1; y++) {
            int radius = y == height + 1 ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= radius + 1) {
                        BlockPos pos = surface.add(dx, y, dz);
                        if (world.isAirBlock(pos)) {
                            world.setBlockState(pos, leaves, 2);
                        }
                    }
                }
            }
        }
    }

    private void generateAbandonedApothecary(World world, Random random, int x, int z) {
        BlockPos surface = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        if (!world.getBlockState(surface).getMaterial().isSolid()
                || !world.isAirBlock(surface.up())) {
            return;
        }
        BlockPos base = surface.up();
        world.setBlockState(base, vazkii.botania.common.block.ModBlocks.altar.getDefaultState(), 2);
        IBlockState wood = vazkii.botania.common.block.ModBlocks.livingwood.getDefaultState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    BlockPos pos = base.add(dx, 0, dz);
                    if (world.isAirBlock(pos)) {
                        world.setBlockState(pos, wood, 2);
                    }
                }
            }
        }
    }
}
