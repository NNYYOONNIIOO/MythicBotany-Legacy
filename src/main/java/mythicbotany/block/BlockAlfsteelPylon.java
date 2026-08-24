package mythicbotany.block;

import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAlfsteelPylon extends BlockContainer {
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(
            0.125D, 0.0D, 0.125D, 0.875D, 21.0D / 16.0D, 0.875D);

    public BlockAlfsteelPylon() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(10.0F);
        setLightLevel(0.8F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAlfsteelPylon();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileAlfsteelPylon) {
            ((TileAlfsteelPylon) tile).detachSpark();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
