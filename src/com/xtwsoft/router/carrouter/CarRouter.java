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
				routeResult.setError("���ܶ�λ������㣡");
				return routeResult;
			}
			StopNode stopNode = (StopNode)CarRGC.getInstance().createEndNode(endEPos,false);
			if(stopNode == null) {
				routeResult.setError("���ܶ�λ�����յ㣡");
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
			//������λ����ԭ������ͬ����β����ܱ����ͬ�����Դ˴μ����ж�
			if(startNode.m_roadEnd.equals(stopNode.m_roadEnd)) {
				routeResult.setError("������ֹ����ͬ��");//�˴��������Ϊ��λ������
				return routeResult;
			}
			
			//�ж���β���Ƿ���һ�����ϻ��ص������
			startNode.setStopNode(stopNode,routeType);
			
			//���뿪ʼ�ڵ㵽open����
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
			
			
			//��գ��ӿ��ڴ��ͷ�
			posNodeHash.clear();
		} catch(Exception ex) {
			ex.printStackTrace();
			routeResult.setError("�����쳣:" + ex.getMessage());

		}
		return routeResult;
	}
	
	private RouteLine buildRouteLine(WorkNode destNode,int routeType) {
		//���յ�������о����ĵ�
		LinkedList workNodeList = new LinkedList();

		WorkNode usedNode = destNode;
		while(usedNode != null) {
			workNodeList.addFirst(usedNode);
			usedNode = usedNode.getRoutePrevNode();
		}
		return new CarRouteLine(routeType,workNodeList);
	}
	
}
