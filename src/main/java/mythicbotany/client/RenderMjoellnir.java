package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.render.entity.RenderSnowballStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders the projectile through the registered Mjoellnir item model. */
@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends RenderSnowballStack<EntityMjoellnir> {
    public RenderMjoellnir(RenderManager manager) {
        super(manager, ModItems.mjoellnir, Minecraft.getMinecraft().getRenderItem(),
                entity -> entity.getItem());
    }

    @Override
    public ItemStack getStackToRender(EntityMjoellnir entity) {
        ItemStack stack = entity.getItem();
        return stack.isEmpty() ? new ItemStack(ModItems.mjoellnir) : stack;
    }
}
