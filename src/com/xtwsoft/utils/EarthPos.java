/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.xtwsoft.utils;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * @author lnie <p/> TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class EarthPos implements Serializable {
	public static double SCALE_FACTOR = 10000000D;

	public static float FSCALE_FACTOR = 10000000F;

	private int longitude;

	private int latitude;

	public EarthPos(int lon, int lat) {
		longitude = lon;
		latitude = lat;
	}

	public EarthPos(double lon, double lat) {
		if (lon > 0) {
			longitude = (int) (lon * SCALE_FACTOR + 0.5);
		} else {
			longitude = (int) (lon * SCALE_FACTOR - 0.5);
		}
		if (lat > 0) {
			latitude = (int) (lat * SCALE_FACTOR + 0.5);
		} else {
			latitude = (int) (lat * SCALE_FACTOR - 0.5);
		}
	}

	public static EarthPos buildEarthPos(String strLatLon) {
		String[] strs = strLatLon.split(",");
		if(strs.length == 2) {
			return buildEarthPos(strs[0],strs[1]);
		}
		return null;
	}
	
	public static EarthPos buildEarthPos(String strLat, String strLon) {
		strLat = strLat.trim();
		strLon = strLon.trim();
		if(strLat.length() > 0 && strLon.length() > 0) {
			
		} else {
			return null;
		}
		try {
			if(strLat.indexOf(".") == -1 && strLon.indexOf(".") == -1) {
				int lat = Integer.parseInt(strLat);
				int lon = Integer.parseInt(strLon);
				return new EarthPos(lon,lat);
			} else {
				double dLat = Double.parseDouble(strLat);
				double dLon = Double.parseDouble(strLon);
				return new EarthPos(dLon,dLat);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public int getILon() {
		return longitude;
	}

	public int getILat() {
		return latitude;
	}

	public int getI7Lon() {
		return longitude;
	}

	public int getI7Lat() {
		return latitude;
	}

	public int getI6Lon() {
		if(longitude < 0) {
			return (longitude - 5) / 10;
		} else {
			return (longitude + 5) / 10;
		}
	}

	public int getI6Lat() {
		if(latitude < 0) {
			return (latitude - 5) / 10;
		} else {
			return (latitude + 5) / 10;
		}
	}

	public double getLongitude() {
		return 1.0 * longitude / SCALE_FACTOR;
	}

	public double getLatitude() {
		return 1.0 * latitude / SCALE_FACTOR;
	}

	public String getLongitudeString() {
		return myformat.format(1.0 * longitude / FSCALE_FACTOR);
	}

	static DecimalFormat myformat = new DecimalFormat("0.000000");

	public String getLatitudeString() {
		return myformat.format(1.0 * latitude / FSCALE_FACTOR);
	}

	public GlobalPos convert2GlobalPos() {
		double posX = (1.0 * longitude / SCALE_FACTOR + 180) / 360;
		double posY = Math.log(Math
				.tan((1.0 * latitude / SCALE_FACTOR + 90) * Math.PI / 360));
		posY = 0.5 - posY / 2 / Math.PI;
		return new GlobalPos(posX, posY);
	}

	public String getLatLonString() {
		String strEarthPos = getLatitudeString() + "," + getLongitudeString();
		return strEarthPos;
	}
	
	public boolean equals(EarthPos ePos) {
		if(ePos != null) {
			if(latitude == ePos.latitude && longitude == ePos.longitude) {
				return true;
			}
		}
		return false;
	}
}
