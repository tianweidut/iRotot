package com.xmobileapp.cammonitor;

import com.xmoblieapp.cammonitor.Rundder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import android.os.Vibrator;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.xmobileapp.cammonitor.CamMonitorView.CamMonitorThread;
import com.xmobileapp.cammonitor.config.CamMonitorParameter;
import com.xmobileapp.cammonitor.util.ActivtyUtil;
import com.xmobileapp.cammonitor.util.DatabaseHelper;
import com.xmobileapp.cammonitor.util.MessageListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class ServerAct extends Activity	implements SeekBar.OnSeekBarChangeListener
{
	
	public static String TAG = "ServerAct";			//Server 界面标签
	
	//页面控件
	private CamMonitorView cmView;		//图片显示View
	private Rundder RudderArm;			//云台控制
	private Rundder RudderMove;					//运动控制
		
	private TextView textSpeed;			//运动速度	
	private SeekBar seekSpeed;			//运动速度
	private SeekBar SeekCam;			//摄像头对焦
	
	private TextView textAngle;			//旋转臂角度	
	private SeekBar	seekAngle;			//旋转臂转角

	private TextView textBtnEnv;		//环境检测 文本按钮
	private TextView textTemp;			//温度
	private TextView textHumidity;		//湿度
	private TextView textCO;			//CO
	private TextView textCH4;			//CH4
	private TextView textLife;			//声明探测信号
	private TextView textCam;			//摄像头对焦
	private TextView textViewArmState;	//旋转臂状态2
	
	private TextView textPointCnts;		//节点放置数量
	
	private TextView textBtnSysStatus;	//系统状态 文本按钮
	private TextView textWorkTime;		//工作时间
	private TextView textMainVoltage;	//主工作电压
	private TextView textSignal;		//信号强度

	private TextView textRobotState;	//机器人状态
	
	private ImageView IVStop;					//停止
	private ImageView IVReturn;					//返回上一级
	private ImageView IVPointSet;				//放置节点
	
	private CamMonitorParameter param;
	public static final int TRY_TIMES = 3;		//下载链接次数
	public int current_times = 1;				//当前次数

	private int userid;
	private String addDB;

	public Handler handler = new Handler();
	public List<String> downloadList = new ArrayList<String>();
	public ProgressDialog	downloadProgressDialog = null;	
	
	
	private Socket socketSendCMD = null;
	private Socket socketRecvData = null;
	
	private Thread recvThread = null;					//接受数据线程
	private PrintWriter mPrintWriter = null;			//输出缓冲区
	private BufferedReader mBufferedReader = null;		//输入缓冲区
	private String mStrMSG = "xx";				
	
	
	private int CurrentProgressAngle = 0;
	private int CurrentProgressSpeed = 0;
	private int CurrentProgressCar	 = 0;

	
	private int pointcnts = 0;
	private String StextTemp;			//温度
	private String StextHumidity;		//湿度
	private String StextCO;				//CO
	private String StextCH4;			//CH4
	private String StextLife;			//声明探测信号
	private String StextWorkTime;	
	private String StextMainVoltage;	//主工作电压
	private String StextSignal;			//信号强度
	private String StextRobotState;		//机器人状态
	
	private int oldHolderAngle =0;
	private int oldMoveAngle =0;
	
	private Vibrator vibrator;
	
	private SensorManager sensorMgr;
	private Sensor sensor;
	private float Sen_x,Sen_y,Sen_z;
	
	
	@Override
	public void onCreate(Bundle icicle) {
		try {
			super.onCreate(icicle);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);		//隐藏标题栏
			setContentView(R.layout.server);						//显示XML页面
			findView();												//关联控件
			setListener();											//设置监听
			fillData();												//填充数据	
			socketBuild();
			//SensorEGG();
			recvThread = new Thread(mRunnable);
			recvThread.start();
			
		} catch (Exception e) {
			Log.e(ServerAct.TAG, e.getMessage(), e);
			ActivtyUtil.showAlert(ServerAct.this, "错误", e.getMessage(), "退出");
		}
	}
	private void SensorEGG()
	{
		sensorMgr = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);			//加速度传感器
		//传感器监听
		SensorEventListener sl = new SensorEventListener()
		{
			@Override 
			public void onSensorChanged(SensorEvent event)
			{
				Sen_x = event.values[0];		//横向
				Sen_y = event.values[1];		//纵向
				Sen_z = event.values[2];		//上下移动
				
				DisplayToast("加速度传感器 x ("+ Sen_x +") y ("+Sen_y+") z ("+Sen_z+")" );		 
			}
			@Override 
			public void onAccuracyChanged(Sensor sensor,int accuarcy)
			{
				//精度发生变化时作调整
			}
		};
		sensorMgr.registerListener(sl, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
	
	private void socketBuild() throws IOException
	{
		sockApp sock = ((sockApp)getApplicationContext());
		try
		{
			mBufferedReader = sock.socketBuild(addDB);	
			socketSendCMD  = sock.getSocketSendCMD();
			socketRecvData = sock.getSocketRecvData();
			
		}catch(Exception e)
		{
			Log.e(ServerAct.TAG, e.getMessage(), e);
			ActivtyUtil.showAlert(ServerAct.this, "sock创建", e.getMessage(), "退出");			
		}

		
	}
	private void notiSound()
	{
		vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{50,100,80,200},-1);
	}
	
	private void findView(){										//关联控件
		
		cmView = (CamMonitorView) findViewById(R.id.cmView);				//图片显示View

		RudderArm=(Rundder)findViewById(R.id.RundderArm);	//云台控制
		RudderMove=(Rundder)findViewById(R.id.RudderMove);					//运动控制
		seekSpeed = (SeekBar)findViewById(R.id.SeekBarMoveSpeed);
		SeekCam = (SeekBar)findViewById(R.id.SeekBarCam);
		textRobotState= (TextView)findViewById(R.id.TextViewRobotState);	//机器人状态		
		textCam = (TextView)findViewById(R.id.textCarmerDest);

		seekAngle = (SeekBar) findViewById(R.id.seekBarAngle);				//旋转臂转角
		textBtnEnv = (TextView) findViewById(R.id.textViewEnv);				//环境检测 文本按钮
		textTemp = (TextView) findViewById(R.id.textServTemp);				//温度
		textHumidity =(TextView) findViewById(R.id.textServHumidity);		//湿度
		textCO=(TextView) findViewById(R.id.textServCO);					//CO
		textCH4=(TextView) findViewById(R.id.textServCH4);					//CH4
		textLife=(TextView) findViewById(R.id.textServLife);				//声明探测信号
		textPointCnts=(TextView) findViewById(R.id.textPointCnts);			//节点放置数量
		textBtnSysStatus=(TextView) findViewById(R.id.TextViewSystem);		//系统状态 文本按钮
		textWorkTime=(TextView) findViewById(R.id.TextViewWorkTime);		//工作时间
		textMainVoltage=(TextView) findViewById(R.id.TextViewMainVoltage);	//主工作电压
		textSignal=(TextView) findViewById(R.id.TextViewSignal);			//信号强度
		textAngle=(TextView) findViewById(R.id.TextViewAngle);				//旋转臂角度
		textSpeed=(TextView) findViewById(R.id.TextViewSpeed);				//运动速度	
		textViewArmState = (TextView)findViewById(R.id.TextViewArmState);	
		
		IVStop = (ImageView) findViewById(R.id.imageViewStop);					//停止
		IVReturn = (ImageView) findViewById(R.id.imageViewReturn);					//返回上一级
		IVPointSet =(ImageView) findViewById(R.id.imageViewPointSet);				//节点放置
	}

	private void setListener(){										//设置监听
		
		textBtnEnv.setOnClickListener(textBtnEnvListener);				//环境检测 文本按钮
		textBtnSysStatus.setOnClickListener(textBtnSysStatusListener);
		seekAngle.setOnSeekBarChangeListener(this);						//角度按钮
		seekSpeed.setOnSeekBarChangeListener(this);						//运动速度
		SeekCam.setOnSeekBarChangeListener(this);						//摄像头对焦
		
		RudderArm.setRudderListener(RudderArmListener);
		RudderMove.setRudderListener(RudderMoveListener);
		
		IVStop.setOnClickListener(btnSpeedStopListener);
		IVReturn.setOnClickListener(btnDisconnectListener);
		IVPointSet.setOnClickListener(btnPointSetListener);
	}
	
	public Rundder.RundderListener RudderArmListener = new Rundder.RundderListener(){
		public void onSteeringWheelChanged(int action,int angle){
			if(action == Rundder.ACTION_RUDDER)
			{
				//事件
				//DisplayToast("角度" + angle);
				if(angle>=0&&angle<=360)
				{
					
					if(Math.abs(oldHolderAngle-angle)>3)
					{
						oldHolderAngle = angle;
						if(angle>45 && angle <135)
						{
							//向上
							CurrentProgressAngle = 90;
							textAngle.setText("旋转臂状态: 向上");
							textViewArmState.setText("旋转臂状态: 向上");
							seekAngle.setProgress(85);
							
						}
						else if(angle >225 && angle < 275)
						{
							//向下
							CurrentProgressAngle = 5;
							textAngle.setText("旋转臂状态: 向下");
							textViewArmState.setText("旋转臂状态: 向下");
							seekAngle.setProgress(15);
						}
						else
						{
							//停止
							CurrentProgressAngle = 50;
							textAngle.setText("旋转臂状态: 停止");
							textViewArmState.setText("旋转臂状态: 停止");
							seekAngle.setProgress(50);
						}
					}
					else
					{
						oldHolderAngle = angle;
					}
					for(int i=0;i<100000;i++);
				}
			}
		}
	};
	
	public Rundder.RundderListener RudderMoveListener = new Rundder.RundderListener(){
		public void onSteeringWheelChanged(int action,int angle){
			
			int speed = 0;
			int arm = 0;
			
			if(action == Rundder.ACTION_RUDDER)
			{
				//事件
				//DisplayToast("角度" + angle);
				if(angle>=0&&angle<=360)
				{
					//SendCMDOp("Move:"+angle);
					if(Math.abs(oldMoveAngle-angle)>3)
					{
						//运动控制 发送
						
						if(CurrentProgressAngle <= 30)
						{
							//向下
							arm = 2;
						}
						else if(CurrentProgressAngle >= 70)
						{
							//向上
							arm = 1;
						}
						else
						{
							//停止
							arm = 0;
						}
						
						speed = CurrentProgressSpeed;
						
						SendCMDOp("$M,"+ speed +','+ angle +','+arm +',');
						
						
						oldMoveAngle = angle;
					}
					else
					{
						oldMoveAngle = angle;
					}
					for(int i=0;i<100000;i++);
				}
			}
		}
	};

	private void socketSendData(String str, Socket sck ) throws IOException{
		//发送数据
		try{
		PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter
						(sck.getOutputStream())),true);
		out.println(str);
		}catch(Exception e)
		{
			Log.i(TAG, "发送数据错误",e);
		}
	}
	
	private Runnable mRunnable = new Runnable()
	{
		public void run()
		{
			while(true)
			{
				try
				{
					if( (mStrMSG = mBufferedReader.readLine()) != null)
					{
						//mStrMSG += "\n";
						mHandler.sendMessage(mHandler.obtainMessage());
					}
				}
				catch(Exception e)
				{
					Log.e(TAG, e.toString());
				}
			}
		}
		
	};
	Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			try
			{
				//此处对mStrMSG进行批量处理，进而填充各个传感器状态
				String in = mStrMSG;
				String out[] = in.split(":");
				if(out.length != 10)
				{
					DisplayToast("接受数据错误");
				}
				else
				{
					StextTemp = out[0];				//温度
					StextHumidity = out[1];			//湿度
					StextCO =out[2];				//CO
					StextCH4 =out[3];				//CH4
					StextLife =out[4];				//声明探测信号
					StextWorkTime =out[5];			//工作时间	
					StextMainVoltage =out[6];		//主工作电压
					StextSignal =out[7];			//信号强度
					StextRobotState =out[8];		//机器人状态
					//写入数据
					textTemp.setText("温度：     "+StextTemp+"度");			//温度
					textHumidity.setText("湿度：    "+StextHumidity+"%");		//湿度
					textCO.setText("一氧化碳： "+StextCO+" ppm");			//CO
					textCH4.setText("甲烷：    "+StextCH4.charAt(0)+StextCH4.charAt(1)+'.'+StextCH4.charAt(2)+StextCH4.charAt(3)+"%");			//CH4
					textLife.setText("生命探测仪：  "+StextLife);			//声明探测信号
					textWorkTime.setText("工作时间：" + StextWorkTime+"分钟");	
					textMainVoltage.setText("主电电压： 14.2V");//+StextMainVoltage+"V");	//主工作电压
					textSignal.setText("信号强度： 强");//+StextSignal+"%");		//信号强度
					textRobotState.setText("机器人状态： 正常");//+StextRobotState);	//机器人状态
				}
				//Java 中需要加入回车。刷新缓冲区
			}
			catch(Exception e)
			{
				Log.e(TAG, e.toString());
			}
		}
	};
	private void socketRecvData() throws IOException{
		//接受数据
		String msg = "";
		try{
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socketRecvData.getInputStream()));
			msg =  br.readLine();
			if(msg != null)
			{
				DisplayToast(msg);
				textWorkTime.setText("工作时间：" + msg);
			}
			else
			{
				DisplayToast("recv Error!!!");
			}
		}catch(Exception e)
		{
			Log.i(TAG, "接收数据错误",e);
		}		
	}
	private String socketRecvDataStr(Socket sck) throws IOException{
		//接受数据
		String msg = "";
		try{
			BufferedReader br = new BufferedReader(
					new InputStreamReader(sck.getInputStream()));
			msg =  br.readLine();
			
		}catch(Exception e)
		{
			Log.i(TAG, "接收数据错误",e);
		}		
		return msg;
	}	
	
	private void fillData() throws Exception{										//填充数据
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null){
			userid = bundle.getInt("id");
			param = DatabaseHelper.query(ServerAct.this, userid);
			cmView.setCmPara(param);
			//address = param.getIp();	//IP地址获取
			addDB = param.getIp();	//IP地址获取
		}else{
			throw new Exception("数据库信息无法获取");
		}
		DisplayToast("已与救援机器人建立通信，IP地址位" + addDB);
	}
	

	public View.OnClickListener btnSpeedStopListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			//SendCMDOp("SpeedStop");
			SendCMDOp("$M,"+ 0 +','+ 0 +','+ 0 +',');
		}
	};

	public View.OnClickListener btnPointSetListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			SendCMDOp("PointSet");
			pointcnts ++;	
			textPointCnts.setText("已放置节点数量:"+String.valueOf(pointcnts));
		}
	};


	public View.OnClickListener btnDisconnectListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			SendCMDOp("AllStop");		//全停止命令
			intentChange();
			setResult(RESULT_OK);
			finish();
		}
	};
	
	public View.OnClickListener textBtnEnvListener = new View.OnClickListener(){
		public void onClick(View v) {
			//声音效果
			notiSound();
			SendCMDOp("$M,"+ 0 +','+ 0 +','+ 0 +',');
			//页面切换处理
			intentChange();
			//ignore intern 切换 环境信息页面切换
			Intent intent = new Intent(ServerAct.this, EnvMain.class);
			startActivityForResult(intent,1);		//跳转下一页 EnvMain
		}
	};
	public View.OnClickListener textBtnSysStatusListener = new View.OnClickListener(){
		public void onClick(View v) {
			//声音效果
			notiSound();
			SendCMDOp("$M,"+ 0 +','+ 0 +','+ 0 +',');
			//页面切换处理cmd
			intentChange();
			//ignore intern 切换 系统信息切换
			Intent intent = new Intent(ServerAct.this, RobotSet.class);
			startActivityForResult(intent,0);		//跳转下一页 RobotSet
		}
	};

	public void SendCMDOp(String str){
		try {
			socketSendData(str,socketSendCMD);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//接收显示传感器数据
	class RecvSensorDataThread extends Thread{
		ServerAct server = null;
		Socket sckRecv = null;
		public RecvSensorDataThread(ServerAct mAct,Socket sck){
			server = mAct;
			sckRecv = sck;
		}
		public void run(){
			//接收传感器数据
			//server.socketRecvData();
		}
		
	}
		
	class DownloadThread extends Thread{
		ServerAct server = null;
    	public DownloadThread(ServerAct mAct){
    		server = mAct;
    	}
    	
		public void run() {
			
			CamMonitorThread cmt = cmView.getThread();
			if(cmt.saveImage()){
				ActivtyUtil.openToast(ServerAct.this, "保存1");
			}
			else{
				ActivtyUtil.openToast(ServerAct.this, "保存2");
			}
//			handler.post(new Runnable(){
//
//				public void run() {
//					if(downloadProgressDialog!=null){
//						downloadProgressDialog.dismiss();
//					}
//					ActivtyUtil.openToast(ServerAct.this, "�������");
//					try{
//						getData(handler);
//					}catch (Exception e) {
//						ActivtyUtil.openToast(ServerAct.this, e.getMessage());
//					}
////					thread.stop();
//				}
				
//			});
		}
  	
    };	

    public void DisplayToast(String str){
    	Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,	//seek
			boolean fromUser) {
		// TODO Auto-generated method stub
		if(seekBar == seekAngle)
		{
		//progress -=  45;
		//String str = " "+(progress);
		//Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		if(progress <=30)
		{
			textAngle.setText("旋转臂状态: 向下");
			textViewArmState.setText("旋转臂状态: 向下");
		}
		else if(progress >= 70)
		{
			textAngle.setText("旋转臂状态: 向上");
			textViewArmState.setText("旋转臂状态: 向上");
		}
		else
		{
			textAngle.setText("旋转臂状态: 停止");
			textViewArmState.setText("旋转臂状态: 停止");
		}
		
		CurrentProgressAngle = progress; 
		
		}
		else if(seekBar == seekSpeed)
		{
			// 速度控制
			textSpeed.setText("当前速度：" + progress +"%");
			CurrentProgressSpeed = progress;
		}
		else if(seekBar == SeekCam)
		{
			//对焦
			textCam.setText("摄像头对焦: " + progress +"%");
			CurrentProgressCar = progress;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		notiSound();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		if(seekBar == seekAngle)
		{
			notiSound();
			//SendCMDOp("Angle:"+CurrentProgressAngle);
			
		}
		else if(seekBar == seekSpeed)
		{
			notiSound();
			//SendCMDOp("Speed:"+CurrentProgressSpeed);
			
		}
		else if(seekBar == SeekCam)
		{
			notiSound();
			SendCMDOp("$C:"+CurrentProgressCar +"#");
		}
			
	}
	
	public void intentChange()
	{
		//发送结束标志
		SendCMDOp("-EOF-Activity");
		//系统资源处理
		cmView.setRunning(false);
		recvThread.stop();
		//套接字注销
		try {
			socketSendCMD.close();
			socketRecvData.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_FIRST_USER)
		{
			setResult(RESULT_FIRST_USER);			//直接返回上一级界面
			finish();
		}
	}
}
