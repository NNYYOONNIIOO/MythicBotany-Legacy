package mythicbotany.registry;

import mythicbotany.item.ItemAlfsteelPick;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import mythicbotany.recipe.ModRecipes;

public final class ModRegistry {
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.ALL);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ModRecipes.registerSpecialFlowerSubTiles();
        event.getRegistry().registerAll(ModItems.ALL);
        for (Block block : ModBlocks.ALL) {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            event.getRegistry().register(itemBlock);
        }
    }

    @SubscribeEvent
    public void inheritTerrapickAbility(ItemCraftedEvent event) {
        if (event.crafting.isEmpty() || event.crafting.getItem() != ModItems.alfsteelPick) {
            return;
        }
        Item terrapick = ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania", "terrapick"));
        if (terrapick == null) {
            return;
        }
        for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
            ItemStack ingredient = event.craftMatrix.getStackInSlot(slot);
            if (!ingredient.isEmpty() && ingredient.getItem() == terrapick) {
                ItemAlfsteelPick.setTipped(event.crafting, true);
                return;
            }
        }
    }
}
