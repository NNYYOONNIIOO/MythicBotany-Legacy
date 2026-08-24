package mythicbotany.client;

import mythicbotany.entity.EntityMjoellnirPlaced;
import mythicbotany.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.client.render.entity.RenderSnowballStack;

@SideOnly(Side.CLIENT)
public final class RenderMjoellnirPlaced extends RenderSnowballStack<EntityMjoellnirPlaced> {
    public RenderMjoellnirPlaced(RenderManager manager) {
        super(manager, ModItems.mjoellnir, Minecraft.getMinecraft().getRenderItem(),
                RenderMjoellnirPlaced::getRenderStack);
    }

    private static ItemStack getRenderStack(EntityMjoellnirPlaced entity) {
        ItemStack stack = entity.getItem();
        return stack.isEmpty() ? new ItemStack(ModItems.mjoellnir) : stack;
    }
}
