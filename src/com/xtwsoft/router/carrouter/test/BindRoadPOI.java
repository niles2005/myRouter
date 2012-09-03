package com.xtwsoft.router.carrouter.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.xtwsoft.router.carrouter.CarDataStore;
import com.xtwsoft.router.carrouter.CarRGC;
import com.xtwsoft.router.carrouter.CarRoadNode;
import com.xtwsoft.utils.Bounds;
import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.GlobalPos;
import com.xtwsoft.utils.LinePosUtil;
import com.xtwsoft.utils.Split;
import com.xtwsoft.utils.gis.EarthPosLengthUtil;
import com.xtwsoft.utils.gis.SutherlandHodgmanClip;
import com.xtwsoft.utils.io.Node;
import com.xtwsoft.utils.io.XtwFileReader;
import com.xtwsoft.utils.io.XtwFileWriter;

/**
 * 绑定道路路由时使用到的POI，如"加油站"，"服务区"，"收费站/高速路出入口","交通指示牌"等
 *
 */
public class BindRoadPOI {
	private CarDataStore m_carDataStore;
	Hashtable m_roadPOIHash = new Hashtable();
	Hashtable m_numHash = new Hashtable();
	StringBuffer m_noRoadPoints = new StringBuffer();
	public BindRoadPOI(File poiFile,String poiType) {
		try {
			if(!poiFile.exists()) {
				return;
			}
			File routerDataFile = new File("D:\\mywork\\SHYT\\datas\\routeData\\test.rmd");
			
			if (!routerDataFile.exists()) {
				System.err.println("route file is not exist:" + routerDataFile.getAbsolutePath());
				return;
			}		
			m_carDataStore = new CarDataStore(routerDataFile);
			CarRGC.initInstance(m_carDataStore);
			
			workFile(poiFile,poiType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			File destFile = new File(poiFile.getParent(), poiType + ".rpmap");
			XtwFileWriter writer = new XtwFileWriter(destFile,"UTF8");
			Enumeration enums = m_roadPOIHash.keys();
			while(enums.hasMoreElements()) {
				String key = (String)enums.nextElement();
				String value = (String)m_roadPOIHash.get(key);
				writer.writeLine(key + " = " + value);
			}
			writer.flush();
			writer.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
//		System.err.println("no road line:" + m_noRoadPoints.toString());
	}
	
	private void workFile(File poiFile,String poiType) {
		try {
			XtwFileReader reader = new XtwFileReader(poiFile,"UTF8");
			String line = reader.readLine();
			if (line.charAt(0) == 65279) {// UTF8 文件标志FF FE FF FE
				line = line.substring(1);
			}
			int count = 0;
			while (line != null) {
				if (line.startsWith("{Point")) {
					List itemList = Split.split(line, "|");
					String poiIndex = (String)itemList.get(1);
					String poiId = poiType + "_" + poiIndex;
					String point = (String)itemList.get(2);
					String[] ss = point.split(",");
					int lat = Integer.parseInt(ss[0]);
					int lon = Integer.parseInt(ss[1]);
					EarthPos ePos = new EarthPos(lon,lat);
					locateRoad(poiId,ePos);
					count++;
//					if(count > 5) {
//						break;
//					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 取得距离某点距离一定范围长度内的所有路线，对于某个加油站，可能在两条路线夹角处，此加油站可以算经过此两条路。
	 * 但对于服务器，只能是某一方向的路线，所有对服务器，需要取得最近线路。
	 * 目前定位选取poi点范围两百米内的路线
	 * @param ePos
	 * @return
	 */
	public void locateRoad(String poiId,EarthPos ePos) {
//		System.err.println(ePos.getLatLonString());
		double theRange = 5.961111111063566E-6;//此距离差不多对应200米
		GlobalPos gPos = ePos.convert2GlobalPos();

		CarRoadNode minCarRoad = null;
		EarthPos minEPos = null;
	
		double px1 = gPos.posX - theRange;
		double py1 = gPos.posY - theRange;
		double px2 = gPos.posX + theRange;
		double py2 = gPos.posY + theRange;
		EarthPos ePos1 = new GlobalPos(px1,py1).convert2EarthPos();
		EarthPos ePos2 = new GlobalPos(px2,py2).convert2EarthPos();
		Bounds bounds = new Bounds(ePos1.getILat() , ePos1.getILon(), ePos2.getILat(), ePos2.getILon());
		List list = m_carDataStore.search(bounds);
		double minLen = Double.MAX_VALUE;
		int count = 0;
		for(int i=0;i<list.size();i++) {
			Node node = (Node)list.get(i);
			if(node.getNodeTypeByte() == 'R') {
				CarRoadNode road = (CarRoadNode)node;
				ArrayList lineList = SutherlandHodgmanClip.clipLine(road.getEPosList(), bounds);
		        if(lineList != null && lineList.size() > 0) {
		        	//现在取最近的垂直点，如果垂点不在线段上，取最近的端点
		        	EarthPos theLPos = LinePosUtil.getLPosInLineList(ePos, lineList);
		        	if(theLPos != null) {
		        		double len = EarthPosLengthUtil.getMeterLength(ePos, theLPos);
		        		if(len < 50) {
		        			count++;
//		        			if(list.size() > 50) {
//		        				
//		        				System.err.println(":::::" + ePos.getLatLonString() + "   " + list.size());
//		    	        		System.err.println("len:" + len);
//			        			ArrayList ePosList = road.getEPosList();
//			        			String ePosString = getEPosString(ePosList);
//			        			System.err.println("index:" + count + "   " + ePosString);
//		        			}
		        			if(len < minLen) {
		        				minLen = len;
		        			}
		        			String roadId = "" + road.getRoadIndex();
		        			String ss = (String)m_roadPOIHash.get(roadId);
		        			if(ss == null) {
		        				ss = "";
		        			}
		        			if(ss.length() > 0) {
		        				ss += ",";
		        			}
		        			ss += poiId;
		        			m_roadPOIHash.put(roadId, ss);
		        			
//		        			m_roadPOIHash.get(key)	        			
		        		}
		        	}
		        }
	        }
		}
		if(count == 0) {
			if(m_noRoadPoints.length() > 0) {
				m_noRoadPoints.append(",");
			}
			m_noRoadPoints.append(ePos.getILat() + "," + ePos.getILon());
		}
//		if(count == 1) {
//			Integer num = (Integer)m_numHash.get(count);
//			if(num == null) {
//				num = new Integer(1);
//			} else {
//				num = new Integer(num.intValue() + 1);
//			}
//			m_numHash.put(count,num);
//		}
	}
	
	public String getEPosString(ArrayList ePosList) {
		StringBuffer strBuff = new StringBuffer();
		if(ePosList.size() > 0) {
			EarthPos p0 = (EarthPos)ePosList.get(0);
			strBuff.append(p0.getILat());
			strBuff.append(",");
			strBuff.append(p0.getILon());
			for(int i=1;i<ePosList.size();i++) {
				EarthPos p1 = (EarthPos)ePosList.get(i);
				strBuff.append(",");
				strBuff.append(p1.getILat());
				strBuff.append(",");
				strBuff.append(p1.getILon());
			}
			return strBuff.toString();
		}
		return "No points";
		
	}
	
	
	public static void main(String[] args) {
//		new BindRoadPOI(new File("D:\\mywork\\SHYT\\datas\\routeData\\tm_jyz_pat_point.xmd"),"jyz");
//		new BindRoadPOI(new File("D:\\mywork\\SHYT\\datas\\routeData\\tm_fwq_pat_point.xmd"),"fwq");
		new BindRoadPOI(new File("D:\\mywork\\SHYT\\datas\\routeData\\tm_sfz_pat_point.xmd"),"sfz");
	}
}
