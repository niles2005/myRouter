/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-14 下午10:22:42
 */
package com.xtwsoft.router;

import java.util.ArrayList;

public class RouteLine {
	protected ArrayList<RouteSegment> m_segmentList = new ArrayList<RouteSegment>();
	
	public void addRouteSegment(RouteSegment segment) {
		m_segmentList.add(segment);
	}

	public void buildJSONResult(StringBuffer strBuff,int index) {
		
	}
}
