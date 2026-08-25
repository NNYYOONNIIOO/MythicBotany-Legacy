package mythicbotany.mixin;

import mythicbotany.pylon.TileAlfsteelPylon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.common.block.tile.mana.TileSpreader;

/** Keeps an Alfsteel pylon as a real spreader receiver after burst tracing. */
@Mixin(TileSpreader.class)
public abstract class TileSpreaderMixin {
    @Shadow
    private IManaReceiver receiver;

    @Unique
    private BlockPos mythicbotany$forcedReceiver;

    @Inject(method = "bindTo", at = @At("TAIL"))
    private void mythicbotany$bindPylon(EntityPlayer player, ItemStack wand,
                                         BlockPos target, EnumFacing side,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || player == null || target == null) {
            return;
        }

        TileEntity tile = player.world.getTileEntity(target);
        if (tile instanceof TileAlfsteelPylon) {
            mythicbotany$forcedReceiver = new BlockPos(target);
            receiver = (IManaReceiver) tile;
        } else {
            mythicbotany$forcedReceiver = null;
        }
    }

    @Inject(method = "checkForReceiver", at = @At("TAIL"))
    private void mythicbotany$restorePylon(CallbackInfo ci) {
        if (mythicbotany$forcedReceiver == null) {
            return;
        }

        TileEntity self = (TileEntity) (Object) this;
        if (!self.hasWorld()) {
            return;
        }

        TileEntity tile = self.getWorld().getTileEntity(mythicbotany$forcedReceiver);
        if (tile instanceof TileAlfsteelPylon) {
            receiver = (IManaReceiver) tile;
        } else {
            mythicbotany$forcedReceiver = null;
            if (receiver instanceof TileAlfsteelPylon) {
                receiver = null;
            }
        }
    }

}
