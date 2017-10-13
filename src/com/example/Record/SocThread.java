package com.example.Record;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.example.viewpagertest.AddMsgActivity;
import com.example.viewpagertest.SocialMessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/*
 * 1->发送消息
 * 2->发送音频
 * 3->获得消息
 */
public class SocThread  extends Thread {
	private String ip = "192.168.1.103";
	private int port = 2233;
	private String TAG = "socket thread";
	private int timeout = 3000;

	public Socket client = null;
//	PrintWriter out;
	OutputStream out;
//	InputStream in;
	BufferedReader in;
	public boolean isRun = true;
//	Handler playHandler;
//	Handler outHandler;
//	Context ctx;
	private String TAG1 = "===Send===";
	SharedPreferences sp;
	Thread receiveMsg;
	public static List<byte[]> arr;
	private ReceiveType typeNum;
	
	public static enum ReceiveType{
		PushMessage,GetMessage,addNum
	}
	
	public SocThread() {
		
	}

	/**
	 * 连接socket服务器
	 */
	public void conn() {

		try {
//			initdate();
			Log.i(TAG, "连接中……");
			client = new Socket(ip, port);
			client.setSoTimeout(timeout);// 设置阻塞时间
			Log.w(TAG, "连接成功");
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
//			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//					client.getOutputStream())), true);
//			in = client.getInputStream();
			out = client.getOutputStream();
			Log.w(TAG, "输入输出流获取成功");
			//Toast.makeText(mcontext, "输入输出流获取成功",Toast.LENGTH_SHORT).show();
		} catch (UnknownHostException e) {
			Log.w(TAG, "连接错误UnknownHostException 重新获取");
			//Toast.makeText(mcontext, "连接错误UnknownHostException 重新获取"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			conn();
		} catch (IOException e) {
			Log.w(TAG, "连接服务器io错误");
			//Toast.makeText(mcontext, "连接服务器io错误"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			Log.w(TAG, "连接服务器错误Exception" + e.getMessage());
			//Toast.makeText(mcontext, "连接服务器错误Exception"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	

	

	/**
	 * 实时接受数据
	 */
	public void run() {
		conn();
		String line = "";
		int num;
		String[] str;
		while (isRun) {
			try {
				if (client != null) {
					try {
						while ((line=in.readLine()) != null) {
							switch(typeNum){
								case PushMessage:
									if(line.equals("File")){
										String path =AddMsgActivity.selectMusic;
										SendFile(path);
									}else if(line.equals("OK")){
										//close();
									}
									break;
								case GetMessage:
									num=line.indexOf("end");
									if(num > 0){
										str=line.split("/");
										Message msg = SocialMessage.socialHandl.obtainMessage();
										msg.obj = str;
										msg.what = 1;
										SocialMessage.socialHandl.sendMessage(msg);// 结果返回
									}else if(line.equals("end")){
										Message msg = SocialMessage.socialHandl.obtainMessage();
										msg.obj = null;
										msg.what = 2;
										SocialMessage.socialHandl.sendMessage(msg);// 结果返回
										//close();
									}
									
									break;
								case addNum:
									break;
								default:
									break;
							}
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//Toast.makeText(mcontext, e.getMessage(),Toast.LENGTH_SHORT).show();
						conn();
					}
				} else {
					Log.w(TAG, "没有可用连接");
					//Toast.makeText(mcontext, "没有可用连接",Toast.LENGTH_SHORT).show();
					conn();
				}
			} catch (Exception e) {
				Log.w(TAG, "数据接收错误" + e.getMessage());
				//Toast.makeText(mcontext, "数据接收错误",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param mess
	 */
	public void Send(String mess,ReceiveType messgaeType) {
		try {
			if (client != null) {
				Log.w(TAG1, "发送" + mess + "至"
						+ client.getInetAddress().getHostAddress() + ":"
						+ String.valueOf(client.getPort()));
//				out.println(mess);
				typeNum=messgaeType;
				byte[] bf = mess.getBytes("UTF-8");
				out.write(bf);
				out.flush();
				
				Log.w(TAG1, "发送成功");
				
			} else {
				Log.w(TAG, "client 不存在");
				conn();
			}

		} catch (Exception e) {
			Log.w(TAG1, "send error");
			e.printStackTrace();
		} finally {
			Log.w(TAG1, "发送完毕");

		}
	}

	public void SendFile (String path){
        try {
        	FileInputStream fileInput = new FileInputStream(path);
            if (client != null || fileInput!=null) {
	            int size = -1;
	            //删除wav头部
	//            byte[] header = new byte[44];
	//            if(fileInput!=null){
	//            	fileInput.read(header,0,44);
	//            }
	            byte[] buffer = new byte[1024];
	            while((size = fileInput.read(buffer, 0, 1024)) != -1){
	                out.write(buffer, 0, size);
	                out.flush();
	            }
	            String mess="<<2>>";
	            byte[] bf = mess.getBytes("UTF-8");
				out.write(bf);
				out.flush();
	            fileInput.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
	
	
	/**
	 * 关闭连接
	 */
	public void close() {
		
		isRun=false;
		try {
			if (client != null) {
				Log.w(TAG, "close in");
				in.close();
				Log.w(TAG, "close out");
				out.close();
				Log.w(TAG, "close client");
				client.close();
			}
		} catch (Exception e) {
			Log.w(TAG, "close err");
			e.printStackTrace();
		}

	}
}
