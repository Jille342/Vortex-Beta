package client.utils;

import client.features.module.ModuleManager;
import client.features.module.render.Notification;
import client.mixin.client.AccessorMinecraft;
import client.mixin.client.AccessorTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class ClientUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();

	public boolean konas() {
		try {
			Class.forName("com.konasclient.client.3");
			return true;
		} catch (ClassNotFoundException|NoClassDefFoundError classNotFoundException) {
			return false;
		}
	}

	public boolean machinelite() {
		try {
			Class.forName("com.lite.machinelite.MachineLite");
			return true;
		} catch (ClassNotFoundException|NoClassDefFoundError classNotFoundException) {
			return false;
		}
	}

	public static void setTimer(float d) {
		((AccessorMinecraft)mc).getTimer().timerSpeed= d;
	}



	public static void addNotification(String str) {
		Notification gui = (Notification) ModuleManager.getModulebyClass(Notification.class);
		if (gui != null) {
			gui.addPanel(str, "");
		}
	}

	public static void addNotification(String str1, String str2) {
		Notification gui = (Notification) ModuleManager.getModulebyClass(Notification.class);
		if (gui != null) {
			gui.addPanel(str1, str2);
		}
	}
}
