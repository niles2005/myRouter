package com.xtwsoft.utils.gis;

import java.util.ArrayList;

import com.xtwsoft.utils.Bounds;
import com.xtwsoft.utils.EarthPos;

public class SutherlandHodgmanClip {
	private static EarthPos intersectionPoint(byte EdgeID, EarthPos p1, EarthPos p2,
			int edgeValue) {
		if (EdgeID == 0)
			return new EarthPos(edgeValue,(int) (p1.getILat() + 1.0 * (edgeValue - p1.getILon())
					* (p2.getILat() - p1.getILat()) / (p2.getILon() - p1.getILon())));
		if (EdgeID == 2)
			return new EarthPos(edgeValue,(int) (p1.getILat() + 1.0 * (edgeValue - p1.getILon())
					* (p2.getILat() - p1.getILat()) / (p2.getILon() - p1.getILon())));
		if (EdgeID == 1)
			return new EarthPos((int) (p1.getILon() + 1.0 * (edgeValue - p1.getILat())
					* (p2.getILon() - p1.getILon()) / (p2.getILat() - p1.getILat())),edgeValue);
		if (EdgeID == 3) {
			return new EarthPos((int) (p1.getILon() + 1.0 * (edgeValue - p1.getILat())
					* (p2.getILon() - p1.getILon()) / (p2.getILat() - p1.getILat())),edgeValue);
		} else
			return null;
	}

	private static boolean isInsideClipWindow(EarthPos p, byte EdgeID,
			int edgeValue) {
		if (EdgeID == 0)
			return p.getILon() >= edgeValue;
		if (EdgeID == 2)
			return p.getILon() <= edgeValue;
		if (EdgeID == 3)
			return p.getILat() >= edgeValue;
		if (EdgeID == 1)
			return p.getILat() <= edgeValue;
		else
			return false;
	}

	public static ArrayList clipPolygon(ArrayList polygon, Bounds bounds) {
		ArrayList workPoly = polygon;
		if (workPoly.size() <= 2) {
			return null;
		}
		ArrayList clippedPoly = null;
		for (byte edgeID = 0; edgeID < 4; edgeID++) {
			clippedPoly = new ArrayList();
			int edge = getEdgeValue(bounds, edgeID);
			for (int i = 0; i < workPoly.size() - 1; i++) {
				EarthPos p = (EarthPos) workPoly.get(i);
				EarthPos p2 = (EarthPos) workPoly.get(i + 1);

				if (isInsideClipWindow(p, edgeID, edge)) {
					if (i == 0) {
						clippedPoly.add(p);
					}
					if (isInsideClipWindow(p2, edgeID, edge)) {
						clippedPoly.add(p2);
					} else {
						EarthPos thePoint = intersectionPoint(edgeID, p, p2, edge);
						clippedPoly.add(thePoint);
					}
					continue;
				}
				if (isInsideClipWindow(p2, edgeID, edge)) {
					clippedPoly.add(intersectionPoint(edgeID, p, p2, edge));
					clippedPoly.add(p2);
				}
			}

			if (clippedPoly.size() > 0) {
				if (clippedPoly.get(0) != clippedPoly
						.get(clippedPoly.size() - 1))
					clippedPoly.add(clippedPoly.get(0));
			}
			workPoly = clippedPoly;
		}

		if (!hasPolygonBody(clippedPoly)) {
			return null;
		}
		return clippedPoly;
	}

	public static boolean hasPolygonBody(ArrayList clippedPoly) {
		Bounds bounds = new Bounds();
		for (int j = 0; j < clippedPoly.size(); j++) {
			EarthPos point = (EarthPos) clippedPoly.get(j);
			bounds.expandToInclude(point.getILat(), point.getILon());
			if (bounds.getWidth() != 0 && bounds.getHeight() != 0) {
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList clipLine(ArrayList ePosList, Bounds bounds) {
		if (ePosList.size() <= 1) {
			return null;
		}
		ArrayList lineList = new ArrayList();
		lineList.add(ePosList);
		ArrayList resultList = new ArrayList();
		for (byte edgeID = 0; edgeID < 4; edgeID++) {
			for (int i = 0; i < lineList.size(); i++) {
				ArrayList theLine = (ArrayList) lineList.get(i);
				clipLine(theLine, resultList, edgeID, bounds);
			}
			lineList = resultList;
			resultList = new ArrayList();
		}

		return lineList;
	}

	private static void clipLine(ArrayList line, ArrayList lineList,
			byte edgeID, Bounds bounds) {
		ArrayList workLine = new ArrayList();
		int edge = getEdgeValue(bounds, edgeID);
		for (int i = 0; i < line.size() - 1; i++) {
			EarthPos p = (EarthPos) line.get(i);
			EarthPos p2 = (EarthPos) line.get(i + 1);

			if (isInsideClipWindow(p, edgeID, edge)) {
				if (i == 0) {
					workLine.add(p);
				}
				if (isInsideClipWindow(p2, edgeID, edge)) {
					workLine.add(p2);
				} else {
					EarthPos thePoint = intersectionPoint(edgeID, p, p2, edge);
					workLine.add(thePoint);
					lineList.add(workLine);
					workLine = new ArrayList();
				}
				continue;
			}
			if (isInsideClipWindow(p2, edgeID, edge)) {
				workLine.add(intersectionPoint(edgeID, p, p2, edge));
				workLine.add(p2);
			}
		}
		if (workLine.size() >= 2) {
			lineList.add(workLine);
		}
	}

	private static int getEdgeValue(Bounds bounds, byte EdgeID) {
		if (EdgeID == 0)
			return bounds.getMinY();
		if (EdgeID == 2)
			return bounds.getMaxY();
		if (EdgeID == 3)
			return bounds.getMinX();
		if (EdgeID == 1)
			return bounds.getMaxX();
		else
			return -1;
	}

	private static void printPoints(String id, ArrayList dataList) {
		StringBuffer strBuff = new StringBuffer();
		for (int i = 0; i < dataList.size(); i++) {
			EarthPos p = (EarthPos) dataList.get(i);
			strBuff.append(p.getILat() + "," + p.getILon() + ",");
		}
		System.out.println(id + " --> " + strBuff.toString());
	}
}
