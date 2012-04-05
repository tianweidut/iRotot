package com.xmobileapp.cammonitor;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class curveDraw extends View{

	int miCnt = 0;
	
	public curveDraw(Context drawView,AttributeSet attrs) {
		super(drawView,attrs);
		// TODO Auto-generated constructor stub
	}
	
	
	public void onDraw(Canvas canvas)
	{
		//颜色计数
		if(miCnt < 100)
		{
			miCnt ++;
		}
		else
		{
			miCnt =0;
		}
		//颜色
		Paint mPaint = new Paint();
		switch(miCnt%4)
		{
		case 0:
			mPaint.setColor(Color.BLUE);
			break;
		case 1:
			mPaint.setColor(Color.RED);
			break;
		case 2:
			mPaint.setColor(Color.WHITE);
			break;
		case 3:
			mPaint.setColor(Color.CYAN);
			break;
		default:
			mPaint.setColor(Color.DKGRAY);
			break;
		}
		//绘制
		canvas.drawRect((320-80)/2, 0, (320-80)/2 + 80, 80, mPaint);
	}
	
}
