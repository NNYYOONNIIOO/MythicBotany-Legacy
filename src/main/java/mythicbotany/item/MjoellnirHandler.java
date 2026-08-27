package mythicbotany.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.tile.TileMjoellnir;
import mythicbotany.config.MythicBotanyConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import vazkii.botania.common.item.relic.ItemThorRing;

/** Shared ownership rules for Mjoellnir and its failed-return drop. */
public final class MjoellnirHandler {
    public static final String RETURN_DROP_TAG = "mythicbotanyMjoellnirReturnDrop";
    private static final String GOLDEN_APPLE_UNTIL_TAG = "mythicbotanyGoldenAppleUntil";
    private static final long GOLDEN_APPLE_DURATION = 20L * 120L;

    public static boolean canHold(EntityPlayer player) {
        if (player == null || player.capabilities.isCreativeMode) {
            return true;
        }
        return !ItemThorRing.getThorRing(player).isEmpty()
                && player.getEntityData().getLong(GOLDEN_APPLE_UNTIL_TAG) >= player.world.getTotalWorldTime();
    }

    /** Drops the hammer; conversion to its placed form is controlled by the config. */
    public static void dropForFailedReturn(EntityPlayer player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) {
            return;
        }
        EntityItem drop = player.dropItem(stack, false);
        if (drop != null) {
            drop.getEntityData().setBoolean(RETURN_DROP_TAG, true);
        }
    }

    public static boolean convertFailedReturnDrop(EntityItem item) {
        if (!MythicBotanyConfig.mjoellnirDropsBecomeBlocks
                || item == null || item.isDead || !item.onGround || item.world.isRemote) {
            return false;
        }
        ItemStack stack = item.getItem();
        if (stack.isEmpty() || stack.getItem() != ModItems.mjoellnir) {
            return false;
        }
        BlockPos start = new BlockPos(MathHelper.floor(item.posX),
                MathHelper.floor(item.getEntityBoundingBox().minY), MathHelper.floor(item.posZ));
        BlockPos pos = findPlacementPosition(item.world, start);
        if (pos == null || !item.world.setBlockState(pos, ModBlocks.mjoellnir.getDefaultState(), 3)) {
            return false;
        }
        TileEntity tile = item.world.getTileEntity(pos);
        if (!(tile instanceof TileMjoellnir)) {
            item.world.setBlockToAir(pos);
            return false;
        }
        ((TileMjoellnir) tile).setItem(stack.copy());
        item.setDead();
        return true;
    }

    /** Stack automatic failed-return hammers vertically when their landing block is occupied. */
    private static BlockPos findPlacementPosition(World world, BlockPos start) {
        if (!world.isBlockLoaded(start)) {
            return null;
        }
        for (int y = Math.max(0, start.getY()); y < world.getHeight(); y++) {
            BlockPos candidate = new BlockPos(start.getX(), y, start.getZ());
            if (world.getBlockState(candidate).getBlock() == ModBlocks.mjoellnir) {
                continue;
            }
            if (!world.getBlockState(candidate).getMaterial().isReplaceable()) {
                continue;
            }
            BlockPos below = candidate.down();
            boolean stackedOnHammer = y > 0
                    && world.getBlockState(below).getBlock() == ModBlocks.mjoellnir;
            if (stackedOnHammer || ModBlocks.mjoellnir.canPlaceBlockAt(world, candidate)
                    || world.isAirBlock(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    public static void sendCannotHoldMessage(EntityPlayer player) {
        player.sendStatusMessage(new TextComponentTranslation(
                "message.mythicbotany.mjoellnir_heavy_pick"), true);
    }

    @SubscribeEvent
    public void onGoldenAppleFinished(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote) {
            return;
        }
        ItemStack consumed = event.getItem();
        if (!consumed.isEmpty() && consumed.getItem() == Items.GOLDEN_APPLE) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            player.getEntityData().setLong(GOLDEN_APPLE_UNTIL_TAG,
                    player.world.getTotalWorldTime() + GOLDEN_APPLE_DURATION);
        }
    }

    @SubscribeEvent
    public void onMjoellnirPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (event.getItem().getItem().getItem() == ModItems.mjoellnir && !canHold(player)) {
            event.setCanceled(true);
            sendCannotHoldMessage(player);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        World world = event.world;
        List<Entity> entities = new ArrayList<>(world.loadedEntityList);
        for (Entity entity : entities) {
            if (!(entity instanceof EntityItem) || entity.isDead) {
                continue;
            }
            EntityItem item = (EntityItem) entity;
            convertFailedReturnDrop(item);
        }
    }
}
