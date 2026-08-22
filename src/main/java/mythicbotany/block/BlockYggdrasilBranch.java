package mythicbotany.block;

import mythicbotany.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** A Yggdrasil branch that fills an empty Gjallar Horn. */
public class BlockYggdrasilBranch extends Block {
    private static final AxisAlignedBB BRANCH_BOX = new AxisAlignedBB(
            5.0D / 16.0D, 0.0D, 8.0D / 16.0D,
            11.0D / 16.0D, 12.0D / 16.0D, 14.0D / 16.0D);

    public BlockYggdrasilBranch() {
        super(Material.WOOD);
        setHardness(4.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);
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
        return BRANCH_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BRANCH_BOX;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.isEmpty() || held.getItem() != ModItems.gjallarHornEmpty) {
            return false;
        }
        if (!worldIn.isRemote) {
            ItemStack fullHorn = new ItemStack(ModItems.gjallarHornFull);
            if (!playerIn.capabilities.isCreativeMode) {
                held.shrink(1);
            }
            if (!playerIn.addItemStackToInventory(fullHorn)) {
                playerIn.dropItem(fullHorn, false);
            }
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_STEP, SoundCategory.BLOCKS,
                    1.0F, 0.7F);
        }
        return true;
    }
}
