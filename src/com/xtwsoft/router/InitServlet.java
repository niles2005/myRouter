/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2012-8-24 下午03:27:04
 */
package com.xtwsoft.router;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.xtwsoft.router.carrouter.CarRouter;

public class InitServlet extends HttpServlet {
	public void init(ServletConfig servletConfig) throws ServletException {
		try {
			// System.err.println("init......");
			String realPath = servletConfig.getServletContext().getRealPath("");
			File routeDataFile = new File(realPath, "sh.rmd");
			if (routeDataFile.exists()) {
				CarRouter.initInstance(routeDataFile);
			} else {
				System.err.println("sdata is not found!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String args[]) {

	}
}
