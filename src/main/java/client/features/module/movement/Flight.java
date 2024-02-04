package client.features.module.movement;
import client.event.Event;
import client.event.listeners.EventMove;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

public class Flight extends Module {
    ModeSetting mode;
    NumberSetting speed;
    public Flight() {

        super("Flight", Keyboard.KEY_NONE,	Category.MOVEMENT);
    }
    public void init() {
        super.init();
        mode = new ModeSetting("Mode", "Motion", new String[]{"Motion"});
        speed = new NumberSetting("Speed", 2.0F,0.25, 5 ,0.25);
        addSetting(mode, speed);
    }


    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setTag(mode.getMode());
            if(mode.getMode().equals("Motion")) {
                mc.thePlayer.onGround= false;
                if (mc.gameSettings.keyBindJump.isKeyDown() && (!mc.gameSettings.keyBindSneak.isKeyDown() || !mc.gameSettings.keyBindJump.isKeyDown())) {
                    mc.thePlayer.motionY = speed.getValue() * 0.6;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown() && (!mc.gameSettings.keyBindSneak.isKeyDown() || !mc.gameSettings.keyBindJump.isKeyDown())) {
                    mc.thePlayer.motionY = -speed.getValue() * 0.6;
                } else {
                    mc.thePlayer.motionY = 0;
                }
            }
        }
        if(e instanceof  EventUpdate) {
            if(   e.isPre()) {

            }
        }
        if (e instanceof EventMove) {
            EventMove em = (EventMove) e;

            if (mode.getMode().equalsIgnoreCase("Antikick") || mode.getMode().equalsIgnoreCase("Motion") || mode.getMode().equalsIgnoreCase("glide")) {

                double speed1;
                if (mc.gameSettings.keyBindSneak.isKeyDown() && mc.gameSettings.keyBindJump.isKeyDown()) {
                   speed1= speed.getValue()*2.5D;
                } else {
                   speed1 = speed.getValue();
                }

                double forward = mc.thePlayer.movementInput.moveForward;
                double strafe = mc.thePlayer.movementInput.moveStrafe;
                float yaw = mc.thePlayer.rotationYaw;
                if ((forward == 0.0D) && (strafe == 0.0D)) {
                    em.setX(0.0D);
                    em.setZ(0.0D);
                } else {
                    if (forward != 0.0D) {
                        if (strafe > 0.0D) {
                            yaw += (forward > 0.0D ? -45 : 45);
                        } else if (strafe < 0.0D) {
                            yaw += (forward > 0.0D ? 45 : -45);
                        }
                        strafe = 0.0D;
                        if (forward > 0.0D) {
                            forward = 1;
                        } else if (forward < 0.0D) {
                            forward = -1;
                        }
                    }
                    em.setX(forward * speed1 * Math.cos(Math.toRadians(yaw + 90.0F))
                            + strafe * speed1* Math.sin(Math.toRadians(yaw + 90.0F)));
                    em.setZ(forward * speed1 * Math.sin(Math.toRadians(yaw + 90.0F))
                            - strafe * speed1* Math.cos(Math.toRadians(yaw + 90.0F)));
                }
            }
        }
    }
}

