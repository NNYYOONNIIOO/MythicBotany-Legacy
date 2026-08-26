package mythicbotany.dimension;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import mythicbotany.world.AlfheimBiomeProvider;
import mythicbotany.world.AlfheimChunkGenerator;

public class WorldProviderAlfheim extends WorldProvider {
    @Override
    protected void init() {
        this.biomeProvider = new AlfheimBiomeProvider(this.world.getWorldInfo());
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.ALFHEIM;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new AlfheimChunkGenerator(world, world.getSeed(),
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
