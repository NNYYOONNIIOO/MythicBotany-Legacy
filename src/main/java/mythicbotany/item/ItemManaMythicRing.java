package mythicbotany.item;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.common.core.helper.ItemNBTHelper;

/** Only the upstream Greatest Mana Ring is an IManaItem storage container. */
public class ItemManaMythicRing extends ItemMythicRing implements IManaItem, IManaTooltipDisplay {
    private static final String TAG_MANA = "mana";
    public ItemManaMythicRing() { super(Effect.MANA); }
    @Override public int getMana(ItemStack stack) { return ItemNBTHelper.getInt(stack, TAG_MANA, 0); }
    @Override public int getMaxMana(ItemStack stack) { return 4000000; }
    @Override public void addMana(ItemStack stack, int mana) {
        long value = (long) getMana(stack) + mana;
        ItemNBTHelper.setInt(stack, TAG_MANA, (int) Math.max(0L, Math.min(getMaxMana(stack), value)));
    }
    @Override public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean canExportManaToPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean isNoExport(ItemStack stack) { return false; }
    @Override public float getManaFractionForDisplay(ItemStack stack) { return (float) getMana(stack) / (float) getMaxMana(stack); }
    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - getManaFractionForDisplay(stack); }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) { return MathHelper.hsvToRGB(getManaFractionForDisplay(stack) / 3.0F, 1.0F, 1.0F); }
}
