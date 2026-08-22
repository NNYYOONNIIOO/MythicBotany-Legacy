package mythicbotany.client;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPIClient;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class ModelHandler {
    private static boolean specialFlowerModelsRegistered;

    private ModelHandler() { }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerModels(ModelRegistryEvent event) {
        registerSpecialFlowerModels();
        registerSpecialFlowerItemModels();
        for (Item item : ModItems.ALL) register(item);
        for (Block block : ModBlocks.ALL) register(Item.getItemFromBlock(block));
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler((stack, tintIndex) ->
                tintIndex == 0 ? 0x9E65D6 : 0x28173D, ModItems.alfPixieSpawnEgg);
    }

    /** Register addon special-flower models before Botania bakes its model map. */
    public static void registerSpecialFlowerModels() {
        if (specialFlowerModelsRegistered) return;
        specialFlowerModelsRegistered = true;
        registerSpecialFlowerModel("mythicbotany_exoblaze", "exoblaze");
        registerSpecialFlowerModel("mythicbotany_wither_aconite", "wither_aconite");
        registerSpecialFlowerModel("mythicbotany_aquapanthus", "aquapanthus");
        registerSpecialFlowerModel("mythicbotany_hellebore", "hellebore");
        registerSpecialFlowerModel("mythicbotany_raindeletia", "raindeletia");
        registerSpecialFlowerModel("mythicbotany_feysythia", "feysythia");
        registerSpecialFlowerModel("mythicbotany_petrunia", "petrunia");
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

    /**
     * ItemBlockSpecialFlower stores the subtile name in NBT.  Registering the
     * seven concrete item models explicitly keeps the item renderer from
     * falling back to Botania's missing specialflower model.
     */
    private static void registerSpecialFlowerItemModels() {
        Item specialFlower = ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania", "specialflower"));
        if (specialFlower == null) return;

        final String[][] mappings = {
                {"mythicbotany_exoblaze", "exoblaze"},
                {"mythicbotany_wither_aconite", "wither_aconite"},
                {"mythicbotany_aquapanthus", "aquapanthus"},
                {"mythicbotany_hellebore", "hellebore"},
                {"mythicbotany_raindeletia", "raindeletia"},
                {"mythicbotany_feysythia", "feysythia"},
                {"mythicbotany_petrunia", "petrunia"}
        };
        ModelResourceLocation[] variants = new ModelResourceLocation[mappings.length + 1];
        variants[0] = new ModelResourceLocation("botania:specialflower", "inventory");
        for (int i = 0; i < mappings.length; i++) {
            variants[i + 1] = new ModelResourceLocation(
                    new ResourceLocation(MythicBotany.MODID, mappings[i][1]), "inventory");
        }
        ModelBakery.registerItemVariants(specialFlower, variants);
        ModelLoader.setCustomMeshDefinition(specialFlower, stack -> {
            String type = stack.hasTagCompound() ? stack.getTagCompound().getString("type") : "";
            for (String[] mapping : mappings) {
                if (mapping[0].equals(type)) {
                    return new ModelResourceLocation(
                            new ResourceLocation(MythicBotany.MODID, mapping[1]), "inventory");
                }
            }
            return variants[0];
        });
    }
}
