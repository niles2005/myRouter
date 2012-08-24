package com.xtwsoft.utils.gis;


public class AngleUtil {
	//计算夹角
//	public static double getCenterAngle(Point p1,Point p2,Point p3){
//		double ang1 = caculateAngle(p2.x,p2.y,p1.x,p1.y);
//		double ang2 = caculateAngle(p3.x,p3.y,p2.x,p2.y);
//		return getCenterAngle(ang1,ang2);
//	}
	
	//计算转角
//	public static double getOrientAngle(Point p1,Point p2,Point p3){
//		double ang1 = caculateAngle(p2.x,p2.y,p1.x,p1.y);
//		double ang2 = caculateAngle(p2.x,p2.y,p3.x,p3.y);
//		double angle = ang2 - ang1;
//		return angle;
//	}
	
//	public static String getOrient(Point p1,Point p2,Point p3){
//		double ang1 = caculateAngle(p2.x,p2.y,p1.x,p1.y);
//		double ang2 = caculateAngle(p2.x,p2.y,p3.x,p3.y);
//		double angle = ang2 - ang1;
//		if(angle > 0) {
//			if(angle < 135) {
//				return "左拐";
//			}
//		} else if(angle < 0){
//			if(angle > -135) {
//				return "右拐";
//			}
//		}
//		return "继续直行";
//	}
	
	public static String getOrient(double p1x,double p1y,double p2x,double p2y,double p3x,double p3y){
		double ang1 = caculateAngle(p2x,p2y,p1x,p1y);
		double ang2 = caculateAngle(p2x,p2y,p3x,p3y);
		double angle = ang2 - ang1;
		if(angle < -180) {
			angle += 360;
		}
		if(angle > 0) {
			if(angle < 160) {
				return "左转";
			}
		} else if(angle < 0){
			if(angle > -160) {
				return "右转";
			}
		}
		return "继续直行";
	}
	
//	public static double getOrientAngle(Point p1,Point p2){
//		double angle = caculateAngle(p1.x,p1.y,p2.x,p2.y);
//		return angle;
//	}
	
	//计算0-360
//	public static double caculateABSAngle(double x1,double y1,double x2,double y2){
//		double tan = (y2-y1)/(x2-x1);
//		
//		//负号为转为算术坐标
//		double anglePI = -Math.atan(tan);
//		
//		double angle = anglePI*180/Math.PI;
//
//		if(x2<x1 && anglePI>0){
//			angle += 180;
//		}
//		
//		if(x2<x1 && anglePI<0){
//			angle += 180;
//		}
//		if(x2>x1 && anglePI<0){
//			angle += 360;
//		}
//		return angle;
//	}
	
//	private static double caculateAngle(double x1,double y1,double x2,double y2){
//		double anglePI = 0;
//		double angle=0;
//		if(x1==x2){
//			if(y1==y2){
//				return 0;
//			}else if(y1>y2){
//				return 90;
//			}else{
//				return -90;
//			}
//		}
//		else{
//			double tan = (y2-y1)/(x2-x1);
//			
//			//负号为转为算术坐标
//			anglePI = -Math.atan(tan);
//			
//			angle = anglePI*180/Math.PI;
//			if(x2<x1 && anglePI>=0){
//				angle -= 180;
//			}
//			
//			if(x2<x1 && anglePI<0){
//				angle += 180;
//			}
//			return angle;
//		}
//	}
//	
	private static double caculateAngle(double x1,double y1,double x2,double y2){
		x2 -= x1;
		y2 -= y1;
		
		if(x2 == 0){
			if(y2 == 0){
				return 0;
			}else if(y2 > 0){
				return 90;
			}else{
				return 270;
			}
		}
		else{
			double tan = y2/x2;
			
			//负号为转为算术坐标
			double anglePI = Math.atan(tan);
			
			double angle = anglePI*180/Math.PI;
			if(x2 < 0) {
				angle += 180;
			} else {
				if(y2 < 0) {
					angle += 360;
				}
			}
			return angle;
		}
	}
	
//	//计算夹角(0-180)
//	private static double getCenterAngle(double ang1,double ang2){
//		if(ang1<0){
//			ang1 = 360 + ang1;
//		}
//		if(ang2<0){
//			ang2 = 360 + ang2;
//		}
//		return Math.abs((180-Math.abs(ang2-ang1)));
//	}
//	
//	//计算转角(0-360)
//	private static double getOrientAngle(double ang1,double ang2){
//		if(ang1<0){
//			ang1 = 360 + ang1;
//		}
//		if(ang2<0){
//			ang2 = 360 + ang2;
//		}
//		return Math.abs((180-Math.abs(ang2-ang1)));
//	}
}
