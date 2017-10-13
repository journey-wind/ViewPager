package com.example.viewpagertest;

import java.util.ArrayList;

import com.example.Record.MediaPlayerUtil;
import com.example.Record.MsgTypeUtil;
import com.example.Record.SocThread;
import com.example.Record.SocThread.ReceiveType;
import com.example.viewpagertest.MsgListViewAdapter.ViewHolder;
import com.example.viewpagertest.SearchListViewAdapter.Callback;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ListView;
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
	public static SocThread socketTh;
	
	public SocialMessage(Context context, View view){
		this.context=context;
		this.view=view;
		socialHandl=new SocialHandler();
		data=new ArrayList<MsgTypeUtil>();
//		socketTh=new SocThread();
//		socketTh.start();
		InitView();
	}

	private void InitView(){
		msgList=(ListView)view.findViewById(R.id.Messagelist);
		msgAdd=(ImageView)view.findViewById(R.id.messageAdd);
		MsgTypeUtil mtu = new MsgTypeUtil("user", "171008184701", "≤‚ ‘3", 
				"10", "10", null, baseMusicPath+"test.mp3");
			data.add(mtu);
		
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
	}

	private void updateMessage() {
		// TODO Auto-generated method stub
		String mess;
		mess="GetNewMessage";
		MainActivity.msgServer.sendMsg(mess);
	}
	class SocialHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			if (msg.what == 1) {
				String[] music = (String[]) msg.obj;
				MsgTypeUtil mtu=null;
				mtu =new MsgTypeUtil(music[1], music[0], music[2], 
					"10", "10", null, baseMusicPath+music[3]);
				data.add(mtu);
			}else if(msg.what == 2){
				msgAdapter.notifyDataSetChanged();
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
