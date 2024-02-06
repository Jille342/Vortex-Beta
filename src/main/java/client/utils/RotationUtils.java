/*
* This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Optional;

public final class RotationUtils implements MCUtil {

    public static final double DEG_TO_RAD = Math.PI / 180.0;

    public static final double RAD_TO_DEG = 180.0 / Math.PI;

    private RotationUtils() {}
    private static float serverYaw;
    private static float serverPitch;
    public static float[] serverRotations = new float[2];



    public static boolean fov(Entity entity, double fov) {
        fov = (fov * 0.5);
        double v = ((double)(Minecraft.getMinecraft().thePlayer.rotationYaw - fovToEntity(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
        return v > 0.0D && v < fov || -fov < v && v < 0.0D;
    }
    public static float[] getNeededFacing(Vec3 target, Vec3 from) {
        double diffX = target.xCoord - from.xCoord;
        double diffY = target.yCoord - from.yCoord;
        double diffZ = target.zCoord - from.zCoord;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[] { MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch) };
    }


    public static float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
            } else
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(Minecraft.getMinecraft().thePlayer.rotationYaw - (float) yawToEntity));
    }
    public static void fixedSensitivity(float[] rotations, float sens) {
        float f = sens * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        rotations[0] = rotations[0] - rotations[0] % gcd;
        rotations[1] = rotations[1] - rotations[1] % gcd;
    }
      public static float fovToEntity(Entity ent) {
        double x = ent.posX - Minecraft.getMinecraft().thePlayer.posX;
        double z = ent.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float) (yaw * -1.0D);
    }
    public static float[] limitAngleChange(float[] currentRotation, float[] targetRotation, float turnSpeed) {
        float yawDifference = getAngleDifference(targetRotation[0], currentRotation[0]);
        float pitchDifference = getAngleDifference(targetRotation[1], currentRotation[1]);
        return new float[] { currentRotation[0] + ((yawDifference > turnSpeed) ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
                currentRotation[1] + ((pitchDifference > turnSpeed) ? turnSpeed : Math.max(pitchDifference, -turnSpeed)) };
    }
    private static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
    }
    public static Vec3 getEyesPos()
    {
        return new Vec3(Minecraft.getMinecraft().thePlayer.posX,
                Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight(),
                Minecraft.getMinecraft().thePlayer.posZ);
    }
    public static Vec3 getServerLookVec()
    {
        float f = MathHelper.cos(-serverYaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-serverYaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-serverPitch * 0.017453292F);
        float f3 = MathHelper.sin(-serverPitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }
    public static boolean faceVectorClient(Vec3 vec)
    {
        float[] rotations = getNeededRotations(vec);

        float oldYaw = Minecraft.getMinecraft().thePlayer.prevRotationYaw;
        float oldPitch = Minecraft.getMinecraft().thePlayer.prevRotationPitch;

        Minecraft.getMinecraft().thePlayer.rotationYaw =
                limitAngleChange(oldYaw, rotations[0], 30);
        Minecraft.getMinecraft().thePlayer.rotationPitch = rotations[1];

        return Math.abs(oldYaw - rotations[0])
                + Math.abs(oldPitch - rotations[1]) < 1F;
    }
    private static float[] getNeededRotations(Vec3 vec)
    {
        Vec3 eyesPos = getEyesPos();

        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }
    public static float limitAngleChange(float current, float intended,
                                         float maxChange)
    {
        float change = MathHelper.wrapAngleTo180_float(intended - current);

        change = MathHelper.clamp_float(change, -maxChange, maxChange);

        return MathHelper.wrapAngleTo180_float(current + change);
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double)player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = (double)MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0D / 3.141592653589793D));
        return new float[]{yaw, pitch};
    }
    public static float[] getRotationsEntity(EntityLivingBase entity) {
        return PlayerUtils.isOnHypixel() && PlayerUtils.isMoving() ? getRotations(entity.posX + MathUtils.randomNumber(0.03D, -0.03D), entity.posY + (double)entity.getEyeHeight() - 0.4D + MathUtils.randomNumber(0.07D, -0.07D), entity.posZ + MathUtils.randomNumber(0.03D, -0.03D)) : getRotations(entity.posX, entity.posY + (double)entity.getEyeHeight() - 0.4D, entity.posZ);
    }
    public static float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapAngleTo180_float((float) -(mc.thePlayer.rotationYaw - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapAngleTo180_double(-(mc.thePlayer.rotationYaw - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }
    public static float[] getRotationsAAC(EntityLivingBase entity) {
        float rotationPitch = RandomUtils.nextFloat(90.0F, 92.0F);
        float rotationYaw = RandomUtils.nextFloat(rotationPitch, 94.0F);
        double posX = entity.posX + (entity.posX - entity.lastTickPosX) * 1.45D - mc.thePlayer.posX - mc.thePlayer.motionX * 1.215D;
        float rotationY2 = RandomUtils.nextFloat(175.0F, 180.0F);
        float rotationY4 = RandomUtils.nextFloat(0.2F, 0.3F);
        float rotationY3 = RandomUtils.nextFloat(rotationY4, 0.1F);
        double posY = entity.posY + 1.45D - mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + mc.thePlayer.getAge() + rotationY3;
        double posZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) * 1.215D - mc.thePlayer.posZ - mc.thePlayer.motionZ * 1.215D;
        double var = Math.hypot(posX, posZ);
        float yaw = (float)(Math.atan2(posZ, posX) * rotationY2 / Math.PI) - rotationYaw;
        float pitch = (float)-(Math.atan2(posY, var) * rotationY2 / Math.PI);
        return new float[] { yaw, pitch + 10.0F };
    }


    public static Rotation calcRotationFromCoords(BlockPos orig, BlockPos dest) {
        return calcRotationFromVec3(new Vec3(orig), new Vec3(dest));
    }

    public static Rotation wrapAnglesToRelative(Rotation current, Rotation target) {
        if (current.yawIsReallyClose(target)) {
            return new Rotation(current.getYaw(), target.getPitch());
        }
        return target.subtract(current).normalize().add(current);
    }

    public static Rotation calcRotationFromVec3(Vec3 orig, Vec3 dest, Rotation current) {
        return wrapAnglesToRelative(current, calcRotationFromVec3(orig, dest));
    }

    private static Rotation calcRotationFromVec3(Vec3 orig, Vec3 dest) {
        double[] delta = {orig.xCoord - dest.xCoord, orig.yCoord - dest.yCoord, orig.zCoord - dest.zCoord};
        double yaw = MathHelper.atan2(delta[0], -delta[2]);
        double dist = Math.sqrt(delta[0] * delta[0] + delta[2] * delta[2]);
        double pitch = MathHelper.atan2(delta[1], dist);
        return new Rotation(
                (float) (yaw * RAD_TO_DEG),
                (float) (pitch * RAD_TO_DEG)
        );
    }

    public static Vec3 calcVec3FromRotation(Rotation rotation) {
        float f = MathHelper.cos(-rotation.getYaw() * (float) DEG_TO_RAD - (float) Math.PI);
        float f1 = MathHelper.sin(-rotation.getYaw() * (float) DEG_TO_RAD - (float) Math.PI);
        float f2 = -MathHelper.cos(-rotation.getPitch() * (float) DEG_TO_RAD);
        float f3 = MathHelper.sin(-rotation.getPitch() * (float) DEG_TO_RAD);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }
    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }
    
    public static Optional<Rotation> reachableOffset(Entity entity, BlockPos pos, Vec3 offsetPos, double blockReachDistance, boolean wouldSneak) {
        /*Vec3 eyes = wouldSneak ? RayTraceUtils.inferSneakingEyePosition(entity) : entity.getPositionEyes(1.0F);
        Rotation rotation = calcRotationFromVec3(eyes, offsetPos, new Rotation(entity.rotationYaw, entity.rotationPitch));
        RayTraceResult result = RayTraceUtils.rayTraceTowards(entity, rotation, blockReachDistance, wouldSneak);
        //System.out.println(result);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (result.getBlockPos().equals(pos)) {
                return Optional.of(rotation);
            }
            if (entity.world.getBlockState(pos).getBlock() instanceof BlockFire && result.getBlockPos().equals(pos.down())) {
                return Optional.of(rotation);
            }
        }
        return Optional.empty();*/
        return null;
    }
}
