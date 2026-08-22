package mythicbotany.block;

import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAlfsteelPylon extends BlockContainer {
    public BlockAlfsteelPylon() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(10.0F);
        setLightLevel(0.8F);
        setSoundType(SoundType.METAL);
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileAlfsteelPylon) {
            ((TileAlfsteelPylon) tile).detachSpark();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
