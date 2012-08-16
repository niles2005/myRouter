/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-14 下午10:53:09
 */
package com.xtwsoft.router.carrouter;

import java.util.List;
import java.util.ListIterator;

import com.xtwsoft.router.RouteLine;
import com.xtwsoft.utils.EarthPos;

public class CarRouteLine extends RouteLine {
	protected int m_routeTotalLen = 0;
	protected int m_routeTotalTime = 0;
	private int m_routeType = 0;
	public CarRouteLine(int routeType,List workNodeList) {
		m_routeType = routeType;

		ListIterator iters = workNodeList.listIterator();
		WorkNode prevNode = null;
		EarthPos startPos = null;
		int totalLen = 0;
		int totalTime = 0;
		CarRouteSegment currSegment = null;
		while(iters.hasNext()) {
			WorkNode workNode = (WorkNode)iters.next();
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
	
	public void buildJSONResult(StringBuffer strBuff,int index) {
		if(m_segmentList.size() == 0) {
			strBuff.append("{}");
		} 
		else {
	
			strBuff.append("{");
			strBuff.append("\"routeType\":\"" + RouterUtil.getRouteTypeName(m_routeType) + "\",");
	
			strBuff.append("\"totallen\":\"");
			strBuff.append(RouterUtil.formatMeterLength(m_routeTotalLen) + "\",");
			strBuff.append("\"totaltime\":\"");
			strBuff.append(RouterUtil.formatSecondTime(m_routeTotalTime) + "\",");
			
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
				strBuff.append(segment1.getSegmentName(segment2));//对于chrome浏览器，此次需把汉字转成编码格式
				strBuff.append("\",");
	//			strBuff.append("\"len\":");
	//			strBuff.append("\"xx公里\",");
	//			strBuff.append("\"time\":");
	//			strBuff.append("\"\",");
				strBuff.append("\"points\":\"");
				segment1.buildPosString(strBuff, false,true);
				strBuff.append("\"}");
			}
			strBuff.append("]");
			strBuff.append("}");
		}
		
	}
	
}
