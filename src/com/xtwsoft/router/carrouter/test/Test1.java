/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-6 下午10:40:17
 */
package com.xtwsoft.router.carrouter.test;

import java.io.File;

import com.xtwsoft.router.RouteResult;
import com.xtwsoft.router.carrouter.CarRouter;
import com.xtwsoft.router.carrouter.RouteUtil;
import com.xtwsoft.utils.EarthPos;

public class Test1 {
	public Test1() {
		File routerDataFile = new File("D:\\mywork\\SHYT\\datas\\routeData\\test.rmd");
		
		if (!routerDataFile.exists()) {
			return;
		}		
		CarRouter.initInstance(routerDataFile);
		
//		//起点在交叉点上
//		EarthPos startEPos = new EarthPos(118.776211,32.042133);
////		EarthPos endEPos = new EarthPos(118.781919,32.041706);
//		EarthPos endEPos = new EarthPos(118.776597,32.043506);
		
		
		
////		//尾点在交叉点上
////		EarthPos startEPos = new EarthPos(118.779962,32.044843);
//		EarthPos startEPos = new EarthPos(118.772095,32.043627);
//		EarthPos endEPos = new EarthPos(118.776211,32.042133);
		

//		//首尾点相同
//		EarthPos startEPos = new EarthPos(118.776211,32.042133);
//		EarthPos endEPos = new EarthPos(118.776211,32.042133);

//		//首尾点不在交叉点上，但相同
//		EarthPos startEPos = new EarthPos(118.777104,32.044692);
//		EarthPos endEPos = new EarthPos(118.777104,32.044692);

		//首尾点在一条road上
//		EarthPos startEPos = new EarthPos(118.782763,32.040243);
//		EarthPos endEPos = new EarthPos(118.782370,32.040356);
		
		
		//长距离
//		EarthPos startEPos = new EarthPos(119.040094,31.652118);
//		EarthPos endEPos = new EarthPos(118.832000,32.326716);


		//多岔路
//		EarthPos startEPos = new EarthPos(118.781371,32.040842);
//		EarthPos endEPos = new EarthPos(118.7798713,32.0359184);

//		EarthPos startEPos = new EarthPos(121.4616381,31.2255070);
//		EarthPos endEPos = new EarthPos(121.4669817,31.2232933);

		//含加油站POI
		EarthPos startEPos = new EarthPos(1192996705,320534290);
		EarthPos endEPos = new EarthPos(1202689001,316886222);
		long t0 = System.currentTimeMillis();
		RouteResult result = CarRouter.getInstance().doRoute(startEPos, endEPos, RouteUtil.RouteTypeLength);
		
		long t1 = System.currentTimeMillis();
		System.err.println("Route By length use time:" + (t1 - t0));
		System.err.println(result.toString());
		
		t0 = t1;
		result = CarRouter.getInstance().doRoute(startEPos, endEPos, RouteUtil.RouteTypeTime);
		
		System.err.println("Route By Time use time:" + (t1 - t0));
		System.err.println(result.toString());
		
	}
	
	public static void main(String[] args) {
		new Test1();
	}
}
