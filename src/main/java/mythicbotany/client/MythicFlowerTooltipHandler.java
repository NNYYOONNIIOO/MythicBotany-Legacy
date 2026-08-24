package mythicbotany.client;

import mythicbotany.MythicBotany;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Ensures MythicBotany flower descriptions appear below Botania's flower type line. */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class MythicFlowerTooltipHandler {
    private static final Map<String, String> DESCRIPTION_KEYS = new HashMap<>();

    static {
        DESCRIPTION_KEYS.put("mythicbotany_exoblaze",
                "tile.botania:flower.mythicbotany_exoblaze.reference");
        DESCRIPTION_KEYS.put("mythicbotany_wither_aconite",
                "tile.botania:flower.mythicbotany_wither_aconite.reference");
        DESCRIPTION_KEYS.put("mythicbotany_aquapanthus",
                "tile.botania:flower.mythicbotany_aquapanthus.reference");
        DESCRIPTION_KEYS.put("mythicbotany_hellebore",
                "tile.botania:flower.mythicbotany_hellebore.reference");
        DESCRIPTION_KEYS.put("mythicbotany_raindeletia",
                "tile.botania:flower.mythicbotany_raindeletia.reference");
        DESCRIPTION_KEYS.put("mythicbotany_feysythia",
                "tile.botania:flower.mythicbotany_feysythia.reference");
        DESCRIPTION_KEYS.put("mythicbotany_petrunia",
                "tile.botania:flower.mythicbotany_petrunia.reference");
    }

    private MythicFlowerTooltipHandler() {
    }

    @SubscribeEvent
    public static void addDescription(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() != Item.getItemFromBlock(ModBlocks.specialFlower)) {
            return;
        }
        String type = ItemBlockSpecialFlower.getType(stack);
        String key = DESCRIPTION_KEYS.get(type);
        if (key == null) {
            return;
        }
        String description = I18n.format(key);
        if (description.equals(key)) {
            return;
        }

        List<String> tooltip = event.getToolTip();
        for (String line : tooltip) {
            if (line.contains(description)) {
                return;
            }
        }

        String generating = I18n.format("botania.flowerType.generating");
        String functional = I18n.format("botania.flowerType.functional");
        int insertAt = Math.min(2, tooltip.size());
        for (int i = 1; i < tooltip.size(); i++) {
            String line = tooltip.get(i);
            if (line.contains(generating) || line.contains(functional)) {
                insertAt = i + 1;
                break;
            }
        }
        tooltip.add(insertAt, TextFormatting.ITALIC + description);
    }
}
