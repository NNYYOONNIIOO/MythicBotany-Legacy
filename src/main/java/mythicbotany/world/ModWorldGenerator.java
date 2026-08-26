package mythicbotany.world;

import mythicbotany.registry.ModBlocks;
import mythicbotany.dimension.ModDimensions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
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
        if (biome == AlfheimBiomes.DREAMWOOD_FOREST && random.nextInt(2) == 0) {
            generateDreamwoodTree(world, random, chunkX * 16 + random.nextInt(16),
                    chunkZ * 16 + random.nextInt(16));
        }
        if (biome == AlfheimBiomes.GOLDEN_FIELDS && random.nextInt(32) == 0) {
            generateAbandonedApothecary(world, random, chunkX * 16 + random.nextInt(16),
                    chunkZ * 16 + random.nextInt(16));
        }
        if (random.nextInt(48) == 0) {
            generateAbandonedApothecary(world, random, chunkX * 16 + random.nextInt(16),
                    chunkZ * 16 + random.nextInt(16));
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
