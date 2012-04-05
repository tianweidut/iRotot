package com.xmobileapp.cammonitor;

import com.xmobileapp.cammonitor.util.ActivtyUtil;
import com.xmoblieapp.cammonitor.Rundder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

//机器人状态设置页面

public class RobotSet extends Activity	{

	//private Button btnConfig;					//配置按钮
	private Button btnPointSet;					//放置节点
	private Button btnPointCallBack;			//回收节点	
	private Button btnNetConfig;				//网络设置
	private Button btnReturn;					//返回上一级
	
	private TextView textWorkTime;				//工作时间
	private TextView textMainVoltage;			//主电压
	private TextView textSubVoltage;			//副电压
	
	private TextView textRobotRollAngle;		//横滚角
	private TextView textRobotYawAngle;			//航偏角
	private TextView textRobotPitchAngle;		//俯仰角
	private TextView textobotWhirlArm;			//旋转臂
	
	private TextView textRobotSetPoints;		//放置节点数量
	private TextView textRobotRemindPoints;		//回收节点数量
	private TextView textRobotWiFi;				//WIFI信号强度
	
	private RadioButton radioAuto;				//自动按钮
	private RadioButton radioMan;				//手动按钮
	private RadioButton radioAutoPath;			//自动轨迹按钮	
	
	private ProgressBar progressRobot;			//机器人进度条
	private Vibrator vibrator;
	
	@Override
	public void onCreate(Bundle icicle) {
		try {
			super.onCreate(icicle);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);		//隐藏标题栏
			setContentView(R.layout.robotset);						//显示XML页面
			findView();												//关联控件
			setListener();											//设置监听
			fillData();												//填充数据	
				
		} catch (Exception e) {
			Log.e(ServerAct.TAG, e.getMessage(), e);
			ActivtyUtil.showAlert(RobotSet.this, "错误", e.getMessage(), "退出");
		}
	}
	@Override
	protected void onStop()
	{
		if(null != vibrator)
		{
			vibrator.cancel();
		}
		super.onStop();
	}
	private void notiSound()
	{
		vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{50,100,80,200},-1);
	}
	public void findView()
	{

		btnPointSet= (Button) findViewById(R.id.btnRobotPointsSet);					//放置节点
		btnPointCallBack= (Button) findViewById(R.id.btnRobotRemainPoints);			//回收节点	
		btnNetConfig= (Button) findViewById(R.id.btnRobotNetwork);					//网络设置
		btnReturn = (Button) findViewById(R.id.btnRobotReturn);						//返回
		
		textWorkTime= (TextView) findViewById(R.id.textRobotWorkTime);				//工作时间
		textMainVoltage= (TextView) findViewById(R.id.textRobotMainVoltage);		//主电压
		textSubVoltage= (TextView) findViewById(R.id.textRobotSubVoltage);			//副电压
		
		textRobotRollAngle= (TextView) findViewById(R.id.textRobotRollAngle);		//横滚角
		textRobotYawAngle= (TextView) findViewById(R.id.textRobotYawAngle);			//航偏角
		textRobotPitchAngle= (TextView) findViewById(R.id.textRobotPitchAngle);		//俯仰角
		textobotWhirlArm= (TextView) findViewById(R.id.textRobotWhirlArm);			//旋转臂
		
		textRobotSetPoints= (TextView) findViewById(R.id.textRobotSetPoints);		//放置节点数量
		textRobotRemindPoints= (TextView) findViewById(R.id.textRobotRemindPoints);	//回收节点数量
		textRobotWiFi= (TextView) findViewById(R.id.textRobotWiFi);					//WIFI信号强度
		
		radioAuto= (RadioButton) findViewById(R.id.radioAuto);						//自动按钮
		radioMan= (RadioButton) findViewById(R.id.radioMan);						//手动按钮
		radioAutoPath= (RadioButton) findViewById(R.id.radioAutoPath);				//自动轨迹按钮	
		
		progressRobot= (ProgressBar) findViewById(R.id.progressBarRobot);			//机器人进度条
		
	}
	public void setListener()
	{
		btnPointSet.setOnClickListener(btnPointSetListener);					//放置节点
		btnPointCallBack.setOnClickListener(btnPointCallBackListener);			//回收节点	
		btnNetConfig.setOnClickListener(btnNetConfigListener);					//网络设置
		btnReturn.setOnClickListener(btnReturnListener); 	

		
	}
	public void fillData()
	{
		
	}	

	
	
	//相应返回
	public View.OnClickListener btnReturnListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			Intent intent = new Intent();
			setResult(RESULT_FIRST_USER,intent);
			finish();
		}
	};
	/*public View.OnClickListener btnConfigListener = new View.OnClickListener(){
		public void onClick(View v) {
		}
	};*/
	public View.OnClickListener btnPointSetListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();	
		}
	};
	public View.OnClickListener btnPointCallBackListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
		}
	};
	public View.OnClickListener btnNetConfigListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
		}
	};


	
}
