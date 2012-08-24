/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-14 下午10:20:23
 */
package com.xtwsoft.router;

import java.util.ArrayList;

import com.xtwsoft.router.carrouter.StartNode;
import com.xtwsoft.router.carrouter.StopNode;
import com.xtwsoft.utils.CharUtil;
import com.xtwsoft.utils.EarthPos;

public class RouteResult {
	private EarthPos m_startEPos;
	private EarthPos m_endEPos;
	
	private String m_errorMessage = null;
	
	private String m_startName;
	private String m_endName;
	
	private int m_routeType;
	
	public RouteResult(EarthPos startEPos,EarthPos endEPos,int routeType) {
		m_startEPos = startEPos;
		m_endEPos = endEPos;
		
		m_routeType = routeType;
		
		if(startEPos == null) {
			m_errorMessage = CharUtil.toUnicode("开始导航经纬度为空！");
		}
		if(endEPos == null) {
			m_errorMessage = CharUtil.toUnicode("目标导航经纬度为空！");
		}

		//首尾点相同
		if(startEPos.getILat() == endEPos.getILat() && startEPos.getILon() == endEPos.getILon()) {
			m_errorMessage = CharUtil.toUnicode("导航起止经纬度相同！");
		}
	}
	
	public void setError(String errorMessage) {
		m_errorMessage = errorMessage;
	}
	
	public String getError() {
		return m_errorMessage;
	}
	
	private StartNode m_startNode = null;
	public void setStartNode(StartNode startNode) {
		m_startNode = startNode;
	}
	
	private StopNode m_stopNode = null;
	public void setStopNode(StopNode stopNode) {
		m_stopNode = stopNode;
	}
	
	public void setStartName(String name) {
		m_startName = name;
	}
	
	public void setEndName(String name) {
		m_endName = name;
	}
	
	private ArrayList<RouteLine> m_routeLineList = new ArrayList<RouteLine>();
	
	public void addRouteLine(RouteLine routeLine) {
		m_routeLineList.add(routeLine);
	}
	
	/**
	 * return json format result
	 */
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{\"startPos\":\"");
		if(m_startEPos != null) {
			strBuff.append(m_startEPos.getLatLonString());
		}
		strBuff.append("\",\"endPos\":\"");
		if(m_endEPos != null) {
			strBuff.append(m_endEPos.getLatLonString());
		}
		
		if(m_startName != null) {
			strBuff.append("\",\"startName\":\"");
			strBuff.append(m_startName);
		}
		if(m_endName != null) {
			strBuff.append("\",\"endName\":\"");
			strBuff.append(m_endName);
		}
		
		if(this.m_startNode != null) {
			strBuff.append("\",\"fromName\":\"");
			strBuff.append(CharUtil.toUnicode(m_startNode.getRoadName()));
			strBuff.append("\",\"fromPos\":\"");
			strBuff.append(m_startNode.getRoadEnd().getLatLonString());
		}
		
		if(this.m_stopNode != null) {
			strBuff.append("\",\"toName\":\"");
			strBuff.append(CharUtil.toUnicode(m_stopNode.getRoadName()));
			strBuff.append("\",\"toPos\":\"");
			strBuff.append(m_stopNode.getRoadEnd().getLatLonString());
		}
		
		if(this.m_errorMessage != null) {
			strBuff.append("\",\"error\":\"");
			strBuff.append(m_errorMessage);
			strBuff.append("\"");
		}
		strBuff.append("\",\"result\":[");
		if(m_routeLineList.size() > 0) {
			for(int i=0;i<m_routeLineList.size();i++) {
				RouteLine routeLine = m_routeLineList.get(i);
				if(i > 0) {
					strBuff.append(",");
				}
				routeLine.buildJSONResult(strBuff,i);
			}
		}
		strBuff.append("]}");

		
		return strBuff.toString();
	}
	
}
