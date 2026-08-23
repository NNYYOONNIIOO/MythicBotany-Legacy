package mythicbotany.rune;

import mythicbotany.registry.ModBlocks;
import mythicbotany.tile.ManaTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.handler.ModSounds;

import java.util.List;
import java.util.Map;

public class TileCentralRuneHolder extends ManaTileEntity {
    private ItemStack center = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private RuneRitualRecipe activeRecipe;
    private int progress;
    private int rotation;

    public boolean insertCenter(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !center.isEmpty() || !output.isEmpty() || activeRecipe != null) {
            return false;
        }
        center = stack.copy();
        center.setCount(1);
        sync();
        return true;
    }

    public ItemStack takeOutput() {
        ItemStack result = output;
        output = ItemStack.EMPTY;
        sync();
        return result;
    }

    public ItemStack takeStoredItem() {
        ItemStack result = !output.isEmpty() ? output : center;
        output = ItemStack.EMPTY;
        center = ItemStack.EMPTY;
        activeRecipe = null;
        progress = 0;
        rotation = 0;
        sync();
        return result;
    }

    public ItemStack getDisplayStack() {
        return !center.isEmpty() ? center.copy() : output.copy();
    }

    public net.minecraft.util.text.ITextComponent getStatusText() {
        if (activeRecipe != null) {
            return new net.minecraft.util.text.TextComponentTranslation(
                    "message.mythicbotany.ritual_progress", progress, activeRecipe.getTicks());
        }
        if (!output.isEmpty()) {
            return new net.minecraft.util.text.TextComponentTranslation(
                    "message.mythicbotany.ritual_complete");
        }
        if (center.isEmpty()) {
            return new net.minecraft.util.text.TextComponentTranslation(
                    "message.mythicbotany.insert_ritual_focus");
        }
        return new net.minecraft.util.text.TextComponentTranslation(
                "message.mythicbotany.waiting_matching_runes");
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        if (activeRecipe != null) {
            if (!patternMatches(activeRecipe, rotation)) {
                activeRecipe = null;
                progress = 0;
                rotation = 0;
                markDirty();
                return;
            }
            progress++;
            if (progress >= activeRecipe.getTicks()) {
                output = activeRecipe.getOutput();
                activeRecipe = null;
                progress = 0;
                rotation = 0;
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            markDirty();
            return;
        }
        if (center.isEmpty() || !output.isEmpty()) {
            return;
        }
        tryStartRitual(null);
    }

    /** Starts a matching ritual, using the tile mana or the activating player's mana. */
    public boolean tryStartRitual(EntityPlayer player) {
        if (world == null || world.isRemote || center.isEmpty() || !output.isEmpty()
                || activeRecipe != null) {
            return false;
        }
        ItemStack manaTarget = player == null
                ? ItemStack.EMPTY : new ItemStack(Blocks.COBBLESTONE);
        for (RuneRitualRecipe recipe : RuneRitualRegistry.getRecipes()) {
            if (!recipe.matchesCenter(center)) {
                continue;
            }
            int candidateRotation = -1;
            for (int candidate = 0; candidate < 4; candidate++) {
                if (patternMatches(recipe, candidate)) {
                    candidateRotation = candidate;
                    break;
                }
            }
            if (candidateRotation < 0) {
                continue;
            }
            if (player == null) {
                if (mana < recipe.getMana()) {
                    continue;
                }
                mana -= recipe.getMana();
            } else if (!consumePlayerMana(manaTarget, player, recipe.getMana())) {
                continue;
            }
            activeRecipe = recipe;
            rotation = candidateRotation;
            progress = 0;
            center = ItemStack.EMPTY;
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.playSound(null, pos, ModSounds.runeAltarStart, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private boolean canExport(ItemStack source, ItemStack target) {
        return source != null && !source.isEmpty() && source.getItem() instanceof IManaItem
                && ((IManaItem) source.getItem()).getMana(source) > 0
                && ((IManaItem) source.getItem()).canExportManaToItem(source, target);
    }

    private boolean consumePlayerMana(ItemStack target, EntityPlayer player, int amount) {
        if (amount <= 0) {
            return true;
        }
        List<ItemStack> inventory = ManaItemHandler.getManaItems(player);
        Map<Integer, ItemStack> baubles = ManaItemHandler.getManaBaubles(player);
        long available = 0L;
        for (ItemStack source : inventory) {
            if (canExport(source, target)) {
                available += ((IManaItem) source.getItem()).getMana(source);
            }
        }
        for (ItemStack source : baubles.values()) {
            if (canExport(source, target)) {
                available += ((IManaItem) source.getItem()).getMana(source);
            }
        }
        if (available < amount) {
            return false;
        }

        int remaining = amount;
        for (ItemStack source : inventory) {
            if (remaining <= 0) {
                break;
            }
            if (canExport(source, target)) {
                int extracted = Math.min(remaining,
                        ((IManaItem) source.getItem()).getMana(source));
                ((IManaItem) source.getItem()).addMana(source, -extracted);
                remaining -= extracted;
            }
        }
        for (Map.Entry<Integer, ItemStack> entry : baubles.entrySet()) {
            if (remaining <= 0) {
                break;
            }
            ItemStack source = entry.getValue();
            if (canExport(source, target)) {
                int extracted = Math.min(remaining,
                        ((IManaItem) source.getItem()).getMana(source));
                ((IManaItem) source.getItem()).addMana(source, -extracted);
                BotaniaAPI.internalHandler.sendBaubleUpdatePacket(player, entry.getKey());
                remaining -= extracted;
            }
        }
        return remaining == 0;
    }

    private boolean patternMatches(RuneRitualRecipe recipe, int candidateRotation) {
        for (RuneRitualRecipe.RunePosition rune : recipe.getRunes()) {
            net.minecraft.util.math.BlockPos runePos = pos.add(rune.getX(candidateRotation), 0,
                    rune.getZ(candidateRotation));
            if (world.getBlockState(runePos).getBlock() != ModBlocks.runeHolder) {
                return false;
            }
            TileEntity tile = world.getTileEntity(runePos);
            if (!(tile instanceof TileRuneHolder)
                    || !RuneRitualRecipe.matches(rune.getRune(), ((TileRuneHolder) tile).getRune())) {
                return false;
            }
        }
        return true;
    }

    public void dropContents() {
        if (world == null || world.isRemote) {
            return;
        }
        drop(center);
        drop(output);
        center = ItemStack.EMPTY;
        output = ItemStack.EMPTY;
        sync();
    }

    private void drop(ItemStack stack) {
        if (!stack.isEmpty()) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D,
                    pos.getZ() + 0.5D, stack.copy()));
        }
    }

    private void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!center.isEmpty()) {
            compound.setTag("Center", center.writeToNBT(new NBTTagCompound()));
        }
        if (!output.isEmpty()) {
            compound.setTag("Output", output.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("Progress", progress);
        compound.setInteger("Rotation", rotation);
        compound.setInteger("Recipe", RuneRitualRegistry.indexOf(activeRecipe));
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        center = compound.hasKey("Center") ? new ItemStack(compound.getCompoundTag("Center")) : ItemStack.EMPTY;
        output = compound.hasKey("Output") ? new ItemStack(compound.getCompoundTag("Output")) : ItemStack.EMPTY;
        progress = Math.max(0, compound.getInteger("Progress"));
        rotation = compound.getInteger("Rotation") & 3;
        int recipeIndex = compound.hasKey("Recipe") ? compound.getInteger("Recipe") : -1;
        activeRecipe = RuneRitualRegistry.getRecipe(recipeIndex);
    }
}
