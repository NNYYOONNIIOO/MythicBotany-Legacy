package mythicbotany.world;

import mythicbotany.dimension.ModDimensions;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Prevents ChunkGeneratorOverworld from placing vanilla trees in Alfheim. */
public final class AlfheimVanillaTreeBlocker {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void denyVanillaTrees(DecorateBiomeEvent.Decorate event) {
        if (event.getWorld().provider.getDimension() == ModDimensions.ALFHEIM_DIMENSION_ID
                && event.getType() == DecorateBiomeEvent.Decorate.EventType.TREE) {
            event.setResult(Event.Result.DENY);
        }
    }
}
