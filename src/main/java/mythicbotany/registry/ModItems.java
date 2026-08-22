package mythicbotany.registry;

import mythicbotany.MythicBotany;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public final class ModItems {
    private static final Item.ToolMaterial ALFSTEEL_TOOLS = EnumHelper.addToolMaterial(
            "MYTHICBOTANY_ALFSTEEL", 4, 2500, 12.0F, 4.0F, 30);
    private static final ItemArmor.ArmorMaterial ALFSTEEL_ARMOR = EnumHelper.addArmorMaterial(
            "MYTHICBOTANY_ALFSTEEL", MythicBotany.MODID + ":alfsteel", 45,
            new int[]{4, 9, 7, 4}, 30, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3.0F);

    public static final Item asgardRune = named(new Item(), "asgard_rune");
    public static final Item vanaheimRune = named(new Item(), "vanaheim_rune");
    public static final Item alfheimRune = named(new Item(), "alfheim_rune");
    public static final Item midgardRune = named(new Item(), "midgard_rune");
    public static final Item joetunheimRune = named(new Item(), "joetunheim_rune");
    public static final Item muspelheimRune = named(new Item(), "muspelheim_rune");
    public static final Item niflheimRune = named(new Item(), "niflheim_rune");
    public static final Item nidavellirRune = named(new Item(), "nidavellir_rune");
    public static final Item helheimRune = named(new Item(), "helheim_rune");
    public static final Item alfsteelIngot = named(new Item(), "alfsteel_ingot");
    public static final Item alfsteelNugget = named(new Item(), "alfsteel_nugget");
    public static final Item alfsteelTemplate = named(new Item(), "alfsteel_template");
    public static final Item alfsteelSword = named(new ItemSword(ALFSTEEL_TOOLS), "alfsteel_sword");
    public static final Item alfsteelPick = named(new AlfsteelPickaxe(ALFSTEEL_TOOLS), "alfsteel_pick");
    public static final Item alfsteelAxe = named(new AlfsteelAxe(ALFSTEEL_TOOLS, 6.0F, -3.1F), "alfsteel_axe");
    public static final Item alfsteelHelmet = named(new ItemArmor(ALFSTEEL_ARMOR, 0, EntityEquipmentSlot.HEAD), "alfsteel_helmet");
    public static final Item alfsteelChestplate = named(new ItemArmor(ALFSTEEL_ARMOR, 0, EntityEquipmentSlot.CHEST), "alfsteel_chestplate");
    public static final Item alfsteelLeggings = named(new ItemArmor(ALFSTEEL_ARMOR, 0, EntityEquipmentSlot.LEGS), "alfsteel_leggings");
    public static final Item alfsteelBoots = named(new ItemArmor(ALFSTEEL_ARMOR, 0, EntityEquipmentSlot.FEET), "alfsteel_boots");
    public static final Item manaRingGreatest = named(new Item().setMaxStackSize(1), "mana_ring_greatest");
    public static final Item auraRingGreatest = named(new Item().setMaxStackSize(1), "aura_ring_greatest");
    public static final Item fadedNetherStar = named(new Item(), "faded_nether_star");
    public static final Item fireRing = named(new Item().setMaxStackSize(1), "fire_ring");
    public static final Item iceRing = named(new Item().setMaxStackSize(1), "ice_ring");
    public static final Item gjallarHornEmpty = named(new Item().setMaxStackSize(1), "gjallar_horn_empty");
    public static final Item gjallarHornFull = named(new Item().setMaxStackSize(1), "gjallar_horn_full");
    public static final Item cursedAndwariRing = named(new Item().setMaxStackSize(1), "cursed_andwari_ring");
    public static final Item andwariRing = named(new Item().setMaxStackSize(1).setMaxDamage(32), "andwari_ring");
    public static final Item fimbultyrTablet = named(new Item(), "fimbultyr_tablet");
    public static final Item kvasirBlood = named(new Item().setMaxStackSize(8), "kvasir_blood");
    public static final Item kvasirMead = named(new ItemFood(8, 0.8F, false).setMaxStackSize(8), "kvasir_mead");
    public static final Item dreamCherry = named(createDreamCherry(), "dream_cherry");
    public static final Item rawElementium = named(new Item(), "raw_elementium");
    public static final Item alfPixieSpawnEgg = named(new Item(), "alf_pixie_spawn_egg");

    public static final Item[] ALL = {
            asgardRune, vanaheimRune, alfheimRune, midgardRune, joetunheimRune,
            muspelheimRune, niflheimRune, nidavellirRune, helheimRune,
            alfsteelIngot, alfsteelNugget, alfsteelTemplate, alfsteelSword, alfsteelPick, alfsteelAxe,
            alfsteelHelmet, alfsteelChestplate, alfsteelLeggings, alfsteelBoots,
            manaRingGreatest, auraRingGreatest, fadedNetherStar, fireRing, iceRing,
            gjallarHornEmpty, gjallarHornFull, cursedAndwariRing, andwariRing,
            fimbultyrTablet, kvasirBlood, kvasirMead, dreamCherry, rawElementium, alfPixieSpawnEgg
    };

    private ModItems() {
    }

    private static ItemFood createDreamCherry() {
        ItemFood food = new ItemFood(10, 1.2F, false);
        food.setPotionEffect(new PotionEffect(MobEffects.GLOWING, 100, 0), 0.3F);
        return food;
    }

    private static <T extends Item> T named(T item, String name) {
        item.setRegistryName(new ResourceLocation(MythicBotany.MODID, name));
        item.setTranslationKey(MythicBotany.MODID + "." + name);
        item.setCreativeTab(MythicBotany.TAB);
        return item;
    }

    private static class AlfsteelPickaxe extends ItemPickaxe {
        private AlfsteelPickaxe(Item.ToolMaterial material) {
            super(material);
        }
    }

    private static class AlfsteelAxe extends ItemAxe {
        private AlfsteelAxe(Item.ToolMaterial material, float attackDamage, float attackSpeed) {
            super(material, attackDamage, attackSpeed);
        }
    }
}
