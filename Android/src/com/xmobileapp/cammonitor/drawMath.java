package com.xmobileapp.cammonitor;

import android.graphics.Point;

public class drawMath {
	//获取两点之间的直线距离
	public static int getLength(float x1,float y1,float x2,float y2){
		return (int)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y1,2));
	}
	//获取线段上某个点的坐标
	public static Point getBorderPoint(Point a,Point b,int cutRadius)
	{
		float radian = getRadian(a,b);
		return new Point(a.x + (int)(cutRadius * Math.cos(radian)), a.x + (int)(cutRadius * Math.sin(radian)));
	}
	
	//获取水平线的夹角
	public static float getRadian(Point a,Point b){
		float lenX = b.x - a.x;
		float lenY = b.y - a.y;
		float len = (float)Math.sqrt(lenX*lenX+lenY*lenY);
		float ang = (float)Math.acos(lenX/len);
		ang = ang * (b.y<a.y?-1:1);
		
		return ang;
 	}
	
}
