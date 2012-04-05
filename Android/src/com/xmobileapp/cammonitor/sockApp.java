package com.xmobileapp.cammonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.R.string;
import android.app.Application;
import android.util.Log;

public class sockApp extends Application{

	private static String TAG = "socket app"; 
	
	private Socket socketSendCMD = null;			//发送命令 套接字
	private Socket socketRecvData = null;			//接收信息 套接字
	
	private static final int SOCKET_TIMEOUT = 1000;		//套接字连接
	private String address;
	private static final int portSendCMD  = 9999;		//发送按钮端口
	private static final int portRecvData = 10000;  	//接受传感器数据数据端口
	
	private PrintWriter mPrintWriter = null;			//输出缓冲区
	private BufferedReader mBufferedReader = null;		//输入缓冲区
	
	//套接字建立 函数
	public BufferedReader socketBuild(String addr) throws IOException
	{
		//套接字建立
		//发送指令套接字建立
		//address = "192.168.2.69";
		address = addr;
		try{
			socketSendCMD = new Socket();
			socketSendCMD.bind(null);
			socketSendCMD.setSoTimeout(SOCKET_TIMEOUT);
			socketSendCMD.connect(new InetSocketAddress(address,portSendCMD), SOCKET_TIMEOUT);
			socketSendData("sendData",socketSendCMD);
					
			}catch( IOException e){
				Log.i(TAG, "SendCMD 错误", e);
				socketSendCMD.close();
			} 
		//接收传感器数据套接字建立
		try{
			socketRecvData = new Socket();
			socketRecvData.bind(null);
			socketRecvData.setSoTimeout(SOCKET_TIMEOUT);
			socketRecvData.connect(new InetSocketAddress(address,portRecvData), SOCKET_TIMEOUT);
			/*String tmp = null;
			if ((tmp = socketRecvDataStr(socketRecvData)) != null)
			{
				textWorkTime.setText("工作时间：" + tmp);
			}*/
			socketSendData("cmdSendStart",socketRecvData);
		//取得输入流
			mBufferedReader = new BufferedReader(new InputStreamReader(socketRecvData.getInputStream()));
			
			}catch(IOException e){
				Log.i(TAG, "socketRecvData 错误", e);
				socketRecvData.close();
			}
		return mBufferedReader;
	}
	//返回发送命令套接字
	public Socket getSocketSendCMD()
	{
		return socketSendCMD;
	}
	//返回接受信息套接字
	public Socket getSocketRecvData()
	{
		return socketRecvData;
	}
	
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
	
	//套接字取消 函数
	public void sockDestory()
	{
		try {
			socketSendCMD.close();
			socketRecvData.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
