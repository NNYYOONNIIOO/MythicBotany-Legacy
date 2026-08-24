package mythicbotany.flower;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.subtile.signature.BasicSignature;

import java.util.List;

/** Adds the short description directly below Botania's generating/functional label. */
public final class MythicFlowerSignature extends BasicSignature {
    private static final String NO_REFERENCE = "mythicbotany.no_reference";
    private final String descriptionKey;

    public MythicFlowerSignature(String name, String descriptionKey) {
        super(name);
        this.descriptionKey = descriptionKey;
    }

    @Override
    public String getUnlocalizedLoreTextForStack(ItemStack stack) {
        // The description is added below by addTooltip. Returning an intentionally
        // untranslated key prevents Botania's optional reference setting from adding it twice.
        return NO_REFERENCE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addTooltip(ItemStack stack, World world, List<String> tooltip) {
        super.addTooltip(stack, world, tooltip);
        String description = I18n.format(descriptionKey);
        if (!description.equals(descriptionKey)) {
            tooltip.add(TextFormatting.ITALIC + description);
        }
    }
}
