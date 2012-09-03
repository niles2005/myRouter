/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-16 下午01:10:19
 */
package com.xtwsoft.router.carrouter.test;

import java.io.File;

import com.xtwsoft.router.carrouter.build.EncodeDataFile;

public class TestBuildFile {
	public static void main(String[] args) {
		File path = new File("D:\\mywork\\SHYT\\datas\\routeData");
		long t0 = System.currentTimeMillis();
		EncodeDataFile encodeFile = new EncodeDataFile();
		
		encodeFile.loadRPMapPath(path);
		
		encodeFile.workPOIFile(new File(path,"tm_fwq_pat_point.xmd"));
		encodeFile.workPOIFile(new File(path,"tm_jyz_pat_point.xmd"));
		encodeFile.workPOIFile(new File(path,"tm_sfz_pat_point.xmd"));

		encodeFile.workRoadFile(new File(path,"tm_dlzx_aat.xmd"));
		
		encodeFile.saveFile(new File(path,"test.rmd"));
		
		long t1 = System.currentTimeMillis();
		System.err.println("use time:" + (t1 - t0));
	}
}
