/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time��2011-9-26 ����10:38:08
 */
package com.xtwsoft.router.carrouter.build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.xtwsoft.router.carrouter.CarRoad;
import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.Split;
import com.xtwsoft.utils.io.RTree;
import com.xtwsoft.utils.io.XtwFileReader;

public class EncodeDataFile {
	public EncodeDataFile(File sourceFile, File destFile) {
		try {
			RTree tree = new RTree();
			
			workFile(sourceFile,tree);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
			tree.writeOutputStream(bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void workFile(File file,RTree tree) {
		try {
			XtwFileReader reader = new XtwFileReader(file,"UTF8");
			String line = reader.readLine();
			if (line.charAt(0) == 65279) {// UTF8 �ļ���־FF FE FF FE
				line = line.substring(1);
			}
			while (line != null) {
				writeLine(line,tree);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeLine(String line,RTree tree) {
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
					if (orient == 0) {// �˴������⣬����֤
						orient = 3;
					}
				}
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
					CarRoad node = new CarRoad(name, orient, pointList,speed);
					tree.insert(node);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String validName(String name) {
		String[] items = name.split("!");
		//ȡ��һ��·��
		for (int i = 0; i < items.length; i++) {
			String ss = items[i];
			if (ss.startsWith("@")) {
			} else {
				return ss;
			}
		}

		//��δȡ�ô�·��������£�ȡ��һ��·��
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
			str = "����" + str.substring(1);
		} else if(str.startsWith("S")) {
			str = "ʡ��" + str.substring(1);
		} else if(str.startsWith("X")) {
			str = "�ص�" + str.substring(1);
		}
		return str;
	}


}
