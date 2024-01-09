package client.features.module.movement;

import client.event.Event;
import client.event.listeners.EventJump;
import client.event.listeners.EventMotion;
import client.event.listeners.EventPacket;
import client.event.listeners.EventPlayerInput;
import client.features.module.Module;

import client.mixin.client.AccessorEntityPlayer;
import client.setting.ModeSetting;
import client.utils.ClientUtils;
import client.utils.MovementUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;

import org.lwjgl.input.Keyboard;

public class Speed extends Module {


    public Speed () {
        super("Speed", Keyboard.KEY_NONE, Module.Category.MOVEMENT);
    }

    ModeSetting mode;

    @Override
    public void init() {
        this.mode = new ModeSetting("Mode", "NCP", new String[] {"NCP", "OldNCP",  "TEST"});
        addSetting(mode);
        super.init();
    }

    double lastSpeed;
    boolean lastGround;
    boolean moving;
    boolean OnGround = false;
    int tickTimer;
    int progress;
    double moveSpeed;
    double lastDist;
    int teleportIdF = 0;

    int teleportId = 0, clearLagTeleportId = 0;

    boolean inTimer;

    @Override
    public void onEvent(Event<?> e) {
		/*if (e instanceof EventPacket) {
			Packet p = ((EventPacket)e).getPacket();
			if (p instanceof SPacketSetSlot && mc.thePlayer.isEntityAlive() && !mc.thePlayer.isDead) {
				SPacketSetSlot packet = (SPacketSetSlot)p;
				if (packet.getStack().getItem() instanceof ItemAir) {
					mc.getConnection().sendPacket(new CPacketChatMessage("/kill"));
					mc.thePlayer.setDead();
				}
			}
		}*/
        if(e instanceof EventMotion && e.isPre()) {
            setTag(mode.getMode());
            EventMotion event = (EventMotion)e;
            switch (mode.getMode()) {

                case "NCP":
                    lastSpeed += 0.0015;
                    lastSpeed*=.9900000095367432D;
                    //mc.thePlayer.motionY -= mc.thePlayer.motionY < .33319999363422365D ? 9.9999E-4D : 0;
                    if(mc.thePlayer.motionY == .33319999363422365) {
                        mc.thePlayer.motionY -= mc.thePlayer.motionY < .33319999363422365D ? 9.9999E-4D : 0;
                    }
                    if (!mc.thePlayer.onGround) {
                        MovementUtils.Strafe(lastSpeed);
                    }else {
                        if (MovementUtils.isMoving())
                            mc.thePlayer.jump();
                        MovementUtils.Strafe(mc.thePlayer.isSprinting()?.15:.12);
                        lastSpeed = Math.sqrt(Math.pow(Math.abs(mc.thePlayer.posX - mc.thePlayer.lastTickPosX), 2) + Math.pow(Math.abs(mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ), 2));
                    }
					/*if (MovementUtils.isMoving()) {
						if (mc.thePlayer.onGround) {
							mc.thePlayer.jump();
						}
						mc.thePlayer.speedInAir = (.0223F);
						MovementUtils.Strafe(MovementUtils.getSpeed());
					} else {
						mc.thePlayer.motionX = 0.0D;
						mc.thePlayer.motionZ = 0.0D;
					}*/
                    break;
                case "OldNCP":
                    if (MovementUtils.isMoving()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionX *= 1.05D;
                            mc.thePlayer.motionZ *= 1.05D;
                        }
                        mc.thePlayer.motionY -= mc.thePlayer.motionY < .33319999363422365D ? 9.9999E-4D*5 : 0;

                        MovementUtils.Strafe(MovementUtils.getSpeed() + (mc.thePlayer.motionY == .33319999363422365D && MovementUtils.getSpeed() > 0.3 ? 0.05 : 0));
                    } else {
                        mc.thePlayer.motionX = 0.0D;
                        mc.thePlayer.motionZ = 0.0D;
                    }
                    break;

            }
        }

        if (e instanceof EventJump) {
            lastSpeed = .32;
            MovementUtils.Strafe(.29);
        }


        if(e instanceof EventPlayerInput) {
            EventPlayerInput event = (EventPlayerInput)e;
            if (!(mode.is("YPort") && tickTimer % 2 != 0) && !mode.is("NCP")) {
                event.setJump(false);
            }
        }
        super.onEvent(e);
    }

    @Override
    public void onEnable() {
        lastSpeed  = MovementUtils.getSpeed();
        super.onEnable();
    }

    @Override
    public void onDisable()	{
        ((AccessorEntityPlayer)mc.thePlayer).speedInAir(.02F);
        super.onDisable();
    }
}