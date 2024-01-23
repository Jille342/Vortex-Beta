package client.utils;

import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;

public class PacketUtils implements MCUtil {
    private static String trash;
    public static void sendPacketNoEvent(Packet packet) {
       mc.getNetHandler().getNetworkManager().sendPacket(packet, (GenericFutureListener)null, new GenericFutureListener[0]);
    }
    public static void sendPacket(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }


   public static void sendRotationPacket() {

   }
}
