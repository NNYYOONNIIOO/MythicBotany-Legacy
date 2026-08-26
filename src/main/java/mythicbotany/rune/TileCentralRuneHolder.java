package mythicbotany.rune;

import mythicbotany.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ICreativeManaProvider;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.core.handler.ModSounds;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TileCentralRuneHolder extends TileEntity implements ITickable {
    private ItemStack center = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private RuneRitualRecipe activeRecipe;
    private final List<ItemStack> consumedInputs = new ArrayList<>();
    private int progress;
    private int rotation;
    /** 1 is the normal world spacing; 2 also accepts the compact one-block-gap-free layout. */
    private int runeCoordinateScale = 1;
    private String lastStatusKey = "message.mythicbotany.waiting_matching_runes";

    private static final class InputSelection {
        private final Map<EntityItem, Integer> amounts = new LinkedHashMap<>();
    }

    public boolean insertCenter(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !center.isEmpty() || !output.isEmpty() || activeRecipe != null) {
            return false;
        }
        center = stack.copy();
        center.setCount(1);
        lastStatusKey = "message.mythicbotany.waiting_matching_runes";
        sync();
        return true;
    }

    public ItemStack takeOutput() {
        ItemStack result = output.copy();
        output = ItemStack.EMPTY;
        lastStatusKey = "message.mythicbotany.insert_ritual_focus";
        sync();
        return result;
    }

    public ItemStack takeStoredItem() {
        if (activeRecipe != null) {
            cancelActive(true);
        }
        ItemStack result = !output.isEmpty() ? output.copy() : center.copy();
        output = ItemStack.EMPTY;
        center = ItemStack.EMPTY;
        activeRecipe = null;
        progress = 0;
        rotation = 0;
        runeCoordinateScale = 1;
        sync();
        return result;
    }

    public ItemStack getDisplayStack() {
        return !center.isEmpty() ? center.copy() : output.copy();
    }

    /** Draw the same radial wand HUD used by Botania's Rune Altar while a ritual runs. */
    @SideOnly(Side.CLIENT)
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        if (activeRecipe == null) {
            return;
        }

        int centerX = res.getScaledWidth() / 2;
        int centerY = res.getScaledHeight() / 2;
        int radius = 34;
        List<RuneRitualRecipe.RunePosition> runes = activeRecipe.getRunes();
        float progressFraction = Math.min(1.0F, Math.max(0.0F,
                (float) progress / (float) activeRecipe.getTicks()));

        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

        if (!center.isEmpty()) {
            mc.getRenderItem().renderItemIntoGUI(center, centerX - 8, centerY - 8);
        }

        if (!runes.isEmpty()) {
            float angle = -90.0F;
            float anglePerRune = 360.0F / runes.size();
            for (RuneRitualRecipe.RunePosition rune : runes) {
                double x = centerX + Math.cos(angle * Math.PI / 180.0D) * radius - 8.0D;
                double y = centerY + Math.sin(angle * Math.PI / 180.0D) * radius - 8.0D;
                mc.getRenderItem().renderItemIntoGUI(rune.getRune(), (int) x, (int) y);
                angle += anglePerRune;
            }
        }

        mc.renderEngine.bindTexture(HUDHandler.manaBar);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.drawTexturedModalRect(centerX + radius + 9, centerY - 8, 0,
                progressFraction >= 1.0F ? 0 : 22, 8, 22, 15);
        RenderHelper.renderProgressPie(centerX + radius + 32, centerY - 8,
                progressFraction, activeRecipe.getOutput());

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public ITextComponent getStatusText() {
        if (activeRecipe != null) {
            return new TextComponentTranslation(
                    "message.mythicbotany.ritual_progress", progress, activeRecipe.getTicks());
        }
        if (!output.isEmpty()) {
            return new TextComponentTranslation("message.mythicbotany.ritual_complete");
        }
        if (center.isEmpty()) {
            return new TextComponentTranslation("message.mythicbotany.insert_ritual_focus");
        }
        return new TextComponentTranslation(lastStatusKey);
    }

    @Override
    public void update() {
        if (world == null || world.isRemote || activeRecipe == null) {
            return;
        }
        if (!patternMatches(activeRecipe, rotation, runeCoordinateScale)) {
            cancelActive(true);
            lastStatusKey = "message.mythicbotany.ritual_wrong_shape";
            sync();
            return;
        }
        progress++;
        if (progress >= activeRecipe.getTicks()) {
            finishRecipe();
        }
        markDirty();
    }

    /** Starts a matching ritual and consumes its mana and dropped ingredients from the player/world. */
    public boolean tryStartRitual(EntityPlayer player) {
        if (world == null || world.isRemote || player == null) {
            return false;
        }
        if (activeRecipe != null) {
            lastStatusKey = "message.mythicbotany.ritual_running";
            return false;
        }
        if (center.isEmpty() || !output.isEmpty()) {
            lastStatusKey = center.isEmpty() ? "message.mythicbotany.insert_ritual_focus"
                    : "message.mythicbotany.ritual_complete";
            return false;
        }

        boolean foundCenterRecipe = false;
        for (RuneRitualRecipe recipe : RuneRitualRegistry.getRecipes()) {
            if (!recipe.matchesCenter(center)) {
                continue;
            }
            foundCenterRecipe = true;
            int candidateTransform = -1;
            int candidateScale = -1;
            for (int candidate = 0; candidate < 8; candidate++) {
                if (patternMatches(recipe, candidate, 1)) {
                    candidateTransform = candidate;
                    candidateScale = 1;
                    break;
                }
            }
            if (candidateTransform < 0) {
                for (int candidate = 0; candidate < 8; candidate++) {
                    if (patternMatches(recipe, candidate, 2)) {
                        candidateTransform = candidate;
                        candidateScale = 2;
                        break;
                    }
                }
            }
            if (candidateTransform < 0) {
                lastStatusKey = "message.mythicbotany.ritual_wrong_shape";
                continue;
            }

            InputSelection selection = findInputs(recipe);
            if (selection == null || !hasSpecialInputs(recipe.getSpecialInputs())) {
                lastStatusKey = "message.mythicbotany.ritual_wrong_items";
                return false;
            }
            if (!consumePlayerMana(player, recipe.getMana())) {
                lastStatusKey = "message.mythicbotany.ritual_less_mana";
                return false;
            }

            consumeInputs(selection);
            consumeSpecialInputs(recipe.getSpecialInputs());
            activeRecipe = recipe;
            rotation = candidateTransform;
            runeCoordinateScale = candidateScale;
            progress = 0;
            lastStatusKey = "message.mythicbotany.ritual_running";
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.playSound(null, pos, ModSounds.runeAltarStart, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        if (!foundCenterRecipe) {
            lastStatusKey = "message.mythicbotany.ritual_wrong_items";
        }
        return false;
    }

    private InputSelection findInputs(RuneRitualRecipe recipe) {
        InputSelection selection = new InputSelection();
        if (recipe.getInputs().isEmpty()) {
            return selection;
        }
        AxisAlignedBB bounds = new AxisAlignedBB(
                pos.getX() - 2.0D, pos.getY() - 2.0D, pos.getZ() - 2.0D,
                pos.getX() + 3.0D, pos.getY() + 3.0D, pos.getZ() + 3.0D);
        List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, bounds);
        Map<EntityItem, Integer> available = new LinkedHashMap<>();
        for (EntityItem entity : entities) {
            if (!entity.getItem().isEmpty()) {
                available.put(entity, entity.getItem().getCount());
            }
        }
        for (RuneRitualRecipe.InputRequirement requirement : recipe.getInputs()) {
            int needed = requirement.getRequiredCount();
            for (Map.Entry<EntityItem, Integer> entry : available.entrySet()) {
                if (needed <= 0) {
                    break;
                }
                EntityItem entity = entry.getKey();
                if (entry.getValue() <= 0 || !requirement.matches(entity.getItem())) {
                    continue;
                }
                int take = Math.min(needed, entry.getValue());
                entry.setValue(entry.getValue() - take);
                selection.amounts.put(entity,
                        selection.amounts.containsKey(entity) ? selection.amounts.get(entity) + take : take);
                needed -= take;
            }
            if (needed > 0) {
                return null;
            }
        }
        return selection;
    }

    private void consumeInputs(InputSelection selection) {
        consumedInputs.clear();
        for (Map.Entry<EntityItem, Integer> entry : selection.amounts.entrySet()) {
            EntityItem entity = entry.getKey();
            int amount = entry.getValue();
            ItemStack stack = entity.getItem().copy();
            int take = Math.min(amount, stack.getCount());
            ItemStack consumed = stack.copy();
            consumed.setCount(take);
            consumedInputs.add(consumed);
            stack.shrink(take);
            entity.setItem(stack);
            if (stack.isEmpty()) {
                entity.setDead();
            }
        }
    }

    private boolean hasSpecialInputs(List<String> specialInputs) {
        return findSpecialInputs(specialInputs).size() == (specialInputs == null ? 0 : specialInputs.size());
    }

    private List<Entity> findSpecialInputs(List<String> specialInputs) {
        List<Entity> matches = new ArrayList<>();
        if (specialInputs == null || specialInputs.isEmpty()) {
            return matches;
        }
        AxisAlignedBB bounds = new AxisAlignedBB(
                pos.getX() - 2.0D, pos.getY() - 2.0D, pos.getZ() - 2.0D,
                pos.getX() + 3.0D, pos.getY() + 3.0D, pos.getZ() + 3.0D);
        Set<Entity> used = new HashSet<>();
        for (String specialInput : specialInputs) {
            Entity match = null;
            for (Entity entity : world.getEntitiesWithinAABB(Entity.class, bounds)) {
                String id = EntityList.getEntityString(entity);
                if (!used.contains(entity) && matchesEntityId(specialInput, id)) {
                    match = entity;
                    break;
                }
            }
            if (match == null) {
                return new ArrayList<>();
            }
            used.add(match);
            matches.add(match);
        }
        return matches;
    }

    private Entity findSpecialInput(String specialInput) {
        if (specialInput == null) {
            return null;
        }
        AxisAlignedBB bounds = new AxisAlignedBB(
                pos.getX() - 2.0D, pos.getY() - 2.0D, pos.getZ() - 2.0D,
                pos.getX() + 3.0D, pos.getY() + 3.0D, pos.getZ() + 3.0D);
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, bounds)) {
            String id = EntityList.getEntityString(entity);
            if (matchesEntityId(specialInput, id)) {
                return entity;
            }
        }
        return null;
    }

    private boolean matchesEntityId(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.equals(actual)) {
            return true;
        }
        int separator = expected.indexOf(':');
        String path = separator < 0 ? expected : expected.substring(separator + 1);
        String normalizedPath = path.replace("_", "").toLowerCase(java.util.Locale.ROOT);
        String normalizedActual = actual.replace("_", "").toLowerCase(java.util.Locale.ROOT);
        return normalizedPath.equals(normalizedActual);
    }

    private void consumeSpecialInputs(List<String> specialInputs) {
        for (Entity entity : findSpecialInputs(specialInputs)) {
            entity.setDead();
        }
    }

    private boolean isCreativeMana(ItemStack stack) {
        return stack.getItem() instanceof ICreativeManaProvider
                && ((ICreativeManaProvider) stack.getItem()).isCreative(stack);
    }

    private boolean hasCreativeMana(EntityPlayer player) {
        for (ItemStack source : ManaItemHandler.getManaItems(player)) {
            if (isCreativeMana(source)) {
                return true;
            }
        }
        for (ItemStack source : ManaItemHandler.getManaBaubles(player).values()) {
            if (isCreativeMana(source)) {
                return true;
            }
        }
        return false;
    }

    private boolean consumePlayerMana(EntityPlayer player, int amount) {
        if (amount <= 0 || player.capabilities.isCreativeMode || hasCreativeMana(player)) {
            return true;
        }
        ItemStack requestor = new ItemStack(Blocks.COBBLESTONE);
        long available = 0L;
        List<ItemStack> inventory = ManaItemHandler.getManaItems(player);
        for (ItemStack source : inventory) {
            if (canExport(source, requestor)) {
                available += ((IManaItem) source.getItem()).getMana(source);
            }
        }
        Map<Integer, ItemStack> baubles = ManaItemHandler.getManaBaubles(player);
        for (ItemStack source : baubles.values()) {
            if (canExport(source, requestor)) {
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
            if (canExport(source, requestor)) {
                IManaItem manaItem = (IManaItem) source.getItem();
                int taken = Math.min(remaining, manaItem.getMana(source));
                manaItem.addMana(source, -taken);
                remaining -= taken;
            }
        }
        for (Map.Entry<Integer, ItemStack> entry : baubles.entrySet()) {
            if (remaining <= 0) {
                break;
            }
            ItemStack source = entry.getValue();
            if (canExport(source, requestor)) {
                IManaItem manaItem = (IManaItem) source.getItem();
                int taken = Math.min(remaining, manaItem.getMana(source));
                manaItem.addMana(source, -taken);
                remaining -= taken;
                BotaniaAPI.internalHandler.sendBaubleUpdatePacket(player, entry.getKey());
            }
        }
        return remaining == 0;
    }

    private boolean canExport(ItemStack source, ItemStack requestor) {
        return source != null && !source.isEmpty()
                && source.getItem() instanceof IManaItem
                && ((IManaItem) source.getItem()).canExportManaToItem(source, requestor)
                && ((IManaItem) source.getItem()).getMana(source) > 0;
    }

    private boolean patternMatches(RuneRitualRecipe recipe, int transform, int coordinateScale) {
        for (RuneRitualRecipe.RunePosition rune : recipe.getRunes()) {
            BlockPos runePos = getRunePos(rune, transform, coordinateScale);
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

    private BlockPos getRunePos(RuneRitualRecipe.RunePosition rune, int transform, int coordinateScale) {
        int x = rune.getX(transform);
        int z = rune.getZ(transform);
        if (coordinateScale > 1) {
            x /= coordinateScale;
            z /= coordinateScale;
        }
        return pos.add(x, 0, z);
    }

    private void finishRecipe() {
        RuneRitualRecipe recipe = activeRecipe;
        center = ItemStack.EMPTY;
        List<ItemStack> results = recipe.getOutputs();
        output = results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
        for (int i = 1; i < results.size(); i++) {
            drop(results.get(i));
        }
        for (RuneRitualRecipe.RunePosition rune : recipe.getRunes()) {
            BlockPos runePos = getRunePos(rune, rotation, runeCoordinateScale);
            TileEntity tile = world.getTileEntity(runePos);
            if (tile instanceof TileRuneHolder) {
                ItemStack runeStack = ((TileRuneHolder) tile).takeRune();
                if (!runeStack.isEmpty() && !rune.isConsumed()) {
                    drop(runeStack);
                }
            }
        }
        activeRecipe = null;
        progress = 0;
        rotation = 0;
        runeCoordinateScale = 1;
        consumedInputs.clear();
        lastStatusKey = "message.mythicbotany.ritual_complete";
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    private void cancelActive(boolean restoreInputs) {
        if (restoreInputs) {
            for (ItemStack stack : consumedInputs) {
                drop(stack);
            }
        }
        consumedInputs.clear();
        activeRecipe = null;
        progress = 0;
        rotation = 0;
        runeCoordinateScale = 1;
        markDirty();
        sync();
    }

    public void dropContents() {
        if (world == null || world.isRemote) {
            return;
        }
        if (activeRecipe != null) {
            cancelActive(true);
        }
        drop(center);
        drop(output);
        center = ItemStack.EMPTY;
        output = ItemStack.EMPTY;
        consumedInputs.clear();
        activeRecipe = null;
        progress = 0;
        rotation = 0;
        runeCoordinateScale = 1;
        sync();
    }

    private void drop(ItemStack stack) {
        if (!stack.isEmpty()) {
            EntityItem entity = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D,
                    pos.getZ() + 0.5D, stack.copy());
            entity.setPickupDelay(20);
            world.spawnEntity(entity);
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
        NBTTagList consumed = new NBTTagList();
        for (ItemStack stack : consumedInputs) {
            consumed.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("ConsumedInputs", consumed);
        compound.setInteger("Progress", progress);
        compound.setInteger("Rotation", rotation);
        compound.setInteger("RuneCoordinateScale", runeCoordinateScale);
        compound.setInteger("Recipe", RuneRitualRegistry.indexOf(activeRecipe));
        compound.setString("Status", lastStatusKey);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        center = compound.hasKey("Center") ? new ItemStack(compound.getCompoundTag("Center")) : ItemStack.EMPTY;
        output = compound.hasKey("Output") ? new ItemStack(compound.getCompoundTag("Output")) : ItemStack.EMPTY;
        consumedInputs.clear();
        NBTTagList consumed = compound.getTagList("ConsumedInputs", 10);
        for (int i = 0; i < consumed.tagCount(); i++) {
            consumedInputs.add(new ItemStack(consumed.getCompoundTagAt(i)));
        }
        progress = Math.max(0, compound.getInteger("Progress"));
        rotation = compound.getInteger("Rotation") & 7;
        runeCoordinateScale = compound.hasKey("RuneCoordinateScale")
                ? Math.max(1, Math.min(2, compound.getInteger("RuneCoordinateScale"))) : 1;
        int recipeIndex = compound.hasKey("Recipe") ? compound.getInteger("Recipe") : -1;
        activeRecipe = RuneRitualRegistry.getRecipe(recipeIndex);
        lastStatusKey = compound.hasKey("Status") ? compound.getString("Status")
                : activeRecipe == null && output.isEmpty()
                ? "message.mythicbotany.waiting_matching_runes" : "message.mythicbotany.ritual_running";
    }
}
