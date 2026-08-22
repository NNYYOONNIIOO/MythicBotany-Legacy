package mythicbotany.block;

import mythicbotany.rune.TileCentralRuneHolder;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockCentralRuneHolder extends BlockContainer {
    public BlockCentralRuneHolder() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(6.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCentralRuneHolder();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileCentralRuneHolder)) {
            return false;
        }
        TileCentralRuneHolder holder = (TileCentralRuneHolder) tile;
        if (worldIn.isRemote) {
            return true;
        }
        ItemStack held = playerIn.getHeldItem(hand);
        if (!held.isEmpty() && holder.insertCenter(held)) {
            if (!playerIn.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            return true;
        }
        if (held.isEmpty()) {
            ItemStack output = holder.takeOutput();
            if (!output.isEmpty()) {
                if (!playerIn.addItemStackToInventory(output)) {
                    playerIn.dropItem(output, false);
                }
            } else {
                playerIn.sendStatusMessage(new TextComponentString(holder.getStatus()), true);
            }
            return true;
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileCentralRuneHolder) {
            ((TileCentralRuneHolder) tile).dropContents();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
