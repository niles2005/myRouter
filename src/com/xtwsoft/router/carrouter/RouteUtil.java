/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-4 下午05:59:13
 */
package com.xtwsoft.router.carrouter;

import java.text.DecimalFormat;

import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.GlobalPos;
import com.xtwsoft.utils.gis.AngleUtil;

public class RouteUtil {
	public static final int RouteTypeLength = 0;
	public static final int RouteTypeTime = 1;
	public static String getRouteTypeName(int routeType) {
		if(RouteTypeLength == routeType) {
			return "Length";
		} else if(RouteTypeTime == routeType) {
			return "Time";
		}
		return "Unknown";
	}
	
	//roadMeterLen:  meter
	//speed    km/h 
	//return: time second
	public static int calRouteTime(double roadMeterLen,int speed) {
		int roadTime = (int)(0.001 * roadMeterLen * 3600 / speed);  //公里 / 时速
		return roadTime;
	}

	public static String getCarLengthString(int len) {
		return len + "米";
	}

	public static String getWalkLengthString(int len) {
		return len + "米";
	}
	
	public static String getWalkTimeString(int second) {
		int hour = second / 3600;
		int reseaseSecond = second % 3600;
		int minute = reseaseSecond / 60;
		reseaseSecond = reseaseSecond % 60;
		if(reseaseSecond > 5) {
			minute++;
		}
		if(minute >= 60) {
			hour++;
			minute -= 60;
		}
		if(hour > 0) {
			if(minute <= 0) {
				return hour + "小时";
			} else {
				return hour + "小时" + minute + "分钟";
			}
		} else if(minute > 0) {
			return minute + "分钟";
		} else {
			return "1分钟";
		}
	}

	public static String formatMeterLength(int meterLen) {
		String ss = null;
		if(meterLen >= 1000) {
			ss = myformat.format(1.0 * meterLen / 1000) + "公里";
		} else {
			ss = meterLen + "米";
		}
		return ss;
	}
	
	//城市道路一般10公里/25分钟
	public static String formatSecondTime(int secondTime) {
		
		int hours = secondTime / 3600;
		int releaseMin = (secondTime % 3600) / 60;
		if(hours > 0) {
			if(releaseMin != 0) {
				return "" + hours + "小时" + releaseMin + "分钟";
			} else {
				return "" + hours + "小时";
			}
		} else {
			if(releaseMin == 0) {
				releaseMin = 1;
			}
			return "" + releaseMin + "分钟";
		}
	}
	
	static DecimalFormat myformat = new DecimalFormat("0.0");	
	public static void main1(String[] args) {
		System.err.println(getWalkTimeString(999999999));
	}
	
	public static String UnknownRoadName = "未知路段";

	
	protected String m_angleInfo = null;
	
	//lastRoadRSecondEndGPos ： 上一条路的倒数第二个点
	public static String crateRouteAnage(EarthPos lastRoadRSecondEndEPos,EarthPos roadFirstEPos,EarthPos roadSecondEPos) {
		GlobalPos lastRoadRSecondEndGPos = lastRoadRSecondEndEPos.convert2GlobalPos();
		GlobalPos roadFirstGPos = roadFirstEPos.convert2GlobalPos();
		GlobalPos roadSecondGPos = roadSecondEPos.convert2GlobalPos();
		return AngleUtil.getOrient(lastRoadRSecondEndGPos.posX,lastRoadRSecondEndGPos.posY,roadFirstGPos.posX,roadFirstGPos.posY,roadSecondGPos.posX,roadSecondGPos.posY);
	}
	
}
