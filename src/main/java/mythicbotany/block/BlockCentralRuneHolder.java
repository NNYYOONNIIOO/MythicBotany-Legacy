package mythicbotany.block;

import mythicbotany.rune.TileCentralRuneHolder;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandable;

public class BlockCentralRuneHolder extends BlockContainer implements IWandable {
    private static final AxisAlignedBB HOLDER_BOX = new AxisAlignedBB(
            5.0D / 16.0D, 0.0D, 5.0D / 16.0D,
            11.0D / 16.0D, 3.0D / 16.0D, 11.0D / 16.0D);

    public BlockCentralRuneHolder() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(6.0F);
        setSoundType(SoundType.METAL);
        setLightOpacity(0);
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return HOLDER_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return HOLDER_BOX;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCentralRuneHolder();
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileCentralRuneHolder)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        TileCentralRuneHolder holder = (TileCentralRuneHolder) tile;
        if (!holder.tryStartRitual(player) && player != null) {
            player.sendStatusMessage(holder.getStatusText(), true);
        }
        return true;
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
        if (playerIn.isSneaking()) {
            ItemStack stored = holder.takeStoredItem();
            if (!stored.isEmpty()) {
                if (!playerIn.addItemStackToInventory(stored)) {
                    playerIn.dropItem(stored, false);
                }
            }
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
                playerIn.sendStatusMessage(holder.getStatusText(), true);
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
