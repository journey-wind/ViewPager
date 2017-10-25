package com.example.viewpagertest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.Record.MediaPlayerUtil;
import com.example.Record.MsgTypeUtil;
import com.example.Record.SocThread;
import com.example.Record.SocThread.ReceiveType;
import com.example.ViewClass.MySwipeRefresh;
import com.example.ViewClass.MySwipeRefresh.OnLoadListener;
import com.example.viewpagertest.MsgListViewAdapter.ViewHolder;
import com.example.viewpagertest.SearchListViewAdapter.Callback;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SocialMessage implements OnItemClickListener, com.example.viewpagertest.MsgListViewAdapter.Callback{
	
	private Context context;
	private View view;
	private ListView msgList;
	private MsgListViewAdapter msgAdapter;
	public static ArrayList<MsgTypeUtil> data;
	public static ImageView msgAdd;
	public static Handler socialHandl;
	private String baseMusicPath = "http://192.168.1.116/music/";
	public static MySwipeRefresh swipeRefreshView;
	public static SocThread socketTh;
	DateFormat df;
	private String newTimer;
	private boolean isLoad;
	private String oldTimer;
	
	public SocialMessage(Context context, View view){
		this.context=context;
		newTimer="000000000000";
		oldTimer="000000000000";
		isLoad=false;
		this.view=view;
		socialHandl=new SocialHandler();
		data=new ArrayList<MsgTypeUtil>();
		df = new SimpleDateFormat("yyMMddHHmmss");
//		socketTh=new SocThread();
//		socketTh.start();
		InitView();
		loadLocalData();
	}

	private void loadLocalData() {
		// TODO Auto-generated method stub
    	String temp1 = MainActivity.sharedPreferences.getString("LastMessageOne", "");
		String temp2 = MainActivity.sharedPreferences.getString("LastMessageTwo", "");
		String temp3 = MainActivity.sharedPreferences.getString("LastMessageThree", "");
		String temp4 = MainActivity.sharedPreferences.getString("LastMessageFour", "");
		String temp5 = MainActivity.sharedPreferences.getString("LastMessageFive", "");
		String temp6 = MainActivity.sharedPreferences.getString("LastMessageSix", "");
		String temp7 = MainActivity.sharedPreferences.getString("LastMessageSeven", "");
		String sumTemp=temp7+temp6+temp5+temp4+temp3+temp2+temp1+"<<5>>";
		if(sumTemp!=""){
			String[] temp=sumTemp.split("<<3>>");
			Message msg1 = SocialMessage.socialHandl.obtainMessage();
			msg1.obj = temp;
			msg1.what = 2;
			SocialMessage.socialHandl.sendMessage(msg1);// 结果返回
		}
		if(sumTemp.equals("end")){
			Message msg1 = SocialMessage.socialHandl.obtainMessage();
			msg1.obj = null;
			msg1.what = 3;
			SocialMessage.socialHandl.sendMessage(msg1);// 结果返回
		}
	}

	private void InitView(){
		swipeRefreshView=(MySwipeRefresh)view.findViewById(R.id.srl);
		msgList=(ListView)view.findViewById(R.id.Messagelist);
		msgAdd=(ImageView)view.findViewById(R.id.messageAdd);
//		MsgTypeUtil mtu = new MsgTypeUtil("user", "171008184701", "测试3", 
//				"10", "10", null, baseMusicPath+"test.mp3");
//			data.add(mtu);
//			mtu = new MsgTypeUtil("user", "171008184701", "测试3", 
//					"10", "10", null, "");
//				data.add(mtu);
//				mtu = new MsgTypeUtil("user", "171008184701", "测试3", 
//						"10", "10", null, "");
//					data.add(mtu);
//		
		msgAdapter=new MsgListViewAdapter(data, context,this);
		msgList.setAdapter(msgAdapter);
		msgAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(MediaPlayerUtil.mInstance!=null){
					MediaPlayerUtil.mInstance.Stop();
				}
				Intent intent = new Intent(); 
			     intent.setClass(context, AddMsgActivity.class);
			     context.startActivity(intent);
			}
		});
		
		swipeRefreshView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				Message msg1 = SocialMessage.socialHandl.obtainMessage();
    			  msg1.obj = null;
    			  msg1.what = 3;
    			  SocialMessage.socialHandl.sendMessage(msg1);// 结果返回
			}
		});
		
		swipeRefreshView.setOnLoadListener(new OnLoadListener() {
			
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
		              @Override
		              public void run() {

		            	  if(!isLoad){
		                  // 添加数据
		            		  isLoad=true;
		            		  Message msg1 = SocialMessage.socialHandl.obtainMessage();
		          			  msg1.obj = null;
		          			  msg1.what = 4;
		          			  SocialMessage.socialHandl.sendMessage(msg1);// 结果返回
		          			  isLoad=false;
		            	  }
		                  // 加载完数据设置为不加载状态，将加载进度收起来
		                  swipeRefreshView.setLoading(false);
		            	  
		              }
		          }, 1200);
			}
		});
        
	}

	class SocialHandler extends Handler{
		private int sum=0;
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			if (msg.what == 1) {
				swipeRefreshView.measure(0,0);
				swipeRefreshView.setRefreshing(true);
			}else if(msg.what == 2){
				MsgTypeUtil mtu=null;
				String[] temp = (String[]) msg.obj;
				for (String string : temp) {
					string=myTrim(string,"\r");
					if(string.equals("<<5>>")){
						continue;
					}
					String[] music = string.split("/");
					int i;
					for(i=0;i<data.size();i++){
						if(data.get(i).time.equals(music[0])){
							data.get(i).lisenNum=music[5];
							break;
						}
					}
					if(i==data.size()){
					mtu =new MsgTypeUtil(music[1], music[0], music[2].trim(), 
							music[5], music[5], null, baseMusicPath+music[3].trim(),music[4].trim());
					data.add(0, mtu);
					sum++;
					}
					
				}
				if(data.size()>0){
					newTimer=data.get(0).time;//获取最新的时间
					oldTimer=data.get(data.size()-1).time;
				}
				if(sum==0){
					Toast.makeText(context, "已是最新消息", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "共更新了"+sum+"条信息", Toast.LENGTH_SHORT).show();
				}
				msgAdapter.RefreshAndSave();
				sum=0;
				swipeRefreshView.setRefreshing(false);
				isLoad=false;
			}else if(msg.what == 3){
				//发送获取new消息给服务器
				isLoad=true;
				swipeRefreshView.setRefreshing(true);
		        String date= df.format(new Date());  
				String str ="GetNewMessage:"+date+":"+oldTimer+":"+data.size();
				if(!MainActivity.msgServer.sendMsg(str)){
					isLoad=false;
					Message msg1 = MainActivity.mainHand.obtainMessage();
	    			  msg1.obj = null;
	    			  msg1.what = 3;
	    			  MainActivity.mainHand.sendMessage(msg1);// 结果返回
				}
			}else if(msg.what == 4){
				//发送获取Old消息给服务器
				swipeRefreshView.setRefreshing(true);
				String str ="GetOldMessage:"+newTimer+":"+data.size();
				if(!MainActivity.msgServer.sendMsg(str)){
					Message msg1 = MainActivity.mainHand.obtainMessage();
	    			  msg1.obj = null;
	    			  msg1.what = 3;
	    			  MainActivity.mainHand.sendMessage(msg1);// 结果返回
				}
			}else if(msg.what == 5){
				MsgTypeUtil mtu=null;
				String[] temp = (String[]) msg.obj;
				for (String string : temp) {
					string=myTrim(string,"\r");
					if(string.equals("<<6>>")){
						continue;
					}
					String[] music = string.split("/");
					int i;
					for(i=0;i<data.size();i++){
						if(data.get(i).time.equals(music[0])){
							data.get(i).lisenNum=music[5];
							break;
						}
					}
					if(i==data.size()){
					mtu =new MsgTypeUtil(music[1], music[0], music[2].trim(), 
							music[5], music[5], null, baseMusicPath+music[3].trim(),music[4].trim());
					data.add(0, mtu);
					sum++;
					}
					
				}
				if(data.size()>0){
					newTimer=data.get(0).time;//获取最新的时间
					oldTimer=data.get(data.size()-1).time;
				}
				if(sum==0){
					Toast.makeText(context, "无消息", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "共更新了"+sum+"条信息", Toast.LENGTH_SHORT).show();
				}
				msgAdapter.RefreshAndSave();
				sum=0;
				swipeRefreshView.setRefreshing(false);
				isLoad=false;
			}
			super.handleMessage(msg);
		}
		private String myTrim(String string, String c) {
			// TODO Auto-generated method stub
			String temp=string;
			if(temp.substring(0, 1).equals(c)){
				temp=temp.substring(1);
			}
			if(temp.substring(temp.length()-1, temp.length()).equals(c)){
				temp=temp.substring(0,temp.length()-1);
			}
			return temp;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	private void sendListenNum(String time){
		String str="Listen:"+time;
		if(!MainActivity.msgServer.sendMsg(str)){
			Message msg = MainActivity.mainHand.obtainMessage();
			msg.obj = null;
			msg.what = 1;
			MainActivity.mainHand.sendMessage(msg);// 结果返回
		}
		return;
	}
	
	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		final ViewHolder holder=(ViewHolder)v.getTag();
		if(holder.musicPath!=null || holder.musicPath!=""){
			if(holder.isfirst){
				//MediaPlayerUtil.getInstance(holder.sb_music,holder.tv_musicPoint);
				MediaPlayerUtil.getInstance(holder);
				holder.iv_play.setImageResource(R.drawable.pause_small);
				if(MediaPlayerUtil.mInstance.Prepared()){
					int t = Integer.parseInt(data.get(holder.index).lisenNum.toString());
					t++;
					data.get(holder.index).lisenNum=String.valueOf(t);
					msgAdapter.RefreshAndSave();
					sendListenNum(holder.time);
					holder.isplay=true;
					holder.isfirst=false;
				}else{
					holder.iv_play.setImageResource(R.drawable.play_small);
				}
				
			}else if(holder.isplay){
				MediaPlayerUtil.mInstance.mediaPlayer.pause();
				holder.iv_play.setImageResource(R.drawable.play_small);
				holder.isplay=false;
			}else if(!holder.isplay){
				holder.iv_play.setImageResource(R.drawable.pause_small);
				MediaPlayerUtil.mInstance.mediaPlayer.start();
				holder.isplay=true;
			}
			
		}
	}
}
