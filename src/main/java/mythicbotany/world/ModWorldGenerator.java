package mythicbotany.world;

import mythicbotany.registry.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/** Adds MythicBotany ores to the vanilla overworld generation pipeline. */
public class ModWorldGenerator implements IWorldGenerator {
    private static final IBlockState ELEMENTIUM = ModBlocks.elementiumOre.getDefaultState();
    private static final IBlockState DRAGONSTONE = ModBlocks.dragonstoneOre.getDefaultState();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) {
            return;
        }
        generateOre(world, random, chunkX, chunkZ, ELEMENTIUM, 6, 8, 4, 40);
        generateOre(world, random, chunkX, chunkZ, DRAGONSTONE, 4, 5, 2, 24);
    }

    private void generateOre(World world, Random random, int chunkX, int chunkZ,
                             IBlockState state, int veinSize, int attempts,
                             int minHeight, int maxHeight) {
        WorldGenMinable generator = new WorldGenMinable(state, veinSize,
                blockState -> blockState.getBlock() == Blocks.STONE);
        int heightRange = maxHeight - minHeight;
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(heightRange + 1);
            int z = chunkZ * 16 + random.nextInt(16);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }
}
