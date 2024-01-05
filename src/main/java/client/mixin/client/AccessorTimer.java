package client.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Timer.class})
public interface AccessorTimer {
    @Accessor("ticksPerSecond")
    float getTickLength();

    @Accessor("ticksPerSecond")
    void setTickLength(float paramFloat);
}
