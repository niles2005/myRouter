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
		long t0 = System.currentTimeMillis();
		File srcFile = new File("test.xmd");
		File destFile = new File("test.rmd");
		new EncodeDataFile(srcFile, destFile);
		long t1 = System.currentTimeMillis();
		System.err.println("use time:" + (t1 - t0));
	}
}
