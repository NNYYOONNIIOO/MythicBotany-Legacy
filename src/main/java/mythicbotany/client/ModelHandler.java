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
import vazkii.botania.api.BotaniaAPIClient;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class ModelHandler {
    private ModelHandler() { }
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : ModItems.ALL) register(item);
        for (Block block : ModBlocks.ALL) register(Item.getItemFromBlock(block));
        registerSpecialFlowerModel("mythicbotany_exoblaze", "exoblaze");
        registerSpecialFlowerModel("mythicbotany_wither_aconite", "wither_aconite");
        registerSpecialFlowerModel("mythicbotany_aquapanthus", "aquapanthus");
        registerSpecialFlowerModel("mythicbotany_hellebore", "hellebore");
        registerSpecialFlowerModel("mythicbotany_raindeletia", "raindeletia");
        registerSpecialFlowerModel("mythicbotany_feysythia", "feysythia");
        registerSpecialFlowerModel("mythicbotany_petrunia", "petrunia");
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) ->
                tintIndex == 0 ? 0x9E65D6 : 0x28173D, ModItems.alfPixieSpawnEgg);
    }
    private static void register(Item item) {
        if (item != null && item.getRegistryName() != null)
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void registerSpecialFlowerModel(String subTileName, String modelName) {
        ResourceLocation location = new ResourceLocation(MythicBotany.MODID, modelName);
        BotaniaAPIClient.registerSubtileModel(subTileName,
                new ModelResourceLocation(location, "normal"),
                new ModelResourceLocation(location, "inventory"));
    }
}

