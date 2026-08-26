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
                    IBlockState state = chunk.getBlockState(pos);
                    if (state.getBlock() == vazkii.botania.common.block.ModBlocks.altGrass
                            && state.getBlock().getMetaFromState(state) != 1) {
                        chunk.setBlockState(pos, Blocks.GRASS.getDefaultState());
                        state = Blocks.GRASS.getDefaultState();
                    }
                    if (state.getBlock() == Blocks.STONE) {
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
            applyLakeSurface(chunk, x, z, surface);
            return;
        }

        // Golden grass is reserved for the center of golden fields. Every
        // other biome uses the vanilla grass block, including transition areas.
        boolean goldenCore = biome == AlfheimBiomes.GOLDEN_FIELDS
                && !isBiomeEdge(x, z, biome, 32);
        IBlockState top = goldenCore ? biome.topBlock : Blocks.GRASS.getDefaultState();
        chunk.setBlockState(new BlockPos(x, surface, z), top);
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

    private void applyLakeSurface(Chunk chunk, int x, int z, int surface) {
        // The overworld generator already gives us a continuous terrain height.
        // Water follows that terrain instead of carving a fixed sand shelf.
        final int seaLevel = 62;
        if (surface >= seaLevel || isBiomeEdge(x, z, AlfheimBiomes.ALFHEIM_LAKES, 24)) {
            setGroundColumn(chunk, x, z, surface, Blocks.GRASS.getDefaultState(),
                    Blocks.DIRT.getDefaultState());
            return;
        }

        // Use dirt for the bed and its shallow subsoil. No sand layer is
        // generated, so shorelines do not turn into an abrupt horizontal band.
        chunk.setBlockState(new BlockPos(x, surface, z), Blocks.DIRT.getDefaultState());
        for (int y = surface - 1; y >= Math.max(1, surface - 3); y--) {
            IBlockState current = chunk.getBlockState(new BlockPos(x, y, z));
            if (current.getMaterial() == Material.GROUND
                    || current.getBlock() == Blocks.STONE
                    || current.getBlock() == vazkii.botania.common.block.ModBlocks.livingrock) {
                chunk.setBlockState(new BlockPos(x, y, z), Blocks.DIRT.getDefaultState());
            }
        }
        IBlockState water = Blocks.WATER.getDefaultState();
        for (int y = surface + 1; y <= seaLevel; y++) {
            chunk.setBlockState(new BlockPos(x, y, z), water);
        }
    }

    private void setGroundColumn(Chunk chunk, int x, int z, int surface,
                                  IBlockState top, IBlockState filler) {
        chunk.setBlockState(new BlockPos(x, surface, z), top);
        for (int y = surface - 1; y >= Math.max(1, surface - 4); y--) {
            IBlockState current = chunk.getBlockState(new BlockPos(x, y, z));
            if (current.getMaterial() == Material.GROUND
                    || current.getBlock() == Blocks.STONE
                    || current.getBlock() == Blocks.DIRT
                    || current.getBlock() == vazkii.botania.common.block.ModBlocks.livingrock) {
                chunk.setBlockState(new BlockPos(x, y, z), filler);
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

    private boolean isBiomeEdge(int x, int z, Biome center, int radius) {
        for (int dx = -radius; dx <= radius; dx += 2) {
            for (int dz = -radius; dz <= radius; dz += 2) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (alfheimWorld.getBiome(new BlockPos(x + dx, 0, z + dz)) != center) {
                    return true;
                }
            }
        }
        return false;
    }
}
