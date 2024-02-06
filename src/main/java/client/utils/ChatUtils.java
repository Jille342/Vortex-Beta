package client.utils;

import client.Client;
import com.google.gson.JsonObject;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatUtils implements MCUtil {
    public final static String chatPrefix = "\2477[\2476Mole\2477] \2478>> \247f";
    public final static String ircchatPrefix = "\2477[\2476Ex\2479IRC\2477] \247f";

    public static void printChat(String text) {

        mc.thePlayer.addChatComponentMessage(new ChatComponentText("["+ Client.NAME+ "] "+text));

    }

    public static void printChatprefix(String text) {
        mc.thePlayer.sendChatMessage(chatPrefix + text);
    }

    public static void printIRCChatprefix(String text) {
        mc.thePlayer.sendChatMessage(ircchatPrefix + text);
    }

    public static void sendChat_NoFilter(String text) {
        mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(text));
    }

    public static void sendChat(String text) {
        mc.thePlayer.sendChatMessage(text);
    }
}