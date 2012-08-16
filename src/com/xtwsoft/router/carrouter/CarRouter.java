package com.xtwsoft.router.carrouter;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;

import com.xtwsoft.router.RouteLine;
import com.xtwsoft.router.RouteResult;
import com.xtwsoft.utils.EarthPos;

public class CarRouter {
	private static CarRouter m_instance = null;
	
	private CarDataStore m_carDataStore = null;
	private CarRouter(File file) {
		m_carDataStore = new CarDataStore(file);
		CarRGC.initInstance(m_carDataStore);
	}
	
	public static void initInstance(File file) {
		if(m_instance == null) {
			m_instance = new CarRouter(file);
		}
	}

	public static CarRouter getInstance() {
		return m_instance;
	}
	
	
	public RouteResult doRoute(EarthPos startEPos,EarthPos endEPos,int routeType) {
		RouteResult routeResult = new RouteResult(startEPos,endEPos,routeType);
		try {
			if(routeResult.getError() != null) {
				return routeResult;
			}
			
			StartNode startNode = (StartNode)CarRGC.getInstance().createEndNode(startEPos,true);
			if(startNode == null) {
				routeResult.setError("不能定位导航起点！");
				return routeResult;
			}
			StopNode stopNode = (StopNode)CarRGC.getInstance().createEndNode(endEPos,false);
			if(stopNode == null) {
				routeResult.setError("不能定位导航终点！");
				return routeResult;
			}
			
			routeResult.setStartNode(startNode);
			routeResult.setStopNode(stopNode);
			
			RootNode rootNode = new RootNode();
			Hashtable posNodeHash = new Hashtable();
			
			String info = startNode.doInit(posNodeHash);
			if(info != null) {
				routeResult.setError(info);
				return routeResult;
			}
			
			info = stopNode.doInit(posNodeHash);
			if(info != null) {
				routeResult.setError(info);
				return routeResult;
			}
			//经过定位处理，原来不相同的首尾点可能变成相同。所以此次加上判断
			if(startNode.m_roadEnd.equals(stopNode.m_roadEnd)) {
				routeResult.setError("导航起止点相同！");//此处可能需改为首位点连线
				return routeResult;
			}
			
			//判断首尾点是否在一条线上或重叠等情况
			startNode.setStopNode(stopNode,routeType);
			
			//加入开始节点到open链表
			rootNode.addOpenLink(startNode);
			
			WorkNode currNode = null;
			while(rootNode.hasNode()) {
				currNode = rootNode.popFirstNode();
				
				if(currNode == stopNode) {
					RouteLine routeLine = buildRouteLine(currNode,routeType);
					routeResult.addRouteLine(routeLine);
					break;
				}
				
				currNode.doLinks(stopNode,rootNode,posNodeHash,routeType);
			}
			
			
			//清空，加快内存释放
			posNodeHash.clear();
		} catch(Exception ex) {
			ex.printStackTrace();
			routeResult.setError("导航异常:" + ex.getMessage());

		}
		return routeResult;
	}
	
	private RouteLine buildRouteLine(WorkNode destNode,int routeType) {
		//从终点回溯所有经过的点
		LinkedList workNodeList = new LinkedList();

		WorkNode usedNode = destNode;
		while(usedNode != null) {
			workNodeList.addFirst(usedNode);
			usedNode = usedNode.getRoutePrevNode();
		}
		return new CarRouteLine(routeType,workNodeList);
	}
	
}
