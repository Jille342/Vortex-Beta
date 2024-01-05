package client.mixin.client;

import client.Client;
import client.event.listeners.EventLightingUpdate;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void preCheckLightFor(EnumSkyBlock p_checkLightFor_1_, BlockPos p_checkLightFor_2_, CallbackInfoReturnable<Boolean> cir) {

        if (Client.onEvent(new EventLightingUpdate(p_checkLightFor_2_, p_checkLightFor_1_)).isCancelled()) {
            cir.cancel();
        }
    }
}