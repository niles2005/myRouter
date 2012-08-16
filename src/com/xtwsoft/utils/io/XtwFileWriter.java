/**
 * 
 *
 * History:
 *   Jun 1, 2007 1:00:00 PM Created by NieLei
 */
package com.xtwsoft.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Jun 1, 2007 1:00:00 PM
 *
 */
public class XtwFileWriter {
	private OutputStreamWriter m_writer = null;
	public XtwFileWriter(String filePath,String charsetName) throws IOException {
		this(new File(filePath),charsetName);
	}
	
	public XtwFileWriter(File file,String charsetName) throws IOException {
		m_writer = new OutputStreamWriter(new FileOutputStream(file),charsetName);
	}

	public XtwFileWriter(File file,String charsetName,boolean append) throws IOException {
		m_writer = new OutputStreamWriter(new FileOutputStream(file,append),charsetName);
	}

	public void writeLine(String str) throws IOException {
		m_writer.write(str);
		m_writer.write("\r\n");
	}
	
	public void write(String str) throws IOException {
		m_writer.write(str);
	}
	
	public void close() throws IOException {
		m_writer.close();
	}

	public void flush() throws IOException {
		m_writer.flush();
	}

}
