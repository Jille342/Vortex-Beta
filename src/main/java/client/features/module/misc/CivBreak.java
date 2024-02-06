package client.features.module.misc;

import client.event.Event;
import client.event.listeners.*;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RandomUtils;
import client.utils.RayTraceUtils;
import client.utils.RotationUtils;
import client.utils.render.RenderUtils;
import com.jcraft.jogg.Packet;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CivBreak extends Module {

    public BlockPos pos;
    public EnumFacing side;
    NumberSetting range;

    public float[] rotations;
    public boolean isBreaking;
    public C07PacketPlayerDigging packet;
    public static int ticks;
    ModeSetting mode;
    NumberSetting delay;
    NumberSetting slowdownDelay;
   public static int slowdownticks;
    public CivBreak() {
        super("CivBreak", Keyboard.KEY_NONE, Module.Category.MISC);
    }
    public void init(){
        super.init();
        this.range = new NumberSetting("Range", 5.0, 4.5, 7.0, 0.1);
        mode = new ModeSetting("Mode", "Legit", new String[]{"Legit","Slowdown"});
        delay = new NumberSetting("Delay", 5.0D, 0.0D, 20.0D, 1.0D);
        slowdownDelay = new NumberSetting("Slowdown Delay", 20, 10, 200, 1.0);
        addSetting(range,mode,slowdownDelay,delay);
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventUpdate) {
            setTag(mode.getMode());
        }
        if (e instanceof EventMotion) {

            if (e.isPre()) {
                BlockPos nexus = getNexus();
                if (nexus != null) {
                    this.pos = nexus;
                    this.side = EnumFacing.DOWN;
                }
                if (pos != null) {
                    if (mc.thePlayer.getDistance(this.pos.getX(), this.pos.getY(), this.pos.getZ()) > ((Double) this.range.getValue()).doubleValue()) {
                        this.packet = null;
                        this.isBreaking = false;
                        return;
                    }

                    EventMotion event = (EventMotion) e;
                    Vec3 from = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                    this.rotations = RotationUtils.getNeededFacing(new Vec3(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D), from);
                    MovingObjectPosition raytrace = RayTraceUtils.rayTrace(((Double)this.range.getValue()).doubleValue(), this.rotations[0], this.rotations[1]);
                    event.setYaw(this.rotations[0]);
                    event.setPitch(this.rotations[1]);
                    EnumFacing side = (raytrace.sideHit != null) ? raytrace.sideHit : this.side;
                    if (!this.isBreaking) {
                        this.isBreaking = true;
                        mc.playerController.clickBlock(this.pos, side);
                    }
                    if (!mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown())
                        mc.thePlayer.onGround = true;

                    if (packet == null) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        mc.playerController.onPlayerDamageBlock(this.pos, side);
                    } else {
                        if (mode.getMode().equals("Legit")) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                            mc.playerController.onPlayerDamageBlock(this.pos, side);
                        }
                        if (mode.getMode().equals("Slowdown")) {
                            slowdownticks++;
                            if (slowdownticks<= slowdownDelay.getValue()) {
                                if (ticks >= ((Double) delay.value).doubleValue()) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, EnumFacing.DOWN));
                                    ticks = 0;
                                }
                                ticks += RandomUtils.nextInt(0, 3);
                            } else {
                                slowdownticks =0;
                                packet = null;
                            }
                        }
                    }
                } else {
                    isBreaking = false;
                }
            }
        }
        if (e instanceof EventBreakSlowdown) {
            if (pos != null)
                e.cancel();
        }
        if (e instanceof EventPacket) {
            EventPacket event = (EventPacket) e;
            if (event.isOutgoing() && event.getPacket() instanceof C07PacketPlayerDigging) {
                C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.getPacket();
                if (packet.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
                    this.packet = (C07PacketPlayerDigging) event.getPacket();
            }
        }
        if (e instanceof EventRenderWorld) {
            if (pos != null) {
                GL11.glDisable(2896);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2929);
                GL11.glEnable(2848);
                GL11.glDepthMask(false);
                GL11.glLineWidth(0.3F);
                Double distance = mc.thePlayer.getDistanceSq(pos);
                if (this.pos != null && distance > range.getValue()) {
                    GL11.glColor4f(1.0F, 0.2F, 0.0F, 0.25F);
                } else if (mc.theWorld.getBlockState(this.pos).getBlock() instanceof BlockAir) {
                    GL11.glColor4f(1.0F, 0.7F, 0.0F, 0.25F);
                } else {
                    GL11.glColor4f(0.2F, 0.9F, 0.0F, 0.25F);
                }
                double x = this.pos.getX() - mc.getRenderManager().viewerPosX;
                double y = this.pos.getY() - mc.getRenderManager().viewerPosY;
                double z = this.pos.getZ() - mc.getRenderManager().viewerPosZ;
                AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                RenderUtils.drawFilledBox(box);
                if (this.pos != null && distance > range.getValue()) {
                    GL11.glColor4f(1.0F, 0.2F, 0.0F, 0.4F);
                } else if (mc.theWorld.getBlockState(this.pos).getBlock() instanceof BlockAir) {
                    GL11.glColor4f(1.0F, 0.7F, 0.0F, 0.4F);
                } else {
                    GL11.glColor4f(0.2F, 0.9F, 0.0F, 0.4F);
                }
                RenderGlobal.drawSelectionBoundingBox(box);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDepthMask(true);
                GL11.glDisable(2848);
                GL11.glEnable(2929);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glEnable(3553);
            }
        }
    }

    public BlockPos getNexus() {
        BlockPos pos = null;
        for (int x = -7; x < 7; x++) {
            for (int y = -7; y < 7; y++) {
                for (int z = -7; z < 7; z++) {
                    pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                    if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.end_stone)
                        return pos;
                }
            }
        }
        return this.pos;
    }
    public void onEnable(){
        super.onEnable();
        pos = null;
        packet = null;
        isBreaking = false;
        ticks = 0;
        slowdownticks =0;
    }
}
