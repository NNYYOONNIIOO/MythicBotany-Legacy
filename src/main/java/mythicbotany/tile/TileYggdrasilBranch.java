package mythicbotany.tile;

import mythicbotany.block.BlockYggdrasilBranch;
import mythicbotany.registry.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.core.helper.RenderHelper;

/** Stores and fills a Gjallar horn using mana supplied by a Mana Spreader. */
public class TileYggdrasilBranch extends ManaTileEntity {
    private static final int MAX_MANA = 10000;
    private static final int MANA_PER_TICK = 10;
    private static final int TICKS_TO_FILL = 600;
    private static final int DRIP_INTERVAL_TICKS = 3;
    private static final double RESOURCE_PARTICLE_X = 0.5D;
    private static final double RESOURCE_PARTICLE_Y = 1.2D;
    private static final double RESOURCE_PARTICLE_Z = 0.0D;
    private ItemStack horn = ItemStack.EMPTY;
    private int progress;
    private int dripTicks;

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        if (world.isRemote) {
            updateWaterDrips();
            return;
        }
        if (!horn.isEmpty() && horn.getItem() == ModItems.gjallarHornEmpty
                && horn.getCount() == 1) {
            if (mana >= MANA_PER_TICK) {
                mana -= MANA_PER_TICK;
                progress++;
                if (progress >= TICKS_TO_FILL) {
                    horn = new ItemStack(ModItems.gjallarHornFull);
                    progress = 0;
                }
                sync();
            }
        } else if (progress != 0) {
            progress = 0;
            sync();
        }
    }

    private void updateWaterDrips() {
        if (!isFilling()) {
            dripTicks = 0;
            return;
        }
        if (++dripTicks < DRIP_INTERVAL_TICKS) {
            return;
        }
        dripTicks = 0;

        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockYggdrasilBranch.FACING);
        double localX = RESOURCE_PARTICLE_X - 0.5D;
        double localZ = RESOURCE_PARTICLE_Z - 0.5D;
        double rotatedX;
        double rotatedZ;
        switch (facing) {
            case EAST:
                rotatedX = -localZ;
                rotatedZ = localX;
                break;
            case SOUTH:
                rotatedX = -localX;
                rotatedZ = -localZ;
                break;
            case WEST:
                rotatedX = localZ;
                rotatedZ = -localX;
                break;
            default:
                rotatedX = localX;
                rotatedZ = localZ;
                break;
        }

        world.spawnParticle(EnumParticleTypes.DRIP_WATER,
                pos.getX() + 0.5D + rotatedX,
                pos.getY() + RESOURCE_PARTICLE_Y,
                pos.getZ() + 0.5D + rotatedZ,
                0.0D, -0.02D, 0.0D);
    }

    private boolean isFilling() {
        return !horn.isEmpty()
                && horn.getItem() == ModItems.gjallarHornEmpty
                && horn.getCount() == 1
                && progress < TICKS_TO_FILL
                && mana >= MANA_PER_TICK;
    }

    @Override
    public int getMaxMana() {
        return MAX_MANA;
    }

    public boolean insertHorn(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() != ModItems.gjallarHornEmpty
                || stack.getCount() != 1 || !horn.isEmpty()) {
            return false;
        }
        if (world != null && world.isRemote) {
            return true;
        }
        horn = stack.copy();
        progress = 0;
        sync();
        return true;
    }

    public ItemStack takeHorn() {
        ItemStack result = horn.copy();
        if (world == null || !world.isRemote) {
            horn = ItemStack.EMPTY;
            progress = 0;
            sync();
        }
        return result;
    }

    public ItemStack getHorn() {
        return horn.copy();
    }

    public int getProgress() {
        return progress;
    }

    public int getProgressRequired() {
        return TICKS_TO_FILL;
    }

    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        HUDHandler.drawSimpleManaHUD(0x4444FF, mana, getMaxMana(),
                I18n.format("block.mythicbotany.yggdrasil_branch"), res);

        ItemStack stored = getHorn();
        if (stored.isEmpty()) {
            return;
        }

        int centerX = res.getScaledWidth() / 2;
        int centerY = res.getScaledHeight() / 2 - 32;
        boolean complete = stored.getItem() == ModItems.gjallarHornFull;
        float fraction = complete ? 1.0F
                : Math.min(1.0F, Math.max(0.0F, (float) progress / (float) TICKS_TO_FILL));

        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemIntoGUI(stored, centerX - 8, centerY - 8);

        mc.renderEngine.bindTexture(HUDHandler.manaBar);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.drawTexturedModalRect(centerX + 33, centerY - 8, 0,
                fraction >= 1.0F ? 0 : 22, 8, 22, 15);
        RenderHelper.renderProgressPie(centerX + 56, centerY - 8, fraction,
                new ItemStack(ModItems.gjallarHornFull));

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void dropContents() {
        if (world == null || world.isRemote || horn.isEmpty()) {
            return;
        }
        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, horn.copy()));
        horn = ItemStack.EMPTY;
        progress = 0;
        sync();
    }

    private void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!horn.isEmpty()) {
            compound.setTag("Horn", horn.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("Progress", progress);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        horn = compound.hasKey("Horn")
                ? new ItemStack(compound.getCompoundTag("Horn")) : ItemStack.EMPTY;
        progress = Math.max(0, compound.getInteger("Progress"));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }
}
