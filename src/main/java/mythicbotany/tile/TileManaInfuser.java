package mythicbotany.tile;

import com.google.common.base.Predicates;
import mythicbotany.recipe.InfuserRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.handler.ModSounds;

import java.util.List;

/**
 * A recipe-scoped mana receiver. It does not keep a general-purpose mana
 * buffer: mana is accepted only while a matching dropped-item recipe exists,
 * and is cleared whenever that recipe is cancelled or completed.
 */
public class TileManaInfuser extends TileEntity implements ITickable, ISparkAttachable {
    private int mana;
    private int manaRequirement;
    private InfuserRecipe activeRecipe;

    @Override
    public void update() {
        if (world == null) {
            return;
        }

        if (world.isRemote) {
            spawnProgressParticles();
            return;
        }

        if (!hasValidPlatform()) {
            clearRecipe();
            return;
        }

        List<EntityItem> items = getItems();
        if (activeRecipe == null) {
            InfuserRecipe recipe = findRecipe(items);
            if (recipe != null) {
                activeRecipe = recipe;
                mana = 0;
                manaRequirement = recipe.getMana();
                syncState();
            }
            return;
        }

        if (!matchesRecipe(items, activeRecipe)) {
            clearRecipe();
            return;
        }

        receiveManaFromSparks();
        if (mana >= manaRequirement) {
            finishRecipe(items.get(0));
        }
    }

    private List<EntityItem> getItems() {
        return world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(pos, pos.add(1, 1, 1)));
    }

    private InfuserRecipe findRecipe(List<EntityItem> items) {
        if (items.size() != 1) {
            return null;
        }
        return InfuserRecipe.find(items.get(0).getItem());
    }

    private boolean matchesRecipe(List<EntityItem> items, InfuserRecipe recipe) {
        return items.size() == 1 && recipe.matches(items.get(0).getItem());
    }

    private void receiveManaFromSparks() {
        if (manaRequirement <= 0) {
            return;
        }
        ISparkEntity spark = getAttachedSpark();
        if (spark == null) {
            return;
        }
        List<ISparkEntity> sparks = SparkHelper.getSparksAround(world,
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        for (ISparkEntity other : sparks) {
            if (other != spark && other.getAttachedTile() instanceof IManaPool) {
                other.registerTransfer(spark);
            }
        }
    }

    private void finishRecipe(EntityItem ingredient) {
        ItemStack remaining = ingredient.getItem().copy();
        remaining.shrink(activeRecipe.getInput().getCount());
        if (remaining.isEmpty()) {
            ingredient.setDead();
        } else {
            ingredient.setItem(remaining);
        }
        ItemStack result = activeRecipe.getOutput();
        EntityItem output = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, result);
        world.spawnEntity(output);
        world.playSound(null, output.posX, output.posY, output.posZ,
                ModSounds.terrasteelCraft, SoundCategory.BLOCKS, 1.0F, 1.0F);
        clearRecipe();
    }

    private void clearRecipe() {
        if (activeRecipe == null && mana == 0 && manaRequirement == 0) {
            return;
        }
        activeRecipe = null;
        mana = 0;
        manaRequirement = 0;
        syncState();
    }

    private boolean hasValidPlatform() {
        BlockPos center = pos.down();
        net.minecraft.block.Block shimmerrock = vazkii.botania.common.block.ModBlocks.shimmerrock;
        return world.getBlockState(center).getBlock() == shimmerrock
                && world.getBlockState(center.north().west()).getBlock() == shimmerrock
                && world.getBlockState(center.north().east()).getBlock() == shimmerrock
                && world.getBlockState(center.south().west()).getBlock() == shimmerrock
                && world.getBlockState(center.south().east()).getBlock() == shimmerrock
                && world.getBlockState(center.north()).getBlock() == net.minecraft.init.Blocks.GOLD_BLOCK
                && world.getBlockState(center.east()).getBlock() == net.minecraft.init.Blocks.GOLD_BLOCK
                && world.getBlockState(center.south()).getBlock() == net.minecraft.init.Blocks.GOLD_BLOCK
                && world.getBlockState(center.west()).getBlock() == net.minecraft.init.Blocks.GOLD_BLOCK;
    }

    private void syncState() {
        markDirty();
        if (world != null && !world.isRemote) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.updateComparatorOutputLevel(pos, state.getBlock());
        }
    }

    @Override
    public boolean isFull() {
        return activeRecipe == null || mana >= manaRequirement;
    }

    @Override
    public void recieveMana(int amount) {
        if (activeRecipe == null || amount == 0 || !matchesRecipe(getItems(), activeRecipe)) {
            return;
        }
        mana = Math.max(0, Math.min(manaRequirement, mana + amount));
        syncState();
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return activeRecipe != null && mana < manaRequirement
                && matchesRecipe(getItems(), activeRecipe);
    }

    @Override
    public int getCurrentMana() {
        return activeRecipe == null ? 0 : mana;
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {
    }

    @Override
    public ISparkEntity getAttachedSpark() {
        if (world == null) {
            return null;
        }
        List<Entity> sparks = world.getEntitiesWithinAABB(Entity.class,
                new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)),
                Predicates.instanceOf(ISparkEntity.class));
        if (sparks.size() == 1) {
            return (ISparkEntity) sparks.get(0);
        }
        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return activeRecipe == null || !matchesRecipe(getItems(), activeRecipe);
    }

    @Override
    public int getAvailableSpaceForMana() {
        return activeRecipe == null ? 0 : Math.max(0, manaRequirement - mana);
    }

    public double getProgress() {
        return activeRecipe == null || manaRequirement <= 0
                ? 0.0D : (double) mana / (double) manaRequirement;
    }

    private void spawnProgressParticles() {
        if (mana <= 0 || manaRequirement <= 0) {
            return;
        }
        int ticks = Math.min(100, Math.max(0, (int) (getProgress() * 100.0D)));
        double tickIncrement = 120.0D;
        double angle = ticks * 5.0D - tickIncrement;
        double radius = Math.sin((ticks - 100) / 10.0D) * 2.0D;
        double vertical = Math.sin(angle * Math.PI / 180.0D * 0.55D);
        float progress = ticks / 100.0F;
        float red = 0.0F;
        float green = progress;
        float blue = 1.0F - progress;
        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + Math.sin(angle * Math.PI / 180.0D) * radius + 0.5D;
            double y = pos.getY() + 0.25D + Math.abs(radius) * 0.7D;
            double z = pos.getZ() + Math.cos(angle * Math.PI / 180.0D) * radius + 0.5D;
            angle += tickIncrement;
            Botania.proxy.wispFX(x, y, z, red, green, blue, 0.85F,
                    (float) vertical * 0.05F, 0.25F, 0.0F);
            Botania.proxy.wispFX(x, y, z, red, green, blue,
                    (float) Math.random() * 0.1F + 0.1F,
                    (float) (Math.random() - 0.5D) * 0.05F,
                    (float) (Math.random() - 0.5D) * 0.05F,
                    (float) (Math.random() - 0.5D) * 0.05F, 0.9F);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Mana", mana);
        compound.setInteger("ManaRequirement", manaRequirement);
        compound.setInteger("Recipe", InfuserRecipe.indexOf(activeRecipe));
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        mana = Math.max(0, compound.getInteger("Mana"));
        manaRequirement = Math.max(0, compound.getInteger("ManaRequirement"));
        int recipeIndex = compound.hasKey("Recipe") ? compound.getInteger("Recipe") : -1;
        activeRecipe = InfuserRecipe.getRecipe(recipeIndex);
        if (activeRecipe != null) {
            manaRequirement = activeRecipe.getMana();
            mana = Math.min(mana, manaRequirement);
        }
        if (activeRecipe == null) {
            mana = 0;
            manaRequirement = 0;
        }
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
