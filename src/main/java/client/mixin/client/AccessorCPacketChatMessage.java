package client.mixin.client;

import net.minecraft.network.play.client.C01PacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({C01PacketChatMessage.class})
public interface AccessorCPacketChatMessage {

    @Accessor("message")
    void message(String paramString);

}
