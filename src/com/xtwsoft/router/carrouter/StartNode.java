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

public class StartNode extends EndNode {
	public StartNode(CarRoadNode carRoad,RoadEnd startPoint,int leftIndex,int rightIndex,CarDataStore carDataStore) {
		super(carRoad,startPoint,leftIndex,rightIndex,carDataStore);
	}
	
	//目的：
	//1.设置首点FGH
	//2.解决首尾点在一点的情况（此处已不判断，已在Route开始时预先判断）
	//3.首尾点在一条线上的情况
	public void setStopNode(StopNode stopNode,int routeType) {
		calcFGH(0, stopNode,routeType);

		if(this.m_road == stopNode.m_road) {
			double startFromAEnd = this.getLengthFromRoadPointAEnd();
			double endFromAEnd = stopNode.getLengthFromRoadPointAEnd();
			
			if(startFromAEnd == endFromAEnd) {//首尾点相同
				
			} else if(startFromAEnd < endFromAEnd) {
				if(this.m_orient == 1) {
					createRouteLineS2E(stopNode,1);
				} else if(m_orient == 2) {
					
				} else if(m_orient == 3) {
					createRouteLineS2E(stopNode,1);
				}
			} else { //>
				if(this.m_orient == 1) {
					
				} else if(m_orient == 2) {
					createRouteLineS2E(stopNode,1);
				} else if(m_orient == 3) {
					createRouteLineS2E(stopNode,2);
				}
			}
		}
	}
	
	
	private void createRouteLineS2E(StopNode stopNode,int orient) {
		ArrayList roadPosList = m_road.getEPosList();
		ArrayList<EarthPos> ePosList = new ArrayList<EarthPos>();

		if(m_leftIndex == m_rightIndex) { //endEPoint 为 roadPoints某点

		} else {//startEPoint 为 roadPoints某两点之间
			ePosList.add(m_roadEnd.m_ePos);
		}
		if(orient == 1) {
			for(int i=m_rightIndex;i<=stopNode.m_leftIndex;i++) {
				EarthPos ePos = (EarthPos)roadPosList.get(i);
				ePosList.add(ePos);
			}
		} else if(orient == 2) {
			for(int i=m_leftIndex;i>=stopNode.m_rightIndex;i--) {
				EarthPos ePos = (EarthPos)roadPosList.get(i);
				ePosList.add(ePos);
			}
		}
		if(stopNode.m_leftIndex == stopNode.m_rightIndex) { //endEPoint 为 roadPoints某点

		} else {//startEPoint 为 roadPoints某两点之间
			ePosList.add(stopNode.m_roadEnd.m_ePos);
		}
		

		int toEndRouteLen = (int)(EarthPosLengthUtil.getLineLength(ePosList) + 0.5);
		int roadLength = m_road.getLength();
		int roadTime = m_road.getTime();
		int toEndRouteTime = (int)(1.0 * toEndRouteLen / roadLength * roadTime);
		
		CarLinker linker = new CarLinker(stopNode.m_roadEnd,toEndRouteLen,toEndRouteTime,this.m_road,ePosList);
		this.addLinker(linker);
	}
	
	//          ->->->->
	//   |------>=======|		     =为目标点
	protected void createLinkNodeForOrient1(ArrayList roadPosList,Hashtable pointNodeHash,CarRoadNode road) {
		EarthPos pZ = (EarthPos)roadPosList.get(roadPosList.size() - 1);
		RoadEnd re = m_carDataStore.getRoadEnd(pZ);
		if(re != null) {
			ArrayList ePosList = new ArrayList();

			if(m_leftIndex == m_rightIndex) { //endEPoint 为 roadPoints某点

			} else {//startEPoint 为 roadPoints某两点之间
				ePosList.add(m_roadEnd.m_ePos);
			}
			for(int i=m_rightIndex;i<roadPosList.size();i++) {
				EarthPos ePos = (EarthPos)roadPosList.get(i);
				ePosList.add(ePos);
			}
			
			int meterLen = (int)(EarthPosLengthUtil.getLineLength(ePosList) + 0.5);
			int roadLength = m_road.getLength();
			int roadTime = m_road.getTime();
			int meterTime = (int)(1.0 * meterLen / roadLength * roadTime);
			
			CarLinker linker = new CarLinker(re,meterLen,meterTime,m_road,ePosList);
			this.addLinker(linker);
			
			WorkNode theNode = new WorkNode(re);
			pointNodeHash.put(re, theNode);
		}
		
	}
	
	//    <-<-<-<-
	//   |=======>------|		     =为目标点
	protected void createLinkNodeForOrient2(ArrayList roadPosList,Hashtable posNodeHash,CarRoadNode road) {
		EarthPos pA = (EarthPos)roadPosList.get(0);
		RoadEnd re = m_carDataStore.getRoadEnd(pA);
		if(re != null) {
			ArrayList ePosList = new ArrayList();

			if(m_leftIndex == m_rightIndex) { //endEPoint 为 roadPoints某点

			} else {//startEPoint 为 roadPoints某两点之间
				ePosList.add(m_roadEnd.m_ePos);
			}
			for(int i=m_leftIndex;i>=0;i--) {
				EarthPos ePos = (EarthPos)roadPosList.get(i);
				ePosList.add(ePos);
			}
			
			int meterLen = (int)(EarthPosLengthUtil.getLineLength(ePosList) + 0.5);
			int roadLength = m_road.getLength();
			int roadTime = m_road.getTime();
			int meterTime = (int)(1.0 * meterLen / roadLength * roadTime);

			CarLinker linker = new CarLinker(re,meterLen,meterTime,m_road,ePosList);
			this.addLinker(linker);

			WorkNode theNode = new WorkNode(re);
			posNodeHash.put(re, theNode);
		}
	}
}
