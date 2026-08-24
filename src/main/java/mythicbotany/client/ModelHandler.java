package mythicbotany.client;

import mythicbotany.MythicBotany;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.pylon.RenderAlfsteelPylon;
import mythicbotany.pylon.TileAlfsteelPylon;
import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.rune.TileRuneHolder;
import mythicbotany.tile.TileYggdrasilBranch;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPIClient;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MythicBotany.MODID)
public final class ModelHandler {
    private static boolean specialFlowerModelsRegistered;
    private static boolean tileEntityRenderersRegistered;

    private ModelHandler() { }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerModels(ModelRegistryEvent event) {
        registerSpecialFlowerModels();
        registerTileEntityRenderers();
        for (Item item : ModItems.ALL) register(item);
        for (Block block : ModBlocks.ALL) {
            register(Item.getItemFromBlock(block));
        }
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
        if (item == null || item.getRegistryName() == null) {
            return;
        }
        if (item == ModItems.mjoellnir) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(ModBlocks.mjoellnir.getRegistryName(), "normal"));
            return;
        }
        if (item == Item.getItemFromBlock(ModBlocks.yggdrasilBranch)) {
            ModelResourceLocation location = new ModelResourceLocation(
                    item.getRegistryName(), "inventory");
            for (int meta = 0; meta < 4; meta++) {
                ModelLoader.setCustomModelResourceLocation(item, meta, location);
            }
            return;
        }
        if (item == ModItems.fadedNetherStar) {
            final ModelResourceLocation location = new ModelResourceLocation(
                    item.getRegistryName(), "inventory");
            ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return location;
                }
            });
            return;
        }
        ResourceLocation model = item == Item.getItemFromBlock(ModBlocks.alfsteelPylon)
                ? new ResourceLocation(MythicBotany.MODID, "itemblock/alfsteel_pylon")
                : item.getRegistryName();
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(model, "inventory"));
    }

    /** Bind the same TESRs used by item stacks to their placed TileEntities. */
    private static void registerTileEntityRenderers() {
        if (tileEntityRenderersRegistered) return;
        tileEntityRenderersRegistered = true;
        ClientRegistry.bindTileEntitySpecialRenderer(TileRuneHolder.class, new RenderRuneHolder());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCentralRuneHolder.class, new RenderCentralRuneHolder());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAlfsteelPylon.class, new RenderAlfsteelPylon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileYggdrasilBranch.class, new RenderYggdrasilBranch());
    }

    private static void registerSpecialFlowerModel(String subTileName, String modelName) {
        ResourceLocation location = new ResourceLocation(MythicBotany.MODID, modelName);
        BotaniaAPIClient.registerSubtileModel(subTileName,
                new ModelResourceLocation(location, "normal"),
                new ModelResourceLocation(location, "inventory"));
    }

}
