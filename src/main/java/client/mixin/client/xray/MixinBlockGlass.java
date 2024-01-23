package client.mixin.client.xray;

import client.features.module.ModuleManager;
import net.minecraft.block.BlockGlass;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockGlass.class})
public class MixinBlockGlass {
    @Inject(method = "getBlockLayer", at = @At(value =  "HEAD"), cancellable = true)
    public void getBlockLayer(CallbackInfoReturnable<EnumWorldBlockLayer> cir){

    }
}
