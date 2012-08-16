/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time£º2011-9-27 ÏÂÎç04:53:42
 */
package com.xtwsoft.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamSerializable {
	public void readInputStream(InputStream is) throws IOException;

	public void writeOutputStream(OutputStream os) throws IOException;
}
