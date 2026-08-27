package mythicbotany.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/** Runtime configuration for MythicBotany client and gameplay settings. */
public final class MythicBotanyConfig {
    public static final float DEFAULT_RITUAL_JEI_RUNE_SCALE = 0.75F;
    public static float ritualJeiRuneScale = DEFAULT_RITUAL_JEI_RUNE_SCALE;

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
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
}
