/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-24 下午02:57:26
 */
package com.xtwsoft.router.carrouter.test;

public class TestUTF8 {

	public static void main(String[] args) {
		String ss = "汉字";
		System.err.println(ss);
		try {
			System.err.println(java.net.URLEncoder.encode(ss, "unicode"));
			String s="\\u"+Integer.toHexString('寻');
			System.err.println(s);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
