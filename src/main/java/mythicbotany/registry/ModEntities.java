package mythicbotany.registry;

import mythicbotany.MythicBotany;
import mythicbotany.entity.EntityAlfPixie;
import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.entity.EntityMjoellnirPlaced;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class ModEntities {
    private static boolean registered;
    private ModEntities() { }
    public static void register() {
        if (registered) return;
        ResourceLocation id = new ResourceLocation(MythicBotany.MODID, "alf_pixie");
        EntityRegistry.registerModEntity(id, EntityAlfPixie.class, "alf_pixie", 100,
                MythicBotany.INSTANCE, 64, 3, true);
        EntityRegistry.registerEgg(id, 0x9E65D6, 0x28173D);
        EntityRegistry.registerModEntity(new ResourceLocation(MythicBotany.MODID, "mjoellnir"),
                EntityMjoellnir.class, "mjoellnir", 101, MythicBotany.INSTANCE, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MythicBotany.MODID, "mjoellnir_placed"),
                EntityMjoellnirPlaced.class, "mjoellnir_placed", 102, MythicBotany.INSTANCE, 64, 1, true);
        registered = true;
    }
}
