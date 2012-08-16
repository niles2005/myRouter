/**
 * 
 *
 * History:
 *   Jun 1, 2007 1:00:00 PM Created by NieLei
 */
package com.xtwsoft.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Jun 1, 2007 1:00:00 PM
 *
 */
public class XtwFileReader {
	private BufferedReader m_reader = null;
	private String m_charsetName = null;
	public XtwFileReader(String filePath,String charsetName) throws IOException {
		this(new File(filePath),charsetName);
	}
	
	public XtwFileReader(File file,String charsetName) throws IOException {
		if(file == null || !file.exists() || !file.isFile()) {
			return;
		}
		m_charsetName = charsetName;
//		System.err.println("read charsetName:" + charsetName);
		m_reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charsetName));
	}

	public XtwFileReader(InputStream in,String charsetName) throws IOException {
		if(in == null) {
			return;
		}
		m_charsetName = charsetName;
//		System.err.println("read charsetName:" + charsetName);
		m_reader = new BufferedReader(new InputStreamReader(in,charsetName));
	}

	public String readLine() throws IOException {
		return m_reader.readLine();	
	}
	
	public void close() throws IOException {
		m_reader.close();
	}
	
	public String getCharsetName() {
		return m_charsetName;
	}
}
