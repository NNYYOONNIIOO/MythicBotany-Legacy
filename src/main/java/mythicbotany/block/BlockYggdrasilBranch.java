package mythicbotany.block;

import mythicbotany.registry.ModItems;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** A Yggdrasil branch that fills an empty Gjallar Horn. */
public class BlockYggdrasilBranch extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final AxisAlignedBB NORTH_BOX = new AxisAlignedBB(
            5.0D / 16.0D, 0.0D, 8.0D / 16.0D,
            11.0D / 16.0D, 12.0D / 16.0D, 14.0D / 16.0D);
    private static final AxisAlignedBB EAST_BOX = new AxisAlignedBB(
            2.0D / 16.0D, 0.0D, 5.0D / 16.0D,
            8.0D / 16.0D, 12.0D / 16.0D, 11.0D / 16.0D);
    private static final AxisAlignedBB SOUTH_BOX = new AxisAlignedBB(
            5.0D / 16.0D, 0.0D, 2.0D / 16.0D,
            11.0D / 16.0D, 12.0D / 16.0D, 8.0D / 16.0D);
    private static final AxisAlignedBB WEST_BOX = new AxisAlignedBB(
            8.0D / 16.0D, 0.0D, 5.0D / 16.0D,
            14.0D / 16.0D, 12.0D / 16.0D, 11.0D / 16.0D);

    public BlockYggdrasilBranch() {
        super(Material.WOOD);
        setHardness(4.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);
        setLightOpacity(0);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileYggdrasilBranch();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            net.minecraft.entity.EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
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
        return getBox(state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getBox(state);
    }

    private static AxisAlignedBB getBox(IBlockState state) {
        switch (state.getValue(FACING)) {
            case EAST: return EAST_BOX;
            case SOUTH: return SOUTH_BOX;
            case WEST: return WEST_BOX;
            default: return NORTH_BOX;
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileYggdrasilBranch)) {
            return false;
        }
        TileYggdrasilBranch branch = (TileYggdrasilBranch) tile;
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.isEmpty()) {
            ItemStack stored = branch.takeHorn();
            if (!stored.isEmpty() && !worldIn.isRemote) {
                if (!playerIn.addItemStackToInventory(stored)) {
                    playerIn.dropItem(stored, false);
                }
            }
            return !stored.isEmpty();
        }
        if (held.getItem() != ModItems.gjallarHornEmpty || held.getCount() != 1
                || !branch.insertHorn(held)) {
            return false;
        }
        if (!worldIn.isRemote && !playerIn.capabilities.isCreativeMode) {
            held.shrink(1);
        }
        if (!worldIn.isRemote) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_STEP, SoundCategory.BLOCKS,
                    1.0F, 0.7F);
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileYggdrasilBranch) {
            ((TileYggdrasilBranch) tile).dropContents();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
