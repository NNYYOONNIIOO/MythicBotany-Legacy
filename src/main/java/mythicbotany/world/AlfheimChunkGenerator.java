package mythicbotany.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

/** Overworld terrain with Alfheim biomes and a livingrock underground. */
public final class AlfheimChunkGenerator extends ChunkGeneratorOverworld {
    public AlfheimChunkGenerator(World world, long seed, boolean mapFeaturesEnabled,
                                  String generatorOptions) {
        super(world, seed, mapFeaturesEnabled, generatorOptions);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        Chunk chunk = super.generateChunk(chunkX, chunkZ);
        IBlockState livingrock = vazkii.botania.common.block.ModBlocks.livingrock.getDefaultState();
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int y = 0; y < 256; y++) {
                    BlockPos pos = new BlockPos(startX + localX, y, startZ + localZ);
                    if (chunk.getBlockState(pos).getBlock() == Blocks.STONE) {
                        chunk.setBlockState(pos, livingrock);
                    }
                }
            }
        }
        return chunk;
    }
}
