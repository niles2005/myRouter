package com.xtwsoft.router.carrouter.test;

import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.GlobalPos;
import com.xtwsoft.utils.gis.EarthPosLengthUtil;

public class TestLength {

	public static void main(String[] args) {
		EarthPos ePos1 = new EarthPos(121.395076,31.168688);
		EarthPos ePos2 = new EarthPos(121.397222,31.168688);
		
		double length = EarthPosLengthUtil.getMeterLength(ePos1, ePos2);
		System.err.println(length);
		GlobalPos gPos1 = ePos1.convert2GlobalPos();
		GlobalPos gPos2 = ePos2.convert2GlobalPos();
		System.err.println((gPos2.posX - gPos1.posX));
	}

}
