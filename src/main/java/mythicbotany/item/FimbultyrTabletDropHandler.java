package mythicbotany.item;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.common.entity.EntityDoppleganger;

/** Adds the tablet to Gaia II's loot for players who learned Mimir's knowledge. */
public final class FimbultyrTabletDropHandler {
    private static final ResourceLocation HARD_LOOT_TABLE =
            new ResourceLocation("botania", "gaia_guardian_2");
    private static final String LOOT_POOL = "mythicbotany_fimbultyr_tablet";

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (!HARD_LOOT_TABLE.equals(event.getName())
                || event.getTable().getPool(LOOT_POOL) != null) {
            return;
        }

        // Botania rolls this table once per participant, with that player as
        // the killer. A loot condition preserves its multiplayer drop rules.
        LootCondition knowsMimir = (random, context) -> {
            Entity killer = context.getKiller();
            return context.getLootedEntity() instanceof EntityDoppleganger
                    && killer instanceof EntityPlayer
                    && killer.getEntityData().getBoolean(ItemGjallarHornFull.KNOWLEDGE_TAG);
        };
        LootEntry tablet = new LootEntryItem(ModItems.fimbultyrTablet, 1, 0,
                new LootFunction[0], new LootCondition[0], LOOT_POOL);
        event.getTable().addPool(new LootPool(new LootEntry[]{tablet},
                new LootCondition[]{knowsMimir}, new RandomValueRange(1),
                new RandomValueRange(0), LOOT_POOL));
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        // Knowledge is permanent, including after dying during a Gaia fight
        // or returning from the End. Keep the existing save-data key.
        if (event.getOriginal().getEntityData().getBoolean(ItemGjallarHornFull.KNOWLEDGE_TAG)) {
            event.getEntityPlayer().getEntityData().setBoolean(ItemGjallarHornFull.KNOWLEDGE_TAG, true);
        }
    }
}
