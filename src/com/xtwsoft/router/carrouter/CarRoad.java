/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午10:45:37
 */
package com.xtwsoft.router.carrouter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.xtwsoft.utils.Bounds;
import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.gis.EarthPosLengthUtil;
import com.xtwsoft.utils.io.IOUtil;
import com.xtwsoft.utils.io.Node;

public class CarRoad extends Node {
	private String m_name = null;
	private int m_orient = 0;
	private byte[] m_posBytes = null;
	private int m_roadLength = 0;//单位：米
	private int m_roadTime = 0;
	
	public CarRoad() {
	}
	
	public CarRoad(String name,int orient,ArrayList ePosList,int speed) {
		try {
			m_name = name;
			m_orient = orient;
			Bounds bounds = new Bounds();
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        for (int i = 0; i < ePosList.size(); i++) {
	            EarthPos pos = (EarthPos) ePosList.get(i);
	            int lat = pos.getILat();
	            int lon = pos.getILon();
	            bounds.expandToInclude(lat, lon);
	            IOUtil.writeInt(baos, lat);
	            IOUtil.writeInt(baos, lon);
	        }
	        m_roadLength = (int)(EarthPosLengthUtil.getLineLength(ePosList) + 0.5);
	        m_roadTime = RouteUtil.calRouteTime(m_roadLength, speed);
	        this.m_bounds = bounds;
	        m_posBytes = baos.toByteArray();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private ArrayList m_ePosList = null;
	public ArrayList getEPosList() {
		if(m_ePosList == null) {
			try {
				int num = m_posBytes.length / 8;
				ArrayList list = new ArrayList(num);
				int pos = 0;
				for(int i=0;i<num;i++) {
					int lat = IOUtil.readInt(m_posBytes, pos);
					pos += 4;
					int lon = IOUtil.readInt(m_posBytes, pos);
					pos += 4;
					list.add(new EarthPos(lon,lat));
				}
				m_ePosList = list;
				m_posBytes = null;
			} catch(Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return m_ePosList;
	}
	
	public int getLength() {
		return m_roadLength;
	}
	
	public int getTime() {
		return m_roadTime;
	}
	
	public int getOrient() {
		return m_orient;
	}

	public String getName() {
		return m_name;
	}
	
	public String toString() {
		return getName() + ":" + m_orient;
	}
	
	public void readInputStream(InputStream is) throws IOException {
		super.readInputStream(is);
		
		m_orient = IOUtil.readByte(is);
		m_roadLength = IOUtil.readInt(is);
		m_roadTime = IOUtil.readInt(is);
		
		m_name = IOUtil.readString(is);
		m_posBytes = IOUtil.readByteArray(is);
	}
	
	public void writeOutputStream(OutputStream os) throws IOException {
		super.writeOutputStream(os);

		IOUtil.writeByte(os, (byte)this.m_orient);
		IOUtil.writeInt(os, m_roadLength);
		IOUtil.writeInt(os, m_roadTime);
		
		IOUtil.writeString(os, m_name);
		IOUtil.writeByteArray(os, m_posBytes);
	}	
	
}
