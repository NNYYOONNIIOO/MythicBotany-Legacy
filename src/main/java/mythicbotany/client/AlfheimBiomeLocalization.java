package mythicbotany.client;

import mythicbotany.MythicBotany;
import mythicbotany.world.AlfheimBiomes;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

/** Applies the active language to the name used by Minecraft's F3 overlay. */
@SideOnly(Side.CLIENT)
public final class AlfheimBiomeLocalization {
    private AlfheimBiomeLocalization() {
    }

    public static void apply() {
        try {
            Field nameField = ReflectionHelper.findField(Biome.class,
                    "biomeName", "field_76791_y");
            nameField.setAccessible(true);
            for (Biome biome : AlfheimBiomes.ALL) {
                if (biome.getRegistryName() == null) {
                    continue;
                }
                String id = biome.getRegistryName().toString().replace(':', '.');
                String key = "biome." + id;
                String localized = I18n.format(key);
                if (!localized.equals(key)) {
                    nameField.set(biome, localized);
                }
            }
        } catch (Exception exception) {
            if (MythicBotany.logger != null) {
                MythicBotany.logger.warn("Unable to apply Alfheim biome localization", exception);
            }
        }
    }
}
