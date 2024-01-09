package client.mixin.client;

import client.Client;
import client.event.listeners.EventKey;
import client.event.listeners.EventTick;
import client.features.module.ModuleManager;
import client.features.module.misc.HitDelayFix;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow private int leftClickCounter;

    @Inject(method = {"shutdown"}, at = @At("HEAD"))
    public void shutdown(CallbackInfo ci)
    {
        ModuleManager.saveModuleSetting();
    }


    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void startGame(CallbackInfo callbackInfo) {
        Client.init();

    }
    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() ) {
            Client.onEvent(new EventKey(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
        }



    }

    @Inject(method ="runTick", at = @At("RETURN"))
    private void runTick(CallbackInfo ci) {
        EventTick eventTick = new EventTick();
        Client.onEvent(eventTick);
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    private void clickMouseAfter(final CallbackInfo ci) {
        if(ModuleManager.getModulebyClass(HitDelayFix.class).enable)
        leftClickCounter = 0;
    }
}
