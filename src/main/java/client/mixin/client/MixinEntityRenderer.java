package client.mixin.client;

import client.Client;
import client.event.listeners.EventCameraTransform;
import client.event.listeners.EventRenderWorld;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.features.module.combat.HitBox;
import client.features.module.combat.Reach;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    Minecraft mc = Minecraft.getMinecraft();
    @Shadow
    private Entity pointedEntity;
    @Shadow protected abstract void setupCameraTransform(float partialTicks, int pass);

    @Inject(method = {"renderWorldPass"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;debugView:Z")})
    private void renderWorldPassPre(int pass, float partialTicks, long finishTimeNano, CallbackInfo paramCallbackInfo) {
        EventRenderWorld e = new EventRenderWorld(partialTicks);
        Client.onEvent(e);
    }
    @Inject(method = {"setupCameraTransform"}, at = {@At(value = "HEAD")})
    private void setupCameraTransform(float p_78479_1_, int p_78479_2_, CallbackInfo ci) {
        EventCameraTransform e = new EventCameraTransform(p_78479_1_, p_78479_2_);
        Client.onEvent(e);
    }

}
