package com.xmobileapp.cammonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.xmobileapp.cammonitor.util.ActivtyUtil;
import com.xmobileapp.cammonitor.util.DatabaseHelper;

//CamMonitor Client 第一个界面

public class CamMonitorClient extends Activity {

	public final static String TAG = "CamMonitorClient";		//Log打印标签

	protected DatabaseHelper helper;		//自己实现的数据库类
	
	protected TextView view;				//main Activity 界面控件
	protected Spinner spinner;
	protected Button btnAdd;
	protected Button btnModify;
	protected Button btnDelete;
	protected Button btnConnect;
	protected Button btnReturn;
	
	private SimpleCursorAdapter adapter;	//适配器 组织空间
	private Cursor cursor;					//数据库cursor
	private Vibrator vibrator;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);	
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.main);
			findView();
			fillDataWithCursor();
			setListenner();
		} catch (Exception e) {
			ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "登录页面创建失败");
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
	private void fillDataWithCursor() throws Exception {
		DatabaseHelper helper = new DatabaseHelper(this);
		cursor = helper.loadAllName();
		int count = cursor.getCount();
		cursor.moveToFirst();
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor,
				new String[] { "name" }, new int[] { android.R.id.text1 });
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.refreshDrawableState();
		helper.close();
		if (count <= 0) {
			btnConnect.setEnabled(false);
			btnDelete.setEnabled(false);
			btnModify.setEnabled(false);
			spinner.setEnabled(false);
		} else {
			btnConnect.setEnabled(true);
			btnDelete.setEnabled(true);
			btnModify.setEnabled(true);
			spinner.setEnabled(true);
		}
	}
	
	//界面控件指定
	private void findView() {
		spinner = (Spinner) findViewById(R.id.SpinnerLdapConfig);
		btnAdd = (Button) findViewById(R.id.BtnNew1);
		btnConnect = (Button) findViewById(R.id.BtnConnect1);
		btnModify = (Button) findViewById(R.id.BtnModify1);
		btnDelete = (Button) findViewById(R.id.BtnDelete1);
		btnReturn = (Button) findViewById(R.id.btn_main_quit);
	}
	
	//设置监听
	private void setListenner() {
		btnAdd.setOnClickListener(btnAddListener);
		btnModify.setOnClickListener(btnModifyListener);
		btnDelete.setOnClickListener(btnDeleteListener);
		btnConnect.setOnClickListener(btnConnectListener);
		btnReturn.setOnClickListener(btnRetunListener);
	}
	
	//代表含义： 0->   1-->   2-->ServerAct页面返回
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					fillDataWithCursor();
				} catch (Exception e) {
					ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(),
							"确定");
				}
			}
		}
		else if(requestCode == 3)
		{
			if(resultCode == Activity.RESULT_FIRST_USER)
			{	
				//此处返回时重新自动链接
				ConnectCam();
			}
		}
	}
	
	//connect 按钮选定
	private View.OnClickListener btnConnectListener = new View.OnClickListener() {

		public void onClick(View v) {
			notiSound();
			ConnectCam();

	}
	};
	
	private View.OnClickListener btnRetunListener = new View.OnClickListener() {

		public void onClick(View v) {
			notiSound();
			finish();
		}
	};		

	private View.OnClickListener btnAddListener = new View.OnClickListener() {

		public void onClick(View v) {
			notiSound();
			Intent intent = new Intent(CamMonitorClient.this, CamMonitorConfigActivity.class);
			startActivityForResult(intent, 0);
		}
	};

	private View.OnClickListener btnModifyListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				notiSound();
				Cursor cc = (Cursor) spinner.getSelectedItem();
				int id = cc.getInt(0);
				Intent intent = new Intent(CamMonitorClient.this, CamMonitorConfigActivity.class);
				intent.putExtra("id", id);
				startActivityForResult(intent, 1);
				// DatabaseHelper.testInsert(CamMonitorClient.this);
				// fillData();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "不能修改");
			}
		}

	};

	private void ConnectCam()
	{
		try {
			Cursor cc = (Cursor) spinner.getSelectedItem();
			int id = cc.getInt(0);
			Intent intent = new Intent(CamMonitorClient.this, ServerAct.class);
			intent.putExtra("id", id);
			startActivityForResult(intent,3);		//跳转下一页 serverAct
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);		//log 调试
			ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "监控页面跳转失败");
		}
	}
	private View.OnClickListener btnDeleteListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				notiSound();
				Cursor cc = (Cursor) spinner.getSelectedItem();
				final int id = cc.getInt(0);
				String name = cc.getString(1);
				new AlertDialog.Builder(CamMonitorClient.this).setTitle("确定删除吗？")
						.setMessage("确定删除" + name + "吗？").setPositiveButton(
								"确定", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										try {
											DatabaseHelper
													.delete(CamMonitorClient.this, id);
											fillDataWithCursor();
											ActivtyUtil.openToast(CamMonitorClient.this,
													"删除成功!");
										} catch (Exception e) {
											Log.e(TAG, e.getMessage(), e);
											ActivtyUtil.openToast(CamMonitorClient.this, e
													.getMessage());
										}
									}
								}).setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}

								}).show();
				// DatabaseHelper.Delete(CamMonitorClient.this, name);
			} catch (Exception e) {
				ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "确定");
			}
		}

	};
}
