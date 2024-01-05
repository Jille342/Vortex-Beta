package client.mixin.client;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(C02PacketUseEntity.class)
public interface AccessorCPacketUseEntity {

    @Accessor("entityId")
    void setEntityId(int entityId);

    @Accessor("action")
    void setAction(C02PacketUseEntity.Action action);

    @Accessor("hitVec")
    void setHitVec(Vec3 vec);


}