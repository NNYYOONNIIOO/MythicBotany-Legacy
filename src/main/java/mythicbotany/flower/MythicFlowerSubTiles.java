package mythicbotany.flower;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.api.subtile.SubTileGenerating;

import java.util.List;

/**
 * MythicBotany flowers use Botania 1.12's single specialflower block.  Their
 * subtype is selected by the type string stored in the ItemStack NBT.
 */
public final class MythicFlowerSubTiles {
    private MythicFlowerSubTiles() {
    }

    public static class Exoblaze extends SubTileFunctional {
        private static final int MANA_PER_BREW = 50;

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote || ticksExisted % 10 != 0 || mana < MANA_PER_BREW) {
                return;
            }
            for (BlockPos pos : BlockPos.getAllInBoxMutable(getPos().add(-2, -1, -2), getPos().add(2, 1, 2))) {
                TileEntity tile = getWorld().getTileEntity(pos);
                if (tile instanceof TileEntityBrewingStand) {
                    TileEntityBrewingStand stand = (TileEntityBrewingStand) tile;
                    int fuel = stand.getField(1);
                    if (fuel < 20) {
                        stand.setField(1, Math.min(20, fuel + 1));
                        stand.markDirty();
                        mana -= MANA_PER_BREW;
                        sync();
                        return;
                    }
                }
            }
        }

        @Override
        public int getMaxMana() {
            return 1000;
        }

        @Override
        public int getColor() {
            return 0xD77BFF;
        }
    }

    public static class WitherAconite extends SubTileGenerating {
        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote || ticksExisted % 20 != 0) {
                return;
            }
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class,
                    new AxisAlignedBB(getPos()).grow(1.5D));
            for (EntityItem entity : items) {
                ItemStack stack = entity.getItem();
                if (stack.getItem() == Items.NETHER_STAR && !stack.isEmpty()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        entity.setDead();
                    }
                    addMana(5000);
                    sync();
                    return;
                }
            }
        }

        @Override
        public int getMaxMana() {
            return 10000;
        }

        @Override
        public int getColor() {
            return 0xB7D9FF;
        }
    }

    public static class Aquapanthus extends SubTileFunctional {
        private static final int MANA_PER_LEVEL = 25;

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote || ticksExisted % 10 != 0 || mana < MANA_PER_LEVEL) {
                return;
            }
            for (BlockPos pos : BlockPos.getAllInBoxMutable(getPos().add(-2, -1, -2), getPos().add(2, 1, 2))) {
                IBlockState state = getWorld().getBlockState(pos);
                if (state.getBlock() == Blocks.CAULDRON) {
                    int level = state.getValue(BlockCauldron.LEVEL);
                    if (level < 3) {
                        getWorld().setBlockState(pos, state.withProperty(BlockCauldron.LEVEL, level + 1), 3);
                        mana -= MANA_PER_LEVEL;
                        sync();
                        return;
                    }
                }
            }
        }

        @Override
        public int getMaxMana() {
            return 1000;
        }

        @Override
        public int getColor() {
            return 0x59D8FF;
        }
    }

    /** Piglins and Hoglins do not exist in the 1.12.2 entity set. */
    public static class Hellebore extends SubTileFunctional {
        @Override
        public int getMaxMana() {
            return 1000;
        }

        @Override
        public int getColor() {
            return 0xFF6B9E;
        }
    }

    public static class Raindeletia extends SubTileGenerating {
        @Override
        public boolean canGeneratePassively() {
            return getWorld().isRainingAt(getPos().up()) && getWorld().canSeeSky(getPos().up());
        }

        @Override
        public int getDelayBetweenPassiveGeneration() {
            return 10;
        }

        @Override
        public int getValueForPassiveGeneration() {
            return 2;
        }

        @Override
        public int getMaxMana() {
            return 1000;
        }

        @Override
        public int getColor() {
            return 0x6EB8FF;
        }
    }

    public static class Feysythia extends SubTileGenerating {
        @Override
        public void onUpdate() {
            super.onUpdate();
            if (getWorld().isRemote || ticksExisted % 20 != 0) {
                return;
            }
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class,
                    new AxisAlignedBB(getPos()).grow(1.5D));
            for (EntityItem entity : items) {
                ItemStack stack = entity.getItem();
                if (stack.getItem() == Items.ENDER_PEARL && !stack.isEmpty()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        entity.setDead();
                    }
                    addMana(250);
                    sync();
                    return;
                }
            }
        }

        @Override
        public int getMaxMana() {
            return 5000;
        }

        @Override
        public int getColor() {
            return 0xD6A2FF;
        }
    }

    public static class Petrunia extends SubTileFunctional {
        @Override
        public int getMaxMana() {
            return 5000;
        }

        @Override
        public int getColor() {
            return 0xFFB7E8;
        }
    }
}
