package mythicbotany.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockFunctionalFlower extends Block {
    public enum Mode {
        EXOBLAZE, WITHER_ACONITE, AQUAPANTHUS, HELLEBORE, RAINDELETIA, FEYSYTHIA, PETRUNIA
    }

    private final Mode mode;

    public BlockFunctionalFlower(Mode mode) {
        super(Material.PLANTS);
        this.mode = mode;
        setTickRandomly(true);
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        AxisAlignedBB area = new AxisAlignedBB(pos).grow(4.0D);
        switch (mode) {
            case EXOBLAZE:
                for (EntityLivingBase entity : living(worldIn, area)) {
                    if (!(entity instanceof EntityPlayer)) {
                        entity.setFire(2);
                    }
                }
                break;
            case WITHER_ACONITE:
                for (EntityLivingBase entity : living(worldIn, area)) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 0));
                }
                break;
            case HELLEBORE:
                for (EntityLivingBase entity : living(worldIn, area)) {
                    entity.heal(1.0F);
                }
                break;
            case AQUAPANTHUS:
                for (BlockPos farmland : BlockPos.getAllInBoxMutable(pos.add(-2, -1, -2), pos.add(2, -1, 2))) {
                    IBlockState farmlandState = worldIn.getBlockState(farmland);
                    if (farmlandState.getBlock() == Blocks.FARMLAND) {
                        worldIn.setBlockState(farmland, farmlandState.withProperty(BlockFarmland.MOISTURE, 7), 2);
                    }
                }
                break;
            case RAINDELETIA:
                worldIn.setRainStrength(0.0F);
                break;
            case FEYSYTHIA:
                for (EntityLivingBase entity : living(worldIn, area)) {
                    entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 0));
                }
                break;
            case PETRUNIA:
                for (EntityLivingBase entity : living(worldIn, area)) {
                    if (!(entity instanceof EntityPlayer)) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 0));
                    }
                }
                break;
            default:
                break;
        }
    }

    private List<EntityLivingBase> living(World world, AxisAlignedBB area) {
        return world.getEntitiesWithinAABB(EntityLivingBase.class, area);
    }
}
