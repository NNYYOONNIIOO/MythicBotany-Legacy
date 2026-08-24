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
        // Botania mana bursts call this method directly; the base implementation
        // clamps both positive and negative changes to this tile's 1000-mana cap.
        super.recieveMana(amount);
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
            int damage = stack.getItemDamage();
            if (stack.getCount() != 1 || manaCost <= 0 || damage <= 0) {
                continue;
            }
            int repairPoints = Math.min(damage, mana / manaCost);
            if (repairPoints <= 0) {
                continue;
            }
            stack.setItemDamage(damage - repairPoints);
            entity.setItem(stack);
            mana -= repairPoints * manaCost;
            repaired = true;
            if (mana <= 0) {
                break;
            }
        }
        if (repaired) {
            markDirty();
        }
    }

    public static int getRepairManaPerPoint(ItemStack stack) {
        if (stack.isEmpty() || stack.getCount() != 1 || !stack.getItem().isDamageable()
                || !stack.isItemDamaged()) {
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
