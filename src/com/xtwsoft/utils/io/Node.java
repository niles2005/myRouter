/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time£º2011-9-26 ÏÂÎç09:47:15
 */
package com.xtwsoft.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.xtwsoft.utils.Bounds;

public abstract class Node implements StreamSerializable {
	
    protected Bounds m_bounds;
    protected Link m_parent;

    public Node() {
    }

    public Bounds getBounds() {
        return m_bounds;
    }

    void setBounds(Bounds envelope) {
        m_bounds = envelope;
    }

    public Link getLinkParent() {
        return this.m_parent;
    }

    public void setLinkParent(Link link) {
        this.m_parent = link;
    }

	public void readInputStream(InputStream is) throws IOException {
    	int minX = IOUtil.readInt(is);
    	int minY = IOUtil.readInt(is);
    	int maxX = IOUtil.readInt(is);
    	int maxY = IOUtil.readInt(is);
		m_bounds = new Bounds(minX,minY,maxX,maxY);
	}
	
	public void writeOutputStream(OutputStream os) throws IOException {
		IOUtil.writeInt(os, m_bounds.getMinX());
		IOUtil.writeInt(os, m_bounds.getMinY());
		IOUtil.writeInt(os, m_bounds.getMaxX());
		IOUtil.writeInt(os, m_bounds.getMaxY());
	}

}
