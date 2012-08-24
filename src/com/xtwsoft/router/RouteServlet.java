/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2010-9-27 下午09:05:37
 */
package com.xtwsoft.router;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xtwsoft.router.carrouter.CarRGC;
import com.xtwsoft.router.carrouter.CarRouter;
import com.xtwsoft.router.carrouter.RouteUtil;
import com.xtwsoft.utils.EarthPos;

public class RouteServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		ServletOutputStream sos = response.getOutputStream();
		response.setContentType("text/html; charset=UTF-8");
		
		String p = request.getParameter("p");
		if(p != null) {
			String strRouteType = request.getParameter("type");
			int routeType = 0;//Time:0  Length :1
			if(strRouteType != null) {
				if("t".equals(strRouteType)) {
					routeType = RouteUtil.RouteTypeTime;
				} else if("l".equals(strRouteType)) {
					routeType = RouteUtil.RouteTypeLength;
				} else {
					return;
				}
			}
			String[] strs = p.split(",");
			if(strs.length == 4) {
				int lat1 = Integer.parseInt(strs[0].trim());
				int lon1 = Integer.parseInt(strs[1].trim());
				int lat2 = Integer.parseInt(strs[2].trim());
				int lon2 = Integer.parseInt(strs[3].trim());
				EarthPos ePos1 = new EarthPos(lon1,lat1);
				EarthPos ePos2 = new EarthPos(lon2,lat2);
				RouteResult result = CarRouter.getInstance().doRoute(ePos1, ePos2,routeType);
				String strResult = result.toString();
				if(strResult != null) {
					sos.write(strResult.getBytes("UTF-8"));	
				}
			}			
		} else {
			String v = request.getParameter("v");
			if(v != null) {
				String[] strs = v.split(",");
				if(strs.length == 2) {
					int lat = Integer.parseInt(strs[0].trim());
					int lon = Integer.parseInt(strs[1].trim());
					EarthPos ePos = new EarthPos(lon,lat);
					doValidEarthPos(ePos,sos);
				}
			}
		}
	}

	private void doValidEarthPos(EarthPos ePos,ServletOutputStream sos) throws IOException {
		EarthPos roadEPos = CarRGC.getInstance().locatePos2Road(ePos);
		if(roadEPos != null) {
			String resultTxt = roadEPos.getILat() + "," + roadEPos.getILon();
			if(resultTxt != null) {
				sos.write(resultTxt.getBytes("UTF-8"));
			}
		}
	}
}
