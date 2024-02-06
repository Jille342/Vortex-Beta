package client.mixin.client;

import client.Client;
import client.event.listeners.EventEntityHitbox;
import client.features.module.ModuleManager;
import client.features.module.player.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class MixinEntity {
    @Shadow private int fire;
    @Shadow private AxisAlignedBB boundingBox;

    @Shadow public boolean noClip;

    @Inject(method = "moveEntity", at = @At(value =  "HEAD"), cancellable = true)
    public void moveEntity(double p_moveEntity_1_, double p_moveEntity_3_, double p_moveEntity_5_, CallbackInfo ci){

    }
    @Inject(method = "getCollisionBorderSize", at = @At("HEAD"), cancellable = true)
    public void getCollisionBorderSize(CallbackInfoReturnable<Float> cir) {
        EventEntityHitbox eventEntityHitbox = new EventEntityHitbox((Entity)(Object)this, this.boundingBox);
        Client.onEvent(eventEntityHitbox);
        cir.setReturnValue(eventEntityHitbox.getSize());
    }
    @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
    public void isEntityInside(CallbackInfoReturnable<Boolean> cir){
        if (noClip || ModuleManager.getModulebyClass(Freecam.class).isEnable()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    public int getFire() {
        return fire;
    }

}
