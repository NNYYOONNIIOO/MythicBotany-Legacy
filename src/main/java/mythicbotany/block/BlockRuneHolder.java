package mythicbotany.block;

import mythicbotany.rune.TileRuneHolder;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRuneHolder extends BlockContainer {
    public BlockRuneHolder() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(6.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRuneHolder();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileRuneHolder)) {
            return false;
        }
        TileRuneHolder holder = (TileRuneHolder) tile;
        if (worldIn.isRemote) {
            return true;
        }
        ItemStack held = playerIn.getHeldItem(hand);
        if (!held.isEmpty() && holder.insertRune(held)) {
            if (!playerIn.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            return true;
        }
        if (held.isEmpty()) {
            ItemStack rune = holder.takeRune();
            if (!rune.isEmpty()) {
                if (!playerIn.addItemStackToInventory(rune)) {
                    playerIn.dropItem(rune, false);
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileRuneHolder) {
            ((TileRuneHolder) tile).dropContents();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
