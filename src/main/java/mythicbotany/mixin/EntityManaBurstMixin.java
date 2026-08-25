package mythicbotany.mixin;

import mythicbotany.block.BlockAlfsteelPylon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.entity.EntityManaBurst;

/** Ensures a pylon is a ray-trace target for both real and simulated bursts. */
@Mixin(EntityManaBurst.class)
public abstract class EntityManaBurstMixin {
    @Redirect(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;"),
            remap = false)
    private RayTraceResult mythicbotany$rayTraceAlfsteelPylon(World world, Vec3d start, Vec3d end) {
        RayTraceResult normalHit = world.rayTraceBlocks(start, end);

        int minX = MathHelper.floor(Math.min(start.x, end.x)) - 1;
        int minY = MathHelper.floor(Math.min(start.y, end.y)) - 1;
        int minZ = MathHelper.floor(Math.min(start.z, end.z)) - 1;
        int maxX = MathHelper.floor(Math.max(start.x, end.x)) + 1;
        int maxY = MathHelper.floor(Math.max(start.y, end.y)) + 1;
        int maxZ = MathHelper.floor(Math.max(start.z, end.z)) + 1;

        RayTraceResult closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(pos);
                    if (!(state.getBlock() instanceof BlockAlfsteelPylon)) {
                        continue;
                    }

                    AxisAlignedBB localBox = state.getBlock().getCollisionBoundingBox(state, world, pos);
                    if (localBox == null) {
                        continue;
                    }
                    AxisAlignedBB box = new AxisAlignedBB(
                            pos.getX() + localBox.minX,
                            pos.getY() + localBox.minY,
                            pos.getZ() + localBox.minZ,
                            pos.getX() + localBox.maxX,
                            pos.getY() + localBox.maxY,
                            pos.getZ() + localBox.maxZ);
                    RayTraceResult hit = box.calculateIntercept(start, end);
                    if (hit == null) {
                        continue;
                    }

                    double distance = start.squareDistanceTo(hit.hitVec);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closest = new RayTraceResult(
                                RayTraceResult.Type.BLOCK, hit.hitVec, hit.sideHit, pos);
                    }
                }
            }
        }
        if (closest == null) {
            return normalHit;
        }
        if (normalHit == null) {
            return closest;
        }
        return start.squareDistanceTo(closest.hitVec) < start.squareDistanceTo(normalHit.hitVec)
                ? closest : normalHit;
    }
}
