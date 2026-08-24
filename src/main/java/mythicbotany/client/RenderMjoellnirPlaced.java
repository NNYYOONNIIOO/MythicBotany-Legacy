package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnirPlaced;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import vazkii.botania.client.render.entity.RenderSnowballStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders the fallback hammer using the registered Mjoellnir item model. */
@SideOnly(Side.CLIENT)
public final class RenderMjoellnirPlaced extends RenderSnowballStack<EntityMjoellnirPlaced> {
    public RenderMjoellnirPlaced(RenderManager manager) {
        super(manager, ModItems.mjoellnir, Minecraft.getMinecraft().getRenderItem(),
                entity -> entity.getItem().isEmpty()
                        ? new net.minecraft.item.ItemStack(ModItems.mjoellnir)
                        : entity.getItem());
    }
}
