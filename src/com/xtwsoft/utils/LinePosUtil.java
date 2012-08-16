package com.xtwsoft.utils;

import java.util.ArrayList;

public class LinePosUtil {
	//产生point3的点到Point1和Point2到连线到垂直点。垂直点可能不在线段上
//	public static Point getPointAtLine(Point point1,Point point2,Point point3) {
//		if(point1.x == point2.x) {
//			return new Point(point1.x,point3.y);
//		} else if(point1.y == point2.y) {
//			return new Point(point3.x,point1.y);
//		} else {
//			double k = 1.0 * ( point2.y - point1. y ) / (point2.x - point1.x );
//			double x = ( k  * k * point1.x + k * (point3.y - point1.y ) + point3.x ) / ( k * k + 1);
//			double y = k * ( x - point1.x) + point1.y;
//			return new Point((int)x,(int)y);
//		}
//	}

	//求点3在点1和点2到线段上的垂直点，可以是在延长线上
	public static EarthPos getPointOnLine(EarthPos ePos1,EarthPos ePos2,EarthPos ePos3) {
		
		if(ePos1.getILon() == ePos2.getILon()) {
			return new EarthPos(ePos1.getILon(),ePos3.getILat());
		} else if(ePos1.getILat() == ePos2.getILat()) {
			return new EarthPos(ePos3.getILon(),ePos1.getILat());
		} else {
			GlobalPos gPos1 = ePos1.convert2GlobalPos();
			GlobalPos gPos2 = ePos2.convert2GlobalPos();
			GlobalPos gPos3 = ePos3.convert2GlobalPos();

			double k = 1.0 * (gPos2.posY - gPos1.posY) / (gPos2.posX - gPos1.posX);
			double gx = (k  * k * gPos1.posX + k * (gPos3.posY - gPos1.posY ) + gPos3.posX) / (k * k + 1);
			double gy = k * ( gx - gPos1.posX) + gPos1.posY;
			GlobalPos gPos = new GlobalPos(gx,gy);
			
			return gPos.convert2EarthPos();
		}
	}

	//求点3在点1和点2到线段上的垂直点，只能在线段上，不能在延长线上
	public static EarthPos getPointInLine(EarthPos ePos1,EarthPos ePos2,EarthPos ePos3) {
		
		if(ePos1.getILon() == ePos2.getILon()) {
			if(ePos3.getILat() >= ePos1.getILat() && ePos3.getILat() <= ePos2.getILat()) {
				
			} else if(ePos3.getILat() <= ePos1.getILat() && ePos3.getILat() >= ePos2.getILat()) {
				
			} else {
				return null;
			}
			return new EarthPos(ePos1.getILon(),ePos3.getILat());
		} else if(ePos1.getILat() == ePos2.getILat()) {
			if(ePos3.getILon() >= ePos1.getILon() && ePos3.getILon() <= ePos2.getILon()) {
				
			} else if(ePos3.getILon() <= ePos1.getILon() && ePos3.getILon() >= ePos2.getILon()) {
				
			} else {
				return null;
			}
			return new EarthPos(ePos3.getILon(),ePos1.getILat());
		} else {
			GlobalPos gPos1 = ePos1.convert2GlobalPos();
			GlobalPos gPos2 = ePos2.convert2GlobalPos();
			GlobalPos gPos3 = ePos3.convert2GlobalPos();

			double k = 1.0 * (gPos2.posY - gPos1.posY) / (gPos2.posX - gPos1.posX);
			double gx = (k  * k * gPos1.posX + k * (gPos3.posY - gPos1.posY ) + gPos3.posX) / (k * k + 1);
			double gy = k * ( gx - gPos1.posX) + gPos1.posY;
			GlobalPos gPos = new GlobalPos(gx,gy);
//			double ll0 = getLen(gPos1,gPos2);
//			double ll1 = getLen(gPos1,gPos);
//			double ll2 = getLen(gPos2,gPos);
//			System.err.println(ll0 + "," + ll1 + "," + ll2);
			if(Math.abs(getLen(gPos1,gPos2) - getLen(gPos1,gPos) - getLen(gPos2,gPos)) < 0.0001) {
				return gPos.convert2EarthPos();
			}
			return null;
		}
	}

	static double EdgeLen = Math.pow(2, 17);
	public static double getLen(GlobalPos gPos1,GlobalPos gPos2) {
		return Math.sqrt(EdgeLen * (gPos1.posX - gPos2.posX) * EdgeLen * (gPos1.posX - gPos2.posX) +  EdgeLen *  (gPos1.posY - gPos2.posY) * EdgeLen *  (gPos1.posY - gPos2.posY));
	}
	
	//取得一点在一条线段上的距离最短的点
	public static EarthPos getLPointInLine(EarthPos ePos,EarthPos[] ePosArray) {
		GlobalPos gPos0 = ePos.convert2GlobalPos();
		double minLen = Double.MAX_VALUE;
		EarthPos minEPos = null;
		for(int j=0;j<ePosArray.length - 1;j++) {
			EarthPos ePos1 = ePosArray[j];
			EarthPos ePos2 = ePosArray[j + 1];

			EarthPos LEPos = LinePosUtil.getPointInLine(ePos1, ePos2, ePos);
			if(LEPos != null) {
				GlobalPos LGPos = LEPos.convert2GlobalPos();
				
				double len = LinePosUtil.getLen(LGPos, gPos0);
				if(len < minLen) {
					minLen = len;
					minEPos = LEPos;
				}
			}
			
			GlobalPos LGPos1 = ePos1.convert2GlobalPos();
			double len = LinePosUtil.getLen(LGPos1, gPos0);
			if(len < minLen) {
				minLen = len;
				minEPos = ePos1;
			}
			
			GlobalPos LGPos2 = ePos2.convert2GlobalPos();
			len = LinePosUtil.getLen(LGPos2, gPos0);
			if(len < minLen) {
				minLen = len;
				minEPos = ePos2;
			}
			
		}		
		return minEPos;
	}
	
	//现在取最近的垂直点，如果垂点不在线段上，取最近的端点
	public static EarthPos getLPointInLine(EarthPos ePos,ArrayList ePosList) {
		GlobalPos gPos0 = ePos.convert2GlobalPos();
		double minLen = Double.MAX_VALUE;
		EarthPos minEPos = null;
		for(int j=0;j<ePosList.size() - 1;j++) {
			EarthPos ePos1 = (EarthPos)ePosList.get(j);
			EarthPos ePos2 = (EarthPos)ePosList.get(j + 1);

			EarthPos LEPos = LinePosUtil.getPointInLine(ePos1, ePos2, ePos);
			if(LEPos != null) {
				GlobalPos LGPos = LEPos.convert2GlobalPos();
				
				double len = LinePosUtil.getLen(LGPos, gPos0);
				if(len < minLen) {
					minLen = len;
					minEPos = LEPos;
				}
			}
			
			GlobalPos LGPos1 = ePos1.convert2GlobalPos();
			double len = LinePosUtil.getLen(LGPos1, gPos0);
			if(len < minLen) {
				minLen = len;
				minEPos = ePos1;
			}
			
			GlobalPos LGPos2 = ePos2.convert2GlobalPos();
			len = LinePosUtil.getLen(LGPos2, gPos0);
			if(len < minLen) {
				minLen = len;
				minEPos = ePos2;
			}
			
		}		
		return minEPos;
	}
	
	//for each line is ArrayList<Point>
	public static EarthPos getLPosInLineList(EarthPos ePos,ArrayList lineList) {
		GlobalPos gPos0 = ePos.convert2GlobalPos();
		double minLen = Double.MAX_VALUE;
		EarthPos minEPos = null;
		
		for(int i=0;i<lineList.size();i++) {
			ArrayList pointList = (ArrayList)lineList.get(i);
			for(int j=0;j<pointList.size() - 1;j++) {
//				Point p1 = (Point)pointList.get(j);
//				Point p2 = (Point)pointList.get(j + 1);
				EarthPos ePos1 = (EarthPos)pointList.get(j);
				EarthPos ePos2 = (EarthPos)pointList.get(j + 1);

				EarthPos LEPos = LinePosUtil.getPointInLine(ePos1, ePos2, ePos);
				if(LEPos != null) {
					GlobalPos LGPos = LEPos.convert2GlobalPos();
					
					double len = LinePosUtil.getLen(LGPos, gPos0);
					if(len < minLen) {
						minLen = len;
						minEPos = LEPos;
					}
				}
				
				GlobalPos LGPos1 = ePos1.convert2GlobalPos();
				double len = LinePosUtil.getLen(LGPos1, gPos0);
				if(len < minLen) {
					minLen = len;
					minEPos = ePos1;
				}
				
				GlobalPos LGPos2 = ePos2.convert2GlobalPos();
				len = LinePosUtil.getLen(LGPos2, gPos0);
				if(len < minLen) {
					minLen = len;
					minEPos = ePos2;
				}
				
			}		
		}
		
		return minEPos;
	}
	
	//for each line is ArrayList<Point>, 此处结果包含LPoint的两端（一共3点，LPoint居中）
	//返回 为EarthPos[3],分别为leftEndPoint,LPoint,rightEndPoint
	//因为SutherlandHodgmanClip的原因，可能剪切数据中没有feature的真实点(比如剪切范围里没有Feature的点)
	//leftEndPoint 和 rightEndPoint 不一定为真实点，可能为剪切点
	//如果LPoint为feature的真实点，leftEndPoint 和 rightEndPoint同时设为LPoint。
	public static EarthPos[] getLEndPosInLineList(EarthPos ePos,ArrayList lineList) {
		GlobalPos gPos0 = ePos.convert2GlobalPos();
		double minLen = Double.MAX_VALUE;
		EarthPos minEPos = null;
		EarthPos endEPos1 = null;
		EarthPos endEPos2 = null;
		
		for(int i=0;i<lineList.size();i++) {
			ArrayList pointList = (ArrayList)lineList.get(i);
			for(int j=0;j<pointList.size() - 1;j++) {
//				Point p1 = (Point)pointList.get(j);
//				Point p2 = (Point)pointList.get(j + 1);
				EarthPos ePos1 = (EarthPos)pointList.get(j);
				EarthPos ePos2 = (EarthPos)pointList.get(j + 1);

				EarthPos LEPos = LinePosUtil.getPointInLine(ePos1, ePos2, ePos);
				if(LEPos != null) {
					GlobalPos LGPos = LEPos.convert2GlobalPos();
					
					double len = LinePosUtil.getLen(LGPos, gPos0);
					if(len < minLen) {
						minLen = len;
						minEPos = LEPos;
						endEPos1 = ePos1;
						endEPos2 = ePos2;
					}
				}
				
				GlobalPos LGPos1 = ePos1.convert2GlobalPos();
				double len = LinePosUtil.getLen(LGPos1, gPos0);
				if(len < minLen) {
					minLen = len;
					minEPos = ePos1;
					endEPos1 = ePos1;
					endEPos2 = ePos1;
				}
				
				GlobalPos LGPos2 = ePos2.convert2GlobalPos();
				len = LinePosUtil.getLen(LGPos2, gPos0);
				if(len < minLen) {
					minLen = len;
					minEPos = ePos2;
					endEPos1 = ePos2;
					endEPos2 = ePos2;
				}
				
			}		
		}

		//中间为目标点，前后点为目标点的前后点。如果是线段真实点，前后点都设为此点。
		return new EarthPos[]{endEPos1,minEPos,endEPos2};
	}
	
	//近视判断，不严格，用于centerP肯定在P1和P2中间的情况。如Router的首尾点位置判断。
//	public static boolean isCenterPoint(Point p1,Point p2,Point centerP) {
//		return isCenterPoint(p1,p2,centerP.x,centerP.y);
//	}
//	
//	//近视判断，不严格，用于centerP肯定在P1和P2中间的情况。如Router的首尾点位置判断。
//	public static boolean isCenterPoint(Point p1,Point p2,int centerX,int centerY) {
//		double len1 = Math.sqrt((p1.x - centerX) * (p1.x - centerX) + (p1.y - centerY) * (p1.y - centerY)); 
//		double len2 = Math.sqrt((p2.x - centerX) * (p2.x - centerX) + (p2.y - centerY) * (p2.y - centerY)); 
//		double lenTotal = Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
//		if(Math.abs(len1 + len2 - lenTotal) <= 0.00001) {
//			return true;
//		}
//		return false;
//	}
	
	
	//判断点为7位的EarthPos的Point表示，所以需考虑溢出的情况
	public static boolean isCenterEarthPoint(EarthPos p1,EarthPos p2,EarthPos centerP) {
		return isCenterEarthPoint(p1,p2,centerP.getILat(),centerP.getILon());
	}
	
	//判断点为7位的EarthPos的Point表示，所以需考虑溢出的情况
	public static boolean isCenterEarthPoint(EarthPos p1,EarthPos p2,int centerX,int centerY) {
		double rr = 0.0001;
		double len1 = Math.sqrt(rr * (p1.getILat() - centerX) * rr * (p1.getILat() - centerX) + rr * (p1.getILon() - centerY) * rr * (p1.getILon() - centerY)); 
		double len2 = Math.sqrt(rr * (p2.getILat() - centerX) * rr * (p2.getILat() - centerX) + rr * (p2.getILon() - centerY) * rr * (p2.getILon() - centerY)); 
		double lenTotal = Math.sqrt(rr * (p2.getILat() - p1.getILat()) * rr * (p2.getILat() - p1.getILat()) + rr * (p2.getILon() - p1.getILon()) * rr * (p2.getILon() - p1.getILon()));
		if(Math.abs(len1 + len2 - lenTotal) <= 0.00001) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main1(String[] args) {
//		EarthPos ePos1 = new EarthPos(1727431560,-435790430);
//		EarthPos ePos2 = new EarthPos(1712490150,-443735840);
//		EarthPos ePos3 = new EarthPos(1713979980,-436999690);
//
//		EarthPos ePos = LinePointUtil.getPointInLine(ePos1, ePos2, ePos3);
//		if(ePos != null) {
//			System.err.println("RF;1;" + ePos.getILat() + "," + ePos.getILon() + "," + ePos3.getILat() + "," + ePos3.getILon() + "|"  + ePos1.getILat() + "," + ePos1.getILon() + "," + ePos2.getILat() + "," + ePos2.getILon() + ";;;;");
//		}
		
		
		
//		EarthPos ePos1 = new EarthPos(1727431560,-435790430);
//		EarthPos ePos2 = new EarthPos(1712490150,-435790430);
//		EarthPos ePos3 = new EarthPos(1713979980,-436999690);
//
//		EarthPos ePos = LinePointUtil.getPointInLine(ePos1, ePos2, ePos3);
//		if(ePos != null) {
//			System.err.println("RF;1;" + ePos.getILat() + "," + ePos.getILon() + "," + ePos3.getILat() + "," + ePos3.getILon() + "|"  + ePos1.getILat() + "," + ePos1.getILon() + "," + ePos2.getILat() + "," + ePos2.getILon() + ";;;;");
//		}

		
		
		EarthPos ePos1 = new EarthPos(1187414050,320654070);
		EarthPos ePos2 = new EarthPos(1187425570,320655750);
		EarthPos ePos3 = new EarthPos(1187371010,320647750);

		EarthPos ePos = LinePosUtil.getPointInLine(ePos1, ePos2, ePos3);
		if(ePos != null) {
			System.err.println("RF;1;" + ePos.getILat() + "," + ePos.getILon() + "," + ePos3.getILat() + "," + ePos3.getILon() + "|"  + ePos1.getILat() + "," + ePos1.getILon() + "," + ePos2.getILat() + "," + ePos2.getILon() + ";;;;");
		}
	}

}
