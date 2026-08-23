package mythicbotany.block;

import mythicbotany.recipe.InfuserRecipe;
import mythicbotany.tile.TileManaInfuser;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.botania.api.BotaniaAPI;

public class BlockManaInfuser extends BlockContainer {
    public BlockManaInfuser() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        BotaniaAPI.blacklistBlockFromMagnet(this, Short.MAX_VALUE);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
        ItemStack held = playerIn.getHeldItem(hand);
        InfuserRecipe recipe = InfuserRecipe.find(held);
        if (!held.isEmpty() && recipe != null) {
            if (!worldIn.isRemote) {
                int inputCount = recipe.getInput().getCount();
                ItemStack target = held.copy();
                target.setCount(inputCount);
                if (!playerIn.capabilities.isCreativeMode) {
                    held.shrink(inputCount);
                }
                EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5D,
                        pos.getY() + 0.5D, pos.getZ() + 0.5D, target);
                item.setPickupDelay(40);
                item.motionX = 0.0D;
                item.motionY = 0.0D;
                item.motionZ = 0.0D;
                worldIn.spawnEntity(item);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileManaInfuser)) {
            return 0;
        }
        double progress = ((TileManaInfuser) tile).getProgress();
        if (progress <= 0.0D) {
            return 0;
        }
        return Math.max(1, Math.min(15, 1 + (int) Math.round(progress * 14.0D)));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
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
