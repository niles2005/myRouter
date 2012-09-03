package com.xtwsoft.router.carrouter.test;

import java.io.File;
import java.util.List;

import com.xtwsoft.utils.Split;
import com.xtwsoft.utils.io.XtwFileReader;
import com.xtwsoft.utils.io.XtwFileWriter;


public class CheckRoadOrient {
	public CheckRoadOrient() {
		File file = new File("D:\\mywork\\SHYT\\SH_YT_0504\\destXMD\\tm_dlzx_aat.xmd");
		try {
			File destFile = new File(file.getAbsolutePath() + ".orient0");
			XtwFileReader reader = new XtwFileReader(file,"UTF8");
			XtwFileWriter writer = new XtwFileWriter(destFile,"UTF8");
			String line = reader.readLine();
			if (line.charAt(0) == 65279) {// UTF8 文件标志FF FE FF FE
				line = line.substring(1);
			}
			while (line != null) {
				if (line.startsWith("{Line")) {
					List itemList = Split.split(line, "|");
					String roadSubType = (String) itemList.get(4);// subtype
					if(!roadSubType.startsWith("motorway")) {
						line = reader.readLine();
						continue;
					}
					String roadInfo = (String) itemList.get(6);
					int orient = 3;
					if (roadInfo.length() >= 3) {
						orient = Integer.parseInt(roadInfo.substring(2));
						if (orient == 0) {// 此处有问题，需验证
							writer.writeLine(line);
						}
					}

				}
				line = reader.readLine();
			}
			reader.close();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new CheckRoadOrient();
	}

}
