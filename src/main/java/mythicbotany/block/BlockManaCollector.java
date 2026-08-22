package mythicbotany.block;

import mythicbotany.tile.TileManaCollector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockManaCollector extends BlockContainer {
    public BlockManaCollector() {
        super(Material.IRON);
        setHardness(4.0F);
        setResistance(8.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileManaCollector();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileManaCollector) {
                TileManaCollector collector = (TileManaCollector) tileEntity;
                playerIn.sendStatusMessage(new TextComponentString("Mana: " + collector.getCurrentMana()
                        + " / " + collector.getMaxMana()), true);
            }
        }
        return true;
    }
}
