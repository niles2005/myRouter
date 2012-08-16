/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-27 下午09:33:19
 */
package com.xtwsoft.router.carrouter;

import java.util.ArrayList;



public class CarLinker {
	public RoadEnd m_roadEnd;
	public int m_linkLen;
	public int m_linkTime;
	public CarRoad m_road;
	
	public CarLinker m_nextLinker = null;
	public CarLinker(RoadEnd roadEnd,int linkLen,int linkTime,CarRoad road) {
		m_roadEnd = roadEnd;
		m_linkLen = linkLen;
		m_linkTime = linkTime;
		m_road = road;
	}
	
	public int getWeight(int routeType) {
		if(routeType == RouterUtil.RouteTypeLength) {
			return m_linkLen;
		} else if(routeType == RouterUtil.RouteTypeTime) {
			return m_linkTime;
		}
		return 0;
	}

	//此Temp PosList为End节点处切分的线段点，如果此值为空，则取road的PosList
	private ArrayList m_tempPosList;
	public CarLinker(RoadEnd roadEnd,int linkLen,int linkTime,CarRoad road,ArrayList tempPosList) {
		m_roadEnd = roadEnd;
		m_linkLen = linkLen;
		m_linkTime = linkTime;
		m_road = road;
		m_tempPosList = tempPosList;
	}
	
	public ArrayList getEPosList() {
		if(m_tempPosList != null) {
			return m_tempPosList;
		}
		return m_road.getEPosList();
	}
	
}