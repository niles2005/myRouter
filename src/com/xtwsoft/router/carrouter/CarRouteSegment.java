/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-14 下午10:59:23
 */
package com.xtwsoft.router.carrouter;

import java.util.ArrayList;

import com.xtwsoft.router.RouteSegment;
import com.xtwsoft.utils.EarthPos;

public class CarRouteSegment extends RouteSegment {
	private int m_totalLen = 0;
	private int m_totalTime = 0;
	private ArrayList m_workNodeList = new ArrayList();
	private ArrayList<EarthPos> m_posList = new ArrayList<EarthPos>();
	private String m_roadName = null;
	public CarRouteSegment(WorkNode workNode1,WorkNode workNode2,String roadName,EarthPos firstPos) {
		m_roadName = roadName;
		m_workNodeList.add(workNode1);
		m_workNodeList.add(workNode2);
		
		m_totalLen = workNode2.m_prevLinker.m_linkLen;
		m_totalTime = workNode2.m_prevLinker.m_linkTime;
		ArrayList thePosList = workNode2.m_prevLinker.getEPosList();
		EarthPos pA = (EarthPos)thePosList.get(0);
		if(pA.getILat() == firstPos.getILat() && pA.getILon() == firstPos.getILon()) {
			for(int i=0;i<thePosList.size();i++) {
				m_posList.add((EarthPos)thePosList.get(i));
			}
		} else {
			for(int i=thePosList.size() - 1;i>=0;i--) {
				m_posList.add((EarthPos)thePosList.get(i));
			}
		}
	}
	
	public String getRoadName() {
		return m_roadName;
	}
	
	public void addWorkNode(WorkNode workNode) {
		m_totalLen += workNode.m_prevLinker.m_linkLen;
		m_totalTime += workNode.m_prevLinker.m_linkTime;
		
		m_workNodeList.add(workNode);
		EarthPos lastPos = m_posList.get(m_posList.size() - 1);
		ArrayList thePosList = workNode.m_prevLinker.getEPosList();
		EarthPos pA = (EarthPos)thePosList.get(0);
		if(pA.getILat() == lastPos.getILat() && pA.getILon() == lastPos.getILon()) {
			for(int i=1;i<thePosList.size();i++) {
				m_posList.add((EarthPos)thePosList.get(i));
			}
		} else {
			for(int i=thePosList.size() - 2;i>=0;i--) {
				m_posList.add((EarthPos)thePosList.get(i));
			}
		}
	}
	
	public EarthPos getFirstPos() {
		return m_posList.get(0);
	}
	
	public EarthPos getLastPos() {
		return m_posList.get(m_posList.size() - 1);
	}
	
	public String getSegmentName(CarRouteSegment nextSegment) {
		String segmentName = null;

		String roadName = this.m_roadName;
		String part1 = null;
		
		String ss = RouteUtil.formatMeterLength(m_totalLen);
		if(RouteUtil.UnknownRoadName.equals(roadName) || roadName.length() == 0) {
			part1 =  "沿着当前路向前" + ss;
		} else {
			part1 =  "沿着<B>" + roadName + "</B>向前" + ss;
		}
		if(nextSegment == null) {
			segmentName =  part1 + ",到达终点";	
		} else {
			String nextRoadName = nextSegment.m_roadName;
			EarthPos p1 = m_posList.get(m_posList.size() - 2);
			EarthPos p2 = m_posList.get(m_posList.size() - 1);
			EarthPos p3 = nextSegment.m_posList.get(1);
			String angleInfo = RouteUtil.crateRouteAnage(p1,p2,p3);

			if(RouteUtil.UnknownRoadName.equals(nextRoadName)) {
				segmentName =  part1 + "处" + angleInfo;
			} else if(nextRoadName.length() == 0) {
				segmentName =  part1 + "处" + angleInfo;
			} else {
				segmentName =  part1 + ",到<B>" + nextRoadName + "</B>" + angleInfo;
			}
		}
		return segmentName;
	}
	
	public void buildPosString(StringBuffer strBuff,boolean isV7,boolean isOffset) {
		if(isOffset) {
			if(isV7) {
				EarthPos p0 = m_posList.get(0);
				strBuff.append(p0.getI7Lat());
				strBuff.append(",");
				strBuff.append(p0.getI7Lon());
				for(int i=1;i<m_posList.size();i++) {
					EarthPos p1 = m_posList.get(i);
					strBuff.append(",");
					strBuff.append((p1.getI7Lat() - p0.getI7Lat()));
					strBuff.append(",");
					strBuff.append((p1.getI7Lon() - p0.getI7Lon()));
					
					p0 = p1;
				}
			} else {
				EarthPos p0 = m_posList.get(0);
				strBuff.append(p0.getI6Lat());
				strBuff.append(",");
				strBuff.append(p0.getI6Lon());
				for(int i=1;i<m_posList.size();i++) {
					EarthPos p1 = m_posList.get(i);
					strBuff.append(",");
					strBuff.append((p1.getI6Lat()- p0.getI6Lat()));
					strBuff.append(",");
					strBuff.append((p1.getI6Lon()- p0.getI6Lon()));
					
					p0 = p1;
				}
			}
		} else {
			if(isV7) {
				EarthPos p0 = m_posList.get(0);
				strBuff.append(p0.getI7Lat());
				strBuff.append(",");
				strBuff.append(p0.getI7Lon());
				for(int i=1;i<m_posList.size();i++) {
					EarthPos p1 = m_posList.get(i);
					strBuff.append(",");
					strBuff.append(p1.getI7Lat());
					strBuff.append(",");
					strBuff.append(p1.getI7Lon());
				}
			} else {
				EarthPos p0 = m_posList.get(0);
				strBuff.append(p0.getI6Lat());
				strBuff.append(",");
				strBuff.append(p0.getI6Lon());
				for(int i=1;i<m_posList.size();i++) {
					EarthPos p1 = m_posList.get(i);
					strBuff.append(",");
					strBuff.append(p1.getI6Lat());
					strBuff.append(",");
					strBuff.append(p1.getI6Lon());
				}
			}
		}
	}
}
