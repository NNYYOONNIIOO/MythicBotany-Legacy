package mythicbotany.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import mythicbotany.tile.TileManaCollector;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.subtile.SubTileGenerating;

@Mixin(value = SubTileGenerating.class, remap = false)
public abstract class SubTileGeneratingMixin {
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void mythicbotany$bindCollector(CallbackInfo callbackInfo) {
        SubTileGenerating flower = (SubTileGenerating) (Object) this;
        if (flower.getWorld() == null || flower.getWorld().isRemote) {
            return;
        }
        BlockPos binding = flower.getBinding();
        TileEntity bound = binding == null ? null : flower.getWorld().getTileEntity(binding);
        if (bound != null && !bound.isInvalid()) {
            if (bound instanceof TileManaCollector) {
                flower.emptyManaIntoCollector();
            }
            return;
        }

        BlockPos origin = flower.getPos();
        int range = SubTileGenerating.LINK_RANGE;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    TileEntity tile = flower.getWorld().getTileEntity(origin.add(x, y, z));
                    if (tile instanceof TileManaCollector) {
                        flower.linkToForcefully(tile);
                        flower.emptyManaIntoCollector();
                        return;
                    }
                }
            }
        }
    }
}
