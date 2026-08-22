package mythicbotany.client;

import java.awt.Color;

import mythicbotany.MythicBotany;
import mythicbotany.item.ItemAlfsteelPick;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Terra-Pick-style animated rainbow mana bar for the alfsteel shatterer. */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class AlfsteelPickTooltipHandler {
    private AlfsteelPickTooltipHandler() { }

    @SubscribeEvent
    public static void onTooltip(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemAlfsteelPick)) return;

        int width = event.getWidth();
        int height = 3;
        int mouseX = event.getX();
        int mouseY = event.getY() - 4;
        FontRenderer font = event.getFontRenderer();
        int level = ItemAlfsteelPick.getLevel(stack);
        int max = ItemAlfsteelPick.LEVELS[Math.min(ItemAlfsteelPick.LEVELS.length - 1, level + 1)];
        boolean ss = level >= ItemAlfsteelPick.LEVELS.length - 1;
        int current = ItemAlfsteelPick.getMana_(stack);
        float percent = level == 0 ? 0.0F : (float) current / (float) max;
        int rainbowWidth = Math.min(Math.max(0, width - (ss ? 0 : 1)), (int) (width * percent));
        float huePer = width == 0 ? 0.0F : 1.0F / width;
        float hueOffset = (System.currentTimeMillis() % 4000L) / 4000.0F;

        GlStateManager.disableDepth();
        Gui.drawRect(mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for (int i = 0; i < rainbowWidth; i++) {
            Gui.drawRect(mouseX + i, mouseY - height, mouseX + i + 1, mouseY,
                    Color.HSBtoRGB(hueOffset + huePer * i, 1.0F, 1.0F));
        }
        Gui.drawRect(mouseX + rainbowWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);

        String rank = I18n.format("botania.rank" + level).replaceAll("&", "\\u00a7");
        font.drawStringWithShadow(rank, mouseX, mouseY - 12, 0xFFFFFF);
        if (!ss) {
            rank = I18n.format("botania.rank" + (level + 1)).replaceAll("&", "\\u00a7");
            font.drawStringWithShadow(rank, mouseX + width - font.getStringWidth(rank), mouseY - 12, 0xFFFFFF);
        }
        GlStateManager.enableDepth();
    }
}

