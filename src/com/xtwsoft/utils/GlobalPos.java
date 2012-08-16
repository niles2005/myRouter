/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.xtwsoft.utils;

import java.io.Serializable;

/**
 * @author lnie <p/> TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GlobalPos implements Serializable {
	public double posX = 0;

	public double posY = 0;

	public GlobalPos(double x, double y) {
		posX = x;
		posY = y;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public double getPosX() {
		return posX;
	}

	public double getLongitude() {
		double lon = posX * 360 - Math.floor(posX) * 360 - 180;
		if (lon > 0) {
			lon = (int) (lon * EarthPos.SCALE_FACTOR + 0.5) / EarthPos.SCALE_FACTOR ;
		} else {
			lon = (int) (lon * EarthPos.SCALE_FACTOR - 0.5) / EarthPos.SCALE_FACTOR;
		}

		return lon;
	}

	public double getPosY() {
		return posY;
	}

	public double getLatitude() {
		if (posY < 0 || posY > 1) {
			return Double.MAX_VALUE;
		}
		double lat = (0.5 - posY) * 2 * Math.PI;// compare to lon
		lat = Math.atan(Math.exp(lat)) / Math.PI * 360 - 90;
		if (lat > 0) {
			lat = (int)(lat * EarthPos.SCALE_FACTOR + 0.5) / EarthPos.SCALE_FACTOR;
		} else {
			lat = (int)(lat * EarthPos.SCALE_FACTOR - 0.5) / EarthPos.SCALE_FACTOR;
		}
		return lat;
	}

	public EarthPos convert2EarthPos() {
		if (posY < 0 || posY > 1) {
			return null;
		}
		double lon = posX * 360 - Math.floor(posX) * 360 - 180;
		if (posX == 1) {
			lon = 180.0;
		}

		double lat = (0.5 - posY) * 2 * Math.PI;// compare to lon
		lat = Math.atan(Math.exp(lat)) / Math.PI * 360 - 90;

		return new EarthPos(lon, lat);
	}

	public String toString() {
		return "GlobalPos:" + posX + "," + posY;
	}
}