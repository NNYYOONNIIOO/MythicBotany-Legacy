package mythicbotany.block;

import mythicbotany.tile.TileManaInfuser;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockManaInfuser extends BlockContainer {
    public BlockManaInfuser() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileManaInfuser();
    }

    private static final AxisAlignedBB THREE_PIXEL_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return THREE_PIXEL_BOX;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof TileManaInfuser)) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        TileManaInfuser infuser = (TileManaInfuser) tileEntity;
        ItemStack held = playerIn.getHeldItem(hand);
        if (!held.isEmpty() && infuser.insertInput(held)) {
            if (!playerIn.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            return true;
        }
        if (held.isEmpty()) {
            ItemStack output = infuser.takeOutput();
            if (!output.isEmpty()) {
                if (!playerIn.addItemStackToInventory(output)) {
                    playerIn.dropItem(output, false);
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileManaInfuser) {
            ((TileManaInfuser) tileEntity).dropContents();
        }
        super.breakBlock(worldIn, pos, state);
    }
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return THREE_PIXEL_BOX;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

}
