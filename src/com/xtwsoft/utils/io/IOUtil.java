/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午09:29:05
 */
package com.xtwsoft.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class IOUtil {
	public static void writeLong(OutputStream os, long v) throws IOException {
		os.write((int) (v >>> 0) & 0xFF);
		os.write((int) (v >>> 8) & 0xFF);
		os.write((int) (v >>> 16) & 0xFF);
		os.write((int) (v >>> 24) & 0xFF);
		os.write((int) (v >>> 32) & 0xFF);
		os.write((int) (v >>> 40) & 0xFF);
		os.write((int) (v >>> 48) & 0xFF);
		os.write((int) (v >>> 56) & 0xFF);
	}

	public static void writeInt(OutputStream os, int v) throws IOException {
		os.write((v >>> 0) & 0xFF);
		os.write((v >>> 8) & 0xFF);
		os.write((v >>> 16) & 0xFF);
		os.write((v >>> 24) & 0xFF);
	}

	public static void writeInt2(OutputStream os, int v) throws IOException {
		os.write((v >>> 0) & 0xFF);
		os.write((v >>> 8) & 0xFF);
	}

	public static void writeShort(OutputStream os, short v) throws IOException {
		os.write((v >>> 0) & 0xFF);
		os.write((v >>> 8) & 0xFF);
	}

	public static void writeByte(OutputStream os, byte v) throws IOException {
		os.write(v);
	}

	public static void writeBoolean(OutputStream os, boolean v) throws IOException {
		if(v) {
			os.write((byte)1);
		} else {
			os.write((byte)0);
		}
	}

	public static void writeByteArray(OutputStream os, byte[] bytes) throws IOException {
		writeInt(os,bytes.length);
		os.write(bytes);
	}

	public static void writeString(OutputStream os, String str) throws IOException {
		byte[] bytes = str.getBytes("UTF-8");
		writeInt(os,(short)bytes.length);
		os.write(bytes);
	}

	public static long readLong(byte[] datas, int pos) throws IOException {
		return ((datas[pos++] & 0xFF) | ((datas[pos++] & 0xFF) << 8)
				| ((datas[pos++] & 0xFF) << 16)
				| ((long) (datas[pos++] & 0xFF) << 24)
				| ((long) (datas[pos++] & 0xFF) << 32)
				| ((long) (datas[pos++] & 0xFF) << 40)
				| ((long) (datas[pos++] & 0xFF) << 48) | ((long) (datas[pos++] & 0xFF) << 56));
	}

	public static int readInt(byte[] datas, int pos) throws IOException {
		return ((datas[pos++] & 0xFF) | ((datas[pos++] & 0xFF) << 8)
				| ((datas[pos++] & 0xFF) << 16) | ((datas[pos++] & 0xFF) << 24));
	}

	public static int readInt2(byte[] datas, int pos) throws IOException {
		return ((datas[pos++] & 0xFF) | ((datas[pos++] & 0xFF) << 8));
	}

	public static short readShort(byte[] datas, int pos) throws IOException {
		return (short) ((datas[pos++] & 0xFF) | ((datas[pos++] & 0xFF) << 8));
	}

	public static byte readByte(byte[] datas, int pos) throws IOException {
		return datas[pos];
	}
	
	public static boolean readBoolean(byte[] datas, int pos) throws IOException {
		byte b = datas[pos];
		return b == 1;
	}

	public static byte[] readByteArray(byte[] datas, int pos) throws IOException {
		int len = readInt(datas, pos);
		byte[] bytes = new byte[len];
		System.arraycopy(datas, pos + 4, bytes, 0, len);
		return bytes;
	}

	public static String readString(byte[] datas, int pos) throws IOException {
		short len = readShort(datas, pos);
		byte[] bytes = new byte[len];
		System.arraycopy(datas, pos + 2, bytes, 0, len);
		return new String(bytes,"UTF-8");
	}
	
	public static long readLong(InputStream is) throws IOException {
		byte b1 = (byte)is.read();
		byte b2 = (byte)is.read();
		byte b3 = (byte)is.read();
		byte b4 = (byte)is.read();
		byte b5 = (byte)is.read();
		byte b6 = (byte)is.read();
		byte b7 = (byte)is.read();
		byte b8 = (byte)is.read();
		return ((b1 & 0xFF) | ((b2 & 0xFF) << 8)
				| ((b3 & 0xFF) << 16)
				| ((long) (b4 & 0xFF) << 24)
				| ((long) (b5 & 0xFF) << 32)
				| ((long) (b6 & 0xFF) << 40)
				| ((long) (b7 & 0xFF) << 48) | ((long) (b8 & 0xFF) << 56));
	}
	
	public static int readInt(InputStream is) throws IOException {
		byte b1 = (byte)is.read();
		byte b2 = (byte)is.read();
		byte b3 = (byte)is.read();
		byte b4 = (byte)is.read();
		return ((b1 & 0xFF) | ((b2 & 0xFF) << 8)
				| ((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24));
	}

	public static int readInt2(InputStream is) throws IOException {
		byte b1 = (byte)is.read();
		byte b2 = (byte)is.read();
		return ((b1 & 0xFF) | ((b2 & 0xFF) << 8));
	}

	public static short readShort(InputStream is) throws IOException {
		byte b1 = (byte)is.read();
		byte b2 = (byte)is.read();
		return (short) ((b1 & 0xFF) | ((b2 & 0xFF) << 8));
	}
	
	public static byte readByte(InputStream is) throws IOException {
		return (byte)is.read();
	}

	public static boolean readBoolean(InputStream is) throws IOException {
		int b = is.read();
		return b == 1;
	}
	
	public static byte[] readByteArray(InputStream is) throws IOException {
		int len = readInt(is);
		byte[] bytes = new byte[len];
		is.read(bytes);
		return bytes;
	}
	
	public static String readString(InputStream is) throws IOException {
		int len = readInt(is);
		byte[] bytes = new byte[len];
		is.read(bytes);
		return new String(bytes,"UTF-8");
	}
	

	public static long readLong(RandomAccessFile raf, int pos)
			throws IOException {
		byte[] datas = new byte[8];
		raf.seek(pos);
		raf.read(datas);
		return ((datas[0] & 0xFF) | ((datas[1] & 0xFF) << 8)
				| ((datas[2] & 0xFF) << 16) | ((long) (datas[3] & 0xFF) << 24)
				| ((long) (datas[4] & 0xFF) << 32)
				| ((long) (datas[5] & 0xFF) << 40)
				| ((long) (datas[6] & 0xFF) << 48) | ((long) (datas[7] & 0xFF) << 56));
	}

	public static int readInt(RandomAccessFile raf, int pos) throws IOException {
		byte[] datas = new byte[4];
		raf.seek(pos);
		raf.read(datas);
		return ((datas[0] & 0xFF) | ((datas[1] & 0xFF) << 8)
				| ((datas[2] & 0xFF) << 16) | ((datas[3] & 0xFF) << 24));
	}

	public static int readInt2(RandomAccessFile raf, int pos) throws IOException {
		byte[] datas = new byte[2];
		raf.seek(pos);
		raf.read(datas);
		return ((datas[0] & 0xFF) | ((datas[1] & 0xFF) << 8));
	}

	public static short readShort(RandomAccessFile raf, int pos)
			throws IOException {
		byte[] datas = new byte[2];
		raf.seek(pos);
		raf.read(datas);
		return (short) ((datas[0] & 0xFF) | ((datas[1] & 0xFF) << 8));
	}

	public static byte readByte(RandomAccessFile raf, int pos)
			throws IOException {
		raf.seek(pos);
		return raf.readByte();
	}

	public static boolean readBoolean(RandomAccessFile raf, int pos) throws IOException {
		raf.seek(pos);
		byte b = raf.readByte();
		return b == 1;
	}
	
	public static byte[] readByteArray(RandomAccessFile raf, int pos)
			throws IOException {
		int len = readInt(raf, pos);
		
		byte[] bytes = new byte[len];
		raf.seek(pos + 4);
		raf.read(bytes);
		return bytes;
	}

	public static String readString(RandomAccessFile raf, int pos) throws IOException {
		short len = readShort(raf, pos);
		byte[] bytes = new byte[len];
		raf.seek(pos + 2);
		raf.read(bytes);
		return new String(bytes,"UTF-8");
	}
	
}
