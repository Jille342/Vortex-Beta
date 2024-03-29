package client.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
	
	public static double getDistanceSq(double[] vec) {
		double dist=0;
		
		for(double d : vec) {
			dist += Math.pow(d, 2);
		}
		
		return Math.sqrt(dist);
	}
	
	public static double getDistanceAd(double[] vec) {
		double dist=0;
		
		for(double d : vec) {
			dist += d;
		}
		
		return dist;
	}

	public static double round(double num, double increment) {
		if (increment < 0.0D) {
			throw new IllegalArgumentException();
		} else {
			BigDecimal bd = new BigDecimal(num);
			bd = bd.setScale((int)increment, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}

	public static double randomNumber(double max, double min) {
		return Math.random() * (max - min) + min;
	}
}
