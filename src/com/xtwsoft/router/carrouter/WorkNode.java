/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-8-22 下午09:23:52
 */
package com.xtwsoft.router.carrouter;

import java.util.Hashtable;

import com.xtwsoft.utils.EarthPos;
import com.xtwsoft.utils.gis.EarthPosLengthUtil;

public class WorkNode {
	protected int fn = Integer.MAX_VALUE;
	protected int gn = Integer.MAX_VALUE;
	protected int hn = Integer.MAX_VALUE;
	
	public void calcFGH(int newGn,WorkNode destNode,int routeType) {
		gn = newGn;
		hn = getWeight2WorkNode(destNode,routeType);
		fn = gn + hn;
	}
	
	protected int getWeight2WorkNode(WorkNode workNode,int routeType) {
		double len = EarthPosLengthUtil.getMeterLength(m_roadEnd.getLat(), m_roadEnd.getLon(), workNode.m_roadEnd.getLat(), workNode.m_roadEnd.getLon());
		if(routeType == RouterUtil.RouteTypeTime) {
			//use max speed: 120KM/H
			int time = RouterUtil.calRouteTime(len, 120);
			return time;
		} else {
			return (int)(len + 0.5);
		}
	}
	
	public int getRoutedLength() {
		return gn;
	}
	
	protected RoadEnd m_roadEnd = null;
	
	public RoadEnd getRoadEnd() {
		return m_roadEnd;
	}
	
	//连接到当前节点的Linker
	protected CarLinker m_prevLinker = null;
	
	private CarLinker m_linker = null;
	
	public EarthPos getEPos() {
		return m_roadEnd.m_ePos;
	}
	
	public WorkNode(RoadEnd roadEnd) {
		m_roadEnd = roadEnd;
		m_linker = roadEnd.getLinker();
	}
	
	public void addLinker(CarLinker linker) {
		linker.m_nextLinker = m_linker;
		m_linker = linker;
	}
	
	protected WorkNode m_routePrevNode = null;
	
	public WorkNode getRoutePrevNode() {
		return m_routePrevNode;
	}
	
	protected WorkNode m_openPrevNode = null;
	protected WorkNode m_openNextNode = null;
	
	public void doLinks(WorkNode destNode,RootNode rootNode,Hashtable posNodeHash,int routeType) {
		CarLinker linker = this.m_linker;
		while(linker != null) {
			RoadEnd roadEnd = linker.m_roadEnd;
			WorkNode node = (WorkNode)posNodeHash.get(roadEnd);
			
			if(node == null) {
				node = new WorkNode(roadEnd);
				posNodeHash.put(roadEnd, node);
			} else {
				if(node.m_openPrevNode == rootNode) {//has used node
					linker = linker.m_nextLinker;
					continue;
				}
			}
			
			if(node != null) {
				int linkWeight = linker.getWeight(routeType);
				int theMinWeight = linkWeight + gn;
				if(theMinWeight < node.gn) {
					node.calcFGH(theMinWeight, destNode,routeType);
					node.m_routePrevNode = this;
					node.m_prevLinker = linker;

					rootNode.addOpenLink(node);
				}
			}
			
			linker = linker.m_nextLinker;
		}
		
	}
}
