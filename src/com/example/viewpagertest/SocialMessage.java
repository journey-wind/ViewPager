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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
	private ArrayList<MsgTypeUtil> data;
	private ImageView msgAdd;
	public static Handler socialHandl;
	private String baseMusicPath = "http://192.168.1.103/music/";
	public static MySwipeRefresh swipeRefreshView;
	public static SocThread socketTh;
	DateFormat df;
	private String newTimer;
	
	public SocialMessage(Context context, View view){
		this.context=context;
		newTimer="000000000000";
		this.view=view;
		socialHandl=new SocialHandler();
		data=new ArrayList<MsgTypeUtil>();
		df = new SimpleDateFormat("yyMMddHHmmss");
//		socketTh=new SocThread();
//		socketTh.start();
		InitView();
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
				swipeRefreshView.setRefreshing(true);
		        String date= df.format(new Date());  
				String str ="GetNewMessage:"+date+":"+newTimer;
				MainActivity.msgServer.sendMsg(str);
			}
		});
		
		swipeRefreshView.setOnLoadListener(new OnLoadListener() {
			
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
		              @Override
		              public void run() {

		                  // 添加数据
		          		  MsgTypeUtil mtu = new MsgTypeUtil("user", "171008184701", "测试3", 
		          				"10", "10", null, baseMusicPath+"test.mp3");
		          		  data.add(mtu);
		          		  mtu = new MsgTypeUtil("user", "171008184701", "测试3", 
		  					"10", "10", null, "");
		  				  data.add(mtu);
		  				  mtu = new MsgTypeUtil("user", "171008184701", "测试3", 
		  						"10", "10", null, "");
		  				  data.add(mtu);
		  		
		                  msgAdapter.RefreshAndSave();
		                  Toast.makeText(SocialMessage.this.context, "加载了" + 5 + "条数据", Toast.LENGTH_SHORT).show();

		                  // 加载完数据设置为不加载状态，将加载进度收起来
		                  swipeRefreshView.setLoading(false);
		              }
		          }, 1200);
			}
		});
        
	}

	private void updateMessage() {
		// TODO Auto-generated method stub
		String mess;
		mess="GetNewMessage";
		MainActivity.msgServer.sendMsg(mess);
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
					if(string.equals("end")){
						continue;
					}
					String[] music = string.split("/");
					mtu =new MsgTypeUtil(music[1], music[0], music[2].trim(), 
							"10", "10", null, baseMusicPath+music[3].trim());
					data.add(0, mtu);
					sum++;
				}
				newTimer=data.get(0).time;
				swipeRefreshView.setRefreshing(false);
				if(sum==0){
					Toast.makeText(context, "已是最新消息", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "共更新了"+sum+"条信息", Toast.LENGTH_SHORT).show();
				}
				msgAdapter.RefreshAndSave();
				sum=0;
				swipeRefreshView.setRefreshing(false);
			}
			super.handleMessage(msg);
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		ViewHolder holder=(ViewHolder)v.getTag();
		if(holder.musicPath!=null || holder.musicPath!=""){
			
			MediaPlayerUtil.getInstance(holder.sb_music);
			
			MediaPlayerUtil.mInstance.Prepared(holder.musicPath);
		}
	}
}
