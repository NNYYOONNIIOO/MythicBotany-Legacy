package mythicbotany.client;

import mythicbotany.MythicBotany;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/** Localizes Alfheim biome registry ids shown by the F3 overlay. */
public final class AlfheimBiomeOverlayHandler {
    private static final String NAMESPACE = MythicBotany.MODID + ".";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void localizeBiomeLine(RenderGameOverlayEvent.Text event) {
        localize(event.getLeft());
        localize(event.getRight());
    }

    private static void localize(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) {
                continue;
            }
            int start = line.indexOf(NAMESPACE);
            if (start < 0) {
                continue;
            }
            int end = start + NAMESPACE.length();
            while (end < line.length() && isIdCharacter(line.charAt(end))) {
                end++;
            }
            String id = line.substring(start, end);
            String localized = translateBiomeId(id);
            if (!localized.equals(id)) {
                lines.set(i, line.substring(0, start) + localized + line.substring(end));
            }
        }
    }

    private static String translateBiomeId(String id) {
        String normalized = id.replace(':', '.');
        String[] keys = {
                "biome." + normalized,
                normalized,
                "biome." + id
        };
        for (String key : keys) {
            String translated = I18n.format(key);
            if (!translated.equals(key)) {
                return translated;
            }
        }
        return id;
    }

    private static boolean isIdCharacter(char character) {
        return character == '_' || character == '-' || character == '.'
                || character >= 'a' && character <= 'z'
                || character >= 'A' && character <= 'Z'
                || character >= '0' && character <= '9';
    }
}
