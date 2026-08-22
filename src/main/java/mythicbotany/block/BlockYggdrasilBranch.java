package mythicbotany.block;

import mythicbotany.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;

/** A Yggdrasil branch that fills an empty Gjallar Horn. */
public class BlockYggdrasilBranch extends Block {
    public BlockYggdrasilBranch() {
        super(Material.WOOD);
        setHardness(4.0F);
        setResistance(4.0F);
        setSoundType(SoundType.WOOD);
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
