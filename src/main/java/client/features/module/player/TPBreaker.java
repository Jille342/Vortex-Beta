package client.features.module.player;

import java.util.ArrayList;
import java.util.List;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.utils.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import tv.twitch.chat.Chat;

/**
 * Created by cool1 on 1/19/2017.
 */
public class TPBreaker extends Module {

    public static BlockPos blockBreaking;
    List<BlockPos> tps = new ArrayList<>();
    BlockPos closest = null;

    public TPBreaker() {
        super("TPBreaker", 0,Category.PLAYER);
    }

    @Override
    public void onDisable(){
        if(blockBreaking != null)
            blockBreaking = null;
        super.onDisable();
    }
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            EventUpdate em = (EventUpdate) event;
            if (em.isPre()) {
                int reach = 6;
                for (int y = reach; y >= -reach; --y) {
                    for (int x = -reach; x <= reach; ++x) {
                        for (int z = -reach; z <= reach; ++z) {
                            BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                            if ( blockChecks(mc.theWorld.getBlockState(pos).getBlock()) && mc.thePlayer.getDistance(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z) < mc.playerController.getBlockReachDistance() - 0.2) {
                                if(!tps.contains(pos))
                                    tps.add(pos);

                            }

                        }
                    }
                }

                if(!tps.isEmpty())
                    for(int i = 0; i < tps.size(); i++){
                        BlockPos tp = tps.get(i);
                        if(mc.thePlayer.getDistance(tp.getX(), tp.getY(), tp.getZ()) > mc.playerController.getBlockReachDistance() - 0.2
                                || mc.theWorld.getBlockState(tp).getBlock() != Blocks.quartz_ore){
                            tps.remove(i);
                        }
                        if(closest == null || mc.thePlayer.getDistance(tp.getX(), tp.getY(), tp.getZ()) < mc.thePlayer.getDistance(closest.getX(), closest.getY(), closest.getZ())){
                            closest = tp;
                        }
                    }



                blockBreaking = null;

            } else {
                if (blockBreaking != null) {

                    mc.thePlayer.swingItem();
                    EnumFacing direction = getClosestEnum(blockBreaking);
                    if (direction != null) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(blockBreaking, 1, mc.thePlayer.inventory.getCurrentItem(), 1f, 1f,1f));
                    }
                }
            }
            if(event instanceof EventMotion){
                EventMotion emm = (EventMotion) event;
                if(closest != null){


                    float[] rot = getRotations(closest, getClosestEnum(closest));
                    emm.setYaw(rot[0]);
                    emm.setPitch(rot[1]);
                            //mc.thePlayer.rotationYaw = rot[0];
                            // mc.thePlayer.rotationPitch = rot[1];
                            blockBreaking = closest;
                    return;
                }
            }
        }

    }





    private boolean blockChecks(Block block) {
        return block == Blocks.quartz_ore;
    }

    public static float[] getRotations(BlockPos block, EnumFacing face){
        double x = block.getX() + 0.5 - mc.thePlayer.posX + (double)face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double)face.getFrontOffsetZ()/2;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() -(block.getY() + 0.5);
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
        if(yaw < 0.0F){
            yaw += 360f;
        }
        return  new float[]{yaw, pitch};
    }

    private EnumFacing getClosestEnum(BlockPos pos){
        EnumFacing closestEnum = EnumFacing.UP;
        float rotations = MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[0]);
        if(rotations >= 45 && rotations <= 135){
            closestEnum = EnumFacing.EAST;
        }else if((rotations >= 135 && rotations <= 180) ||
                (rotations <= -135 && rotations >= -180)){
            closestEnum = EnumFacing.SOUTH;
        }else if(rotations <= -45 && rotations >= -135){
            closestEnum = EnumFacing.WEST;
        }else if((rotations >= -45 && rotations <= 0) ||
                (rotations <= 45 && rotations >= 0)){
            closestEnum = EnumFacing.NORTH;
        }
        if (MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) > 75 ||
                MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) < -75){
            closestEnum = EnumFacing.UP;
        }
        return closestEnum;
    }
}