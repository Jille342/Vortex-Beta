package client.mixin.client;

import client.Client;
import client.event.listeners.EventRender2D;
import client.features.module.ModuleManager;
import client.features.module.render.HUD;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiInGame {

    @Inject(method = "renderGameOverlay", at = @At("HEAD"))
    private void renderGameOverlay(float p_renderGameOverlay_1_, CallbackInfo ci) {

        if(ModuleManager.getModulebyClass(HUD.class).enable) {
            Client.hud2.draw();
        }


    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {

        final EventRender2D eventRender2D = new EventRender2D(partialTicks);
        Client.onEvent(eventRender2D);
        }
    }

