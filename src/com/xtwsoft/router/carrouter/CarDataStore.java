/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-29 下午02:17:24
 */
package com.xtwsoft.router.carrouter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.xtwsoft.utils.Bounds;
import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.io.Node;
import com.xtwsoft.utils.io.NodeBuilder;
import com.xtwsoft.utils.io.NodeLister;
import com.xtwsoft.utils.io.RTree;

public class CarDataStore {
	private RTree m_tree = null;
	private Hashtable m_hash = new Hashtable();

	public CarDataStore(File file) {
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			readInputStream(bis);
			bis.close();
			
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
    public void readInputStream(InputStream is) throws IOException {
		m_tree = new RTree(new NodeBuilder() {
			public Node createNode(byte nodeTypeByte) {
				if(nodeTypeByte == 'P') {
					return new POINode();
				} else if(nodeTypeByte == 'R') {
					return new CarRoadNode();
				} 
				return null;
			}
		});
		m_tree.readInputStream(is);
		m_tree.searchAll(new NodeLister() {
			public void addNode(Node node) {
				if(node.getNodeTypeByte() == 'R') {
					addCarRoad((CarRoadNode)node);
				} else if(node.getNodeTypeByte() == 'P') {
					addPOINode((POINode)node);
				}
			}
		});
    }
    
	private void addCarRoad(CarRoadNode road) {
		ArrayList ePosList = road.getEPosList();
		
		int orient = road.getOrient();
		if(orient == 0) {
			return;
		}
		EarthPos posA = (EarthPos)ePosList.get(0);
		EarthPos posZ = (EarthPos)ePosList.get(ePosList.size() - 1);
		
		int len = road.getLength();
		int time = road.getTime();
		RoadEnd re1 = createRoadEnd(posA);
		RoadEnd re2 = createRoadEnd(posZ);
		if(orient == 1) {
			re1.addLinker(new CarLinker(re2,len,time,road));
		} else if(orient == 2) {
			re2.addLinker(new CarLinker(re1,len,time,road));
		} else if(orient == 3) {
			re1.addLinker(new CarLinker(re2,len,time,road));
			
			re2.addLinker(new CarLinker(re1,len,time,road));
		}
	}

	private void addPOINode(POINode poiNode) {
		m_hash.put(poiNode.getId(), poiNode);
	}
	
	public POINode getPOINode(String poiNodeId) {
		return (POINode)m_hash.get(poiNodeId);
	}
	
	private RoadEnd createRoadEnd(EarthPos ePos) {
		String key = ePos.getILat() + "," + ePos.getILon();
		RoadEnd re = (RoadEnd)m_hash.get(key);
		if(re == null) {
			re = new RoadEnd(ePos);
			m_hash.put(key, re);
		}
		return re;
	}
	
	
	public List search(Bounds query) {
		return m_tree.search(query);
	}

	public RoadEnd getRoadEnd(int lat,int lon) {
		return (RoadEnd)m_hash.get(lat + "," + lon);
	}
	
	public RoadEnd getRoadEnd(EarthPos ePos) {
		return (RoadEnd)m_hash.get(ePos.getILat() + "," + ePos.getILon());
	}

    public void writeOutputStream(OutputStream os) throws IOException {
    	m_tree.writeOutputStream(os);
    }
}
