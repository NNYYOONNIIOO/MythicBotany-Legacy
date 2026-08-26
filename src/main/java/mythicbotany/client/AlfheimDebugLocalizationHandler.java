package mythicbotany.client;

import mythicbotany.MythicBotany;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/** Localizes the biome id printed by the F3 debug overlay. */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class AlfheimDebugLocalizationHandler {
    private static final String BIOME_PREFIX = "Biome: ";

    private AlfheimDebugLocalizationHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void localizeBiomeName(RenderGameOverlayEvent.Text event) {
        localize(event.getLeft());
        localize(event.getRight());
    }

    private static void localize(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || !line.startsWith(BIOME_PREFIX)) {
                continue;
            }
            String biomeId = line.substring(BIOME_PREFIX.length());
            if (!biomeId.startsWith(MythicBotany.MODID + ".")) {
                continue;
            }
            String key = "biome." + biomeId;
            String localized = I18n.format(key);
            if (!localized.equals(key)) {
                lines.set(i, BIOME_PREFIX + localized);
            }
        }
    }
}
