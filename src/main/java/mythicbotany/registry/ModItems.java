package mythicbotany.registry;

import com.google.common.collect.Multimap;
import mythicbotany.MythicBotany;
import mythicbotany.item.AlfsteelRepairHelper;
import mythicbotany.item.ItemAlfsteelAxe;
import mythicbotany.item.ItemAlfsteelPick;
import mythicbotany.item.ItemAlfsteelSword;
import mythicbotany.item.ItemMjoellnir;
import mythicbotany.item.ItemFimbultyrTablet;
import mythicbotany.item.ItemGjallarHornFull;
import mythicbotany.item.ItemKvasirMead;
import mythicbotany.item.ItemMythicRing;
import mythicbotany.item.ItemManaMythicRing;
import mythicbotany.item.ItemAuraMythicRing;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaDiscountArmor;
import vazkii.botania.api.mana.IManaGivingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.model.armor.ModelArmorTerrasteel;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.UUID;

public final class ModItems {
    private static final Item.ToolMaterial ALFSTEEL_TOOLS = EnumHelper.addToolMaterial(
            "MYTHICBOTANY_ALFSTEEL", 4, 2500, 12.0F, 4.0F, 30);
    private static final Item.ToolMaterial ALFSTEEL_PICK_TOOLS = EnumHelper.addToolMaterial(
            "MYTHICBOTANY_ALFSTEEL_PICK", 3, 4600, 9.0F, 3.0F, 30);
    private static final ItemArmor.ArmorMaterial ALFSTEEL_ARMOR = EnumHelper.addArmorMaterial(
            "MYTHICBOTANY_ALFSTEEL", MythicBotany.MODID + ":alfsteel", 45,
            new int[]{3, 8, 6, 3}, 30, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3.0F);

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
    public static final Item alfsteelSword = named(new ItemAlfsteelSword(ALFSTEEL_TOOLS), "alfsteel_sword");
    public static final Item mjoellnir = named(new ItemMjoellnir(ALFSTEEL_TOOLS), "mjoellnir");
    public static final Item alfsteelPick = named(new ItemAlfsteelPick(ALFSTEEL_PICK_TOOLS), "alfsteel_pick");
    public static final Item alfsteelAxe = named(new ItemAlfsteelAxe(ALFSTEEL_TOOLS, 5.0F, -2.8F), "alfsteel_axe");
    public static final Item alfsteelHelmet = named(new AlfsteelArmor(ALFSTEEL_ARMOR, EntityEquipmentSlot.HEAD), "alfsteel_helmet");
    public static final Item alfsteelChestplate = named(new AlfsteelArmor(ALFSTEEL_ARMOR, EntityEquipmentSlot.CHEST), "alfsteel_chestplate");
    public static final Item alfsteelLeggings = named(new AlfsteelArmor(ALFSTEEL_ARMOR, EntityEquipmentSlot.LEGS), "alfsteel_leggings");
    public static final Item alfsteelBoots = named(new AlfsteelArmor(ALFSTEEL_ARMOR, EntityEquipmentSlot.FEET), "alfsteel_boots");
    public static final Item manaRingGreatest = named(new ItemManaMythicRing(), "mana_ring_greatest");
    public static final Item auraRingGreatest = named(new ItemAuraMythicRing(), "aura_ring_greatest");
    public static final Item fadedNetherStar = named(
            new Item().setMaxStackSize(1).setMaxDamage(1200000), "faded_nether_star");
    public static final Item fireRing = named(new ItemMythicRing(ItemMythicRing.Effect.FIRE), "fire_ring");
    public static final Item iceRing = named(new ItemMythicRing(ItemMythicRing.Effect.ICE), "ice_ring");
    public static final Item gjallarHornEmpty = named(new Item().setMaxStackSize(1), "gjallar_horn_empty");
    public static final Item gjallarHornFull = named(new ItemGjallarHornFull(), "gjallar_horn_full");
    public static final Item cursedAndwariRing = named(new ItemMythicRing(ItemMythicRing.Effect.CURSED_ANDWARI), "cursed_andwari_ring");
    public static final Item andwariRing = named(new ItemMythicRing(ItemMythicRing.Effect.ANDWARI), "andwari_ring");
    public static final Item fimbultyrTablet = named(new ItemFimbultyrTablet(), "fimbultyr_tablet");
    public static final Item kvasirBlood = named(new Item().setMaxStackSize(8), "kvasir_blood");
    public static final Item kvasirMead = named(new ItemKvasirMead(), "kvasir_mead");
    public static final Item dreamCherry = named(createDreamCherry(), "dream_cherry");

    public static final Item[] ALL = {
            asgardRune, vanaheimRune, alfheimRune, midgardRune, joetunheimRune,
            muspelheimRune, niflheimRune, nidavellirRune, helheimRune,
            alfsteelIngot, alfsteelNugget, alfsteelTemplate, mjoellnir, alfsteelSword, alfsteelPick, alfsteelAxe,
            alfsteelHelmet, alfsteelChestplate, alfsteelLeggings, alfsteelBoots,
            manaRingGreatest, auraRingGreatest, fadedNetherStar, fireRing, iceRing,
            gjallarHornEmpty, gjallarHornFull, cursedAndwariRing, andwariRing,
            fimbultyrTablet, kvasirBlood, kvasirMead, dreamCherry
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
        item.setTranslationKey(MythicBotany.MODID + ":" + name);
        item.setCreativeTab(MythicBotany.TAB);
        return item;
    }

    private static class AlfsteelArmor extends ItemArmor
            implements ISpecialArmor, IManaDiscountArmor, IManaGivingItem {
        private AlfsteelArmor(ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
            super(material, 0, slot);
            setMaxDamage(5200);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public ModelBiped getArmorModel(EntityLivingBase entityLiving,
                                        net.minecraft.item.ItemStack stack,
                                        EntityEquipmentSlot armorSlot, ModelBiped original) {
            ModelBiped model = new ModelArmorTerrasteel(armorSlot);
            model.setModelAttributes(original);
            return model;
        }

        @Override
        public String getArmorTexture(net.minecraft.item.ItemStack stack, Entity entity,
                                      EntityEquipmentSlot slot, String type) {
            return MythicBotany.MODID + ":textures/model/armor_alfsteel.png";
        }

        @Override
        public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot,
                                                                          ItemStack stack) {
            Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
            if (slot == armorType) {
                UUID uuid = new UUID((getTranslationKey(stack) + slot.toString()).hashCode(), 0L);
                if (armorType == EntityEquipmentSlot.HEAD) {
                    modifiers.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(
                            uuid, "Alfsteel reach", 2.0D, 0));
                    modifiers.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(
                            uuid, "Alfsteel knockback resistance", 0.15D, 0));
                } else if (armorType == EntityEquipmentSlot.CHEST) {
                    modifiers.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(
                            uuid, "Alfsteel knockback resistance", 0.4D, 0));
                } else if (armorType == EntityEquipmentSlot.LEGS) {
                    modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(
                            uuid, "Alfsteel movement speed", 0.4D, 1));
                    modifiers.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(
                            new UUID((getTranslationKey(stack) + slot.toString()).hashCode(), 1L),
                            "Alfsteel knockback resistance", 0.3D, 0));
                } else if (armorType == EntityEquipmentSlot.FEET) {
                    modifiers.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(
                            uuid, "Alfsteel knockback resistance", 0.15D, 0));
                }
            }
            return modifiers;
        }

        @Override
        public void onArmorTick(World world, EntityPlayer player, net.minecraft.item.ItemStack stack) {
            if (!world.isRemote) {
                AlfsteelRepairHelper.repairArmor(stack, player, world.getTotalWorldTime());
                if (armorType == EntityEquipmentSlot.HEAD && hasFullSet(player)) {
                    if (player.shouldHeal() && player.ticksExisted % 40 == 0) {
                        player.heal(1.0F);
                    }
                    ManaItemHandler.dispatchManaExact(stack, player, 1, true);
                }
            }
        }

        @Override
        public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player,
                                                            net.minecraft.item.ItemStack armor,
                                                            DamageSource source, double damage, int slot) {
            return new ISpecialArmor.ArmorProperties(0, damageReduceAmount / 25.0D,
                    armor.getMaxDamage());
        }

        @Override
        public int getArmorDisplay(EntityPlayer player, net.minecraft.item.ItemStack armor, int slot) {
            return damageReduceAmount;
        }

        @Override
        public void damageArmor(EntityLivingBase entity, net.minecraft.item.ItemStack stack,
                                DamageSource source, int damage, int slot) {
            ToolCommons.damageItem(stack, damage, entity, AlfsteelRepairHelper.ARMOR_MANA_PER_DURABILITY);
        }

        @Override
        public float getDiscount(ItemStack stack, int slot, EntityPlayer player, ItemStack tool) {
            return armorType == EntityEquipmentSlot.HEAD && hasFullSet(player) ? 0.2F : 0.0F;
        }

        private boolean hasFullSet(EntityPlayer player) {
            return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof AlfsteelArmor
                    && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof AlfsteelArmor
                    && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof AlfsteelArmor
                    && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof AlfsteelArmor;
        }
    }
}
