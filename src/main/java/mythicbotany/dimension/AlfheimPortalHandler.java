package mythicbotany.dimension;

import mythicbotany.block.BlockReturnPortal;
import mythicbotany.network.NetworkHandler;
import mythicbotany.registry.ModItems;
import mythicbotany.registry.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import vazkii.botania.api.recipe.ElvenPortalUpdateEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Connects Botania's elven portal to the legacy Alfheim dimension. */
public final class AlfheimPortalHandler {
    private static final int PORTAL_TIME = 120;
    private static final Map<UUID, Integer> portalTimes = new HashMap<>();
    private static final Map<UUID, Long> lastPortalTicks = new HashMap<>();
    private static final Map<UUID, EntityPlayerMP> activePortalPlayers = new HashMap<>();
    private static final Set<UUID> playersInPortal = new HashSet<>();
    private static final float DREAM_CHERRY_DROP_CHANCE = 0.05F;

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
            if (player.isRiding()) {
                continue;
            }
            if (advancePortalTime(player)) {
                teleportToAlfheim(player, event.portalTile.getPos());
            }
        }
    }

    /** Called by the Alfheim return portal once per collision tick. */
    public static void onReturnPortalCollision(EntityPlayerMP player, BlockPos portalPos) {
        int dimension = player.world.provider.getDimension();
        if ((dimension != ModDimensions.ALFHEIM_DIMENSION_ID && dimension != 0)
                || player.isRiding()) {
            return;
        }
        if (advancePortalTime(player)) {
            if (dimension == ModDimensions.ALFHEIM_DIMENSION_ID) {
                teleportToOverworld(player, portalPos);
            } else {
                teleportToAlfheim(player, portalPos);
            }
        }
    }

    @SubscribeEvent
    public void onDreamwoodLeavesHarvest(HarvestDropsEvent event) {
        if (event.getState().getBlock() != ModBlocks.dreamwoodLeaves) {
            return;
        }

        EntityPlayer harvester = event.getHarvester();
        ItemStack held = harvester == null ? ItemStack.EMPTY : harvester.getHeldItemMainhand();
        if (event.isSilkTouching() || held.getItem() instanceof ItemShears) {
            return;
        }

        event.getDrops().clear();
        if (event.getWorld().rand.nextFloat() < DREAM_CHERRY_DROP_CHANCE) {
            event.getDrops().add(new ItemStack(ModItems.dreamCherry));
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
        activePortalPlayers.put(id, player);
        if (player.timeUntilPortal > 0) {
            if (portalTimes.remove(id) != null) {
                NetworkHandler.sendPortalEffect(player, 0);
            }
            lastPortalTicks.remove(id);
            activePortalPlayers.remove(id);
            return false;
        }

        long worldTick = player.world.getTotalWorldTime();
        if (Long.valueOf(worldTick).equals(lastPortalTicks.get(id))) {
            return false;
        }
        lastPortalTicks.put(id, worldTick);
        int time = portalTimes.containsKey(id) ? portalTimes.get(id) + 1 : 1;
        portalTimes.put(id, time);
        NetworkHandler.sendPortalEffect(player, time);
        if (time < PORTAL_TIME) {
            return false;
        }

        portalTimes.remove(id);
        lastPortalTicks.remove(id);
        activePortalPlayers.remove(id);
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
        NetworkHandler.sendPortalEffect(player, PORTAL_TIME);
    }

    private static void teleportToOverworld(EntityPlayerMP player, BlockPos sourcePos) {
        WorldServer target = DimensionManager.getWorld(0);
        if (target != null) {
            player.changeDimension(0, new TeleporterAlfheim(target, sourcePos, true));
            NetworkHandler.sendPortalEffect(player, PORTAL_TIME);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        tickReturnPortalWorld(DimensionManager.getWorld(0));
        tickReturnPortalWorld(DimensionManager.getWorld(ModDimensions.ALFHEIM_DIMENSION_ID));
        for (UUID id : new HashSet<>(portalTimes.keySet())) {
            if (!playersInPortal.contains(id)) {
                EntityPlayerMP player = activePortalPlayers.remove(id);
                if (player != null) {
                    NetworkHandler.sendPortalEffect(player, 0);
                }
                portalTimes.remove(id);
                lastPortalTicks.remove(id);
            }
        }
        playersInPortal.clear();
    }

    private static void tickReturnPortalWorld(WorldServer world) {
        if (world == null) {
            return;
        }
        for (EntityPlayer player : new ArrayList<EntityPlayer>(world.playerEntities)) {
            if (!(player instanceof EntityPlayerMP)) {
                continue;
            }
            EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
            AxisAlignedBB playerBox = serverPlayer.getEntityBoundingBox();
            int minX = (int) Math.floor(playerBox.minX);
            int maxX = (int) Math.floor(playerBox.maxX);
            int minY = (int) Math.floor(playerBox.minY);
            int maxY = (int) Math.floor(playerBox.maxY);
            int minZ = (int) Math.floor(playerBox.minZ);
            int maxZ = (int) Math.floor(playerBox.maxZ);
            BlockPos portalPos = null;
            for (int x = minX; x <= maxX && portalPos == null; x++) {
                for (int y = minY; y <= maxY && portalPos == null; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockPos candidate = new BlockPos(x, y, z);
                        AxisAlignedBB blockBox = new AxisAlignedBB(x, y, z,
                                x + 1.0D, y + 1.0D, z + 1.0D);
                        if (world.getBlockState(candidate).getBlock() == ModBlocks.returnPortal
                                && blockBox.intersects(playerBox)) {
                            portalPos = candidate;
                            break;
                        }
                    }
                }
            }
            if (portalPos != null) {
                onReturnPortalCollision(serverPlayer, portalPos);
            }
        }
    }
}
