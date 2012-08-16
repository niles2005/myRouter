/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午09:47:15
 */
package com.xtwsoft.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.xtwsoft.utils.Bounds;

public class RTree implements StreamSerializable {
	
	public static final int MaxLinkNodes = 100;
	public static final int MinLinkNodes = 50;

	private Link m_root = null;

	private void setRoot(Link link) {
		this.m_root = link;
		this.m_root.setLinkParent(null);
	}

	public RTree() {
		m_root = new Link(MaxLinkNodes, true);
	}

	public Bounds getBounds() {
		return (m_root == null) ? null : m_root.getBounds();
	}

	public List search(Bounds query) {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireShared();
			ArrayList matches = new ArrayList();

			m_root.search(query, matches);

			return matches;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	public List searchAll() {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireShared();
			ArrayList matches = new ArrayList();

			m_root.searchAll(matches);

			return matches;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	public void search(Bounds query,NodeLister lister) {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireShared();

			m_root.search(query, lister);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	public void searchAll(NodeLister lister) {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireShared();
			m_root.searchAll(lister);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	public void search(Bounds query,ArrayList list) {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireShared();

			m_root.search(query, list);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	public void searchAll(ArrayList list) {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireShared();
			m_root.searchAll(list);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	public void insert(Node node) {
		Lock lock = null;
		try {
			lock = LockManager.getInstance().aquireExclusive();

			Link leaf = m_root.chooseLeaf(node);

			leaf.addNode(node);

			if (leaf.getNodeCount() > MaxLinkNodes) {
				Link[] split = leaf.splitLink();
				this.adjustTree(split[0], split[1]);
			}

		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			LockManager.getInstance().release(lock);
		}
	}

	private void adjustTree(Link link1, Link link2) {
		if (link2 == null) {
			return;
		}

		while (true) {
			if (link1 == m_root) {
				if (link2 != null) {
					Link newRoot = new Link(MaxLinkNodes, false);
					newRoot.addNode(link1);
					newRoot.addNode(link2);

					setRoot(newRoot);
				}
				break;
			}

			Link p = (Link) link1.getLinkParent();

			if (link2 != null) {
				p.addNode(link2);

				if (p.getNodeCount() > MaxLinkNodes) {
					Link[] split = p.splitLink();
					link1 = split[0];
					link2 = split[1];
				} else {
					link2 = null;
					link1 = p;
				}
			} else {
				link1 = p;
			}
		}
	}
	
    //广度优先排列
    private void listNodes(ArrayList list, Hashtable hash,Link node) {
        Link theLink = (Link) node;
        for (int i = 0; i < theLink.getNodeCount(); i++) {
            Node subNode = theLink.getNode(i);
            hash.put(subNode, list.size());
            list.add(subNode);
        }
        for (int i = 0; i < theLink.getNodeCount(); i++) {
            Node subNode = theLink.getNode(i);
            if (subNode instanceof Link) {
                listNodes(list, hash,(Link) subNode);
            }
        }
    }
	
    private NodeBuilder m_nodeBuilder = null;
    
    //此处的构造函数目的是为了从文件反序列化对象，所以必须传入一个非空的NodeBuilder
    public RTree(NodeBuilder nodeBuilder) {
    	m_nodeBuilder = nodeBuilder;
    	if(m_nodeBuilder == null) {
    		throw new RuntimeException("NodeBuilder should not be null!");
    	}
    }
	
    //此处写和读的设计里采用List和Hashtable，主要的目的是不用在Node或Link中记录对象的关系属性，即链接属性，
    //而是在此处记录，便于Node和Link的清洁。
    public void readInputStream(InputStream is) throws IOException {
		int nodeNum = IOUtil.readInt(is);
		ArrayList list = new ArrayList(nodeNum);
		//hash<Node,link's current sub node index>
		Hashtable hash = new Hashtable();
		
		for(int i=0;i<nodeNum;i++) {
			byte nodeType = IOUtil.readByte(is);
			int parentIndex = IOUtil.readInt(is);
			Node theNode = null;
			if(nodeType == 'L') {
				theNode = new Link();
				theNode.readInputStream(is);
				
				list.add(theNode);
				hash.put(theNode, 0);
			} else {//'N'
				theNode = m_nodeBuilder.createNode();
				theNode.readInputStream(is);

				list.add(theNode);
			}
			if(parentIndex >= 0) {
				Link link = (Link)list.get(parentIndex);
				int index = (Integer)hash.get(link);
				link.buildNode(theNode, index);
				hash.put(link,index + 1);
			} else {
				if(i == 0 && theNode instanceof Link) {
					this.m_root = (Link)theNode;
				}
			}
		}
//		ArrayList tmpList = new ArrayList();
//		//hash<Node,Node index in list>
//		Hashtable tmpHash = new Hashtable();
//		
//		tmpHash.put(m_root, tmpList.size());
//		tmpList.add(m_root);
//		
//		listNodes(tmpList,tmpHash,this.m_root);
	}
	
    public void writeOutputStream(OutputStream os) throws IOException {
		ArrayList list = new ArrayList();
		//hash<Node,Node index in list>
		Hashtable hash = new Hashtable();
		
        hash.put(m_root, list.size());
        list.add(m_root);
		
		listNodes(list,hash,this.m_root);
		
		IOUtil.writeInt(os, list.size());
		for(int i=0;i<list.size();i++) {
			Node node = (Node)list.get(i);
			Link parentLink = node.m_parent;
			int parentIndex = -1;
			if(parentLink != null) {
				parentIndex = (Integer)hash.get(parentLink);
			}
			
			if(node instanceof Link) {
				IOUtil.writeByte(os,(byte)'L');
			} else {//Node
				IOUtil.writeByte(os,(byte)'N');
			}
			IOUtil.writeInt(os, parentIndex);
			node.writeOutputStream(os);
		}
	}
}
