package client.mixin.client;

import client.Client;
import client.event.EventType;
import client.event.listeners.EventJump;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow protected abstract boolean isPlayer();

    @Shadow public abstract boolean isPotionActive(int p_isPotionActive_1_);

    @Shadow public abstract PotionEffect getActivePotionEffect(Potion p_getActivePotionEffect_1_);

    protected Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "jump", at = @At("HEAD"))
    private void onPreUpdate(CallbackInfo ci) {
        if (!this.isPlayer())
            return;
        EventJump e = new EventJump();
        e.setType(EventType.PRE);
        Client.onEvent(e);
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void onPostUpdate(CallbackInfo ci) {
        if (!this.isPlayer())
            return;
        EventJump e = new EventJump();
        e.setType(EventType.POST);
        Client.onEvent(e);
    }
}
