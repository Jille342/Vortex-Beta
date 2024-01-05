package client.utils;

import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class WorldUtils {

    public static TimeHelper tpsCounter = new TimeHelper();

    public static double tps;

    public static void onTime(S03PacketTimeUpdate packet) {
        tps = Math.round(tpsCounter.getCurrentMS() - tpsCounter.getLastMS()) / 50;
        tpsCounter.reset();
    }

}
