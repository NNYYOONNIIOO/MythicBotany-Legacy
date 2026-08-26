package mythicbotany.dimension;

import mythicbotany.item.ItemKvasirMead;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent;

import java.util.List;

/** Connects Botania's elven portal to the legacy Alfheim dimension. */
public final class AlfheimPortalHandler {
    @SubscribeEvent
    public void onElvenPortalUpdate(ElvenPortalUpdateEvent event) {
        if (!event.open || event.portalTile == null || event.portalTile.getWorld() == null) {
            return;
        }
        World source = event.portalTile.getWorld();
        if (source.isRemote || source.provider.getDimension() != 0) {
            return;
        }

        List<EntityPlayerMP> players = source.getEntitiesWithinAABB(EntityPlayerMP.class, event.aabb);
        for (EntityPlayerMP player : players) {
            if (!player.getEntityData().getBoolean(ItemKvasirMead.KNOWLEDGE_TAG)
                    || player.isRiding()) {
                continue;
            }
            if (DimensionManager.getWorld(ModDimensions.ALFHEIM_DIMENSION_ID) == null) {
                DimensionManager.initDimension(ModDimensions.ALFHEIM_DIMENSION_ID);
            }
            WorldServer target = DimensionManager.getWorld(ModDimensions.ALFHEIM_DIMENSION_ID);
            if (target == null) {
                player.sendMessage(new TextComponentTranslation(
                        "message.mythicbotany.alfheim_not_loaded"));
                continue;
            }
            player.changeDimension(ModDimensions.ALFHEIM_DIMENSION_ID,
                    new TeleporterAlfheim(target));
        }
    }
}
