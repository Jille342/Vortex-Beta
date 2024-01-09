package client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

import java.util.Comparator;

import javax.annotation.Nullable;

public class TargetUtils {

	protected static Minecraft mc = client.Client.mc;

	public static Entity currentTarget;

	public static boolean thePlayer = true;
	public static boolean Animal = false;


	public static double getDistance(@Nullable Entity target) {
		return currentTarget==null?0:currentTarget.getDistanceToEntity(target==null?mc.thePlayer:target);
	}

	public static boolean canAttack(Vec3 vec, Vec3 pos) {
		boolean flag = mc.theWorld.rayTraceBlocks(vec, pos, false, true, false) == null;
		double d0 = 36.0D;

		if (!flag)
		{
			d0 = 9.0D;
		}
		if (vec.squareDistanceTo(pos) < d0) {
			return true;
		}
		return false;
	}
	//height * 0.85F
}
