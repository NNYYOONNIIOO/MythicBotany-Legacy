package mythicbotany.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

/** Overworld terrain with Alfheim biomes and a livingrock underground. */
public final class AlfheimChunkGenerator extends ChunkGeneratorOverworld {
    private final World alfheimWorld;

    public AlfheimChunkGenerator(World world, long seed, boolean mapFeaturesEnabled,
                                 String generatorOptions) {
        super(world, seed, mapFeaturesEnabled, generatorOptions);
        this.alfheimWorld = world;
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        Chunk chunk = super.generateChunk(chunkX, chunkZ);
        IBlockState livingrock = vazkii.botania.common.block.ModBlocks.livingrock.getDefaultState();
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = startX + localX;
                int z = startZ + localZ;
                for (int y = 1; y < 255; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (chunk.getBlockState(pos).getBlock() == Blocks.STONE) {
                        chunk.setBlockState(pos, livingrock);
                    }
                }
                applyBiomeSurface(chunk, x, z, alfheimWorld.getBiome(new BlockPos(x, 0, z)));
            }
        }
        return chunk;
    }

    private void applyBiomeSurface(Chunk chunk, int x, int z, Biome biome) {
        int surface = findSurface(chunk, x, z);
        if (surface <= 1) {
            return;
        }

        if (biome == AlfheimBiomes.ALFHEIM_LAKES) {
            final int waterLevel = 63;
            // Do not carve every lake column to one flat shelf. Only columns
            // near sea level become water; higher terrain remains a grassy
            // shoreline. Sand is limited to the actual seabed.
            if (surface > waterLevel + 4) {
                chunk.setBlockState(new BlockPos(x, surface, z), Blocks.GRASS.getDefaultState());
                for (int y = surface - 1; y >= Math.max(1, surface - 4); y--) {
                    chunk.setBlockState(new BlockPos(x, y, z), Blocks.DIRT.getDefaultState());
                }
                return;
            }
            if (surface >= waterLevel) {
                for (int y = waterLevel; y <= surface; y++) {
                    chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                }
                surface = waterLevel - 1;
            }
            BlockPos seabed = new BlockPos(x, surface, z);
            IBlockState seabedState = chunk.getBlockState(seabed);
            if (seabedState.getBlock() == Blocks.STONE
                    || seabedState.getBlock() == Blocks.DIRT
                    || seabedState.getBlock() == vazkii.botania.common.block.ModBlocks.livingrock) {
                chunk.setBlockState(seabed, Blocks.SAND.getDefaultState());
            }
            for (int y = surface + 1; y <= waterLevel; y++) {
                chunk.setBlockState(new BlockPos(x, y, z), Blocks.WATER.getDefaultState());
            }
            return;
        }

        chunk.setBlockState(new BlockPos(x, surface, z), biome.topBlock);
        for (int y = surface - 1; y >= Math.max(1, surface - 4); y--) {
            IBlockState current = chunk.getBlockState(new BlockPos(x, y, z));
            if (current.getMaterial() == Material.GROUND
                    || current.getBlock() == Blocks.STONE
                    || current.getBlock() == Blocks.DIRT
                    || current.getBlock() == vazkii.botania.common.block.ModBlocks.livingrock) {
                chunk.setBlockState(new BlockPos(x, y, z), biome.fillerBlock);
            }
        }
    }

    private int findSurface(Chunk chunk, int x, int z) {
        for (int y = 254; y > 1; y--) {
            if (chunk.getBlockState(new BlockPos(x, y, z)).getMaterial().isSolid()) {
                return y;
            }
        }
        return -1;
    }
}
