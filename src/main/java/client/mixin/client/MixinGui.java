package client.mixin.client;

import client.Client;
import client.utils.RenderingUtils;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.GlStateManager.disableFog;
import static net.minecraft.client.renderer.GlStateManager.disableLighting;

@Mixin({Gui.class})
public class MixinGui {
    @Inject(method = "drawGradientRect", at = @At("HEAD"), cancellable = true)
    private void daRect(final CallbackInfo callbackInfo) {





        // Use custom background
        callbackInfo.cancel();
    }
}
