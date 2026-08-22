package mythicbotany.dimension;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

public class WorldProviderAlfheim extends WorldProvider {
    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.ALFHEIM;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorOverworld(world, world.getSeed(),
                world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().getGeneratorOptions());
    }

    @Override
    public boolean isSurfaceWorld() {
        return true;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

}
