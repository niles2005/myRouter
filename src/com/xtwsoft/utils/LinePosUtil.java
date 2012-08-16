package com.xtwsoft.utils;

import java.util.ArrayList;

public class LinePosUtil {
	//����point3�ĵ㵽Point1��Point2�����ߵ���ֱ�㡣��ֱ����ܲ����߶���
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

	//���3�ڵ�1�͵�2���߶��ϵĴ�ֱ�㣬���������ӳ�����
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

	//���3�ڵ�1�͵�2���߶��ϵĴ�ֱ�㣬ֻ�����߶��ϣ��������ӳ�����
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
	
	//ȡ��һ����һ���߶��ϵľ�����̵ĵ�
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
	
	//����ȡ����Ĵ�ֱ�㣬������㲻���߶��ϣ�ȡ����Ķ˵�
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
	
	//for each line is ArrayList<Point>, �˴��������LPoint�����ˣ�һ��3�㣬LPoint���У�
	//���� ΪEarthPos[3],�ֱ�ΪleftEndPoint,LPoint,rightEndPoint
	//��ΪSutherlandHodgmanClip��ԭ�򣬿��ܼ���������û��feature����ʵ��(������з�Χ��û��Feature�ĵ�)
	//leftEndPoint �� rightEndPoint ��һ��Ϊ��ʵ�㣬����Ϊ���е�
	//���LPointΪfeature����ʵ�㣬leftEndPoint �� rightEndPointͬʱ��ΪLPoint��
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

		//�м�ΪĿ��㣬ǰ���ΪĿ����ǰ��㡣������߶���ʵ�㣬ǰ��㶼��Ϊ�˵㡣
		return new EarthPos[]{endEPos1,minEPos,endEPos2};
	}
	
	//�����жϣ����ϸ�����centerP�϶���P1��P2�м���������Router����β��λ���жϡ�
//	public static boolean isCenterPoint(Point p1,Point p2,Point centerP) {
//		return isCenterPoint(p1,p2,centerP.x,centerP.y);
//	}
//	
//	//�����жϣ����ϸ�����centerP�϶���P1��P2�м���������Router����β��λ���жϡ�
//	public static boolean isCenterPoint(Point p1,Point p2,int centerX,int centerY) {
//		double len1 = Math.sqrt((p1.x - centerX) * (p1.x - centerX) + (p1.y - centerY) * (p1.y - centerY)); 
//		double len2 = Math.sqrt((p2.x - centerX) * (p2.x - centerX) + (p2.y - centerY) * (p2.y - centerY)); 
//		double lenTotal = Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
//		if(Math.abs(len1 + len2 - lenTotal) <= 0.00001) {
//			return true;
//		}
//		return false;
//	}
	
	
	//�жϵ�Ϊ7λ��EarthPos��Point��ʾ�������迼����������
	public static boolean isCenterEarthPoint(EarthPos p1,EarthPos p2,EarthPos centerP) {
		return isCenterEarthPoint(p1,p2,centerP.getILat(),centerP.getILon());
	}
	
	//�жϵ�Ϊ7λ��EarthPos��Point��ʾ�������迼����������
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
