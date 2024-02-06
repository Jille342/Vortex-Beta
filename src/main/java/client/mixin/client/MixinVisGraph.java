/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package client.mixin.client;

import client.features.module.ModuleManager;
import client.features.module.player.Freecam;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public abstract class MixinVisGraph {
    @Inject(method = "func_178606_a", at = @At("HEAD"), cancellable = true)
    public void visGraph(BlockPos p_178606_1_, CallbackInfo ci){
        if(ModuleManager.getModulebyClass(Freecam.class).isEnable())
        ci.cancel();
    }

}
