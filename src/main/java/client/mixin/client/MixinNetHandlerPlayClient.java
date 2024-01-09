package client.mixin.client;

import client.Client;
import client.event.EventDirection;
import client.event.EventType;
import client.event.listeners.EventHandleTeleport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

     Minecraft client;
    @Shadow NetworkManager netManager;
    @Shadow boolean doneLoadingTerrain;

    @Inject(method = "handlePlayerPosLook", at = @At("HEAD"), cancellable = true)
    public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn, CallbackInfo ci)
    {
        EventHandleTeleport e = new EventHandleTeleport(packetIn);
        e.setDirection(EventDirection.INCOMING);
        e.setType(EventType.PRE);
        Client.onEvent(e);

        if (e.isCancellTeleporting() || e.isCancelled()) {
            ci.cancel();
            EntityPlayer entityplayer = this.client.thePlayer;
            double d0 = packetIn.getX();
            double d1 = packetIn.getY();
            double d2 = packetIn.getZ();
            float f = packetIn.getYaw();
            float f1 = packetIn.getPitch();

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X))
            {
                d0 += entityplayer.posX;
            }
            else
            {
                entityplayer.motionX = 0.0D;
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y))
            {
                d1 += entityplayer.posY;
            }
            else
            {
                entityplayer.motionY = 0.0D;
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z))
            {
                d2 += entityplayer.posZ;
            }
            else
            {
                entityplayer.motionZ = 0.0D;
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT))
            {
                f1 += entityplayer.rotationPitch;
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT))
            {
                f += entityplayer.rotationYaw;
            }

            this.netManager.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

            if (!this.doneLoadingTerrain)
            {
                this.client.thePlayer.prevPosX = this.client.thePlayer.posX;
                this.client.thePlayer.prevPosY = this.client.thePlayer.posY;
                this.client.thePlayer.prevPosZ = this.client.thePlayer.posZ;
                this.doneLoadingTerrain = true;
                this.client.displayGuiScreen((GuiScreen)null);
            }
        }
    }

    @Inject(method = "handlePlayerPosLook", at = @At("RETURN"))
    public void PostHandlePlayerPosLook(S08PacketPlayerPosLook packetIn, CallbackInfo ci)
    {
        EventHandleTeleport e = new EventHandleTeleport(packetIn);
        e.setDirection(EventDirection.INCOMING);
        e.setType(EventType.POST);
        Client.onEvent(e);
    }
}
