/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-24 下午03:05:00
 */
package com.xtwsoft.utils;

public class CharUtil {
	public static String toUnicode(String str) {
		StringBuffer strBuff = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if(ch > 256) {
				strBuff.append("\\u" + Integer.toHexString(ch));
			} else {
				strBuff.append(ch);
			}
		}
		return strBuff.toString();
	}
	
	public static String toUnicode(char ch) {
		if(ch > 256) {
			return "\\u" + Integer.toHexString(ch);
		} else {
			return "" + ch;
		}
	}
	
	public static void main(String[] args) {
		System.err.println(CharUtil.toUnicode("开始导航"));
	}
	
}
