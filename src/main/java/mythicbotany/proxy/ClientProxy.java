package mythicbotany.proxy;

import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        for (Item item : ModItems.ALL) {
            registerModel(item);
        }
        for (net.minecraft.block.Block block : ModBlocks.ALL) {
            registerModel(Item.getItemFromBlock(block));
        }
    }

    private void registerModel(Item item) {
        if (item != null && item.getRegistryName() != null) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
