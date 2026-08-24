package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnirPlaced;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.render.entity.RenderSnowballStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders the fallback entity through the registered Mjoellnir item model. */
@SideOnly(Side.CLIENT)
public final class RenderMjoellnirPlaced extends RenderSnowballStack<EntityMjoellnirPlaced> {
    public RenderMjoellnirPlaced(RenderManager manager) {
        super(manager, ModItems.mjoellnir, Minecraft.getMinecraft().getRenderItem(),
                entity -> entity.getItem());
    }

    @Override
    public ItemStack getStackToRender(EntityMjoellnirPlaced entity) {
        ItemStack stack = entity.getItem();
        return stack.isEmpty() ? new ItemStack(ModItems.mjoellnir) : stack;
    }
}
