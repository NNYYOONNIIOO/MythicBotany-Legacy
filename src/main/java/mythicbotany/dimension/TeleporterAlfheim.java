package mythicbotany.dimension;

import mythicbotany.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.state.enums.LivingWoodVariant;

public class TeleporterAlfheim extends Teleporter {
    private final WorldServer world;
    private final BlockPos sourcePos;
    private final boolean returning;

    public TeleporterAlfheim(WorldServer world) {
        this(world, world.getSpawnPoint(), false);
    }

    public TeleporterAlfheim(WorldServer world, BlockPos sourcePos, boolean returning) {
        super(world);
        this.world = world;
        this.sourcePos = sourcePos;
        this.returning = returning;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
        BlockPos destination = returning ? findOverworldDestination() : findOrCreateReturnPortal();
        entity.setLocationAndAngles(destination.getX() + 0.5D, destination.getY(),
                destination.getZ() + 0.5D, rotationYaw, 0.0F);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.timeUntilPortal = 200;
    }

    private BlockPos findOrCreateReturnPortal() {
        BlockPos existing = findBlock(ModBlocks.returnPortal);
        if (existing != null) {
            return existing;
        }

        BlockPos source = sourcePos == null ? world.getSpawnPoint() : sourcePos;
        BlockPos surface = world.getTopSolidOrLiquidBlock(
                new BlockPos(source.getX(), 0, source.getZ()));
        BlockPos center = surface.up();
        IBlockState livingwood = vazkii.botania.common.block.ModBlocks.livingwood
                .getDefaultState()
                .withProperty(BotaniaStateProps.LIVINGWOOD_VARIANT, LivingWoodVariant.DEFAULT);
        IBlockState glimmering = vazkii.botania.common.block.ModBlocks.livingwood
                .getDefaultState()
                .withProperty(BotaniaStateProps.LIVINGWOOD_VARIANT, LivingWoodVariant.GLIMMERING);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    world.setBlockState(center.add(x, 0, z),
                            x == 0 || z == 0 ? glimmering : livingwood, 3);
                }
            }
        }
        world.setBlockState(center, ModBlocks.returnPortal.getDefaultState(), 3);
        return center;
    }

    private BlockPos findOverworldDestination() {
        BlockPos portal = findBlock(vazkii.botania.common.block.ModBlocks.alfPortal);
        if (portal != null) {
            return portal.up();
        }
        BlockPos source = sourcePos == null ? world.getSpawnPoint() : sourcePos;
        return world.getTopSolidOrLiquidBlock(
                new BlockPos(source.getX(), 0, source.getZ())).up();
    }

    private BlockPos findBlock(Block block) {
        BlockPos source = sourcePos == null ? world.getSpawnPoint() : sourcePos;
        for (int y = world.getActualHeight() - 1; y >= 0; y--) {
            BlockPos pos = new BlockPos(source.getX(), y, source.getZ());
            if (world.getBlockState(pos).getBlock() == block) {
                return pos;
            }
        }
        return null;
    }
}
