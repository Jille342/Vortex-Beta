package client.features.module.misc;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventRenderWorld;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RotationUtils;
import client.utils.render.RenderUtils;
import com.jcraft.jogg.Packet;
import com.sun.javafx.geom.Vec3d;
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
    ModeSetting mode;
    public CivBreak() {
        super("CivBreak", Keyboard.KEY_NONE, Module.Category.MISC);
    }
    public void init(){
        super.init();
        this.range = new NumberSetting("Range", 5.0, 4.5, 7.0, 0.1);
        mode = new ModeSetting("Mode", "Legit", new String[]{"Legit"});
        addSetting(range,mode);
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setTag(mode.getMode());
        }
        if(e instanceof EventMotion) {

            if(e.isPre()){
                BlockPos nexus = getNexus();
                if (nexus != null) {
                    this.pos = nexus;
                    this.side = EnumFacing.DOWN;
                }
                if(pos != null) {
                    if (mc.thePlayer.getDistance(this.pos.getX(), this.pos.getY(), this.pos.getZ()) > ((Double)this.range.getValue()).doubleValue()) {
                        this.packet = null;
                        this.isBreaking = false;
return;
                    }

                    EventMotion event = (EventMotion) e;
                    Vec3 from = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY+ mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                    this.rotations = RotationUtils.getNeededFacing(new Vec3(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D), from);
                    event.setYaw(this.rotations[0]);
                    event.setPitch(this.rotations[1]);
                    if (!this.isBreaking) {
                        this.isBreaking = true;
                        mc.playerController.clickBlock(this.pos, side);
                    }
                    if(packet == null) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        mc.playerController.onPlayerDamageBlock(this.pos, side);
                    } else {
                        if(mode.getMode().equals("Legit")) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                            mc.playerController.onPlayerDamageBlock(this.pos, side);
                        }
                    }
                }
            }
        }
        if(e instanceof EventRenderWorld){
            Color c = new Color(240);
            float[] colors = { c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F };
            if (this.pos != null) {
                GL11.glDisable(2896);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2929);
                GL11.glEnable(2848);
                GL11.glDepthMask(false);
                GL11.glLineWidth(1.0F);
                if (this.pos != null && MathHelper.sqrt_double(mc.thePlayer.getDistanceSq(this.pos)) > ((Double)this.range.getValue()).doubleValue()) {
                    GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
                } else if (mc.theWorld.getBlockState(this.pos).getBlock() instanceof net.minecraft.block.BlockAir) {
                    GL11.glColor4f(colors[0], colors[1], colors[2], 1.0F);
                } else {
                    GL11.glColor4f(colors[0], colors[1], colors[2], 1.0F);
                }
                double var10000 = this.pos.getX();
                mc.getRenderManager();
                double var17 = var10000 - mc.getRenderManager().viewerPosX;
                var10000 = this.pos.getY();
                mc.getRenderManager();
                double y = var10000 - mc.getRenderManager().viewerPosY;
                var10000 = this.pos.getZ();
                mc.getRenderManager();
                double z = var10000 - mc.getRenderManager().viewerPosZ;
                double xo = 1.0D;
                double yo = 1.0D;
                double zo = 1.0D;
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(var17, y, z, var17 + xo, y + yo, z + zo));
                if (this.pos != null && MathHelper.sqrt_double(mc.thePlayer.getDistanceSq(this.pos)) > ((Double)this.range.getValue()).doubleValue()) {
                    GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.11F);
                } else if (mc.theWorld.getBlockState(this.pos).getBlock() instanceof net.minecraft.block.BlockAir) {
                    GL11.glColor4f(colors[0], colors[1], colors[2], 0.11F);
                } else {
                    GL11.glColor4f(colors[0], colors[1], colors[2], 0.11F);
                }
                RenderUtils.drawFilledBox(new AxisAlignedBB(var17, y, z, var17 + xo, y + yo, z + zo));
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
}
