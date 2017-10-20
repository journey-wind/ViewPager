package com.example.Record;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.TimerTask;

import com.example.viewpagertest.AddMsgActivity;
import com.example.viewpagertest.MainActivity;
import com.example.viewpagertest.SocialMessage;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BackService  {
	private static final String TAG = "BackService";
	private static final long HEART_BEAT_RATE = 30 * 1000;

	public static final String HOST = "192.168.1.103";// "192.168.1.21";//
	public static final int PORT = 2233;
	
	public static final String MESSAGE_ACTION="org.feng.message_ACTION";
	public static final String HEART_BEAT_ACTION="org.feng.heart_beat_ACTION";
	
	private ReadThread mReadThread;
	private ReceiveType typeNum;
	
	long sum_size=0;
	long sum_file=0;
	
	public static enum ReceiveType{
		PushMessage,GetMessage,addNum
	}
	

	private WeakReference<Socket> mSocket;

	// For heart Beat
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {

		@Override
		public void run() {
			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
				boolean isSuccess = sendMsg("");//就发送一个\r\n过去 如果发送失败，就重新初始化一个socket
				if (!isSuccess) {
					Message msgg = MainActivity.mainHand.obtainMessage();
					msgg.what = 3;
					MainActivity.mainHand.sendMessage(msgg);// 结果返回
					mHandler.removeCallbacks(heartBeatRunnable);
					mReadThread.release();
					releaseLastSocket(mSocket);
					new InitSocketThread().start();
				}
			}
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	private long sendTime = 300L;

	public BackService() {

		new InitSocketThread().start();
		
	}
	public boolean SendFile (String path){
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		int size = -1;
		 sum_size=0;
		 sum_file=0;
        byte[] buffer = new byte[1024];
        try {
        	FileInputStream fileInput = new FileInputStream(path);
        	
        	if (!soc.isClosed() && !soc.isOutputShutdown()) {
				OutputStream os = soc.getOutputStream();
				while((size = fileInput.read(buffer, 0, 1024)) != -1){
					os.write(buffer, 0, size);
					os.flush();
					sum_size+=size;
	                sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
				}
				String mess="<<2>>";
	            byte[] bf = mess.getBytes("UTF-8");
				os.write(bf);
				os.flush();
	            fileInput.close();
			} else {
				return false;
			}
	           
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } 
        return true;
    }
	public boolean sendMsg(String msg) {
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {
				OutputStream os = soc.getOutputStream();
				String message = msg ;
				os.write(message.getBytes("UTF-8"));
				os.flush();
				sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void initSocket() {//初始化Socket
		try {
			Socket so = new Socket(HOST, PORT);
			mSocket = new WeakReference<Socket>(so);
			mReadThread = new ReadThread(so);
			mReadThread.start();
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
			Message msgg = MainActivity.mainHand.obtainMessage();
			msgg.what = 5;
			MainActivity.mainHand.sendMessage(msgg);// 结果返回
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Message msgg = MainActivity.mainHand.obtainMessage();
			msgg.obj=e.getMessage();
			msgg.what = 4;
			MainActivity.mainHand.sendMessage(msgg);// 结果返回
		} catch (IOException e) {
			e.printStackTrace();
			Message msgg = MainActivity.mainHand.obtainMessage();
			msgg.obj=e.getMessage();
			msgg.what = 4;
			MainActivity.mainHand.sendMessage(msgg);// 结果返回
		}
		
	}

	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			initSocket();
		}
	}

	// Thread to read content from Socket
	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@SuppressLint("NewApi")
		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			String tempStr = ""; 
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int length = 0;
					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart && ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String message = new String(Arrays.copyOf(buffer,
									length)).trim();
							Log.e(TAG, message);
							//收到服务器过来的消息，就通过Broadcast发送出去
							
								if(message.equals("InsertOK")){
									String path =AddMsgActivity.selectMusic;
									if(!(path==null||path.equals(""))){
										if(!SendFile(path)){
											Message msg = new Message(); 
									        Bundle b = new Bundle();
									        b.putInt("FileChange", -102);
									        msg.setData(b);
									        AddMsgActivity.selectHand.sendMessage(msg);
										}
									}else{
										Message msg = new Message(); 
								        Bundle b = new Bundle();
								        b.putInt("FileChange", -101);
								        msg.setData(b);
								        AddMsgActivity.selectHand.sendMessage(msg);
									}
								}else if(message.equals("FileOk")){
									Message msg = new Message(); 
							        Bundle b = new Bundle();
							        b.putInt("FileChange", -101);
							        msg.setData(b);
							        AddMsgActivity.selectHand.sendMessage(msg);
								}else if(message.indexOf("<<5>>")>=0){
									tempStr+=message;
									String[] temp =tempStr.split("<<3>>");
									Message msg = SocialMessage.socialHandl.obtainMessage();
									msg.obj = temp;
									msg.what = 2;
									SocialMessage.socialHandl.sendMessage(msg);// 结果返回
									tempStr="";
									//close();
								}else if(message.indexOf("<<3>>") > 0){
									tempStr+=message;
								}else if(message.indexOf("<<6>>")>=0){
									tempStr+=message;
									String[] temp =tempStr.split("<<4>>");
									Message msg = SocialMessage.socialHandl.obtainMessage();
									msg.obj = temp;
									msg.what = 5;
									SocialMessage.socialHandl.sendMessage(msg);// 结果返回
									tempStr="";
									//close();
								}else if(message.indexOf("<<4>>") > 0){
									tempStr+=message;
								}
								
								
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
