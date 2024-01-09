package client.utils;

import client.mixin.client.AccessorMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class TimerUtils {

    public static Timer getTimer (){
        return ((AccessorMinecraft) Minecraft.getMinecraft()).getTimer();
    }
}
