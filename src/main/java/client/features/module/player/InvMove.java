package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.util.BlockPos;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

public class InvMove extends Module {
    ModeSetting mode;
    boolean inInventory = false;
    public InvMove() {
        super("InvMove", 0 , Category.PLAYER);
    }
    @Override
    public void init() {
        super.init();
        mode = new ModeSetting("Mode ", "Normal", new String[]{"Normal"});
        addSetting(mode);
    }

    public void onEvent(Event event) {
        if (mc.currentScreen instanceof GuiChat) {
            return;
        }
        if(event instanceof EventUpdate){
            setTag(mode.getMode());
            EventUpdate em = (EventUpdate)event;
            if(em.isPre()){
            }
        }
        if (event instanceof EventTick) {
            if (mc.currentScreen != null) {

                    KeyBinding[] moveKeys = new KeyBinding[]{
                            mc.gameSettings.keyBindForward,
                            mc.gameSettings.keyBindBack,
                            mc.gameSettings.keyBindLeft,
                            mc.gameSettings.keyBindRight,
                            mc.gameSettings.keyBindJump
                    };
                    for (KeyBinding bind : moveKeys){
                        KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                    }
                    if(!inInventory){
                        if(mode.getMode() == "AACP"){
                            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
                        }
                        inInventory = !inInventory;
                    }
            }else{
                if(inInventory){
                    if(mode.getMode() == "AACP"){
                        mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, Action.START_SPRINTING));
                    }
                    inInventory = !inInventory;
                }
            }
        }
        if (event instanceof EventPacket) {
            EventPacket ep = (EventPacket) event;
            Packet packet = ep.getPacket();
            if(packet instanceof C0BPacketEntityAction){
                C0BPacketEntityAction p = (C0BPacketEntityAction)packet;
                if(p.getAction() == Action.START_SPRINTING && inInventory && mode.getMode() == "AACP")
                    ep.setCancelled(true);
            }
        }
    }
}