package mythicbotany.flower;

import mythicbotany.registry.ModItems;
import mythicbotany.rune.TileCentralRuneHolder;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.api.item.IPetalApothecary;

import java.util.List;

/** 1.12.2 Botania specialflower subtiles used by MythicBotany. */
public final class MythicFlowerSubTiles {
    private MythicFlowerSubTiles() {
    }

    public static class Exoblaze extends SubTileFunctional {
        public static final int MANA_PER_BREW = 50;
        private static final int RANGE = 3;

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote || ticksExisted % 10 != 0) {
                return;
            }
            outer:
            for (int x = -RANGE; x <= RANGE; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -RANGE; z <= RANGE; z++) {
                        if (mana < MANA_PER_BREW) {
                            break outer;
                        }
                        TileEntity tile = getWorld().getTileEntity(getPos().add(x, y, z));
                        if (tile instanceof TileEntityBrewingStand) {
                            TileEntityBrewingStand stand = (TileEntityBrewingStand) tile;
                            if (stand.getField(1) < 20) {
                                stand.setField(1, stand.getField(1) + 1);
                                stand.markDirty();
                                mana -= MANA_PER_BREW;
                                sync();
                            }
                        }
                    }
                }
            }
        }

        @Override public int getMaxMana() { return 300; }
        @Override public int getColor() { return 0xFFFF22; }
    }

    public static class WitherAconite extends SubTileGenerating {
        public static final int DEFAULT_MANA_PER_STAR = 1200000;
        private static final int MAX_MANA = DEFAULT_MANA_PER_STAR / 500;
        private static final int MAX_TRANSFER = DEFAULT_MANA_PER_STAR / 2000;

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote) {
                return;
            }
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class,
                    new AxisAlignedBB(getPos()).grow(1.0D));
            if (items.size() != 1) {
                return;
            }
            EntityItem entity = items.get(0);
            ItemStack stack = entity.getItem();
            if (stack.getCount() != 1) {
                return;
            }
            if (stack.getItem() == Items.NETHER_STAR) {
                stack = new ItemStack(ModItems.fadedNetherStar);
                entity.setItem(stack);
                entity.setNoDespawn();
                sync();
                return;
            }
            if (stack.getItem() != ModItems.fadedNetherStar) {
                return;
            }
            int remaining = Math.max(0, stack.getMaxDamage() - stack.getItemDamage());
            int transfer = Math.min(MAX_TRANSFER, Math.min(MAX_MANA - mana, remaining));
            if (transfer <= 0) {
                return;
            }
            stack.setItemDamage(stack.getItemDamage() + transfer);
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                stack = ItemStack.EMPTY;
            }
            mana = Math.min(MAX_MANA, mana + transfer);
            entity.setItem(stack);
            entity.setNoDespawn();
            sync();
        }

        @Override public int getMaxMana() { return MAX_MANA; }
        @Override public int getColor() { return 0x333333; }
    }

    public static class Aquapanthus extends SubTileFunctional {
        public static final int MANA_PER_TICK = 2;
        public static final int TICKS_TO_FILL = 20;
        private BlockPos currentlyFilling;
        private int fillingSince;
        private int tickToNextCheck;

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote) {
                return;
            }
            if (currentlyFilling != null) {
                TileEntity tile = getWorld().getTileEntity(currentlyFilling);
                if (mana < MANA_PER_TICK || !canFill(getWorld().getBlockState(currentlyFilling), tile)) {
                    currentlyFilling = null;
                    fillingSince = 0;
                    sync();
                    return;
                }
                mana -= MANA_PER_TICK;
                fillingSince++;
                if (fillingSince >= TICKS_TO_FILL) {
                    fill(currentlyFilling, tile);
                    currentlyFilling = null;
                    fillingSince = 0;
                }
                sync();
                return;
            }
            if (tickToNextCheck > 0) {
                tickToNextCheck--;
                return;
            }
            tickToNextCheck = 5;
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos candidate = getPos().add(x, 0, z);
                    TileEntity tile = getWorld().getTileEntity(candidate);
                    if (canFill(getWorld().getBlockState(candidate), tile)) {
                        currentlyFilling = candidate;
                        fillingSince = 0;
                        sync();
                        return;
                    }
                }
            }
        }

        private boolean canFill(IBlockState state, TileEntity tile) {
            if (state.getBlock() == Blocks.CAULDRON) {
                return state.getValue(BlockCauldron.LEVEL) < 3;
            }
            if (isThaumcraftCrucible(tile)) {
                return fillThaumcraftWater(tile, false) > 0;
            }
            return tile instanceof IPetalApothecary && ((IPetalApothecary) tile).hasWater() == false;
        }

        private void fill(BlockPos pos, TileEntity tile) {
            IBlockState state = getWorld().getBlockState(pos);
            if (state.getBlock() == Blocks.CAULDRON) {
                int level = state.getValue(BlockCauldron.LEVEL);
                if (level < 3) {
                    getWorld().setBlockState(pos, state.withProperty(BlockCauldron.LEVEL, level + 1), 3);
                }
            } else if (isThaumcraftCrucible(tile)) {
                fillThaumcraftWater(tile, true);
            } else if (tile instanceof IPetalApothecary) {
                ((IPetalApothecary) tile).setWater(true);
                tile.markDirty();
            }
        }

        private boolean isThaumcraftCrucible(TileEntity tile) {
            return tile != null && "thaumcraft.common.tiles.crafting.TileCrucible".equals(
                    tile.getClass().getName());
        }

        private int fillThaumcraftWater(TileEntity tile, boolean doFill) {
            if (!isThaumcraftCrucible(tile) || FluidRegistry.WATER == null) {
                return 0;
            }
            FluidStack water = new FluidStack(FluidRegistry.WATER, 1000);
            try {
                if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)) {
                    IFluidHandler handler = tile.getCapability(
                            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
                    if (handler != null) {
                        return handler.fill(water, doFill);
                    }
                }
            } catch (Exception ignored) {
                // Thaumcraft revisions expose the tank differently; use the public fill method below.
            }
            try {
                java.lang.reflect.Method method = tile.getClass().getMethod(
                        "fill", FluidStack.class, boolean.class);
                Object result = method.invoke(tile, water, doFill);
                return result instanceof Number ? ((Number) result).intValue() : 0;
            } catch (Exception ignored) {
                return 0;
            }
        }


        @Override
        public void writeToPacketNBT(NBTTagCompound tag) {
            super.writeToPacketNBT(tag);
            tag.setInteger("fillingSince", fillingSince);
            if (currentlyFilling == null) {
                tag.setInteger("fillingY", -1);
            } else {
                tag.setInteger("fillingX", currentlyFilling.getX());
                tag.setInteger("fillingY", currentlyFilling.getY());
                tag.setInteger("fillingZ", currentlyFilling.getZ());
            }
        }

        @Override
        public void readFromPacketNBT(NBTTagCompound tag) {
            super.readFromPacketNBT(tag);
            fillingSince = tag.getInteger("fillingSince");
            currentlyFilling = tag.getInteger("fillingY") < 0 ? null
                    : new BlockPos(tag.getInteger("fillingX"), tag.getInteger("fillingY"), tag.getInteger("fillingZ"));
        }

        @Override public int getMaxMana() { return 300; }
        @Override public int getColor() { return 0x4444FF; }
    }

    /** Piglins/Hoglins have no 1.12.2 equivalent time-in-overworld state. */
    public static class Hellebore extends SubTileFunctional {
        @Override public int getMaxMana() { return 300; }
        @Override public int getColor() { return 0xCD3EBB; }
    }

    public static class Raindeletia extends SubTileGenerating {
        @Override
        public boolean canGeneratePassively() {
            return getGenerationRate() > 0.0F;
        }

        @Override
        public int getDelayBetweenPassiveGeneration() {
            float rate = getGenerationRate();
            return rate <= 0.0F ? 1 : Math.max(1, Math.round(1.0F / rate));
        }

        @Override
        public int getValueForPassiveGeneration() {
            float rate = getGenerationRate();
            if (rate <= 0.0F) {
                return 0;
            }
            return Math.max(1, Math.round(rate * getDelayBetweenPassiveGeneration()));
        }

        private float getGenerationRate() {
            float multiplier = 0.0F;
            if (getWorld().isRainingAt(getPos())) {
                multiplier = getWorld().isThundering() ? 3.0F : 0.09F;
            }
            net.minecraft.block.Block soil = getWorld().getBlockState(getPos().down()).getBlock();
            if (soil == vazkii.botania.common.block.ModBlocks.enchantedSoil) {
                multiplier *= 5.0F;
            } else if (soil == vazkii.botania.common.block.ModBlocks.altGrass) {
                multiplier *= 2.0F;
            }
            return multiplier * 5.0F;
        }

        @Override public int getMaxMana() { return 300; }
        @Override public int getColor() { return 0x1E1CD8; }
    }

    public static class Feysythia extends SubTileGenerating {
        private static final String[][] LEVEL_ITEMS = new String[][] {
            { "feywild:fey_dust" },
            { "feywild:lesser_fey_gem" },
            { "feywild:greater_fey_gem" },
            { "feywild:shiny_fey_gem" },
            { "feywild:brilliant_fey_gem" }
        };

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote || mana > 0) {
                return;
            }
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class,
                    new AxisAlignedBB(getPos()).grow(1.0D));
            for (EntityItem entity : items) {
                ItemStack stack = entity.getItem();
                int level = getLevel(stack);
                if (level <= 0) {
                    continue;
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    entity.setDead();
                } else {
                    entity.setItem(stack);
                }
                mana = Math.min(getMaxMana(), (int) Math.ceil(Math.sqrt(level * 1.3D) * 263D));
                sync();
                return;
            }
        }

        private static int getLevel(ItemStack stack) {
            for (int level = LEVEL_ITEMS.length - 1; level >= 0; level--) {
                for (String value : LEVEL_ITEMS[level]) {
                    if (matches(stack, value)) {
                        return level + 1;
                    }
                }
            }
            return 0;
        }

        private static boolean matches(ItemStack stack, String value) {
            if (value == null || value.isEmpty() || value.startsWith("forge:")) {
                return false;
            }
            String[] parts = value.split("\\|", 2);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0]));
            if (item == null || stack.getItem() != item) {
                return false;
            }
            if (parts.length < 2) {
                return true;
            }
            try {
                int metadata = Integer.parseInt(parts[1]);
                return metadata < 0 || stack.getMetadata() == metadata;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        @Override public int getMaxMana() { return 300; }
        @Override public int getColor() { return 0x45FFAC; }
    }

    /** The central holder requests ritual mana from the activating player. */
    public static class Petrunia extends SubTileFunctional {
        @Override public int getMaxMana() { return 300; }
        @Override public int getColor() { return 0xB71A1A; }
    }
}
