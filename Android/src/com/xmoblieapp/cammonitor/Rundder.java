package com.xmoblieapp.cammonitor;

import android.view.View;

import com.xmobileapp.cammonitor.drawMath;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class Rundder extends SurfaceView implements Runnable,Callback{
	
	private SurfaceHolder mHolder;
	private boolean isStop = false;
	private Thread mThread;
	private Paint  mPaint;
	private Point  mRockPosition;		//摇杆位置
	private Point mCtrlPoint = new Point(100,105);	//摇杆起始位置
	private int mRudderRadius = 40;		//摇杆半径
	private int mWheelRadius  = 80;		//摇杆活动半径
	private RundderListener listener = null;		//事件回溯接口
	public static final int ACTION_RUDDER = 1,ACTION_ATTACK = 2;	//1.摇杆事件 2.按钮事件
	
	public Rundder(Context context)
	{
		super(context);	
	}
	
	public Rundder(Context context,AttributeSet as)
	{
		super(context,as);
				this.setKeepScreenOn(true);
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mThread = new Thread(this);
		
		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setAntiAlias(true);					//抗锯齿
		mRockPosition = new Point(mCtrlPoint);		//初始位置
		setFocusable(true);
		setFocusableInTouchMode(true);
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSPARENT);	//设置背景透明

	}	
	
	//回调接口
	public void setRudderListener(RundderListener rockerListener)
	{
		listener = rockerListener;
	}
	
	@Override
	public void run()
	{
		Canvas canvas = null;
		while(!isStop)
		{
			try{
				canvas = mHolder.lockCanvas();
				canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
				mPaint.setColor(Color.WHITE);
				canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius, mPaint);		//绘制范围
				mPaint.setColor(Color.BLACK);
				canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius-10, mPaint);		//绘制范围
				mPaint.setColor(Color.WHITE);
				canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, 5, mPaint);		//绘制范围
				mPaint.setColor(Color.RED);
				canvas.drawCircle(mRockPosition.x, mRockPosition.y, mRudderRadius, mPaint);	//绘制摇杆
			}catch(Exception e){
				e.printStackTrace();
			}finally
			{
				if(canvas != null)
				{
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
			
			try{
				Thread.sleep(15);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	             int height) {
		        
	}
	@Override 
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread.start();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		isStop = true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int len = drawMath.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(), event.getY());
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			//如果不在指定范围内则忽略
			if(len > mWheelRadius)
			{
				return true;
			}
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if(len <= mWheelRadius)		//如果手指在摇杆活动范围内，则摇杆处于手指触摸位置
			{
				mRockPosition.set((int)event.getX(), (int)event.getY());
			}
			else	//设置摇杆位置，使其处于手指触摸方向的 摇杆活动范围边缘
			{
				mRockPosition = drawMath.getBorderPoint(mCtrlPoint,new Point((int)event.getX(),(int)event.getY()) ,mWheelRadius);
			}
			
			if(listener != null)
			{
				float radian = drawMath.getRadian(mCtrlPoint, new Point((int)event.getX(),(int)event.getY()));
				listener.onSteeringWheelChanged(ACTION_RUDDER,Rundder.this.getAngleCouvert(radian));
			}
			
		}
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			mRockPosition = new Point(mCtrlPoint);
		}
		return true;
	}
	
	//获取摇杆偏移角度 0-360
	private int getAngleCouvert(float radian){
		int tmp = (int)Math.round(radian/Math.PI*180);
		return ((tmp<0)?(-tmp):(180+180-tmp));
	}
	
	//回调接口
	public interface RundderListener{
		 void onSteeringWheelChanged(int action,int angle);
	}
	
}
