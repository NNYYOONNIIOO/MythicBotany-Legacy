package mythicbotany;

import mythicbotany.proxy.CommonProxy;
import mythicbotany.recipe.ModRecipes;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.registry.ModEntities;
import mythicbotany.tile.TileManaCollector;
import mythicbotany.tile.TileManaInfuser;
import mythicbotany.tile.TileYggdrasilBranch;
import mythicbotany.tile.TileMjoellnir;
import mythicbotany.pylon.TileAlfsteelPylon;
import mythicbotany.world.ModWorldGenerator;
import mythicbotany.dimension.ModDimensions;
import mythicbotany.dimension.AlfheimPortalHandler;
import mythicbotany.lexicon.MythicLexicon;
import mythicbotany.network.NetworkHandler;
import mythicbotany.item.AlfsteelArmorHandler;
import mythicbotany.item.MjoellnirHandler;
import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.rune.TileRuneHolder;
import mythicbotany.rune.RuneRitualRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = MythicBotany.MODID, name = MythicBotany.NAME, version = MythicBotany.VERSION,
        dependencies = "required-after:botania;required-after:baubles")
public final class MythicBotany {
    public static final String MODID = "mythicbotany";
    public static final String NAME = "MythicBotany";
    public static final String VERSION = "0.1.0";

    public static final CreativeTabs TAB = new CreativeTabs(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.alfsteelIngot);
        }

    };

    @Mod.Instance(MODID)
    public static MythicBotany INSTANCE;

    @net.minecraftforge.fml.common.SidedProxy(
            clientSide = "mythicbotany.proxy.ClientProxy",
            serverSide = "mythicbotany.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    public MythicBotany() {
        MinecraftForge.EVENT_BUS.register(new mythicbotany.registry.ModRegistry());
        MinecraftForge.EVENT_BUS.register(new AlfsteelArmorHandler());
        MinecraftForge.EVENT_BUS.register(new MjoellnirHandler());
        MinecraftForge.EVENT_BUS.register(new AlfheimPortalHandler());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        NetworkHandler.init();
        ModDimensions.register();
        GameRegistry.registerTileEntity(TileManaInfuser.class, tileId("mana_infuser"));
        GameRegistry.registerTileEntity(TileManaCollector.class, tileId("mana_collector"));
        GameRegistry.registerTileEntity(TileYggdrasilBranch.class, tileId("yggdrasil_branch"));
        GameRegistry.registerTileEntity(TileMjoellnir.class, tileId("mjoellnir"));
        GameRegistry.registerTileEntity(TileRuneHolder.class, tileId("rune_holder"));
        GameRegistry.registerTileEntity(TileCentralRuneHolder.class, tileId("central_rune_holder"));
        GameRegistry.registerTileEntity(TileAlfsteelPylon.class, tileId("alfsteel_pylon"));
        ModEntities.register();
        GameRegistry.registerWorldGenerator(new ModWorldGenerator(), 0);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModRecipes.register();
        RuneRitualRegistry.registerDefaults();
        MythicLexicon.register();
        proxy.init();
        logger.info("MythicBotany 1.12.2 core initialized with {} blocks and {} items",
                ModBlocks.ALL.length, ModItems.ALL.length);
    }

    private static ResourceLocation tileId(String name) {
        return new ResourceLocation(MODID, name);
    }
}
