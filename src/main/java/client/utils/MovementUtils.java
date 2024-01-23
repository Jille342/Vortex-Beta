package client.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.MathHelper;


public class MovementUtils {

	protected static Minecraft mc = Minecraft.getMinecraft();

	public static Vec3 getMotionVector() {
		return new Vec3(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
	}

	public static void setMotionVector(Vec3 vec) {
		mc.thePlayer.motionX = vec.xCoord;
		mc.thePlayer.motionY = vec.yCoord;
		mc.thePlayer.motionZ = vec.zCoord;
	}
	public static void Strafe(double d) {
		float Forward = (mc.gameSettings.keyBindForward.isKeyDown()?1:0)-(mc.gameSettings.keyBindBack.isKeyDown()?1:0);
		float Strafing = (mc.gameSettings.keyBindRight.isKeyDown()?1:0)-(mc.gameSettings.keyBindLeft.isKeyDown()?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(mc.thePlayer.rotationYaw);

		if(Forward==0&&Strafing==0) {d=0;}

		mc.thePlayer.motionX=0;
		mc.thePlayer.motionZ=0;

		mc.thePlayer.motionX=Math.sin(r)*d;
		mc.thePlayer.motionZ=Math.cos(r)*d;
	}
	public static void setSpeed(double moveSpeed) {
		setSpeed(moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
	}
	public static double getJumpBoostModifier(double baseJumpHeight) {
		if (mc.thePlayer.isPotionActive(Potion.jump)) {
			int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
			baseJumpHeight += (double)((float)(amplifier + 1) * 0.1F);
		}

		return baseJumpHeight;
	}
	public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		mc.thePlayer.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
		mc.thePlayer.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
	}


	public static void setPosition(double x, double y, double z) {
		mc.thePlayer.setPosition(x, y, z);
	}

	public static void setPosition(BlockPos pos) {
		mc.thePlayer.setPosition(pos.getX()+.5, pos.getY(), pos.getZ()+.5);
	}

	public static void clip(double x, double y, double z) {
		mc.thePlayer.setPosition(mc.thePlayer.posX+x, mc.thePlayer.posY+y, mc.thePlayer.posZ+z);
	}

	public static void yClip(double y) {
		mc.thePlayer.setPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ);
	}

	public static void vClip(double d) {
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ);
	}

	public static int InputY() {
		return (mc.thePlayer.movementInput.jump ? 1 : 0) + (mc.thePlayer.movementInput.sneak ? -1 : 0);
	}


	public static double getSpeed() {
		if (mc.thePlayer == null) return 0;
		return Math.sqrt(Math.pow(mc.thePlayer.motionX, 2)+Math.pow(mc.thePlayer.motionZ, 2));
	}

	public static double toRadian(double d) {
		return d * Math.PI / 180;
	}


	public static void strafe(float r) {
		mc.thePlayer.motionX=Math.sin(r)*getSpeed();
		mc.thePlayer.motionZ=Math.cos(r)*getSpeed();
	}


	public static void clip() {
		mc.thePlayer.setPosition(mc.thePlayer.posX+mc.thePlayer.motionX, mc.thePlayer.posY+mc.thePlayer.motionY, mc.thePlayer.posZ+mc.thePlayer.motionZ);
	}

	public static void freeze() {
		mc.thePlayer.motionX=0;
		mc.thePlayer.motionY=0;
		mc.thePlayer.motionZ=0;
	}

	public static boolean isMoving() {
		float moveForward = mc.thePlayer.moveForward;
		float Strafing = mc.thePlayer.moveForward;
		return moveForward!=0||Strafing!=0;
	}
	public static float getBaseSpeed() {
		float baseSpeed = 0.2873F;
		if (mc.thePlayer.isPotionActive(Potion.getPotionFromResourceLocation(String.valueOf(1)))) {
			int amp = mc.thePlayer.getActivePotionEffect(Potion.getPotionFromResourceLocation(String.valueOf(1))).getAmplifier();
			baseSpeed *= 1.0F + 0.2F * (amp + 1);
		}
		return baseSpeed;
	}
	public static double InputX() {
		if (!isMoving()) return 0;
		float Forward = (mc.gameSettings.keyBindForward.isKeyDown()?1:0)-(mc.gameSettings.keyBindBack.isPressed()?1:0);
		float Strafing = (mc.gameSettings.keyBindRight.isKeyDown()?1:0)-(mc.gameSettings.keyBindLeft.isPressed()?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(mc.thePlayer.rotationYaw);
		return Math.sin(r);
	}

	public static double InputZ() {
		if (!isMoving()) return 0;
		float Forward = (mc.gameSettings.keyBindForward.isKeyDown()?1:0)-(mc.gameSettings.keyBindBack.isPressed()?1:0);
		float Strafing = (mc.gameSettings.keyBindRight.isKeyDown()?1:0)-(mc.gameSettings.keyBindLeft.isPressed()?1:0);

		double r = Math.atan2(Forward, Strafing)-1.57079633-toRadian(mc.thePlayer.rotationYaw);
		return Math.cos(r);
	}

	public static double nextY(double y) {
		return (y - .08D) * .9800000190734863D;
	}

	public static double nextSpeed(double d) {
		return d*.9900000095367432D;
	}

}