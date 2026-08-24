package mythicbotany.block;

import mythicbotany.item.MjoellnirHandler;
import mythicbotany.tile.TileMjoellnir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** The physical block form of a Mjoellnir that could not return to its owner. */
public class BlockMjoellnir extends BlockContainer {
    public BlockMjoellnir() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setLightOpacity(0);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMjoellnir();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        if (!MjoellnirHandler.canHold(playerIn)) {
            MjoellnirHandler.sendCannotHoldMessage(playerIn);
            return true;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileMjoellnir)) {
            return false;
        }
        ItemStack stack = ((TileMjoellnir) tile).takeItem();
        if (stack.isEmpty()) {
            return false;
        }
        worldIn.setBlockToAir(pos);
        if (!playerIn.addItemStackToInventory(stack.copy())) {
            playerIn.dropItem(stack, false);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!worldIn.isRemote && tile instanceof TileMjoellnir) {
            ItemStack stack = ((TileMjoellnir) tile).takeItem();
            if (!stack.isEmpty()) {
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5D,
                        pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
