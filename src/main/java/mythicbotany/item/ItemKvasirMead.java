package mythicbotany.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/** Kvasir's mead records the first time a player learns its hidden knowledge. */
public class ItemKvasirMead extends ItemFood {
    public static final String KNOWLEDGE_TAG = "MythicBotanyKvasirKnowledge";

    public ItemKvasirMead() {
        super(8, 0.8F, false);
        setMaxStackSize(8);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        if (!worldIn.isRemote) {
            boolean alreadyKnown = player.getEntityData().getBoolean(KNOWLEDGE_TAG);
            player.getEntityData().setBoolean(KNOWLEDGE_TAG, true);
            player.sendMessage(new TextComponentTranslation(alreadyKnown
                    ? "message.mythicbotany.kvasir_known"
                    : "message.mythicbotany.kvasir_knowledge"));
        }
    }
}
