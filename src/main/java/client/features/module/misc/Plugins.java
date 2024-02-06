package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.utils.ChatUtils;
import joptsimple.internal.Strings;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Plugins extends Module {
  public static int ticks;

    public Plugins() {
        super("Plugins", 0, Category.MISC);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;
        mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("/"));
        ticks = 0;
        super.onEnable();
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            ticks++;
            if(ticks>=2000){
                ChatUtils.printChatprefix("Plugins check timed out...");
                ticks = 0;
            }
        }
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket) e);
if(event.getPacket() instanceof S3APacketTabComplete){
    final S3APacketTabComplete s3APacketTabComplete = (S3APacketTabComplete) event.getPacket();
    final List<String> plugins = new ArrayList<>();
    final String[] commands = s3APacketTabComplete.func_149630_c();

    for (final String command1 : commands) {
        final String[] command = command1.split(":");
        if (command.length > 1) {
            final String pluginName = command[0].replace("/", "");
            if (!plugins.contains(pluginName))
                plugins.add(pluginName);
        }
        ChatUtils.printChat(command[0]);
    }

    Collections.sort(plugins);
    if (!plugins.isEmpty()) {
        ChatUtils.printChat("§aPlugins §7(§8" + plugins.size() + "§7): §c" + Strings.join(plugins.toArray(new String[0]), "§7, §c"));
    } else {
        ChatUtils.printChat("§cNo plugins found.");
    }
    toggle();
 ticks= 0;
}
        }
            }
}