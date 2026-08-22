package mythicbotany.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterAlfheim extends Teleporter {
    private final WorldServer world;

    public TeleporterAlfheim(WorldServer world) {
        super(world);
        this.world = world;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        BlockPos spawn = world.getSpawnPoint();
        BlockPos surface = world.getTopSolidOrLiquidBlock(spawn);
        entity.setLocationAndAngles(surface.getX() + 0.5D, surface.getY() + 1.0D,
                surface.getZ() + 0.5D, rotationYaw, 0.0F);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
    }
}
