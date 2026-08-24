package mythicbotany.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.subtile.SubTileGenerating;

@Mixin(value = SubTileGenerating.class, remap = false)
public abstract class SubTileGeneratingMixin {
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void mythicbotany$bindCollector(CallbackInfo callbackInfo) {
        ManaCollectorLinker.bindAndTransfer((SubTileGenerating) (Object) this);
    }
}
