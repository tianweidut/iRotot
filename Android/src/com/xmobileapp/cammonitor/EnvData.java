package com.xmobileapp.cammonitor;

import java.io.File;
import java.io.FileOutputStream;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
//import org.achartengine.chartdemo.demo.R;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.xmobileapp.cammonitor.util.ActivtyUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class EnvData extends Activity{
	
	private GraphicalView mChartView;
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();	
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private String mDateFormat;
	private String xTitle="2mins";
	private String yTitle="CO";
	private String yUnit="%";
	//private int YAxisMin;
	private int YAxisMax=15;
	//private int XAxisMin;
	private int XAxisMax=2;
	
	private Button btnReturn;		//运动按钮 前
	private Button btnConfirm;		//运动按钮 前
	
	private RadioGroup  rGButtonAttri;
	private RadioGroup  rGButtonTime;
	private Vibrator vibrator;
/*	private RadioButton radioCO;				 
	private RadioButton radioCH4;				 
	private RadioButton radioDust;				 
	private RadioButton radioHumdity;				 
	private RadioButton radioTemp;				 
	
	private RadioButton radio2mins;				 
	private RadioButton radio10mins;				 
	private RadioButton radio1hour;			*/	  
	
	//private curveDraw mcurveDraw ; 
/*	private int parameter;
	private int time;*/

    protected void onRestoreInstanceState(Bundle savedState) {
	    super.onRestoreInstanceState(savedState);
	    mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
	    mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
	    mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
	    mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
	    mDateFormat = savedState.getString("date_format");
	  }

	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putSerializable("dataset", mDataset);
	    outState.putSerializable("renderer", mRenderer);
	    outState.putSerializable("current_series", mCurrentSeries);
	    outState.putSerializable("current_renderer", mCurrentRenderer);
	    outState.putString("date_format", mDateFormat);
	  }	
	@Override
	public void onCreate(Bundle icicle) {
		try {
			super.onCreate(icicle);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);		//隐藏标题栏
			setContentView(R.layout.envdata);						//显示XML页面
			findView();												//关联控件
			mDataset=getDemoDataset();
	        mRenderer=getLineRenderer();
			setListener();											//设置监听
			fillData();												//填充数据
		} catch (Exception e) {
			Log.e(ServerAct.TAG, e.getMessage(), e);
			ActivtyUtil.showAlert(EnvData.this, "错误", e.getMessage(), "退出");
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
		//drawView = (curveDraw) findViewById(R.id.chart);
		
		btnReturn = (Button) findViewById(R.id.btnDrawData);					//保存按钮
		btnConfirm = (Button) findViewById(R.id.btnEnvDataBack);				

/*		radioCO= (RadioButton) findViewById(R.id.radioCO);					
		radioCH4= (RadioButton) findViewById(R.id.radioCH4);				
		radioDust= (RadioButton) findViewById(R.id.radioDust);				
		radioHumdity= (RadioButton) findViewById(R.id.radioHumdity);		
		radioTemp= (RadioButton) findViewById(R.id.radioTemp);				
		
		radio2mins= (RadioButton) findViewById(R.id.radio2mins);				
		radio10mins= (RadioButton) findViewById(R.id.radio10mins);				
		radio1hour= (RadioButton) findViewById(R.id.radio1hour);	*/			
		rGButtonAttri= (RadioGroup) findViewById(R.id.rGButtonAttri);
		rGButtonTime= (RadioGroup) findViewById(R.id.rGButtonTime);
	}
	public void setListener()
	{
		btnReturn.setOnClickListener(btnbtnReturnListener);			//保存按钮
		btnConfirm.setOnClickListener(btnConfirmListener);		//运动按钮 前
		rGButtonAttri.setOnCheckedChangeListener(rGButtonAttriListener);
		rGButtonTime.setOnCheckedChangeListener(rGButtonTimeListener);
	}

	public void fillData()
	{
		
	}	

	//监听返回按键
	public boolean OnKeyDown(int keyCode, KeyEvent event)
	{
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			notiSound();
			Intent intent = new Intent();
			intent.setClass(EnvData.this, EnvMain.class);	//返回主控页面
			startActivity(intent);
			EnvData.this.finish();
		}
		return false;
	}
	
	public View.OnClickListener btnbtnReturnListener = new View.OnClickListener(){
		public void onClick(View v) {
	        if (mChartView != null) {				
	        	notiSound();
	        	mRenderer.setXAxisMax(XAxisMax);
	        	mRenderer.setYAxisMax(YAxisMax);
	        	mRenderer.setXTitle(xTitle);
	        	mRenderer.setYTitle(yTitle+"("+yUnit+")");
	        	mCurrentSeries.setTitle(yTitle+"("+yUnit+")"+"/"+xTitle);
		        mChartView.repaint();
	        }

		}
	};
	public View.OnClickListener btnConfirmListener = new View.OnClickListener(){
		public void onClick(View v) {
			notiSound();
			finish();
		}
	};
	
	
	public RadioGroup.OnCheckedChangeListener rGButtonAttriListener = new RadioGroup.OnCheckedChangeListener(){
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			notiSound();
			switch(checkedId){
			case R.id.radioCH4:
				yTitle="甲烷";
				YAxisMax=10;
				yUnit="%";
				break;
			case R.id.radioCO:
				yTitle="一氧化碳";
				YAxisMax=15;
				yUnit="%";
				break;
			case R.id.radioDust:
				yTitle="粉尘";
				YAxisMax=50;
				yUnit="mg/m^3";
				break;
			case R.id.radioHumdity:
				yTitle="湿度";
				YAxisMax=100;
				yUnit="%";
				break;
			case R.id.radioTemp:
				yTitle="温度";
				YAxisMax=50;
				yUnit="度";
				break;
			default:
				yTitle="CO";
				YAxisMax=15;
				yUnit="%";
				break;
			}
		}
	};
	public RadioGroup.OnCheckedChangeListener rGButtonTimeListener = new RadioGroup.OnCheckedChangeListener(){
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			notiSound();
			switch(checkedId){
			case R.id.radio2mins:
				xTitle="2mins";
				XAxisMax=2;
				break;
			case R.id.radio10mins:
				xTitle="10mins";
				XAxisMax=10;
				break;
			case R.id.radio1hour:
				xTitle="60mins";
				XAxisMax=60;
				break;
			default:
				xTitle="2mins";	
				XAxisMax=2;
				break;
			}
		}
	};
	
	private XYMultipleSeriesRenderer getLineRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setPointStyle(PointStyle.CIRCLE);
        r.setColor(Color.GREEN);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        renderer.setAxesColor(Color.DKGRAY);
        renderer.setLabelsColor(Color.LTGRAY);
        renderer.setFitLegend(true);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setMargins(new int[] { 30, 30, 30, 30 });
	    renderer.setZoomButtonsVisible(true);
	    renderer.setPointSize(10);	  
        renderer.setChartTitle( "历史数据" );
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle+"("+yUnit+")");
        renderer.setShowGrid(true);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(YAxisMax);
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(XAxisMax);
        mCurrentRenderer=r;
        return renderer;
      }

	//线图数据
	  private XYMultipleSeriesDataset getDemoDataset() {
	        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	        String seriesTitle = yTitle+"/"+xTitle;
	        XYSeries series = new XYSeries(seriesTitle);
	        dataset.addSeries(series);
	        series.add(0.5, 0.5);
	        series.add(1, 5);
	        series.add(1.5, 2);
	        series.add(2, 15);
	        series.add(30,50);
	        mCurrentSeries=series;
	        return dataset;
	      }		
	  protected void onResume()
	  {
		  super.onResume();
		    if (mChartView == null) {
		      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		      mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
		      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		    }else {
		    	mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
		        mChartView.repaint();
		    }
	  }
}
