package client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class InventoryUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();

	public static short actionNumber = 0;

	private static int currentItem;

	public static int getSlot() {
		return mc.thePlayer.inventory.currentItem;
	}

	public static void setSlot(int slot) {
		if (slot > 8 || slot < 0) return;
		mc.thePlayer.inventory.currentItem = slot;
	}


	public static void moveItem(int before, int after) {
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, before, 0, 1, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, after, 0, 1, mc.thePlayer);
		mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, before, 0, 1, mc.thePlayer);
	}


	public static void push() {
		currentItem = mc.thePlayer.inventory.currentItem;
	}

	public static void pop() {
		mc.thePlayer.inventory.currentItem = currentItem;
	}
}
