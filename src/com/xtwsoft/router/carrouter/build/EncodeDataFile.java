/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午10:38:08
 */
package com.xtwsoft.router.carrouter.build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.xtwsoft.router.carrouter.CarRoadNode;
import com.xtwsoft.router.carrouter.POINode;
import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.Split;
import com.xtwsoft.utils.io.RTree;
import com.xtwsoft.utils.io.XtwFileReader;

public class EncodeDataFile {
	private Hashtable m_rpMapHash = new Hashtable(); 
	RTree m_tree = new RTree();
	public EncodeDataFile() {
	}
	
	public void saveFile(File destFile) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
			m_tree.writeOutputStream(bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//加载Road和POI对应关系图
	public void loadRPMapPath(File path) {
		File[] files = path.listFiles();
		for(int i=0;i<files.length;i++) {
			File file = files[i];
			if(file.getName().endsWith(".rpmap")) {
				loadRPMapFile(file);
			}
		}
	}
	
	public void loadRPMapFile(File file) {
		try {
			XtwFileReader reader = new XtwFileReader(file,"UTF8");
			String line = reader.readLine();
			if (line.charAt(0) == 65279) {// UTF8 文件标志FF FE FF FE
				line = line.substring(1);
			}
			while (line != null) {
				List list = Split.split(line, "=");
				if(list.size() == 2) {
					String roadId = ((String)list.get(0)).trim();
					String poiId = ((String)list.get(1)).trim();
					m_rpMapHash.put(roadId, poiId);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void workRoadFile(File file) {
		try {
			XtwFileReader reader = new XtwFileReader(file,"UTF8");
			String line = reader.readLine();
			if (line.charAt(0) == 65279) {// UTF8 文件标志FF FE FF FE
				line = line.substring(1);
			}
			while (line != null) {
				writeRoadLine(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeRoadLine(String line) {
		line = line.trim();
		if (line.length() == 0) {
			return;
		}

		try {
			if (line.startsWith("{Line")) {
				List itemList = Split.split(line, "|");
				String roadSubType = (String) itemList.get(4);// subtype
				if (roadSubType.length() == 0) {
					return;
				}
				if (roadSubType.equals("footway")) {
					return;
				}
				if (roadSubType.equals("construction")) {
					return;
				}
				int speed = 0;
				if(roadSubType.startsWith("motorway")) {
					speed = 80;
				} else if(roadSubType.startsWith("expressway")) {
					speed = 60;
				} else if(roadSubType.startsWith("primary")) {
					speed = 45;
				} else if(roadSubType.startsWith("secondary")) {
					speed = 40;
				} else if(roadSubType.startsWith("trunk")) {
					speed = 35;
				} else if(roadSubType.startsWith("residential")) {
					speed = 25;
				}
				if(speed <= 0) {
					return;
				}
				String roadInfo = (String) itemList.get(6);
				int orient = 3;
				if (roadInfo.length() >= 3) {
					orient = Integer.parseInt(roadInfo.substring(2));
					if (orient == 0) {// 此处有问题，需验证
						orient = 3;
					}
				}
				int roadIndex = Integer.parseInt((String)itemList.get(1));
				String name = (String) itemList.get(5);
				name = validName(name);
				String strPos = (String) itemList.get(2);
				String[] strs = strPos.split(",");
				ArrayList pointList = new ArrayList();
				EarthPos pa = null;
				EarthPos pz = null;
				for (int i = 0; i < strs.length; i += 2) {
					int lat = Integer.parseInt(strs[i]);
					int lon = Integer.parseInt(strs[i + 1]);
					EarthPos p = new EarthPos(lon, lat);
					if (i == 0) {
						pa = p;
					} else if (i == strs.length - 2) {
						pz = p;
					}
					pointList.add(p);
				}
				if (pa.getILat() == pz.getILat() && pa.getILon() == pz.getILon()) {
					System.err.println("same end point line:" + line);
				}
				if (pointList.size() > 1) {
					CarRoadNode node = new CarRoadNode(roadIndex,name, orient, pointList,speed);
					m_tree.insert(node);
					
					String poiIds = (String)m_rpMapHash.get("" + node.getRoadIndex());
					if(poiIds != null) {
						node.addPOINodes(poiIds);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String validName(String name) {
		String[] items = name.split("!");
		//取第一个路名
		for (int i = 0; i < items.length; i++) {
			String ss = items[i];
			if (ss.startsWith("@")) {
			} else {
				return ss;
			}
		}

		//在未取得纯路名的情况下，取第一个路号
		for (int i = 0; i < items.length; i++) {
			String ss = items[i];
			if (ss.startsWith("@")) {
				return roadFlagDecode(ss);
			}
		}
		return "";
	}

	private String validName0(String name) {
		String roadName = "";
		String[] items = name.split("!");
		for (int i = 0; i < items.length; i++) {
			String ss = items[i];
			if(roadName.length() > 0) {
				roadName += "|";
			}
			if (ss.startsWith("@")) {
				roadName += roadFlagDecode(ss);
			} else {
				roadName += ss;
			}
		}
		return roadName;
	}
	
	public static String roadFlagDecode(String str) {
		str = str.substring(1);
		int pos = str.indexOf("#");
		if(pos != -1) {
			str = str.substring(0,pos);
		}
		if(str.startsWith("G")) {
			str = "国道" + str.substring(1);
		} else if(str.startsWith("S")) {
			str = "省道" + str.substring(1);
		} else if(str.startsWith("X")) {
			str = "县道" + str.substring(1);
		}
		return str;
	}


	public void workPOIFile(File file) {
		try {
			XtwFileReader reader = new XtwFileReader(file,"UTF8");
			String line = reader.readLine();
			if (line.charAt(0) == 65279) {// UTF8 文件标志FF FE FF FE
				line = line.substring(1);
			}
			while (line != null) {
				writePOILine(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writePOILine(String line) {
		line = line.trim();
		if (line.length() == 0) {
			return;
		}

		try {
			if (line.startsWith("{Point")) {
				List itemList = Split.split(line, "|");
				String index = (String) itemList.get(1);
				String name = (String) itemList.get(5);
				String strPos = (String) itemList.get(2);
				String[] strs = strPos.split(",");
				int lat = Integer.parseInt(strs[0]);
				int lon = Integer.parseInt(strs[1]);
				String type = (String) itemList.get(6);
				POINode node = new POINode(index,lat,lon,name,type);
				m_tree.insert(node);
			} 
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
