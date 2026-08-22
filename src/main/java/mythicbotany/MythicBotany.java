package mythicbotany;

import mythicbotany.proxy.CommonProxy;
import mythicbotany.recipe.ModRecipes;
import mythicbotany.registry.ModBlocks;
import mythicbotany.registry.ModItems;
import mythicbotany.tile.TileManaCollector;
import mythicbotany.tile.TileFunctionalFlower;
import mythicbotany.tile.TileManaInfuser;
import mythicbotany.pylon.TileAlfsteelPylon;
import mythicbotany.world.ModWorldGenerator;
import mythicbotany.entity.EntityAlfPixie;
import mythicbotany.rune.TileCentralRuneHolder;
import mythicbotany.rune.TileRuneHolder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
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
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        GameRegistry.registerTileEntity(TileManaInfuser.class, "mythicbotany_mana_infuser");
        GameRegistry.registerTileEntity(TileManaCollector.class, "mythicbotany_mana_collector");
        GameRegistry.registerTileEntity(TileFunctionalFlower.class, "mythicbotany_functional_flower");
        GameRegistry.registerTileEntity(TileRuneHolder.class, "mythicbotany_rune_holder");
        GameRegistry.registerTileEntity(TileCentralRuneHolder.class, "mythicbotany_central_rune_holder");
        GameRegistry.registerTileEntity(TileAlfsteelPylon.class, "mythicbotany_alfsteel_pylon");
        net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(
                new net.minecraft.util.ResourceLocation(MODID, "alf_pixie"), EntityAlfPixie.class,
                "alf_pixie", 1, INSTANCE, 64, 3, true);
        GameRegistry.registerWorldGenerator(new ModWorldGenerator(), 0);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModRecipes.register();
        proxy.init();
        logger.info("MythicBotany 1.12.2 core initialized with {} blocks and {} items",
                ModBlocks.ALL.length, ModItems.ALL.length);
    }
}
