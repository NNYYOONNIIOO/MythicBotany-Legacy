package mythicbotany.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/** A thrown Mjoellnir which strikes living targets and then returns to its owner. */
public class EntityMjoellnir extends EntityThrowable {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(
            EntityMjoellnir.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Boolean> RETURNING = EntityDataManager.createKey(
            EntityMjoellnir.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CREATIVE_THROW = EntityDataManager.createKey(
            EntityMjoellnir.class, DataSerializers.BOOLEAN);
    private static final int MAX_FLIGHT_TICKS = 80;
    private static final double MAX_FLIGHT_DISTANCE_SQUARED = 64.0D * 64.0D;

    public EntityMjoellnir(World world) {
        super(world);
        setNoGravity(true);
    }

    public EntityMjoellnir(World world, EntityLivingBase thrower, ItemStack stack) {
        super(world, thrower);
        setItem(stack);
        setNoGravity(true);
    }

    @Override
    protected void entityInit() {
        dataManager.register(ITEM, ItemStack.EMPTY);
        dataManager.register(RETURNING, false);
        dataManager.register(CREATIVE_THROW, false);
    }

    public ItemStack getItem() {
        return dataManager.get(ITEM);
    }

    public void setItem(ItemStack stack) {
        dataManager.set(ITEM, stack == null ? ItemStack.EMPTY : stack.copy());
    }

    public void setCreativeThrow(boolean creative) {
        dataManager.set(CREATIVE_THROW, creative);
    }

    private boolean isCreativeThrow() {
        return dataManager.get(CREATIVE_THROW);
    }

    private boolean isReturning() {
        return dataManager.get(RETURNING);
    }

    private void startReturning() {
        if (!isReturning()) {
            dataManager.set(RETURNING, true);
            inGround = false;
            throwableShake = 0;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote || isReturning()) {
            return;
        }
        if (result.entityHit instanceof EntityLivingBase && result.entityHit != getThrower()) {
            EntityLivingBase target = (EntityLivingBase) result.entityHit;
            target.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 5.0F);
            world.addWeatherEffect(new EntityLightningBolt(world, target.posX, target.posY, target.posZ, true));
        }
        startReturning();
    }

    @Override
    public void onUpdate() {
        setNoGravity(true);
        if (!isReturning()) {
            super.onUpdate();
            if (!world.isRemote && !isDead) {
                EntityLivingBase owner = getThrower();
                if (ticksExisted >= MAX_FLIGHT_TICKS
                        || owner != null && getDistanceSq(owner) >= MAX_FLIGHT_DISTANCE_SQUARED) {
                    startReturning();
                }
            }
            return;
        }

        EntityLivingBase owner = getThrower();
        if (!(owner instanceof EntityPlayer) || !owner.isEntityAlive()) {
            if (!world.isRemote && ticksExisted > MAX_FLIGHT_TICKS + 40) {
                dropAndKill();
            }
            return;
        }

        EntityPlayer player = (EntityPlayer) owner;
        double targetX = player.posX;
        double targetY = player.posY + player.getEyeHeight() * 0.75D;
        double targetZ = player.posZ;
        double x = targetX - posX;
        double y = targetY - posY;
        double z = targetZ - posZ;
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance < 1.5D) {
            if (!world.isRemote) {
                returnToOwner(player);
            }
            return;
        }

        double speed = Math.min(2.2D, 0.65D + distance * 0.035D);
        motionX = x / distance * speed;
        motionY = y / distance * speed;
        motionZ = z / distance * speed;
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        setPosition(posX, posY, posZ);
        rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
        rotationPitch = (float) (Math.atan2(motionY,
                Math.sqrt(motionX * motionX + motionZ * motionZ)) * 180.0D / Math.PI);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    private void returnToOwner(EntityPlayer player) {
        if (isCreativeThrow()) {
            setItem(ItemStack.EMPTY);
            setDead();
            return;
        }
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            if (player.getHeldItemMainhand().isEmpty()) {
                player.setHeldItem(EnumHand.MAIN_HAND, stack);
            } else if (player.getHeldItemOffhand().isEmpty()) {
                player.setHeldItem(EnumHand.OFF_HAND, stack);
            } else if (!player.addItemStackToInventory(stack)) {
                player.dropItem(stack, false);
            }
        }
        setItem(ItemStack.EMPTY);
        setDead();
    }

    private void dropAndKill() {
        ItemStack stack = getItem();
        if (!isCreativeThrow() && !stack.isEmpty()) {
            entityDropItem(stack, 0.1F);
        }
        setItem(ItemStack.EMPTY);
        setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            compound.setTag("Item", stack.writeToNBT(new NBTTagCompound()));
        }
        compound.setBoolean("Returning", isReturning());
        compound.setBoolean("CreativeThrow", isCreativeThrow());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setItem(compound.hasKey("Item", 10)
                ? new ItemStack(compound.getCompoundTag("Item")) : ItemStack.EMPTY);
        dataManager.set(RETURNING, compound.getBoolean("Returning"));
        dataManager.set(CREATIVE_THROW, compound.getBoolean("CreativeThrow"));
    }
}
