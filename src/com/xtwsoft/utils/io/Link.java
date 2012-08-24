/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午09:48:05
 */
package com.xtwsoft.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.xtwsoft.utils.Bounds;

public class Link extends Node {

	private boolean m_isLeaf;
	protected int m_nodeCount = 0;
	protected Node[] m_nodes;

	protected Link(int maxLinkEntries, boolean isLeaf) {
		this.m_nodes = new Node[maxLinkEntries + 1];
		this.m_isLeaf = isLeaf;
	}

	protected Link() {
	}

	protected void addNode(Node node) {
		this.m_nodes[this.m_nodeCount++] = node;
		node.setLinkParent(this);
		if (this.m_bounds == null) {
			this.m_bounds = new Bounds(node.getBounds());
		} else {
			this.m_bounds.expandToInclude(node.getBounds());
		}
		expandBounds(node.getBounds());
	}
	
	//just insert node 
	protected void buildNode(Node node,int index) {
		m_nodes[index] = node;
		node.m_parent = this;
	}

	private void expandBounds(Bounds bounds) {
		if (this.m_parent != null) {
			m_parent.m_bounds.expandToInclude(bounds);
			m_parent.expandBounds(bounds);
		}
	}

	protected void clear() {
		Arrays.fill(this.m_nodes, null);
		this.m_nodeCount = 0;
		this.m_bounds = null;
	}

	protected boolean isLeaf() {
		return m_isLeaf;
	}

	protected int getNodeCount() {
		return this.m_nodeCount;
	}

	protected Node getNode(int n) {
		return (Node) this.m_nodes[n];
	}

	protected Link chooseLeaf(Node newNode) {
		if (isLeaf()) {
			return this;
		}
		// Find the best Node
		Node best = null;
		double lastArea = Double.POSITIVE_INFINITY;
		double currentArea = 0d;

		for (int i = 0; i < m_nodeCount; i++) {
			Node theNode = m_nodes[i];

			currentArea = this.getAreaIncrease(theNode.getBounds(), newNode
					.getBounds());

			if (currentArea < lastArea) {
				lastArea = currentArea;
				best = theNode;
			} else if ((currentArea == lastArea)
					&& (this.getNodeArea(best) > this.getNodeArea(theNode))) {
				best = theNode;
			}
		}

		if (best instanceof Link) {
			return ((Link) best).chooseLeaf(newNode);
		}
		return best.getLinkParent();
	}

	private double getNodeArea(Node e) {
		return this.getBoundsArea(e.getBounds());
	}

	private double getBoundsArea(Bounds env) {
		return env.getWidth() * env.getHeight();
	}

	private double getAreaIncrease(Bounds orig, Bounds add) {
		double ret = 0d;

		Bounds env = new Bounds(orig);
		double w = env.getWidth();
		double h = env.getHeight();

		// Expand the bounds
		env.expandToInclude(add);

		double nw = env.getWidth();
		double nh = env.getHeight();

		ret += ((nw - w) * nh); // new height
		ret += ((nh - h) * w); // old width

		return ret;
	}

	private double getAreaDifference(Bounds env, Node e1, Node e2) {
		return this.getBoundsArea(env) - this.getNodeArea(e1)
				- this.getNodeArea(e2);
	}

	protected Link[] splitLink() {

		Node[] firsts = this.quadraticPickSeeds(m_nodes, m_nodeCount);

		ArrayList entries = new ArrayList(m_nodeCount - 2);

		for (int i = 0; i < m_nodeCount; i++) {
			if (!m_nodes[i].equals(firsts[0]) && !m_nodes[i].equals(firsts[1])) {
				entries.add(m_nodes[i]);
			}
		}

		// Clear the link in order to reuse it
		clear();

		Link newLink = new Link(RTree.MaxLinkNodes, isLeaf());

		Link[] ret = new Link[] { this, newLink };
		ret[0].addNode(firsts[0]);
		ret[1].addNode(firsts[1]);

		int pointer = -1;

		while (true) {
			if (entries.size() == 0) {
				break;
			} else {
				/*
				 * If the remaining elements are not enough for reaching the
				 * minLinkElements of a group or are just right to reach it, add
				 * all entries to the group
				 */
				if ((ret[0].getNodeCount() + entries.size()) <= RTree.MinLinkNodes) {
					for (int i = 0; i < entries.size(); i++) {
						ret[0].addNode((Node) entries.get(i));
					}

					break;
				}

				if ((ret[1].getNodeCount() + entries.size()) <= RTree.MinLinkNodes) {
					for (int i = 0; i < entries.size(); i++) {
						ret[1].addNode((Node) entries.get(i));
					}

					break;
				}
			}

			Node toAssign = this.quadraticPickNext(ret, entries);

			double d1 = this.getAreaIncrease(ret[0].getBounds(), toAssign.getBounds());
			double d2 = this.getAreaIncrease(ret[1].getBounds(), toAssign.getBounds());

			if (d1 < d2) {
				pointer = 0;
			} else if (d1 > d2) {
				pointer = 1;
			} else {
				// If areas increase are the same, smallest wins
				d1 = this.getBoundsArea(ret[0].getBounds());
				d2 = this.getBoundsArea(ret[1].getBounds());

				if (d1 < d2) {
					pointer = 0;
				} else if (d1 > d2) {
					pointer = 1;
				} else {
					/*
					 * If areas are the same the one with less entries wins
					 */
					if (ret[0].getNodeCount() < ret[1].getNodeCount()) {
						pointer = 0;
					} else {
						pointer = 1;
					}
				}
			}

			ret[pointer].addNode(toAssign);

			entries.remove(toAssign);
		}
		return ret;
	}

	private Node[] quadraticPickSeeds(Node[] entries, int nodeCount) {
		Node[] ret = new Node[2];
		double choosedD = Double.NEGATIVE_INFINITY;

		// Foreach pair of entries...
		for (int i = 0; i < (nodeCount - 1); i++) {
			Bounds env = new Bounds(entries[i].getBounds());

			for (int j = i + 1; j < nodeCount; j++) {
				env.expandToInclude(entries[j].getBounds());

				// find the inefficency of groupping together
				double actualD = this.getAreaDifference(env, entries[i], entries[j]);

				// Choose the pair with the largest area
				if (actualD > choosedD) {
					choosedD = actualD;
					ret[0] = entries[i];
					ret[1] = entries[j];
				}
			}
		}

		return ret;
	}

	private Node quadraticPickNext(Link[] links, ArrayList entries) {
		double maxDiff = Double.NEGATIVE_INFINITY;

		Node ret = null;
		for (int i = 0; i < entries.size(); i++) {
			Node theNode = (Node) entries.get(i);
			Bounds bounds = theNode.getBounds();
			double d0 = this.getAreaIncrease(links[0].getBounds(), bounds);
			double d1 = this.getAreaIncrease(links[1].getBounds(), bounds);

			double diff = Math.abs(d0 - d1);

			if (diff > maxDiff) {
				maxDiff = diff;
				ret = theNode;
			}
		}

		return ret;
	}

	protected void search(Bounds query, List matchList) {
		for (int i = 0; i < m_nodeCount; i++) {
			Node node = getNode(i);
			if (node.getBounds().intersects(query)) {
				if (node instanceof Link) {
					((Link) node).search(query, matchList);
				} else {
					matchList.add(node);
				}
			}
		}
	}

	protected void searchAll(List matchList) {
		for (int i = 0; i < m_nodeCount; i++) {
			Node node = getNode(i);
			if (node instanceof Link) {
				((Link) node).searchAll(matchList);
			} else {
				matchList.add(node);
			}
		}
	}

	protected void search(Bounds query, NodeLister lister) {
		for (int i = 0; i < m_nodeCount; i++) {
			Node node = getNode(i);
			if (node.getBounds().intersects(query)) {
				if (node instanceof Link) {
					((Link) node).search(query, lister);
				} else {
					lister.addNode(node);
				}
			}
		}
	}

	protected void searchAll(NodeLister lister) {
		for (int i = 0; i < m_nodeCount; i++) {
			Node node = getNode(i);
			if (node instanceof Link) {
				((Link) node).searchAll(lister);
			} else {
				lister.addNode(node);
			}
		}
	}
	
	public void readInputStream(InputStream is) throws IOException {
		super.readInputStream(is);
		
		m_isLeaf = IOUtil.readBoolean(is);

		m_nodeCount = IOUtil.readShort(is);
		m_nodes = new Node[m_nodeCount];
	}
	
	public void writeOutputStream(OutputStream os) throws IOException {
		super.writeOutputStream(os);
		
		IOUtil.writeBoolean(os, m_isLeaf);

		IOUtil.writeShort(os, (short)m_nodeCount);
	}
	
}
