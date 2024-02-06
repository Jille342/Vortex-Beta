package client.utils;

import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RayTraceUtils implements MCUtil {
    public static MovingObjectPosition rayTrace(double blockReachDistance, float yaw, float pitch) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec4 = getVectorForRotation(pitch, yaw);
        Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
        return mc.thePlayer.worldObj.rayTraceBlocks(vec3, vec5, false, false, true);
    }

    protected static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((f1 * f2), f3, (f * f2));
    }

}
