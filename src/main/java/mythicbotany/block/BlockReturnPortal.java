package mythicbotany.block;

import mythicbotany.dimension.AlfheimPortalHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.state.BotaniaStateProps;

/** A non-solid portal block used to return from Alfheim. */
public final class BlockReturnPortal extends Block {
    public BlockReturnPortal() {
        super(Material.PORTAL);
        setHardness(-1.0F);
        setResistance(6000000.0F);
        setLightLevel(0.75F);
        setSoundType(SoundType.GLASS);
        setTickRandomly(true);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!world.isRemote && entity instanceof EntityPlayerMP) {
            AlfheimPortalHandler.onReturnPortalCollision((EntityPlayerMP) entity, pos);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, java.util.Random random) {
        removeIfFrameBroken(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
                                Block blockIn, BlockPos fromPos) {
        removeIfFrameBroken(world, pos);
    }

    private static void removeIfFrameBroken(World world, BlockPos pos) {
        if (!world.isRemote && !hasValidFrame(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    public static boolean isFrameBlock(IBlockState state) {
        return state != null
                && state.getBlock() == vazkii.botania.common.block.ModBlocks.livingwood;
    }

    public static boolean hasValidFrame(World world, BlockPos center) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if ((x != 0 || z != 0)
                        && !isFrameBlock(world.getBlockState(center.add(x, 0, z)))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world,
                                                 BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
