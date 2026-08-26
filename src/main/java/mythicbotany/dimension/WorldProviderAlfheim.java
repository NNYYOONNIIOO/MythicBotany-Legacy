package mythicbotany.dimension;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.gen.IChunkGenerator;
import mythicbotany.world.AlfheimChunkGenerator;
import mythicbotany.world.VanillaTransitionAlfheimBiomeProvider;

public class WorldProviderAlfheim extends WorldProvider {
    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new VanillaTransitionAlfheimBiomeProvider(
                this.world.getWorldInfo());
        this.generateLightBrightnessTable();
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
        return true;
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        if (player != null && player.getBedLocation() != null
                && player.getSpawnDimension() == getDimension()) {
            return getDimension();
        }
        return 0;
    }

}
