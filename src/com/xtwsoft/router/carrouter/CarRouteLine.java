/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-14 下午10:53:09
 */
package com.xtwsoft.router.carrouter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.xtwsoft.router.RouteLine;
import com.xtwsoft.utils.CharUtil;
import com.xtwsoft.utils.EarthPos;

public class CarRouteLine extends RouteLine {
	protected int m_routeTotalLen = 0;
	protected int m_routeTotalTime = 0;
	private int m_routeType = 0;
	private HashSet m_poiHash = new HashSet();
	private CarDataStore m_carDataStore = null;
	
	public CarRouteLine(CarDataStore carDataStore,int routeType,List workNodeList) {
		m_carDataStore = carDataStore;
		m_routeType = routeType;

		ListIterator iters = workNodeList.listIterator();
		WorkNode prevNode = null;
		EarthPos startPos = null;
		int totalLen = 0;
		int totalTime = 0;
		CarRouteSegment currSegment = null;
		while(iters.hasNext()) {
			WorkNode workNode = (WorkNode)iters.next();
			
			if(workNode.m_prevLinker != null) {
				List poiIdList = workNode.m_prevLinker.m_road.getPOINodeIdList();
				if(poiIdList != null) {
					for(int i=0;i<poiIdList.size();i++) {
						m_poiHash.add(poiIdList.get(i));
					}
				}
			}
			
			if(prevNode == null) {
				startPos = workNode.getEPos();
				prevNode = workNode;
			} else {
				CarLinker roadLinker = workNode.m_prevLinker;
				totalLen += roadLinker.m_linkLen;
				totalTime += roadLinker.m_linkTime;
				String roadName = roadLinker.m_road.getName();
				
				if(currSegment == null) {
					currSegment = new CarRouteSegment(prevNode,workNode,roadName,startPos);
					m_segmentList.add(currSegment);
				} else {
					if(roadName.equals(currSegment.getRoadName())) {
						currSegment.addWorkNode(workNode);	
					} else {
						startPos = currSegment.getLastPos();
						currSegment = new CarRouteSegment(prevNode,workNode,roadName,startPos);
						m_segmentList.add(currSegment);
					}
				}
				
				
				prevNode = workNode;
			}
		}
		m_routeTotalLen = totalLen; 
		m_routeTotalTime = totalTime; 
		
	}
	
	public void buildJSONResult(StringBuffer strBuff) {
		if(m_segmentList.size() == 0) {
		} 
		else {
			strBuff.append(",\"routeType\":\"" + RouteUtil.getRouteTypeName(m_routeType) + "\",");
	
			strBuff.append("\"totallen\":\"");
			strBuff.append(CharUtil.toUnicode(RouteUtil.formatMeterLength(m_routeTotalLen)) + "\",");
			strBuff.append("\"totaltime\":\"");
			strBuff.append(CharUtil.toUnicode(RouteUtil.formatSecondTime(m_routeTotalTime)) + "\",");
			
			strBuff.append("\"segments\":");
			strBuff.append("[");
			for(int i=0;i<m_segmentList.size();i++) {
				if(i > 0) {
					strBuff.append(",");
				}
				strBuff.append("{");
				strBuff.append("\"index\":");
				strBuff.append(i + 1);
				strBuff.append(",");
				strBuff.append("\"info\":\"");
				CarRouteSegment segment1 = (CarRouteSegment)m_segmentList.get(i);
				CarRouteSegment segment2 = null;
				if(i < m_segmentList.size() - 1) {
					segment2 = (CarRouteSegment)m_segmentList.get(i + 1);
				}
				strBuff.append(segment1.getSegmentName(segment2));
				strBuff.append("\",");
				strBuff.append("\"points\":\"");
				segment1.buildPosString(strBuff, false,true);
				strBuff.append("\"}");
			}
			strBuff.append("]");
		}
//		strBuff.append("\"poi\":[{\"type\":\"jyz\",\"name\":\"加油站\",\"items\":[{\"name\":\"石化加油站\",\"pos\":\"31.233220,121.453742\"},{\"name\":\"石化加油站\",\"pos\":\"31.227349,121.474341\"},{\"name\":\"红星加油站\",\"pos\":\"31.245549,121.481894\"},{\"name\":\"加油站\",\"pos\":\"31.237917,121.511420\"},{\"name\":\"加油站\",\"pos\":\"31.213843,121.517600\"}]},{\"type\":\"sfz\",\"name\":\"收费站\",\"items\":[{\"name\":\"三阳收费站\",\"pos\":\"31.215018,121.489447\"},{\"name\":\"王铁收费站\",\"pos\":\"31.257290,121.491507\"},{\"name\":\"丁里收费站\",\"pos\":\"31.240266,121.467474\"},{\"name\":\"永康收费站\",\"pos\":\"31.227936,121.511420\"},{\"name\":\"十八岗收费站\",\"pos\":\"31.230872,121.446188\"}]},{\"type\":\"fwq\",\"name\":\"服务区\",\"items\":[{\"name\":\"谭家桥服务区\",\"pos\":\"31.233220,121.480521\"},{\"name\":\"长安服务区\",\"pos\":\"31.229110,121.461981\"},{\"name\":\"龙坞服务区\",\"pos\":\"31.215605,121.483267\"},{\"name\":\"德清服务区\",\"pos\":\"31.225000,121.518973\"},{\"name\":\"大隐服务区\",\"pos\":\"31.248484,121.548499\"}]}],");
		
		if(m_poiHash.size() > 0) {
			Hashtable<String,List> typeHash = new Hashtable<String,List>();
			
			Iterator poiIters = m_poiHash.iterator();
			while(poiIters.hasNext()) {
				String poiNodeId = (String)poiIters.next();
				POINode poiNode = m_carDataStore.getPOINode(poiNodeId);
				if(poiNode != null) {
					String type = poiNode.getType();
					List list = typeHash.get(type);
					if(list == null) {
						list = new ArrayList();
						typeHash.put(type,list);
					}
					list.add(poiNode);
				}
			}
			strBuff.append(",\"poi\":[");
			for(int i=0;i<POITypes.length;i+=2) {
				String type = POITypes[i];
				if(i > 0) {
					strBuff.append(",");
				}
				strBuff.append("{\"type\":\"");
				strBuff.append(type);
				strBuff.append("\",\"name\":\"");
				strBuff.append(CharUtil.toUnicode(POITypes[i + 1]));
				strBuff.append("\",\"items\":[");
				
//				{\"name\":\"石化加油站\",\"pos\":\"31.233220,121.453742\"}
				List list = typeHash.get(type);
				if(list != null) {
					for(int j=0;j<list.size();j++) {
						if(j > 0) {
							strBuff.append(",");
						}
						POINode poiNode = (POINode)list.get(j);
						strBuff.append("{\"name\":\"");
						strBuff.append(CharUtil.toUnicode(poiNode.getName()));
						strBuff.append("\",\"pos\":\"");
						strBuff.append(poiNode.getLat());
						strBuff.append(",");
						strBuff.append(poiNode.getLon());
						strBuff.append("\"}");
					}
				}
				strBuff.append("]}");
			}
			strBuff.append("]");
		}
	}
	
	private static String[] POITypes = new String[]{"jyz","加油站","fwq","服务区","sfz","收费站"};
}
