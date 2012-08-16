/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time��2011-9-6 ����02:24:04
 */
package com.xtwsoft.router.carrouter;

import java.util.ArrayList;
import java.util.List;

import com.xtwsoft.utils.Bounds;
import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.GlobalPos;
import com.xtwsoft.utils.LinePosUtil;
import com.xtwsoft.utils.gis.SutherlandHodgmanClip;

//reverse geocoding
public class CarRGC {
	private static CarRGC m_instance = null;
	
	
	private CarDataStore m_carDataStore = null;
	private CarRGC(CarDataStore carDataStore) {
		m_carDataStore = carDataStore;
	}
	
	public static void initInstance(CarDataStore carDataStore) {
		if(carDataStore == null) {
			return;
		}
		if(m_instance == null) {
			m_instance = new CarRGC(carDataStore);
		}
	}


	public static CarRGC getInstance() {
		return m_instance;
	}
	
	private static final double EdgeLen = 1<<(25 - 0);
	private static final double Range = 30.0 / EdgeLen;
	

	
	//ȡ�þ�γ�ȵĵ�ַ
	public String getLocationName(EarthPos ePos) {
		double theRange = Range;
		GlobalPos gPos = ePos.convert2GlobalPos();

		CarRoad minCarRoad = null;
		int count = 0;
		while(minCarRoad == null) {
			double px1 = gPos.posX - theRange;
			double py1 = gPos.posY - theRange;
			double px2 = gPos.posX + theRange;
			double py2 = gPos.posY + theRange;
			EarthPos ePos1 = new GlobalPos(px1,py1).convert2EarthPos();
			EarthPos ePos2 = new GlobalPos(px2,py2).convert2EarthPos();
			Bounds bounds = new Bounds(ePos1.getILat() , ePos1.getILon(), ePos2.getILat(), ePos2.getILon());
			List list = m_carDataStore.search(bounds);
			
			if(list.size() == 1) {
				minCarRoad = (CarRoad)list.get(0);
				break;
			}
			
			double minLen = Double.MAX_VALUE;
			for(int i=0;i<list.size();i++) {
				CarRoad road = (CarRoad)list.get(i);
				ArrayList lineList = SutherlandHodgmanClip.clipLine(road.getEPosList(), bounds);
		        if(lineList != null && lineList.size() > 0) {
		        	//ԭ����ȡ��������е�
//		        	for(int j=0;j<lineList.size();j++) {
//		        		ArrayList points = (ArrayList)lineList.get(j);
//		        		for(int k=0;k<points.size();k++) {
//			        		Point p = (Point)points.get(k);
//			        		double len = getLen(ePos,p.x,p.y);
//			        		if(len < minLen) {
//			        			minLen = len;
//			        			minFeatureRf = featureRf;
//			        			minPoint = p;
//			        		}
//		        		}
//		        	}
		        	//����ȡ����Ĵ�ֱ�㣬������㲻���߶��ϣ�ȡ����Ķ˵�
		        	EarthPos theLPos = LinePosUtil.getLPosInLineList(ePos, lineList);
		        	if(theLPos != null) {
		        		double len = getLen(ePos,theLPos.getILat(),theLPos.getILon());
		        		if(len < minLen) {
		        			minLen = len;
		        			minCarRoad = road;
		        		}
		        	}
		        }
			}
			if(count >= 5) {
				break;
			}
			theRange *= 2;
			count++;
		}
		if(minCarRoad == null) {
			return null;
		}
		return minCarRoad.getName();
	}
	
	//��λ�㵽·��������ĵ�
	public EarthPos locatePos2Road(EarthPos ePos) {
		double theRange = Range;
		GlobalPos gPos = ePos.convert2GlobalPos();

		CarRoad minCarRoad = null;
		EarthPos minEPos = null;
		int count = 0;
		while(minCarRoad == null) {
			double px1 = gPos.posX - theRange;
			double py1 = gPos.posY - theRange;
			double px2 = gPos.posX + theRange;
			double py2 = gPos.posY + theRange;
			EarthPos ePos1 = new GlobalPos(px1,py1).convert2EarthPos();
			EarthPos ePos2 = new GlobalPos(px2,py2).convert2EarthPos();
			Bounds bounds = new Bounds(ePos1.getILat() , ePos1.getILon(), ePos2.getILat(), ePos2.getILon());
			List list = m_carDataStore.search(bounds);
			double minLen = Double.MAX_VALUE;
			for(int i=0;i<list.size();i++) {
				CarRoad road = (CarRoad)list.get(i);
				ArrayList lineList = SutherlandHodgmanClip.clipLine(road.getEPosList(), bounds);
		        if(lineList != null && lineList.size() > 0) {
		        	//ԭ����ȡ��������е�
//		        	for(int j=0;j<lineList.size();j++) {
//		        		ArrayList points = (ArrayList)lineList.get(j);
//		        		for(int k=0;k<points.size();k++) {
//			        		Point p = (Point)points.get(k);
//			        		double len = getLen(ePos,p.x,p.y);
//			        		if(len < minLen) {
//			        			minLen = len;
//			        			minFeatureRf = featureRf;
//			        			minPoint = p;
//			        		}
//		        		}
//		        	}
		        	//����ȡ����Ĵ�ֱ�㣬������㲻���߶��ϣ�ȡ����Ķ˵�
		        	EarthPos theLPos = LinePosUtil.getLPosInLineList(ePos, lineList);
		        	if(theLPos != null) {
		        		double len = getLen(ePos,theLPos.getILat(),theLPos.getILon());
		        		if(len < minLen) {
		        			minLen = len;
		        			minCarRoad = road;
		        			minEPos = theLPos;
		        		}
		        	}
		        }
			}
			if(count >= 5) {
				break;
			}
			theRange *= 2;
			count++;
		}
		return minEPos;
	}
	
	public EndNode createEndNode(EarthPos ePos,boolean isStart) {
		double theRange = Range;
		GlobalPos gPos = ePos.convert2GlobalPos();

		CarRoad minCarRoad = null;
		
//		minLEndEPoints ΪEarthPos[3],�ֱ�ΪleftEndPoint,LPoint,rightEndPoint
		//��ΪSutherlandHodgmanClip��ԭ�򣬿��ܼ���������û��road����ʵ��(������з�Χ��û��road�ĵ�)
		//leftEndPoint �� rightEndPoint ��һ��Ϊ��ʵ�㣬����Ϊ���е�
		//���LPointΪroad����ʵ�㣬leftEndPoint �� rightEndPointͬʱ��ΪLPoint��
		EarthPos[] minLEndEPoses = null;
		
		int count = 0;
		while(minCarRoad == null) {
			double px1 = gPos.posX - theRange;
			double py1 = gPos.posY - theRange;
			double px2 = gPos.posX + theRange;
			double py2 = gPos.posY + theRange;
			EarthPos ePos1 = new GlobalPos(px1,py1).convert2EarthPos();
			EarthPos ePos2 = new GlobalPos(px2,py2).convert2EarthPos();
			Bounds bounds = new Bounds(ePos1.getILat() , ePos1.getILon(), ePos2.getILat(), ePos2.getILon());
			List list = m_carDataStore.search(bounds);
			double minLen = Double.MAX_VALUE;
			for(int i=0;i<list.size();i++) {
				CarRoad road = (CarRoad)list.get(i);
				ArrayList lineList = SutherlandHodgmanClip.clipLine(road.getEPosList(), bounds);
		        if(lineList != null && lineList.size() > 0) {
		        	//ԭ����ȡ��������е�
//		        	for(int j=0;j<lineList.size();j++) {
//		        		ArrayList points = (ArrayList)lineList.get(j);
//		        		for(int k=0;k<points.size();k++) {
//			        		Point p = (Point)points.get(k);
//			        		double len = getLen(ePos,p.x,p.y);
//			        		if(len < minLen) {
//			        			minLen = len;
//			        			minFeatureRf = featureRf;
//			        			minPoint = p;
//			        		}
//		        		}
//		        	}
		        	//����ȡ����Ĵ�ֱ�㼰���ˣ�������㲻���߶��ϣ�ȡ����Ķ˵㣨LEnd��L & 2End Ϊ����,L ���У�
		        	EarthPos[] theLEndEPoses = LinePosUtil.getLEndPosInLineList(ePos, lineList);
		        	if(theLEndEPoses != null && theLEndEPoses[1] != null) {
		        		double len = getLen(ePos,theLEndEPoses[1].getILat(),theLEndEPoses[1].getILon());
		        		if(len < minLen) {
		        			minLen = len;
		        			minCarRoad = road;
		        			minLEndEPoses = theLEndEPoses;
		        		}
		        	}
		        }
			}
			if(count >= 5) {
				break;
			}
			theRange *= 2;
			count++;
		}
		if(minCarRoad == null || minLEndEPoses == null) {
			return null;
		}
		
		//��ȡLPoint����������ʵ�����������LPointΪRoad����ʵ�㣬����������ͬ��ͬ��ΪLPonit����
		int leftIndex = -1;
		int rightIndex = -1;
		ArrayList roadPoses = minCarRoad.getEPosList();
		
		//������ͬ��LPoint����ΪFeature����ʵ��,Ҳ�����Ǽ��з�Χ�Ķ˵㡣
		if(minLEndEPoses[0] == minLEndEPoses[1] && minLEndEPoses[1] == minLEndEPoses[2]) {
			int checkX = minLEndEPoses[1].getILat();
			int checkY = minLEndEPoses[1].getILon();
			for(int i=0;i<roadPoses.size();i++) {
				EarthPos thePos = (EarthPos)roadPoses.get(i);
				if(thePos.getILat() == checkX && thePos.getILon() == checkY) {
					leftIndex = i;
					rightIndex = i;
					break;
				}
			}
		} else {//is in point line Ŀ�����points��ĳ���߶��ϣ��߶�����Ϊm_LEndEPoints[0],m_LEndEPoints[2]
			for(int i=0;i<roadPoses.size();i++) {
				EarthPos thePos = (EarthPos)roadPoses.get(i);
				if(thePos.getILat() == minLEndEPoses[0].getILat() && thePos.getILon() == minLEndEPoses[0].getILon()) {
					leftIndex = i;
					rightIndex = i + 1;
					break;
				}
				if(thePos.getILat() == minLEndEPoses[2].getILat() && thePos.getILon() == minLEndEPoses[2].getILon()) {
					leftIndex = i - 1;
					rightIndex = i;
					break;
				}
			}
			
		}
		
		if(leftIndex == -1) {//��δ�ҵ��������ж����߶��м�
			int centerX = minLEndEPoses[1].getILat();
			int centerY = minLEndEPoses[1].getILon();
			
			EarthPos p0 = (EarthPos)roadPoses.get(0);
			for(int i=1;i<roadPoses.size();i++) {
				EarthPos p1 = (EarthPos)roadPoses.get(i);
				if(LinePosUtil.isCenterEarthPoint(p0, p1, centerX, centerY)) {
					leftIndex = i - 1;
					rightIndex = i;
					break;
				}
				p0 = p1;
			}
		}
		if(leftIndex == -1) {
			return null;
		}
		
		//�Ƿ�λ�ڽ������
		RoadEnd roadEnd = m_carDataStore.getRoadEnd(minLEndEPoses[1]);
		
		if(isStart) {
			if(roadEnd == null) {
				roadEnd = new RoadEnd(minLEndEPoses[1]);
			}
			return new StartNode(minCarRoad,roadEnd,leftIndex,rightIndex,m_carDataStore);
		} else {
			if(roadEnd == null) {
				roadEnd = new RoadEnd(minLEndEPoses[1]);
			}
			return new StopNode(minCarRoad,roadEnd,leftIndex,rightIndex,m_carDataStore);
		}
	}
	
	private double getLen(EarthPos ePos,int x,int y) {
		double xx = 0.001 * (ePos.getILat() - x);
		double yy = 0.001 * (ePos.getILon() - y);
		return Math.sqrt(xx * xx + yy * yy);
	}
	

}
