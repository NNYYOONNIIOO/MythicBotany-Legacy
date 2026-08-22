package mythicbotany.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mythicbotany.tile.TileFunctionalFlower;

import java.util.Random;

public class BlockFunctionalFlower extends BlockContainer {
    public enum Mode {
        EXOBLAZE, WITHER_ACONITE, AQUAPANTHUS, HELLEBORE, RAINDELETIA, FEYSYTHIA, PETRUNIA
    }

    private final Mode mode;

    public BlockFunctionalFlower(Mode mode) {
        super(Material.PLANTS);
        this.mode = mode;
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFunctionalFlower(mode);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

}
