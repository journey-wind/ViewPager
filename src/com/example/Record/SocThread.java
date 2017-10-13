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
 * 1->������Ϣ
 * 2->������Ƶ
 * 3->�����Ϣ
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
	 * ����socket������
	 */
	public void conn() {

		try {
//			initdate();
			Log.i(TAG, "�����С���");
			client = new Socket(ip, port);
			client.setSoTimeout(timeout);// ��������ʱ��
			Log.w(TAG, "���ӳɹ�");
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
//			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//					client.getOutputStream())), true);
//			in = client.getInputStream();
			out = client.getOutputStream();
			Log.w(TAG, "�����������ȡ�ɹ�");
			//Toast.makeText(mcontext, "�����������ȡ�ɹ�",Toast.LENGTH_SHORT).show();
		} catch (UnknownHostException e) {
			Log.w(TAG, "���Ӵ���UnknownHostException ���»�ȡ");
			//Toast.makeText(mcontext, "���Ӵ���UnknownHostException ���»�ȡ"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			conn();
		} catch (IOException e) {
			Log.w(TAG, "���ӷ�����io����");
			//Toast.makeText(mcontext, "���ӷ�����io����"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (Exception e) {
			Log.w(TAG, "���ӷ���������Exception" + e.getMessage());
			//Toast.makeText(mcontext, "���ӷ���������Exception"+ e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	

	

	/**
	 * ʵʱ��������
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
										SocialMessage.socialHandl.sendMessage(msg);// �������
									}else if(line.equals("end")){
										Message msg = SocialMessage.socialHandl.obtainMessage();
										msg.obj = null;
										msg.what = 2;
										SocialMessage.socialHandl.sendMessage(msg);// �������
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
					Log.w(TAG, "û�п�������");
					//Toast.makeText(mcontext, "û�п�������",Toast.LENGTH_SHORT).show();
					conn();
				}
			} catch (Exception e) {
				Log.w(TAG, "���ݽ��մ���" + e.getMessage());
				//Toast.makeText(mcontext, "���ݽ��մ���",Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��������
	 * 
	 * @param mess
	 */
	public void Send(String mess,ReceiveType messgaeType) {
		try {
			if (client != null) {
				Log.w(TAG1, "����" + mess + "��"
						+ client.getInetAddress().getHostAddress() + ":"
						+ String.valueOf(client.getPort()));
//				out.println(mess);
				typeNum=messgaeType;
				byte[] bf = mess.getBytes("UTF-8");
				out.write(bf);
				out.flush();
				
				Log.w(TAG1, "���ͳɹ�");
				
			} else {
				Log.w(TAG, "client ������");
				conn();
			}

		} catch (Exception e) {
			Log.w(TAG1, "send error");
			e.printStackTrace();
		} finally {
			Log.w(TAG1, "�������");

		}
	}

	public void SendFile (String path){
        try {
        	FileInputStream fileInput = new FileInputStream(path);
            if (client != null || fileInput!=null) {
	            int size = -1;
	            //ɾ��wavͷ��
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
	 * �ر�����
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
