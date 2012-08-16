/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-8 下午03:11:09
 */
package com.xtwsoft.router.carrouter;

import com.xtwsoft.utils.EarthPos;


public class RootNode extends WorkNode {
	public RootNode() {
		super(new RoadEnd(new EarthPos(-1,-1)));
	}
	
	public void addOpenLink(WorkNode workNode) {
		//workNode 可能已经在Link链表中，如果在，先移除。
		if(workNode.m_openPrevNode != null) {
			workNode.m_openPrevNode.m_openNextNode = workNode.m_openNextNode;
			if(workNode.m_openNextNode != null) {
				workNode.m_openNextNode.m_openPrevNode = workNode.m_openPrevNode;
			}
		}
		
		//从开始节点的第二个节点开始，开始节点为起始节点，永远不为空。
		WorkNode currNode = this;
		WorkNode nextNode = currNode.m_openNextNode;
		boolean isAddOpen = false;
		while(nextNode != null) {
			if(nextNode.fn > workNode.fn) {
				currNode.m_openNextNode = workNode;
				workNode.m_openPrevNode = currNode;
				
				workNode.m_openNextNode = nextNode;
				nextNode.m_openPrevNode = workNode;
				
				isAddOpen = true;
				break;
			}
			currNode = nextNode;
			nextNode = currNode.m_openNextNode;
		}
		if(!isAddOpen) {
			currNode.m_openNextNode = workNode;
			workNode.m_openPrevNode = currNode;
			workNode.m_openNextNode = null;
		}
	}

	public void removeOpenLink(WorkNode workNode) {
		if(workNode.m_openPrevNode != null) {
			workNode.m_openPrevNode.m_openNextNode = workNode.m_openNextNode;
			if(workNode.m_openNextNode != null) {
				workNode.m_openNextNode.m_openPrevNode = workNode.m_openPrevNode;
			}
		}
	}
	
	public boolean hasNode() {
		return m_openNextNode != null;
	}
	
	public WorkNode popFirstNode() {
		if(m_openNextNode != null) {
			WorkNode firstNode = m_openNextNode;//(WorkNode)openNodes.remove(openNodes.size() - 1);
			m_openNextNode = firstNode.m_openNextNode;
			if(m_openNextNode != null) {
				m_openNextNode.m_openPrevNode = this;
			}
			return firstNode;
		}
		return null;
	}
}
