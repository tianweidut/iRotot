package com.xmobileapp.cammonitor;

import com.xmobileapp.cammonitor.util.ActivtyUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

//环境检测器设置页面
public class EnvMain extends Activity	{
	
	public static String TAG = "EnvMain";
	
	//页面控件
	private Button btnHistory;			//查看历史
	private Button btnSetAlarm;			//设置报警
	private Button btnCancelAlarm;		//取消报警
	private EditText editAlarmNum;		//报警数据
	private Button btnSaveAlarmNum;		//保存数据
	
	private Button btnStartTurn;		//开始转动
	private Button btnStopTurn;			//停止转动

	private ToggleButton togConfig;		//配置通道
	private ImageView IVReturn;			//返回上一级		
	
	private RadioButton textTemp;			//温度
	private RadioButton textHumidity;		//湿度
	private RadioButton textCO;			//CO
	private RadioButton textCH4;			//CH4
	private TextView textLife;			//声明探测信号
	
	private TextView textSensorFowd;				//传感器正前
	private TextView textSensorFowdRight;			//传感器左前
	private TextView textSensorFowdLeft;			//传感器右前
	private TextView textSensorBack;				//传感器正后
	private TextView textSensorBackLeft;			//传感器左后
	private TextView textSensorBackRight;			//传感器右后

	private TextView textChannel1;				//通道1
	private TextView textChannel2;				//通道2
	private TextView textChannel3;				//通道3
	private TextView textChannel4;				//通道4
	private TextView textChannel5;				//通道5
	private TextView textChannel6;				//通道6
	private TextView textChannel7;				//通道7
	private TextView textChannel8;				//通道8
	
	private ImageView imageSensorFowd;			//方位图片 正前
	private ImageView imageSensorFowdRight;		//方位图片 右前
	private ImageView imageSensorFowdLeft;		//方位图片 左前
	private ImageView imageSensorBack;			//方位图片 正后
	private ImageView imageSensorBackLeft;		//方位图片 左后
	private ImageView imageSensorBackRight;		//方位图片 右后
	private Vibrator vibrator;
	private RadioGroup RadioGroup1;
	
	private ProgressBar progressBarLife;		//生命检测进度条
	
	private String temperature;
	private String humidity;
	private String CH4;
	private String CO;
	private int choiceID;
	
	
	@Override
	public void onCreate(Bundle icicle) {
		try {
			super.onCreate(icicle);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);		//隐藏标题栏
			setContentView(R.layout.envmain);						//显示XML页面
			findView();												//关联控件
			setListener();											//设置监听
			fillData();												//填充数据	
		
		} catch (Exception e) {
			Log.e(ServerAct.TAG, e.getMessage(), e);
			ActivtyUtil.showAlert(EnvMain.this, "错误", e.getMessage(), "退出");
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
		//UI元素关联
		btnHistory = (Button) findViewById(R.id.buttonDataHistory);				//查看历史
		btnSetAlarm = (Button) findViewById(R.id.btnsetEnvAlarm);			//设置报警
		btnStartTurn = (Button) findViewById(R.id.btnStartTurn);			//开始转动
		btnStopTurn = (Button) findViewById(R.id.btnStopTurn);				//停止转动
		btnCancelAlarm = (Button) findViewById(R.id.btncancelEnvAlarm);		//取消报警
		togConfig = (ToggleButton) findViewById(R.id.toggleConfig);					//配置
		IVReturn = (ImageView) findViewById(R.id.imageViewEnvReturn);				//返回	
		btnSaveAlarmNum = (Button) findViewById(R.id.btnSaveAlterNum);				//保存
		
		textTemp = (RadioButton) findViewById(R.id.radiotemperature);				//温度
		textHumidity = (RadioButton) findViewById(R.id.radioHumdity);		//湿度
		textCO = (RadioButton) findViewById(R.id.radioCO);					//CO
		textCH4 = (RadioButton) findViewById(R.id.radioCH4);					//CH4
		textLife = (TextView) findViewById(R.id.textEnvLife);				//声明探测信号
		
		textSensorFowd = (TextView) findViewById(R.id.textSensorFor);					//传感器正前
		textSensorFowdRight = (TextView) findViewById(R.id.textSensorForRight);			//传感器左前
		textSensorFowdLeft = (TextView) findViewById(R.id.textSensorForLeft);			//传感器右前
		textSensorBack = (TextView) findViewById(R.id.textSensorBack);					//传感器正后
		textSensorBackLeft = (TextView) findViewById(R.id.textSensorBackLeft);			//传感器左后
		textSensorBackRight = (TextView) findViewById(R.id.textSensorBackRight);		//传感器右后

		textChannel1 = (TextView) findViewById(R.id.textChannel1);				//通道1
		textChannel2 = (TextView) findViewById(R.id.textChannel2);				//通道2
		textChannel3 = (TextView) findViewById(R.id.textChannel3);				//通道3
		textChannel4 = (TextView) findViewById(R.id.textChannel4);				//通道4
		textChannel5 = (TextView) findViewById(R.id.textChannel5);				//通道5
		textChannel6 = (TextView) findViewById(R.id.textChannel6);				//通道6
		textChannel7 = (TextView) findViewById(R.id.textChannel7);				//通道7
		textChannel8 = (TextView) findViewById(R.id.textChannel8);				//通道8
		
		
		imageSensorFowd = (ImageView) findViewById(R.id.imageViewFowd);			//方位图片 正前
		imageSensorFowdRight = (ImageView) findViewById(R.id.ImageViewFowdRight);		//方位图片 右前
		imageSensorFowdLeft = (ImageView) findViewById(R.id.ImageViewFowdLeft);		//方位图片 左前
		imageSensorBack = (ImageView) findViewById(R.id.ImageView0Back);			//方位图片 正后
		imageSensorBackLeft = (ImageView) findViewById(R.id.ImageView0BackLeft);		//方位图片 左后
		imageSensorBackRight = (ImageView) findViewById(R.id.ImageView0BackRight);		//方位图片 右后
		
		progressBarLife = (ProgressBar) findViewById(R.id.progressBarLife);			//生命检测进度条
		
		RadioGroup1 = (RadioGroup)findViewById(R.id.radioGroupEnvMain);
		editAlarmNum = (EditText)findViewById(R.id.editTextAlarm);
	}
	public void setListener()
	{
		btnHistory.setOnClickListener(btnHistoryListener);				//查看历史
		btnSetAlarm.setOnClickListener(btnSetAlarmListener);			//设置报警
		btnStartTurn.setOnClickListener(btnStartTurnListener);			//开始转动
		btnStopTurn.setOnClickListener(btnStopTurnListener);			//停止转动
		btnCancelAlarm.setOnClickListener(btnCancelAlarmListener);		//取消报警
		togConfig.setOnCheckedChangeListener(togOnCheckedChangeListener);
		IVReturn.setOnClickListener(btnReturnListener);					//返回上一级
		btnSaveAlarmNum.setOnClickListener(btnSaveAlarmNumListener);	//保存
		RadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == textTemp.getId())
				{
					choiceID = 1;
					editAlarmNum.setText(temperature);			//温度
				}
				else if(checkedId == textHumidity.getId())
				{
					choiceID = 2;

					editAlarmNum.setText(humidity);		//湿度

				}
				else if(checkedId == textCO.getId())
				{
					choiceID = 4;
					editAlarmNum.setText(CO);			//CO
				}
				else if(checkedId == textCH4.getId())
				{
					choiceID = 3;
					editAlarmNum.setText(CH4);			//CH4
				}
				
			}
		});
		
	}
	public void fillData()
	{
		btnStopTurn.setEnabled(false);
		btnSaveAlarmNum.setEnabled(false);
		editAlarmNum.setEnabled(false);
		temperature = "26";
		humidity = "46";
		CH4 = "0.03";
		CO = "0.05";
		choiceID = 5;
	}	
	
	
	public View.OnClickListener btnReturnListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			//返回上一级菜单
			Intent intent = new Intent();
			setResult(RESULT_FIRST_USER,intent);
			finish();
		}
	};
	
	public View.OnClickListener btnHistoryListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			//ignore intern 切换 环境信息页面切换
			btnHistory.setBackgroundColor(Color.BLUE);
			Intent intent = new Intent(EnvMain.this, EnvData.class);
			startActivity(intent);		//跳转下一页 EnvMain
		}
	};
	
	public View.OnClickListener btnSetAlarmListener = new View.OnClickListener(){
		public void onClick(View v) {
			//
			notiSound();
			btnSaveAlarmNum.setEnabled(true);
			btnSetAlarm.setEnabled(false);
			editAlarmNum.setEnabled(true);
		}
	};
	public View.OnClickListener btnSaveAlarmNumListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			ActivtyUtil.openToast(EnvMain.this, String.valueOf(choiceID));
			switch(choiceID)
			{
			case 1:
				if(editAlarmNum.getText().toString() != "")
				{
					temperature = editAlarmNum.getText().toString();
				}
				break;
			case 2:
				if(editAlarmNum.getText().toString() != "")
				{
					humidity = editAlarmNum.getText().toString();
				}
				break;
			case 3:
				if(editAlarmNum.getText().toString() != "")
				{
					CH4 = editAlarmNum.getText().toString();
				}
				break;
			case 4:
				if(editAlarmNum.getText().toString() != "")
				{
					CO = editAlarmNum.getText().toString();
				}
				break;
			default:
				break;
			}
			
			textTemp.setText("温度：     "+temperature+"度");			//温度
			textHumidity.setText("湿度：    "+humidity+"%");		//湿度
			textCO.setText("CO：    "+CO+"%");			//CO
			textCH4.setText("甲烷：    "+CH4+"%");			//CH4
				
			btnSaveAlarmNum.setEnabled(false);
			btnSetAlarm.setEnabled(true);
			editAlarmNum.setEnabled(false);
			editAlarmNum.setHint("请输入报警值...");
		}
	};
	public View.OnClickListener btnStartTurnListener = new View.OnClickListener(){
		public void onClick(View v) {
			//
			notiSound();
			btnStartTurn.setEnabled(false);
			btnStopTurn.setEnabled(true);
		}
	};
	public View.OnClickListener btnStopTurnListener = new View.OnClickListener(){
		public void onClick(View v) {
			//
			notiSound();
			btnStartTurn.setEnabled(true);
			btnStopTurn.setEnabled(false);
		}
	};
	public View.OnClickListener btnCancelAlarmListener = new View.OnClickListener(){
		public void onClick(View v) {
			//
			notiSound();
			ActivtyUtil.openToast(EnvMain.this, "取消报警");
		}
	};
	//配置按钮
	public CompoundButton.OnCheckedChangeListener togOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
	{
		@Override 
		public void onCheckedChanged(CompoundButton b,boolean isChecked)
		{
			notiSound();
			if(isChecked)
			{
				ActivtyUtil.openToast(EnvMain.this, "配置通道已打开");
			}
			else
			{
				ActivtyUtil.openToast(EnvMain.this, "配置通道已关闭");
			}
		}
	};
	
	//监听返回按键
	public boolean OnKeyDown(int keyCode, KeyEvent event)
	{
		ActivtyUtil.openToast(EnvMain.this, "ok");
		
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			Intent intent = new Intent();
			intent.setClass(EnvMain.this, ServerAct.class);
			startActivity(intent);
			EnvMain.this.finish();
		}
		
		return false;
		
	}
}

