package mythicbotany.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAlfPixie extends EntityMob {
    private double targetX;
    private double targetY;
    private double targetZ;
    private int targetTicks;

    public EntityAlfPixie(World world) {
        super(world);
        setSize(0.55F, 0.75F);
        setNoGravity(true);
        enablePersistence();
        experienceValue = 3;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAttackMelee(this, 1.15D, true));
        tasks.addTask(2, new EntityAIWander(this, 0.8D));
        tasks.addTask(3, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityEnderman>(
                this, EntityEnderman.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityEndermite>(
                this, EntityEndermite.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public void onLivingUpdate() {
        if (!world.isRemote) {
            updateFlightTarget();
            steerTowardsFlightTarget();
        }
        super.onLivingUpdate();
        updateFlightRotation();
    }

    private void updateFlightTarget() {
        if (targetTicks-- > 0) {
            return;
        }
        targetTicks = 20 + rand.nextInt(40);
        targetX = posX + (rand.nextDouble() * 2.0D - 1.0D) * 8.0D;
        targetY = posY + (rand.nextDouble() * 2.0D - 1.0D) * 5.0D;
        targetZ = posZ + (rand.nextDouble() * 2.0D - 1.0D) * 8.0D;
        targetY = Math.max(1.0D, Math.min(250.0D, targetY));
    }

    private void steerTowardsFlightTarget() {
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1.0D) {
            targetTicks = 0;
            return;
        }
        motionX = motionX * 0.90D + dx / distance * 0.035D;
        motionY = motionY * 0.90D + dy / distance * 0.035D;
        motionZ = motionZ * 0.90D + dz / distance * 0.035D;
    }

    public void travel(Vec3d travelVector) {
        move(MoverType.SELF, motionX, motionY, motionZ);
        updateFlightRotation();
        motionX *= 0.91D;
        motionY *= 0.91D;
        motionZ *= 0.91D;
    }

    /** Keep the rendered body aligned with the direction the pixie is flying. */
    private void updateFlightRotation() {
        double horizontalSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (horizontalSpeed < 1.0E-4D && Math.abs(motionY) < 1.0E-4D) {
            return;
        }

        float yaw = (float) (MathHelper.atan2(motionZ, motionX) * 180.0D / Math.PI)
                - 90.0F;
        float pitch = (float) (-MathHelper.atan2(motionY, horizontalSpeed)
                * 180.0D / Math.PI);
        rotationYaw = yaw;
        rotationYawHead = yaw;
        renderYawOffset = yaw;
        rotationPitch = pitch;
    }

    public void fall(float distance, float damageMultiplier) {
        // Pixies fly and do not take fall damage.
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
