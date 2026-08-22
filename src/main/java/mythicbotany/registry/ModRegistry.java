package mythicbotany.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModRegistry {
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.ALL);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.ALL);
        for (Block block : ModBlocks.ALL) {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            event.getRegistry().register(itemBlock);
        }
    }
}
