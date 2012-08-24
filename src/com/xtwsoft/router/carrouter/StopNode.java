/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-6 下午06:07:17
 */
package com.xtwsoft.router.carrouter;

import java.util.ArrayList;
import java.util.Hashtable;

import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.gis.EarthPosLengthUtil;

public class StopNode extends EndNode {
	public StopNode(CarRoad carRoad,RoadEnd roadEnd,int leftIndex,int rightIndex,CarDataStore carDataStore) {
		super(carRoad,roadEnd,leftIndex,rightIndex,carDataStore);
	}
	
	//   ->->->->
	//   |=======>------|		     =为目标点
	protected void createLinkNodeForOrient1(ArrayList roadPoints,Hashtable pointNodeHash,CarRoad road) {
		EarthPos pA = (EarthPos)roadPoints.get(0);
		RoadEnd re = m_carDataStore.getRoadEnd(pA.getILat(), pA.getILon());
		if(re != null) {
			ArrayList ePosList = new ArrayList();
			for(int i=0;i<=m_leftIndex;i++) {
				EarthPos ePos = (EarthPos)roadPoints.get(i);
				ePosList.add(ePos);
			}

			if(m_leftIndex == m_rightIndex) { //endEPoint 为 roadPoints某点

			} else {//endEPoint 为 roadPoints某两点之间
				ePosList.add(m_roadEnd.m_ePos);
			}

			int toEndRouteLen = (int)(EarthPosLengthUtil.getLineLength(ePosList) + 0.5);
			int roadLength = m_road.getLength();
			int roadTime = m_road.getTime();
			int toEndRouteTime = (int)(1.0 * toEndRouteLen / roadLength * roadTime);
			
			CarLinker linker = new CarLinker(this.m_roadEnd,toEndRouteLen,toEndRouteTime,this.m_road,ePosList);
			
			WorkNode theNode = new WorkNode(re);
			theNode.addLinker(linker);
			pointNodeHash.put(re, theNode);
		}
	}
	
	//          <-<-<-<-
	//   |------>=======|		      =为目标点
	protected void createLinkNodeForOrient2(ArrayList roadPoints,Hashtable pointNodeHash,CarRoad road) {
		EarthPos pZ = (EarthPos)roadPoints.get(roadPoints.size() - 1);
		RoadEnd re = m_carDataStore.getRoadEnd(pZ);
		if(re != null) {
			ArrayList ePosList = new ArrayList();

			for(int i=roadPoints.size()-1;i>=m_rightIndex;i--) {
				EarthPos ePos = (EarthPos)roadPoints.get(i);
				ePosList.add(ePos);
			}
			if(m_leftIndex == m_rightIndex) { //endEPoint 为 roadPoints某点

			} else {//endEPoint 为 roadPoints某两点之间
				ePosList.add(m_roadEnd.m_ePos);
			}
			
			int toEndRouteLen = (int)(EarthPosLengthUtil.getLineLength(ePosList) + 0.5);
			int roadLength = m_road.getLength();
			int roadTime = m_road.getTime();
			int toEndRouteTime = (int)(1.0 * toEndRouteLen / roadLength * roadTime);
			
			CarLinker linker = new CarLinker(this.m_roadEnd,toEndRouteLen,toEndRouteTime,this.m_road,ePosList);
			
			WorkNode theNode = new WorkNode(re);
			theNode.addLinker(linker);
			pointNodeHash.put(re, theNode);
		}
	}
	
}
