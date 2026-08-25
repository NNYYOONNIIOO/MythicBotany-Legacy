package mythicbotany.block;

import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.client.core.handler.HUDHandler;

public class BlockAlfsteelPylon extends BlockContainer implements IWandable, IWandHUD {
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(
            0.125D, 0.0D, 0.125D, 0.875D, 21.0D / 16.0D, 0.875D);

    public BlockAlfsteelPylon() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(10.0F);
        setLightLevel(0.8F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAlfsteelPylon();
    }

    @Override
    public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world,
                                BlockPos pos, EnumFacing side) {
        // The TileEntity handles the bind/select operation. This marker makes
        // the Forest Wand enter Botania's normal binding path for the block.
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileAlfsteelPylon) {
            TileAlfsteelPylon pylon = (TileAlfsteelPylon) tile;
            HUDHandler.drawSimpleManaHUD(0xFFAA00, pylon.getCurrentMana(),
                    pylon.getMaxMana(), getHudName(), res);
        }
    }

    @SideOnly(Side.CLIENT)
    private String getHudName() {
        ItemStack stack = new ItemStack(this);
        String key = stack.getTranslationKey() + ".name";
        String localized = I18n.format(key);
        if (!localized.equals(key)) {
            return localized;
        }

        key = "tile.mythicbotany.alfsteel_pylon.name";
        localized = I18n.format(key);
        if (!localized.equals(key)) {
            return localized;
        }

        return I18n.format("block.mythicbotany.alfsteel_pylon");
    }

    @Override
    public float getEnchantPowerBonus(World world, BlockPos pos) {
        return 15.0F;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || held.getCount() != 1
                || TileAlfsteelPylon.getRepairManaPerPoint(held) <= 0) {
            return false;
        }
        if (!worldIn.isRemote) {
            ItemStack repairStack = held.copy();
            player.setHeldItem(hand, ItemStack.EMPTY);
            EntityItem entity = new EntityItem(worldIn, pos.getX() + 0.5D,
                    pos.getY() + 1.35D, pos.getZ() + 0.5D, repairStack);
            entity.motionX = 0.0D;
            entity.motionY = 0.0D;
            entity.motionZ = 0.0D;
            entity.setPickupDelay(40);
            worldIn.spawnEntity(entity);
        }
        return true;
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
        return BOUNDING_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileAlfsteelPylon) {
            ((TileAlfsteelPylon) tile).detachSpark();
        }
        super.breakBlock(worldIn, pos, state);
    }
}
