package mythicbotany.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/** Runtime configuration for MythicBotany client and gameplay settings. */
public final class MythicBotanyConfig {
    public static final float DEFAULT_RITUAL_JEI_RUNE_SCALE = 0.75F;
    public static final boolean DEFAULT_ALFSTEEL_SWORD_PULSE_INHERITS_WEAPON_ATTRIBUTES = false;
    public static final boolean DEFAULT_HELLEBORE_HEALS_ZOMBIE_VILLAGERS = true;
    public static final boolean DEFAULT_UNSEENS_NETHER_BACKPORT_INTEGRATION = true;
    public static final boolean DEFAULT_ALLOW_FIMBULTYR_TABLET_ALFHEIM_TRAVEL = false;
    public static final String[] DEFAULT_ALFSTEEL_TEMPLATE_DROP_ENTITIES = {
            "minecraft:witch",
            "minecraft:vindication_illager",
            "minecraft:evocation_illager"
    };
    public static final float DEFAULT_ALFSTEEL_TEMPLATE_DROP_CHANCE = 0.15F;
    public static final int DEFAULT_MJOELLNIR_LAUNCH_COOLDOWN = 20;
    public static final float DEFAULT_MJOELLNIR_LIGHTNING_DAMAGE = 5.0F;
    public static final boolean DEFAULT_MJOELLNIR_REQUIRE_GOLDEN_APPLE = true;
    public static final float DEFAULT_MJOELLNIR_FLIGHT_SPEED = 1.5F;
    public static final float DEFAULT_MJOELLNIR_FLIGHT_DISTANCE = 64.0F;
    public static final boolean DEFAULT_MJOELLNIR_DROPS_BECOME_BLOCKS = true;

    public static float ritualJeiRuneScale = DEFAULT_RITUAL_JEI_RUNE_SCALE;
    public static boolean alfsteelSwordPulseInheritsWeaponAttributes =
            DEFAULT_ALFSTEEL_SWORD_PULSE_INHERITS_WEAPON_ATTRIBUTES;
    public static boolean helleboreHealsZombieVillagers =
            DEFAULT_HELLEBORE_HEALS_ZOMBIE_VILLAGERS;
    public static boolean enableUnseensNetherBackportIntegration =
            DEFAULT_UNSEENS_NETHER_BACKPORT_INTEGRATION;
    public static boolean allowFimbultyrTabletAlfheimTravel =
            DEFAULT_ALLOW_FIMBULTYR_TABLET_ALFHEIM_TRAVEL;
    public static String[] alfsteelTemplateDropEntities =
            DEFAULT_ALFSTEEL_TEMPLATE_DROP_ENTITIES.clone();
    public static float alfsteelTemplateDropChance = DEFAULT_ALFSTEEL_TEMPLATE_DROP_CHANCE;
    public static int mjoellnirLaunchCooldown = DEFAULT_MJOELLNIR_LAUNCH_COOLDOWN;
    public static float mjoellnirLightningDamage = DEFAULT_MJOELLNIR_LIGHTNING_DAMAGE;
    public static boolean mjoellnirRequiresGoldenApple = DEFAULT_MJOELLNIR_REQUIRE_GOLDEN_APPLE;
    public static float mjoellnirFlightSpeed = DEFAULT_MJOELLNIR_FLIGHT_SPEED;
    public static float mjoellnirFlightDistance = DEFAULT_MJOELLNIR_FLIGHT_DISTANCE;
    public static boolean mjoellnirDropsBecomeBlocks = DEFAULT_MJOELLNIR_DROPS_BECOME_BLOCKS;

    private MythicBotanyConfig() {
    }

    public static void load(File file) {
        Configuration configuration = new Configuration(file);
        try {
            ritualJeiRuneScale = configuration.getFloat(
                    "ritualJeiRuneScale",
                    Configuration.CATEGORY_CLIENT,
                    DEFAULT_RITUAL_JEI_RUNE_SCALE,
                    0.1F,
                    2.0F,
                    "Scale of the item icons and spacing in the rune area of the rune ritual JEI recipe.");

            alfsteelSwordPulseInheritsWeaponAttributes = configuration.getBoolean(
                    "alfsteelSwordPulseInheritsWeaponAttributes",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_ALFSTEEL_SWORD_PULSE_INHERITS_WEAPON_ATTRIBUTES,
                    "Whether an Alfsteel Sword mana pulse inherits the sword's attack and enchantment attributes.");
            helleboreHealsZombieVillagers = configuration.getBoolean(
                    "helleboreHealsZombieVillagers",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_HELLEBORE_HEALS_ZOMBIE_VILLAGERS,
                    "Whether Hellebore heals and begins curing zombie villagers in its range.");
            enableUnseensNetherBackportIntegration = configuration.getBoolean(
                    "enableUnseensNetherBackportIntegration",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_UNSEENS_NETHER_BACKPORT_INTEGRATION,
                    "Whether Hellebore prevents Nether Backport Piglins and Hoglins in its range from zombifying.");
            allowFimbultyrTabletAlfheimTravel = configuration.getBoolean(
                    "allowFimbultyrTabletAlfheimTravel",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_ALLOW_FIMBULTYR_TABLET_ALFHEIM_TRAVEL,
                    "Whether the Fimbultyr Tablet can teleport players between the Overworld and Alfheim.");
            alfsteelTemplateDropEntities = configuration.getStringList(
                    "alfsteelTemplateDropEntities",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_ALFSTEEL_TEMPLATE_DROP_ENTITIES,
                    "Entity registry names that can drop an Alfsteel Template when killed with an Alfsteel Sword.");
            alfsteelTemplateDropChance = configuration.getFloat(
                    "alfsteelTemplateDropChance",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_ALFSTEEL_TEMPLATE_DROP_CHANCE,
                    0.0F,
                    1.0F,
                    "Chance for an Alfsteel Template to drop from a configured target killed with an Alfsteel Sword.");
            mjoellnirLaunchCooldown = configuration.getInt(
                    "mjoellnirLaunchCooldown",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_MJOELLNIR_LAUNCH_COOLDOWN,
                    0,
                    Integer.MAX_VALUE,
                    "Cooldown in ticks after launching Mjoellnir; zero disables it.");
            mjoellnirLightningDamage = configuration.getFloat(
                    "mjoellnirLightningDamage",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_MJOELLNIR_LIGHTNING_DAMAGE,
                    0.0F,
                    Float.MAX_VALUE,
                    "Damage dealt by the lightning projectile created by Mjoellnir.");
            mjoellnirRequiresGoldenApple = configuration.getBoolean(
                    "mjoellnirRequiresGoldenApple",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_MJOELLNIR_REQUIRE_GOLDEN_APPLE,
                    "Whether lifting or picking up Mjoellnir requires the player to have eaten a golden apple.");
            mjoellnirFlightSpeed = Math.max(0.01F, configuration.getFloat(
                    "mjoellnirFlightSpeed",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_MJOELLNIR_FLIGHT_SPEED,
                    0.01F,
                    100.0F,
                    "Mjoellnir's outward flight speed. This value cannot be zero."));
            mjoellnirFlightDistance = Math.max(0.01F, configuration.getFloat(
                    "mjoellnirFlightDistance",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_MJOELLNIR_FLIGHT_DISTANCE,
                    0.01F,
                    100000.0F,
                    "Maximum distance in blocks that Mjoellnir can fly before returning. This value cannot be zero."));
            mjoellnirDropsBecomeBlocks = configuration.getBoolean(
                    "mjoellnirDropsBecomeBlocks",
                    Configuration.CATEGORY_GENERAL,
                    DEFAULT_MJOELLNIR_DROPS_BECOME_BLOCKS,
                    "Whether a dropped Mjoellnir becomes a placed block when it lands on the ground.");
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
}
