/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time��2011-9-7 ����10:25:34
 */
package com.xtwsoft.router.carrouter;

import java.util.ArrayList;
import java.util.Hashtable;

import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.gis.EarthPosLengthUtil;

//�˵�ڵ�   
public abstract class EndNode extends WorkNode {
	protected int m_leftIndex = -1;
	protected int m_rightIndex = -1;
	protected CarRoad m_road = null;
	
	protected int m_orient = 0;
	
	protected CarDataStore m_carDataStore = null;
	public EndNode(CarRoad carRoad,RoadEnd roadEnd,int leftIndex,int rightIndex,CarDataStore carDataStore) {
		super(roadEnd);
		m_road = carRoad;
		m_leftIndex = leftIndex;
		m_rightIndex = rightIndex;
		
		m_orient = m_road.getOrient();
		m_carDataStore = carDataStore;
	}
	
	public String getRoadName() {
		return m_road.getName();
	}
	
	/**
	 * 
	 * @param pointNodeHash
	 * @return error message,if is null,valid.
	 */
	public String doInit(Hashtable pointNodeHash) {
		ArrayList roadPosList = m_road.getEPosList();
		
		EarthPos pA = (EarthPos)roadPosList.get(0);
		EarthPos pZ = (EarthPos)roadPosList.get(roadPosList.size() - 1);
		//����Ƿ�λ��·�߶˵���,����ǣ��Ͳ��ؽ��е�����Ӷ���
		if(m_roadEnd.equals(pA)) {

		} else if(m_roadEnd.equals(pZ)) {
			
		} else {
			if(m_orient == 1) {
				this.createLinkNodeForOrient1(roadPosList,pointNodeHash,m_road);
			} else if(m_orient == 2) {
				this.createLinkNodeForOrient2(roadPosList,pointNodeHash,m_road);
			} else if(m_orient == 3) {//˫��
				this.createLinkNodeForOrient1(roadPosList,pointNodeHash,m_road);
				this.createLinkNodeForOrient2(roadPosList,pointNodeHash,m_road);
			} else if(m_orient == 0) {//���ɵ���
				return "�˴���·���ܵ�����";
			}
		}

		//���ڿ�ʼ�������ڴ˴����룬��Ϊ���ܻ�CarPoint
		pointNodeHash.put(this.m_roadEnd, this);
		return null;
	}
	
	//��������𣬵�Ŀ���ľ��룬������β����ͬһ�����������������double��������ȷ����
	public double getLengthFromRoadPointAEnd() {
		ArrayList roadPoints = this.m_road.getEPosList();
		ArrayList tmpList = new ArrayList();
		
		for(int i=0;i<=m_leftIndex;i++) {
			EarthPos thePos = (EarthPos)roadPoints.get(i);
			tmpList.add(thePos);
		}
		
		if(m_leftIndex == m_rightIndex) { //endEPoint Ϊ roadPointsĳ��

		} else {//startEPoint Ϊ roadPointsĳ����֮��
			tmpList.add(m_roadEnd.m_ePos);
		}
		return EarthPosLengthUtil.getLineLength(tmpList);
	}

	
	protected abstract void createLinkNodeForOrient1(ArrayList roadPoints,Hashtable pointNodeHash,CarRoad road);
	
	protected abstract void createLinkNodeForOrient2(ArrayList roadPoints,Hashtable pointNodeHash,CarRoad road);
	
}
