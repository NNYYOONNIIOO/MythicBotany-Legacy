package mythicbotany.pylon;

import java.util.List;

import mythicbotany.tile.ManaTileEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Alfsteel pylon for the Botania 1.12 mana API.
 *
 * Botania 1.12 exposes mana receivers but not the later spark attachment
 * interfaces, so this tile uses the stable receiver contract and forwards
 * stored mana to adjacent MythicBotany mana tiles.
 */
public class TileAlfsteelPylon extends ManaTileEntity {
    private static final int ALFSTEEL_TOOL_MANA_PER_POINT = 100;
    private static final int ALFSTEEL_ARMOR_MANA_PER_POINT = 70;
    private static final int MENDING_MANA_PER_POINT = 200;
    private static final int MAX_PYLON_MANA = 1000;

    @Override
    public int getMaxMana() {
        return MAX_PYLON_MANA;
    }

    @Override
    public void recieveMana(int amount) {
        if (amount <= 0 || isFull()) {
            return;
        }
        int accepted = Math.min(amount, getMaxMana() - mana);
        if (accepted > 0) {
            mana += accepted;
            markDirty();
        }
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return mana < getMaxMana();
    }

    /** Kept as a block-break hook for parity with later pylon implementations. */
    public void detachSpark() {
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        repairTopItem();
    }

    /** Repairs alfsteel equipment and Mending equipment dropped on the pylon. */
    private void repairTopItem() {
        if (mana <= 0) {
            return;
        }
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                pos.getX() + 1.0D, pos.getY() + 2.0D, pos.getZ() + 1.0D);
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        boolean repaired = false;
        for (EntityItem entity : items) {
            ItemStack stack = entity.getItem();
            int manaCost = getRepairManaPerPoint(stack);
            if (stack.getCount() != 1 || manaCost <= 0 || mana < manaCost) {
                continue;
            }
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
            entity.setItem(stack);
            mana -= manaCost;
            repaired = true;
        }
        if (repaired) {
            markDirty();
        }
    }

    public static int getRepairManaPerPoint(ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().isDamageable() || !stack.isItemDamaged()) {
            return 0;
        }
        if (isAlfsteel(stack)) {
            return stack.getItem() instanceof ItemArmor
                    ? ALFSTEEL_ARMOR_MANA_PER_POINT : ALFSTEEL_TOOL_MANA_PER_POINT;
        }
        return isMendingRepairable(stack) ? MENDING_MANA_PER_POINT : 0;
    }

    private static boolean isMendingRepairable(ItemStack stack) {
        return stack.getItem().isDamageable()
                && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0;
    }

    private static boolean isAlfsteel(ItemStack stack) {
        ResourceLocation id = stack.getItem().getRegistryName();
        return id != null && "mythicbotany".equals(id.getNamespace())
                && id.getPath().startsWith("alfsteel_");
    }
}
