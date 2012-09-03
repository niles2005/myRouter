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
	
//	private ArrayList<RouteLine> m_routeLineList = new ArrayList<RouteLine>();
//	
//	public void addRouteLine(RouteLine routeLine) {
//		m_routeLineList.add(routeLine);
//	}
	
	private RouteLine m_routeLine = null;
	
	public void setRouteLine(RouteLine routeLine) {
		m_routeLine = routeLine;
	}
	
	/**
	 * return json format result
	 */
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{");
		
//		strBuff.append("\"poi\":[{\"type\":\"jyz\",\"name\":\"加油站\",\"items\":[{\"name\":\"石化加油站\",\"pos\":\"31.233220,121.453742\"},{\"name\":\"石化加油站\",\"pos\":\"31.227349,121.474341\"},{\"name\":\"红星加油站\",\"pos\":\"31.245549,121.481894\"},{\"name\":\"加油站\",\"pos\":\"31.237917,121.511420\"},{\"name\":\"加油站\",\"pos\":\"31.213843,121.517600\"}]},{\"type\":\"sfz\",\"name\":\"收费站\",\"items\":[{\"name\":\"三阳收费站\",\"pos\":\"31.215018,121.489447\"},{\"name\":\"王铁收费站\",\"pos\":\"31.257290,121.491507\"},{\"name\":\"丁里收费站\",\"pos\":\"31.240266,121.467474\"},{\"name\":\"永康收费站\",\"pos\":\"31.227936,121.511420\"},{\"name\":\"十八岗收费站\",\"pos\":\"31.230872,121.446188\"}]},{\"type\":\"fwq\",\"name\":\"服务区\",\"items\":[{\"name\":\"谭家桥服务区\",\"pos\":\"31.233220,121.480521\"},{\"name\":\"长安服务区\",\"pos\":\"31.229110,121.461981\"},{\"name\":\"龙坞服务区\",\"pos\":\"31.215605,121.483267\"},{\"name\":\"德清服务区\",\"pos\":\"31.225000,121.518973\"},{\"name\":\"大隐服务区\",\"pos\":\"31.248484,121.548499\"}]}],");
		
		
		strBuff.append("\"startPos\":\"");
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
			strBuff.append(m_stopNode.getRoadEnd().getLatLonString() + "\"");
		}
		
		if(this.m_errorMessage != null) {
			strBuff.append(",\"error\":\"");
			strBuff.append(m_errorMessage);
			strBuff.append("\"");
		}
		
		if(m_routeLine != null) {
			m_routeLine.buildJSONResult(strBuff);
		}
		
//		strBuff.append("\",\"result\":[");
//		if(m_routeLineList.size() > 0) {
//			for(int i=0;i<m_routeLineList.size();i++) {
//				RouteLine routeLine = m_routeLineList.get(i);
//				if(i > 0) {
//					strBuff.append(",");
//				}
//				routeLine.buildJSONResult(strBuff,i);
//			}
//		}
//		strBuff.append("]");

		strBuff.append("}");
		return strBuff.toString();
	}
	
}
