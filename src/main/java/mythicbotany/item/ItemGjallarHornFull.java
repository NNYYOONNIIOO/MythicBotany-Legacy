package mythicbotany.item;

import mythicbotany.registry.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/** The filled horn is a drinkable source of Mimir's knowledge. */
public class ItemGjallarHornFull extends Item {
    private static final String KNOWLEDGE_TAG = "MythicBotanyMimirKnowledge";

    public ItemGjallarHornFull() {
        setMaxStackSize(1);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack horn = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, horn);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase living) {
        if (!(living instanceof EntityPlayer)) {
            return stack;
        }

        EntityPlayer player = (EntityPlayer) living;
        if (!world.isRemote && !player.getEntityData().getBoolean(KNOWLEDGE_TAG)) {
            player.getEntityData().setBoolean(KNOWLEDGE_TAG, true);
            player.sendMessage(new TextComponentTranslation("message.mythicbotany.mimir_knowledge"));
        }

        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(ModItems.gjallarHornEmpty);
            }
        }
        return stack;
    }
}
