package com.keyue.qlm.util;

public class ComputeDistance {
	
	private static  double EARTH_RADIUS = 6370996.81;
	private static double pi=Math.PI;
	
	private static double rad(double d)
		{
	    return d * Math.PI / 180.0;
	}
	/**
	 * 计算2点之间的距离
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static  double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
		double s= EARTH_RADIUS*Math.acos(Math.cos(lat1*pi/180 )*Math.cos(lat2*pi/180)*Math.cos(lng1*pi/180 -lng2*pi/180)+
				Math.sin(lat1*pi/180 )*Math.sin(lat2*pi/180));
	   return s;
	}

}
