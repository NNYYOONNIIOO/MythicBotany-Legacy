package mythicbotany.item;

import mythicbotany.MythicBotany;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * A normal 1.12.2 monster placer with a fixed entity id.  Keeping the native
 * placer implementation is important: it handles both right-click spawning
 * and the vanilla spawn-egg NBT format used by commands and creative tabs.
 */
public class ItemAlfPixieSpawnEgg extends ItemMonsterPlacer {
    private static final ResourceLocation ENTITY_ID = new ResourceLocation(MythicBotany.MODID, "alf_pixie");

    public ItemAlfPixieSpawnEgg() {
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            ItemMonsterPlacer.applyEntityIdToItemStack(stack, ENTITY_ID);
            items.add(stack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemMonsterPlacer.applyEntityIdToItemStack(stack, ENTITY_ID);
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        ItemMonsterPlacer.applyEntityIdToItemStack(stack, ENTITY_ID);
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}
