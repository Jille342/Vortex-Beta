package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;

/**
 * Created by cool1 on 1/19/2017.
 */
public class TPBreaker extends Module {

    public static BlockPos blockBreaking;
    private double xPos, yPos, zPos, minx;

    ModeSetting mode;
    NumberSetting radius1;
    public TPBreaker() {
        super("TPBreaker", 0, Category.PLAYER);
    }

    @Override
    public void onDisable(){
        blockBreaking = null;
        super.onDisable();
    }

    public void init() {
        mode = new ModeSetting("Mode", "RightClick", new String[]{"RightClick"});
        this.radius1 = new NumberSetting("Radius", 5, 1, 10, 1f);
        addSetting(mode, radius1);
        super.init();

    }
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            setTag(mode.getMode());
            EventUpdate em = (EventUpdate) event;
           if(mode.getMode().equals("RightClick")  && em.isPre()){
                int radius = (int) radius1.getValue();
                for(int x = -radius; x < radius; x++){
                    for(int y = radius; y > -radius; y--){
                        for(int z = -radius; z < radius; z++){
                            this.xPos = mc.thePlayer.posX + x;
                            this.yPos = mc.thePlayer.posY + y;
                            this.zPos = mc.thePlayer.posZ + z;

                            BlockPos blockPos = new BlockPos(this.xPos, this.yPos, this.zPos);
                            Block block = mc.theWorld.getBlockState(blockPos).getBlock();

                            if(block == Blocks.quartz_ore){
                                minx = block.getBlockBoundsMinX();

                                    mc.thePlayer.swingItem();

                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(blockPos, 1, mc.thePlayer.inventory.getCurrentItem(), 1f, 1f,1f));
                                blockBreaking = blockPos;
                                if (event instanceof EventMotion) {
                                    EventMotion emm = (EventMotion) event;
                                    float[] rotations = getBlockRotations(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                                    emm.setYaw(rotations[0]);
                                    emm.setPitch(rotations[1]);
                                }
                                return;
                            }
                        }
                    }
                }
                blockBreaking = null;
            }

        }
    }


    private boolean blockChecks(Block block) {
        return block == Blocks.quartz_ore;
    }

    public float[] getBlockRotations(double x, double y, double z) {
        double var4 = x - mc.thePlayer.posX + 0.5;
        double var5 = z - mc.thePlayer.posZ + 0.5;
        double var6 = y - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 1.0);
        double var7 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
        float var8 = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        return new float[]{var8, (float) (-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793))};
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.UP;
        } else if (!mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.DOWN;
        } else if (!mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.EAST;
        } else if (!mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.WEST;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.SOUTH;
        } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.NORTH;
        }
        MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
        if (rayResult != null && rayResult.getBlockPos() == pos) {
            return rayResult.sideHit;
        }
        return direction;
    }
}