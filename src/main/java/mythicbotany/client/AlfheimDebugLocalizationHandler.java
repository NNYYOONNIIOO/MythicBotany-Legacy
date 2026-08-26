package mythicbotany.client;

import mythicbotany.MythicBotany;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/** Localizes the biome id printed by the F3 debug overlay. */
public final class AlfheimDebugLocalizationHandler {
    private static final String BIOME_PREFIX = "Biome: ";

    public AlfheimDebugLocalizationHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void localizeBiomeName(RenderGameOverlayEvent.Text event) {
        localize(event.getLeft());
        localize(event.getRight());
    }

    private static void localize(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) {
                continue;
            }

            // Forge's debug overlay has two 1.12 layouts: some versions add
            // "Biome: ", while GuiOverlayDebugForge can emit the registry id
            // as a bare line. Handle both forms.
            boolean hasPrefix = line.startsWith(BIOME_PREFIX);
            String biomeId = hasPrefix ? line.substring(BIOME_PREFIX.length()) : line;
            if (!biomeId.startsWith(MythicBotany.MODID + ".")) {
                continue;
            }
            String key = "biome." + biomeId;
            String localized = I18n.format(key);
            if (!localized.equals(key)) {
                lines.set(i, hasPrefix ? BIOME_PREFIX + localized : localized);
            }
        }
    }
}
