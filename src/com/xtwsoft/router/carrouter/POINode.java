package com.xtwsoft.router.carrouter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.xtwsoft.utils.Bounds;
import com.xtwsoft.utils.io.IOUtil;
import com.xtwsoft.utils.io.Node;

public class POINode extends Node {
	private String m_name = null;
	private String m_id = null;
	private int m_lat = 0;
	private int m_lon = 0;
	private String m_type = null;

	public POINode() {
		super((byte)'P');
	}
		
	public POINode(String index,int lat,int lon,String name,String type) {
		super((byte)'P');
		m_type = type;
		m_id = type + "_" + index;
		m_lat = lat;
		m_lon = lon;
		m_name = name;
		m_bounds = new Bounds();
		m_bounds.expandToInclude(m_lat, m_lon);
	}
	
	public String getType() {
		return m_type;
	}
	
	public String getName() {
		return m_name;
	}

	public int getLat() {
		return m_lat;
	}
	
	public int getLon() {
		return m_lon;
	}
	
	public String getId() {
		return m_id;
	}
	
	public void readInputStream(InputStream is) throws IOException {
		super.readInputStream(is);
		
		m_id = IOUtil.readString(is);
		int pos = m_id.indexOf("_");
		if(pos != -1) {
			m_type = m_id.substring(0,pos);
		}
		m_lat = IOUtil.readInt(is);
		m_lon = IOUtil.readInt(is);
		
		m_name = IOUtil.readString(is);
	}
	
	public void writeOutputStream(OutputStream os) throws IOException {
		super.writeOutputStream(os);

		IOUtil.writeString(os, m_id);
		IOUtil.writeInt(os, m_lat);
		IOUtil.writeInt(os, m_lon);
		
		IOUtil.writeString(os, m_name);
	}	
	
}
