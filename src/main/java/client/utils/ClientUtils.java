package client.utils;

import client.features.module.ModuleManager;
import client.mixin.client.AccessorMinecraft;
import net.minecraft.client.Minecraft;

public class ClientUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();



	public static void setTimer(float d) {
		((AccessorMinecraft) mc).getTimer().timerSpeed = d;
	}


}
