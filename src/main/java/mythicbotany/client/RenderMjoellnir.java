package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.client.render.entity.RenderSnowballStack;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends RenderSnowballStack<EntityMjoellnir> {
    public RenderMjoellnir(RenderManager manager) {
        super(manager, ModItems.mjoellnir, Minecraft.getMinecraft().getRenderItem(),
                EntityMjoellnir::getItem);
    }

    @Override
    public ItemStack getStackToRender(EntityMjoellnir entity) {
        ItemStack stack = entity.getItem();
        return stack.isEmpty() ? new ItemStack(ModItems.mjoellnir) : stack;
    }
}
