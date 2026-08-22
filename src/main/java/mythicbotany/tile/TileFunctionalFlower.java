package mythicbotany.tile;

import mythicbotany.block.BlockFunctionalFlower;
import net.minecraft.block.BlockFarmland;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * Mana-backed implementation of MythicBotany's functional flowers.
 *
 * The 1.12 Botania API exposes mana receivers through TileEntities, so the
 * flower block itself is deliberately kept lightweight and all ticking state
 * lives here.
 */
public class TileFunctionalFlower extends ManaTileEntity {
    private static final int RANGE = 4;
    private static final int COST = 20;

    private BlockFunctionalFlower.Mode mode;

    public TileFunctionalFlower() {
        this(BlockFunctionalFlower.Mode.EXOBLAZE);
    }

    public TileFunctionalFlower(BlockFunctionalFlower.Mode mode) {
        this.mode = mode;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote || world.getTotalWorldTime() % 10L != 0L
                || mana < COST || world.isBlockPowered(pos)) {
            return;
        }

        BlockFunctionalFlower.Mode blockMode = getBlockMode();
        if (blockMode != null) {
            mode = blockMode;
        }
        if (applyEffect()) {
            mana -= COST;
            markDirty();
        }
    }

    private BlockFunctionalFlower.Mode getBlockMode() {
        if (world == null) {
            return mode;
        }
        if (world.getBlockState(pos).getBlock() instanceof BlockFunctionalFlower) {
            return ((BlockFunctionalFlower) world.getBlockState(pos).getBlock()).getMode();
        }
        return mode;
    }

    private boolean applyEffect() {
        AxisAlignedBB area = new AxisAlignedBB(pos).grow(RANGE);
        switch (mode) {
            case EXOBLAZE:
                for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                    if (!(entity instanceof EntityPlayer)) {
                        entity.setFire(2);
                    }
                }
                return true;
            case WITHER_ACONITE:
                for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 0));
                }
                return true;
            case HELLEBORE:
                for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                    if (entity.getHealth() < entity.getMaxHealth()) {
                        entity.heal(1.0F);
                    }
                }
                return true;
            case AQUAPANTHUS:
                boolean hydrated = false;
                for (BlockPos farmland : BlockPos.getAllInBoxMutable(pos.add(-2, -1, -2), pos.add(2, -1, 2))) {
                    if (world.getBlockState(farmland).getBlock() == Blocks.FARMLAND
                            && world.getBlockState(farmland).getValue(BlockFarmland.MOISTURE) < 7) {
                        world.setBlockState(farmland, world.getBlockState(farmland)
                                .withProperty(BlockFarmland.MOISTURE, 7), 2);
                        hydrated = true;
                    }
                }
                return hydrated;
            case RAINDELETIA:
                if (world.isRaining() || world.isThundering()) {
                    world.setRainStrength(0.0F);
                    world.setThunderStrength(0.0F);
                    return true;
                }
                return false;
            case FEYSYTHIA:
                for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 0));
                }
                return true;
            case PETRUNIA:
                for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
                    if (!(entity instanceof EntityPlayer)) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 0));
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
