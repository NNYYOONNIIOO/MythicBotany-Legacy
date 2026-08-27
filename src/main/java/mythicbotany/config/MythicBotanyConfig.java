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

    public static float ritualJeiRuneScale = DEFAULT_RITUAL_JEI_RUNE_SCALE;
    public static boolean alfsteelSwordPulseInheritsWeaponAttributes =
            DEFAULT_ALFSTEEL_SWORD_PULSE_INHERITS_WEAPON_ATTRIBUTES;
    public static boolean helleboreHealsZombieVillagers =
            DEFAULT_HELLEBORE_HEALS_ZOMBIE_VILLAGERS;
    public static boolean enableUnseensNetherBackportIntegration =
            DEFAULT_UNSEENS_NETHER_BACKPORT_INTEGRATION;
    public static boolean allowFimbultyrTabletAlfheimTravel =
            DEFAULT_ALLOW_FIMBULTYR_TABLET_ALFHEIM_TRAVEL;

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
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
}
