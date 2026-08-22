package mythicbotany.item;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;

/** Alfsteel sword with a Botania 1.12 mana-burst beam on right click. */
public class ItemAlfsteelSword extends ItemSword implements IManaItem, IManaTooltipDisplay {
    private static final String TAG_MANA = "mana";
    private static final int MAX_MANA = 4000000;
    private static final int BURST_MANA = 1000;

    public ItemAlfsteelSword(Item.ToolMaterial material) {
        super(material);
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("mythicbotany", "beam"), (stack, world, entity) -> 0.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.getCooldownTracker().hasCooldown(this)) return new ActionResult<>(EnumActionResult.PASS, stack);
        if (player.capabilities.isCreativeMode || ManaItemHandler.requestManaExactForTool(stack, player, BURST_MANA, true)) {
            if (!world.isRemote) {
                Entity burst = createBurst(player, stack);
                if (burst != null) world.spawnEntity(burst);
            }
            player.getCooldownTracker().setCooldown(this, 10);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, 0));
        return super.hitEntity(stack, target, attacker);
    }

    private static Entity createBurst(EntityPlayer player, ItemStack source) {
        try {
            Class<?> burstClass = Class.forName("vazkii.botania.common.entity.EntityManaBurst");
            Constructor<?> chosen = null;
            for (Constructor<?> candidate : burstClass.getConstructors()) {
                Class<?>[] parameters = candidate.getParameterTypes();
                if (parameters.length == 1 && parameters[0].isAssignableFrom(player.getClass())) {
                    chosen = candidate;
                    break;
                }
            }
            if (chosen == null) return null;
            Object burst = chosen.newInstance(player);
            invokeIfPresent(burst, "setColor", new Class<?>[]{int.class}, 0xB9A7FF);
            invokeIfPresent(burst, "setMana", new Class<?>[]{int.class}, BURST_MANA);
            invokeIfPresent(burst, "setStartingMana", new Class<?>[]{int.class}, BURST_MANA);
            invokeIfPresent(burst, "setMinManaLoss", new Class<?>[]{int.class}, 40);
            invokeIfPresent(burst, "setManaLossPerTick", new Class<?>[]{int.class}, 40);
            invokeIfPresent(burst, "setGravity", new Class<?>[]{float.class}, 0.0F);
            invokeIfPresent(burst, "setSourceLens", new Class<?>[]{ItemStack.class}, source.copy());
            invokeIfPresent(burst, "setBurstSource", new Class<?>[]{ItemStack.class}, source.copy());
            return burst instanceof Entity ? (Entity) burst : null;
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }

    private static void invokeIfPresent(Object target, String name, Class<?>[] types, Object value) {
        try {
            Method method = target.getClass().getMethod(name, types);
            method.invoke(target, value);
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // Botania revisions expose slightly different optional burst setters.
        }
    }

    @Override public int getMana(ItemStack stack) { return ItemNBTHelper.getInt(stack, TAG_MANA, 0); }
    @Override public int getMaxMana(ItemStack stack) { return MAX_MANA; }
    @Override public void addMana(ItemStack stack, int mana) {
        ItemNBTHelper.setInt(stack, TAG_MANA, Math.max(0, Math.min(MAX_MANA, getMana(stack) + mana)));
    }
    @Override public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean canExportManaToPool(ItemStack stack, TileEntity pool) { return true; }
    @Override public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) { return true; }
    @Override public boolean isNoExport(ItemStack stack) { return false; }
    @Override public float getManaFractionForDisplay(ItemStack stack) { return (float) getMana(stack) / (float) MAX_MANA; }
    @Override public boolean showDurabilityBar(ItemStack stack) { return true; }
    @Override public double getDurabilityForDisplay(ItemStack stack) { return 1.0D - getManaFractionForDisplay(stack); }
    @Override public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(getManaFractionForDisplay(stack) / 3.0F, 1.0F, 1.0F);
    }
}

