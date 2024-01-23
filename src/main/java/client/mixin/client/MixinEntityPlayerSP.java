package client.mixin.client;

import client.Client;
import client.event.EventType;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.ModuleManager;
import client.features.module.movement.NoSlowdown;
import client.features.module.render.NoSwing;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scala.collection.parallel.ParIterableLike;

import java.util.List;

import static net.minecraft.network.play.client.C0BPacketEntityAction.Action.*;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

     @Final
    NetworkManager connection;
    @Shadow boolean serverSprintState;
    @Shadow boolean serverSneakState;
    @Shadow double lastReportedPosX;
    @Shadow double lastReportedPosY;
    @Shadow double lastReportedPosZ;
    @Shadow float lastReportedYaw;
    @Shadow float lastReportedPitch;
    @Shadow int positionUpdateTicks;
     boolean prevOnGround;
     boolean autoJumpEnabled;
    @Shadow Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {super(worldIn, playerProfile);}

    @Shadow protected abstract boolean isCurrentViewEntity();

    @Shadow public MovementInput movementInput;

    @Shadow protected int sprintToggleTimer;

    @Shadow @Final public NetHandlerPlayClient sendQueue;

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        EventUpdate e = new EventUpdate();
        e.setType(EventType.PRE);
        Client.onEvent(e);
    }


    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion event = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
event.setType(EventType.PRE);
Client.onEvent(event);
        boolean sprinting = isSprinting() ;

        if (sprinting != serverSprintState) {
            if (sprinting)
                sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, START_SPRINTING));
            else sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, STOP_SPRINTING));

            serverSprintState = sprinting;
        }

        boolean sneaking = isSneaking();

        if (sneaking != serverSneakState  ) {
            if (sneaking)
                sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, START_SNEAKING));
            else sendQueue.addToSendQueue(new C0BPacketEntityAction((EntityPlayerSP) (Object) this, STOP_SNEAKING));

            serverSneakState = sneaking;
        }

        if (isCurrentViewEntity()) {
            float yaw = rotationYaw;
            float pitch = rotationPitch;

            if (event.isModded()) {
                yaw = event.getYaw();
                pitch = event.getPitch();
            }

            double xDiff = posX - lastReportedPosX;
            double yDiff = getEntityBoundingBox().minY - lastReportedPosY;
            double zDiff = posZ - lastReportedPosZ;
            double yawDiff = yaw - this.lastReportedYaw;
            double pitchDiff = pitch - this.lastReportedPitch;
            boolean moved = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > 9.0E-4 || positionUpdateTicks >= 20;
            boolean rotated = yawDiff != 0 || pitchDiff != 0;

            if (ridingEntity == null) {
                if (moved && rotated) {
                    sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(posX, getEntityBoundingBox().minY, posZ, yaw, pitch, onGround));
                } else if (moved) {
                    sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, getEntityBoundingBox().minY, posZ, onGround));
                } else if (rotated) {
                    sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, onGround));
                } else {
                    sendQueue.addToSendQueue(new C03PacketPlayer(onGround));
                }
            } else {
                sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(motionX, -999, motionZ, yaw, pitch, onGround));
                moved = false;
            }

            ++positionUpdateTicks;

            if (moved) {
                lastReportedPosX = posX;
                lastReportedPosY = getEntityBoundingBox().minY;
                lastReportedPosZ = posZ;
                positionUpdateTicks = 0;
            }

            if (rotated) {
                this.lastReportedYaw = yaw;
                this.lastReportedPitch = pitch;
            }
        }

      EventMotion event2 = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        event2.setType(EventType.POST);
        Client.onEvent(event2);

        ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "RETURN"), cancellable = true)
    private void PostUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion event = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        event.setType(EventType.POST);
        Client.onEvent(event);
    }


    @Inject(method = "onLivingUpdate",at = @At(value =  "RETURN"))
    private void onLivingUpdate(CallbackInfo ci){
        if (this.isUsingItem() && !this.isRiding()) {
          if(ModuleManager.getModulebyClass(NoSlowdown.class).isEnable()) {
              if (NoSlowdown.mode.getMode().equals("NCP")) {
                  movementInput.moveForward *= 0.2F;
                  movementInput.moveStrafe *= 0.2F;
                  this.sprintToggleTimer = 0;
              }
          }
        }
    }
    @Inject(method = "swingItem", at = @At(value =  "HEAD"), cancellable = true)
    private void swingItem(CallbackInfo ci) {
    if(ModuleManager.getModulebyClass(NoSwing.class).isEnable()) {
        ci.cancel();

        if(!NoSwing.enableServerSide.isEnable()){
            sendQueue.addToSendQueue(new C0APacketAnimation());
        }
    }
    }
    @Unique
    public void sendMovePacket(EventMotion event) {
        boolean flag = this.isSprinting();
        if (flag != this.serverSprintState) {
            if (flag) {
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, START_SPRINTING));
            } else {
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, STOP_SPRINTING));
            }

            this.serverSprintState = flag;
        }

        boolean flag1 = this.isSneaking();

        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.connection.sendPacket(new C0BPacketEntityAction(mc.thePlayer, START_SNEAKING));
            } else {
                this.connection.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = flag1;
        }

        if (this.isCurrentViewEntity()) {
            double d0 = event.x - this.lastReportedPosX;
            double d1 = event.y - this.lastReportedPosY;
            double d2 = event.z - this.lastReportedPosZ;
            double d3 = (double) (event.yaw - this.lastReportedYaw);
            double d4 = (double) (event.pitch - this.lastReportedPitch);
            ++this.positionUpdateTicks;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            if (mc.thePlayer.ridingEntity != null) {
                this.connection.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, this.motionZ, event.yaw, event.pitch, event.onGround));
                flag2 = false;
            } else if (flag2 && flag3) {
                this.connection.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
            } else if (flag2) {
                this.connection.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(event.x, event.y, event.z, event.onGround));

            } else if (flag3) {
                this.connection.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(event.yaw, event.pitch, event.onGround));
            } else if (this.prevOnGround != event.onGround) {
                this.connection.sendPacket(new C03PacketPlayer(event.onGround));
            }

            if (flag2) {
                this.lastReportedPosX = event.x;
                this.lastReportedPosY = event.y;
                this.lastReportedPosZ = event.z;
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = event.yaw;
                this.lastReportedPitch = event.pitch;
            }

            this.prevOnGround = event.onGround;
        }
    }

}
