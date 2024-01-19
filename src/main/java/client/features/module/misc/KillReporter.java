package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.TimeHelper;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;

public class KillReporter extends Module {
    public TimeHelper timer = new TimeHelper();

    public ArrayList<String> sendQueue = new ArrayList<>();
    NumberSetting delay;



    public KillReporter() {
        super("KillReporter", 0,Category.MISC);
    }
    public void init(){
        super.init();
        this.delay = new NumberSetting("Send Delay", 1000, 1000, 5000, 1000F);
        addSetting(delay);
    }
    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {;
            if (!this.sendQueue.isEmpty() && this.timer.hasReached(delay.getValue())) {
                String user = this.sendQueue.get(0);
                sendMessage(user);
                this.sendQueue.remove(0);
                this.timer.reset();
            }
        }
        if(e instanceof  EventPacket) {
            if (e.isIncoming() &&
                    ((EventPacket) e).getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) ((EventPacket) e).getPacket();
                String message = EnumChatFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getUnformattedText());
                if (message != null && !message.isEmpty()) {
                    String[] text = message.split(" ");
                    if ((text[1].equalsIgnoreCase("killed") || text[1].equalsIgnoreCase("shot")) && text[0].startsWith(String.valueOf(mc.thePlayer.getName()) + "(") ) {
                        String user = text[2].replaceAll("\\(.+?\\)", "");
                        this.sendQueue.add(user);
                    }
                    if((text[1].equalsIgnoreCase("killed") || text[1].equalsIgnoreCase("shot")) && text[2].contains(String.valueOf(mc.thePlayer.getName()) + "(")) {
                        String user = text[0].replaceAll("\\(.+?\\)", "");
                        this.sendQueue.add(user);
                    }
                }
            }
        }
    }


    private void sendMessage(String user) {
      mc.thePlayer.sendChatMessage("/report " + user + " "+ "KillAura Reach Velocity");
    }
}
