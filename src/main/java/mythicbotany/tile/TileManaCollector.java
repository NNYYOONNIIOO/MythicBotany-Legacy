package mythicbotany.tile;

import com.google.common.base.Predicates;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.ManaNetworkEvent;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;

import java.util.List;

/** Mana collector compatible with Botania flowers and Spark networks. */
public class TileManaCollector extends ManaTileEntity implements IManaCollector, ISparkAttachable {
    private boolean networkRegistered;

    @Override
    public void onLoad() {
        super.onLoad();
        registerToManaNetwork();
    }

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        if (world.isRemote) {
            onClientDisplayTick();
            return;
        }
        registerToManaNetwork();

        ISparkEntity spark = getAttachedSpark();
        if (spark == null) {
            return;
        }
        List<ISparkEntity> sparks = SparkHelper.getSparksAround(world,
                pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
        for (ISparkEntity other : sparks) {
            if (other != spark && other.getAttachedTile() != null
                    && other.getAttachedTile() != this) {
                spark.registerTransfer(other);
            }
        }
    }

    @Override
    public void recieveMana(int amount) {
        int oldMana = mana;
        super.recieveMana(amount);
        if (oldMana != mana && world != null && !world.isRemote) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, -999, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void invalidate() {
        removeFromManaNetwork();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        removeFromManaNetwork();
        super.onChunkUnload();
    }

    private void removeFromManaNetwork() {
        if (networkRegistered) {
            ManaNetworkEvent.removeCollector(this);
            networkRegistered = false;
        }
    }

    private void registerToManaNetwork() {
        if (world != null && !world.isRemote && !isInvalid() && !networkRegistered) {
            ManaNetworkEvent.addCollector(this);
            networkRegistered = true;
        }
    }

    @Override
    public void onClientDisplayTick() {
        // No client-only collector animation is required.
    }

    @Override
    public float getManaYieldMultiplier(IManaBurst burst) {
        return 1.0F;
    }

    @Override
    public int getMaxMana() {
        return 10000;
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {
        // Spark attachment is discovered from the entity above this tile.
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        if (world == null) {
            return null;
        }
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class,
                new AxisAlignedBB(pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                        pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D),
                Predicates.instanceOf(ISparkEntity.class));
        return sparks.size() == 1 ? (ISparkEntity) sparks.get(0) : null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return isFull();
    }

    @Override
    public int getAvailableSpaceForMana() {
        return Math.max(0, getMaxMana() - getCurrentMana());
    }
}
