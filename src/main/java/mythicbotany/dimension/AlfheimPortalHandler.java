package mythicbotany.dimension;

import mythicbotany.item.ItemKvasirMead;
import mythicbotany.block.BlockReturnPortal;
import mythicbotany.registry.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Connects Botania's elven portal to the legacy Alfheim dimension. */
public final class AlfheimPortalHandler {
    private static final int PORTAL_TIME = 120;
    private static final Map<UUID, Integer> portalTimes = new HashMap<>();
    private static final Map<UUID, Long> lastPortalTicks = new HashMap<>();
    private static final Set<UUID> playersInPortal = new HashSet<>();

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
            if (advancePortalTime(player)) {
                teleportToAlfheim(player, event.portalTile.getPos());
            }
        }
    }

    /** Called by the Alfheim return portal once per collision tick. */
    public static void onReturnPortalCollision(EntityPlayerMP player, BlockPos portalPos) {
        if (player.world.provider.getDimension() != ModDimensions.ALFHEIM_DIMENSION_ID
                || player.isRiding()) {
            return;
        }
        if (advancePortalTime(player)) {
            teleportToOverworld(player, portalPos);
        }
    }

    /** Beds set a spawn point in Alfheim, but never advance the night. */
    @SubscribeEvent
    public void onPlayerSleep(PlayerSleepInBedEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.provider.getDimension() != ModDimensions.ALFHEIM_DIMENSION_ID
                || player.world.isRemote) {
            return;
        }
        player.setSpawnPoint(event.getPos(), false);
        event.setResult(SleepResult.NOT_POSSIBLE_NOW);
    }

    /** Nether portals are not valid in Alfheim; End portals are unaffected. */
    @SubscribeEvent
    public void denyNetherPortal(BlockEvent.PortalSpawnEvent event) {
        if (event.getWorld().provider.getDimension() == ModDimensions.ALFHEIM_DIMENSION_ID) {
            event.setCanceled(true);
        }
    }

    /** Breaking any part of a return portal's frame removes its surface. */
    @SubscribeEvent
    public void removeReturnPortalOnFrameBreak(BlockEvent.BreakEvent event) {
        World world = event.getWorld();
        if (event.isCanceled()
                || world.provider.getDimension() != ModDimensions.ALFHEIM_DIMENSION_ID
                || !BlockReturnPortal.isFrameBlock(event.getState())) {
            return;
        }
        BlockPos broken = event.getPos();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos candidate = broken.add(x, y, z);
                    if (world.getBlockState(candidate).getBlock() == ModBlocks.returnPortal) {
                        world.setBlockToAir(candidate);
                    }
                }
            }
        }
    }

    private static boolean advancePortalTime(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        playersInPortal.add(id);
        if (player.timeUntilPortal > 0) {
            portalTimes.remove(id);
            lastPortalTicks.remove(id);
            return false;
        }

        long worldTick = player.world.getTotalWorldTime();
        if (Long.valueOf(worldTick).equals(lastPortalTicks.get(id))) {
            return false;
        }
        lastPortalTicks.put(id, worldTick);
        int time = portalTimes.containsKey(id) ? portalTimes.get(id) + 1 : 1;
        portalTimes.put(id, time);
        if (time < PORTAL_TIME) {
            return false;
        }

        portalTimes.remove(id);
        lastPortalTicks.remove(id);
        return true;
    }

    private static void teleportToAlfheim(EntityPlayerMP player, BlockPos sourcePos) {
        if (DimensionManager.getWorld(ModDimensions.ALFHEIM_DIMENSION_ID) == null) {
            DimensionManager.initDimension(ModDimensions.ALFHEIM_DIMENSION_ID);
        }
        WorldServer target = DimensionManager.getWorld(ModDimensions.ALFHEIM_DIMENSION_ID);
        if (target == null) {
            player.sendMessage(new TextComponentTranslation(
                    "message.mythicbotany.alfheim_not_loaded"));
            return;
        }
        player.changeDimension(ModDimensions.ALFHEIM_DIMENSION_ID,
                new TeleporterAlfheim(target, sourcePos, false));
    }

    private static void teleportToOverworld(EntityPlayerMP player, BlockPos sourcePos) {
        WorldServer target = DimensionManager.getWorld(0);
        if (target != null) {
            player.changeDimension(0, new TeleporterAlfheim(target, sourcePos, true));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        portalTimes.keySet().removeIf(id -> !playersInPortal.contains(id));
        lastPortalTicks.keySet().removeIf(id -> !playersInPortal.contains(id));
        playersInPortal.clear();
    }
}
