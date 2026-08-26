package mythicbotany.registry;

import mythicbotany.item.ItemAlfsteelPick;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import mythicbotany.recipe.RecipeAlfsteelPickElementium;
import mythicbotany.recipe.ModRecipes;
import mythicbotany.world.AlfheimBiomes;
import net.minecraft.world.biome.Biome;

public final class ModRegistry {
    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        event.getRegistry().registerAll(AlfheimBiomes.ALL);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ModRecipes.registerSpecialFlowerSubTiles();
        event.getRegistry().registerAll(ModBlocks.ALL);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.ALL);
        for (Block block : ModBlocks.ALL) {
            if (block == ModBlocks.mjoellnir) {
                continue;
            }
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            event.getRegistry().register(itemBlock);
        }
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        Item elementiumPick = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("botania", "elementiumpick"));
        if (elementiumPick == null) {
            return;
        }
        RecipeAlfsteelPickElementium recipe = new RecipeAlfsteelPickElementium(elementiumPick);
        recipe.setRegistryName(new ResourceLocation("mythicbotany", "alfsteel_pick_elementium"));
        event.getRegistry().register(recipe);
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
