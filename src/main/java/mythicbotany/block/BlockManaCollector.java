package mythicbotany.block;

import mythicbotany.tile.TileManaCollector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;

public class BlockManaCollector extends BlockContainer implements IWandHUD {
    public BlockManaCollector() {
        super(Material.IRON);
        setHardness(4.0F);
        setResistance(8.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileManaCollector();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileManaCollector) {
            TileManaCollector collector = (TileManaCollector) tile;
            HUDHandler.drawSimpleManaHUD(0x4444FF, collector.getCurrentMana(),
                    collector.getMaxMana(), I18n.format("block.mythicbotany.mana_collector"), res);
        }
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
