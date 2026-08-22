package mythicbotany.client;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.util.ResourceLocation;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class ModelHandler {
    private ModelHandler() { }
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ModItems.ALL) register(item);
        for (Block block : ModBlocks.ALL) register(Item.getItemFromBlock(block));
        Item specialFlowerItem = Item.getItemFromBlock(vazkii.botania.common.block.ModBlocks.specialFlower);
        if (specialFlowerItem != null) {
            for (int meta = 0; meta < 16; meta++) {
                ModelLoader.setCustomModelResourceLocation(specialFlowerItem, meta,
                        new ModelResourceLocation(new ResourceLocation("botania_special", "specialflower"), "inventory"));
            }
        }
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) ->
                tintIndex == 0 ? 0x9E65D6 : 0x28173D, ModItems.alfPixieSpawnEgg);
    }
    private static void register(Item item) {
        if (item != null && item.getRegistryName() != null)
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}


