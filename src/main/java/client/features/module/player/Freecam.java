package client.features.module.player;

import client.event.Event;
import client.event.listeners.*;
import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.MovementUtils;
import client.utils.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;


public class Freecam extends Module {
    private EntityOtherPlayerMP Field2739;
    public NumberSetting speed;
    private double Field2741;
    private double Field2742;
    private double Field2743;
    private int Field2744 = 0;

    public Freecam() {
        super("Freecam", 0,Category.PLAYER);
    }
    public void init(){
        super.init();
      speed = new NumberSetting( "Speed", 1.0, 1.0, 10.0, 1.0);
        addSetting(speed);
    }

    public void onEvent(Event<?> event) {
if(event instanceof EventUpdate) {
    mc.thePlayer.noClip = true;
}


        if(event instanceof EventPacket) {
            if (event.isIncoming() && (((EventPacket) event).getPacket() instanceof S07PacketRespawn || ((EventPacket) event).getPacket() instanceof S02PacketLoginSuccess)) {
               this.toggle();
            }

            if (!event.isIncoming()) {
                if (!(((EventPacket) event).getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) && !(((EventPacket) event).getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) && !(((EventPacket) event).getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)) {
                    if (((EventPacket) event).getPacket() instanceof C03PacketPlayer) {
                        C03PacketPlayer var2 = (C03PacketPlayer)((EventPacket) event).getPacket();
                        ++this.Field2744;
                        if (this.Field2744 > 20) {
                            event.setCancelled(true);
                           PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.Field2739.posX, this.Field2739.posY, this.Field2739.posZ, this.Field2739.onGround));
                            this.Field2744 = 0;
                        }
                    } else if (((EventPacket) event).getPacket() instanceof C0BPacketEntityAction) {
                        C0BPacketEntityAction var3 = (C0BPacketEntityAction)((EventPacket) event).getPacket();
                        if (var3.getAction() == Action.START_SPRINTING || var3.getAction() == Action.STOP_SPRINTING) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    event.setCancelled(true);
                    PacketUtils.sendPacket(new C03PacketPlayer(this.Field2739.onGround));
                }

            }
        }

        if (event instanceof EventMove) {
            float speed1 = (float) speed.getValue();
            EventMove em = (EventMove) event;
            if (mc.thePlayer.movementInput.jump) {
                em.setY(mc.thePlayer.motionY = speed1);
            } else if (mc.thePlayer.movementInput.sneak) {
                em.setY(mc.thePlayer.motionY = -speed1);
            } else {
                em.setY(mc.thePlayer.motionY = 0.0D);
            }
            speed1 = (float) Math.max(speed.getValue(), getBaseMoveSpeed());
            double forward = mc.thePlayer.movementInput.moveForward;
            double strafe = mc.thePlayer.movementInput.moveStrafe;
            float yaw = mc.thePlayer.rotationYaw;
            if ((forward == 0.0D) && (strafe == 0.0D)) {
                em.setX(0.0D);
                em.setZ(0.0D);
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        strafe = 1;
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
                        + strafe * speed1 * Math.sin(Math.toRadians(yaw + 90.0F)));
                em.setZ(forward * speed1 * Math.sin(Math.toRadians(yaw + 90.0F))
                        - strafe * speed1 * Math.cos(Math.toRadians(yaw + 90.0F)));
            }
        }
        if(event instanceof EventPushBlock) {
            event.setCancelled(true);
        }
        if(event instanceof EventBlockBounds) {
            event.setCancelled(true);
        }
    }
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }


    public float Method276() {
        float var1 = mc.thePlayer.rotationYaw;
        float var2 = mc.thePlayer.moveForward;
        float var3 = mc.thePlayer.moveStrafing;
        var1 += (float)(var2 < 0.0F ? 180 : 0);
        if (var3 < 0.0F) {
            var1 += var2 < 0.0F ? -45.0F : (var2 == 0.0F ? 90.0F : 45.0F);
        }

        if (var3 > 0.0F) {
            var1 -= var2 < 0.0F ? -45.0F : (var2 == 0.0F ? 90.0F : 45.0F);
        }

        return var1 * 0.017453292F;
    }

    public void Method277(double a) {
        mc.thePlayer.motionX = (double)(-MathHelper.sin(this.Method276())) * a;
        mc.thePlayer.motionZ = (double)MathHelper.cos(this.Method276()) * a;
    }

    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer == null) {
            this.Field2739 = null;
         this.toggle();
        } else {
         PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
            this.Field2744 = 0;
            this.Field2739 = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            this.Field2739.clonePlayer(mc.thePlayer, true);
            this.Field2739.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            this.Field2739.rotationYawHead = mc.thePlayer.rotationYawHead;
            this.Field2739.setEntityId(-1337);
            this.Field2739.setSneaking(mc.thePlayer.isSneaking());
            this.Field2739.onGround = mc.thePlayer.onGround;
            mc.theWorld.addEntityToWorld(this.Field2739.getEntityId(), this.Field2739);
            mc.renderGlobal.loadRenderers();
            this.Field2741 = mc.thePlayer.posX;
            this.Field2742 = mc.thePlayer.posY;
            this.Field2743 = mc.thePlayer.posZ;
        }
    }

    public void onDisable() {
        super.onDisable();
        if (this.isVaildWorldAndPlayer()) {
            if (this.Field2739 != null) {
                this.Method277(0.0);
                mc.thePlayer.setLocationAndAngles(this.Field2739.posX, this.Field2739.posY, this.Field2739.posZ, this.Field2739.rotationYaw, this.Field2739.rotationPitch);
                mc.thePlayer.rotationYawHead = this.Field2739.rotationYawHead;
                mc.theWorld.removeEntityFromWorld(this.Field2739.getEntityId());
                mc.thePlayer.setSneaking(this.Field2739.isSneaking());
                this.Field2739 = null;
                mc.thePlayer.setPosition(this.Field2741, this.Field2742, this.Field2743);
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.noClip = false;
                mc.theWorld.removeEntityFromWorld(-1);
                mc.renderGlobal.loadRenderers();
            }
        }
    }
    public boolean isVaildWorldAndPlayer() {
        return mc.thePlayer != null && mc.theWorld != null;
    }
}