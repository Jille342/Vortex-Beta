package client.mixin.client.xray;

import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class MixinWorldRenderer {

    public void putColorMultiplier(float p_putColorMultiplier_1_, float p_putColorMultiplier_2_, float p_putColorMultiplier_3_, int p_putColorMultiplier_4_, CallbackInfo ci){

    }
}
