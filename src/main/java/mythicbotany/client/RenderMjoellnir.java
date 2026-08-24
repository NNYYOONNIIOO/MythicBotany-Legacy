package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnir;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.render.entity.RenderSnowballStack;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnir extends RenderSnowballStack<EntityMjoellnir> {
    public RenderMjoellnir(RenderManager manager) {
        super(manager, ModItems.mjoellnir, Minecraft.getMinecraft().getRenderItem(),
                RenderMjoellnir::getRenderStack);
    }

    private static ItemStack getRenderStack(EntityMjoellnir entity) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            return new ItemStack(ModItems.mjoellnir);
        }
        ItemStack renderStack = stack.copy();
        renderStack.setItemDamage(0);
        return renderStack;
    }
}
