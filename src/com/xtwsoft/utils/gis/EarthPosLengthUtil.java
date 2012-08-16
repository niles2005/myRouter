/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time£º2011-8-19 ÉÏÎç10:30:42
 */
package com.xtwsoft.utils.gis;

import java.util.ArrayList;

import com.xtwsoft.utils.EarthPos;

public class EarthPosLengthUtil {
	private static double SCALE_FACTOR = 10000000D;
	private static double arc=Math.PI/180;

	public static double getMeterLength(int latA,int lonA,int latB,int lonB) {
		double c=latA / SCALE_FACTOR * arc;
		double d=lonA / SCALE_FACTOR * arc;
		double e=latB / SCALE_FACTOR * arc;
		double f=lonB / SCALE_FACTOR * arc;
		double g=c-e;
		double h=d-f;
		double i=2*Math.asin(Math.sqrt(Math.pow(Math.sin(g/2),2)+Math.cos(c)*Math.cos(e)*Math.pow(Math.sin(h/2),2)));
		return  i * 6378137;
	}

	public static double getMeterLength(EarthPos ePosA,EarthPos ePosB) {
		return getMeterLength(ePosA.getILat(),ePosA.getILon(),ePosB.getILat(),ePosB.getILon());
	}

	public static double getLineLength(EarthPos[] ePosArray,int index1,int index2) {
		EarthPos ePos0 = ePosArray[index1];
		double len = 0;
		for(int i=index1 + 1;i<=index2;i++) {
			EarthPos ePos1 = ePosArray[i];
			len += getMeterLength(ePos0.getILat(),ePos0.getILon(),ePos1.getILat(),ePos1.getILon());
			ePos0 = ePos1;
		}
		return len;
	}
	
	public static double getLineLength(EarthPos[] ePosArray) {
		EarthPos ePos0 = ePosArray[0];
		double len = 0;
		for(int i=1;i<ePosArray.length;i++) {
			EarthPos ePos1 = ePosArray[i];
			len += getMeterLength(ePos0.getILat(),ePos0.getILon(),ePos1.getILat(),ePos1.getILon());
			ePos0 = ePos1;
		}
		return len;
	}
	
	public static double getLineLength(ArrayList ePosList) {
		EarthPos ePos0 = (EarthPos)ePosList.get(0);
		double len = 0;
		for(int i=1;i<ePosList.size();i++) {
			EarthPos ePos1 = (EarthPos)ePosList.get(i);
			len += getMeterLength(ePos0.getILat(),ePos0.getILon(),ePos1.getILat(),ePos1.getILon());
			ePos0 = ePos1;
		}
		return len;
	}
	
	
}
