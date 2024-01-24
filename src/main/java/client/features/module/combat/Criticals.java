package client.features.module.combat;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.ModeSetting;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

public class Criticals extends Module {
    ModeSetting mode;
    public Criticals() {

        super("Criticals", Keyboard.KEY_NONE,	Category.COMBAT);
    }
    public void init() {
        super.init();
        mode = new ModeSetting("Mode", "ACP", new String[]{"Packet", "ACP", "NCP"});
        addSetting(mode);
    }


    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate)
            setTag(mode.getMode());

        if (e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isOutgoing()) {
                if (event.getPacket() instanceof C02PacketUseEntity) {
                    C02PacketUseEntity packetUseEntity = (C02PacketUseEntity) event.getPacket();
                    if (packetUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK  && mc.thePlayer.onGround) {
                        switch (mode.getMode()) {
                            case("ACP"):
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.28E-7D, mc.thePlayer.posZ, false));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                break;
                            case("Packet"):
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625D, mc.thePlayer.posZ, false));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                break;
                            case "NCP":
                                double random = Math.random() * 0.0003;
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06252F + random, mc.thePlayer.posZ, true));
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + random, mc.thePlayer.posZ, false));
                                break;

                        }
                    }
                }
            }
        }
        if(e instanceof  EventUpdate) {
         if(   e.isPre()) {

            }
        }
    }
}

