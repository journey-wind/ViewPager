package com.example.viewpagertest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.Record.CaoZuoMp3Utils;
import com.example.Record.CaoZuoWavUtils;
import com.example.ViewClass.CommomDialog;
import com.example.ViewClass.DragListView;
import com.example.ViewClass.Loading_view;
import com.example.ViewClass.DragListView.DragItemChangeListener;
import com.example.viewpagertest.SynthesisListViewAdapter.Callback;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class synthesisMusic implements Callback {

	public static enum MusicType{
		Null,WavType,Mp3Type
	}
	private Context context;
	private MusicType synthesisType;
	private View view;
	public static Handler myHander;
	public static ArrayList<String> data;
	private DragListView dragListView;
	private SynthesisListViewAdapter adapter;
	private ImageButton imgbtnAdd;
	private ImageButton imgbtnSave;
	private Loading_view loading;
	TextView loadTv;

	public synthesisMusic(Context context, View view){
		this.context=context;
		this.view=view;
		myHander=new SynthesisHandler();
		initVew();
		initListen();
		if(data.size()>=0){
			data.clear();
			adapter.notifyDataSetChanged();
		}
		synthesisType=MusicType.Null;
	}
	
	private void initVew() {
		// TODO Auto-generated method stub
		data=new ArrayList<>();
//		data.add("/jgyj.pss");
//		data.add("/kjtdsf.pss");
//		data.add("/丰功硕德.pss");
//		data.add("/萌江湖同人.pss");
		adapter=new SynthesisListViewAdapter(data, context, this);
		dragListView = (DragListView) view.findViewById(R.id.dragLvi);
        dragListView.setAdapter(adapter);
        dragListView.setDragImageSourceId(R.id.iv_replace,R.id.imgbtn_delete);  
        dragListView.setDragItemChangeListener(new ListItemMove()); 
        imgbtnSave = (ImageButton)view.findViewById(R.id.imgbtn_save);
        imgbtnAdd = (ImageButton)view.findViewById(R.id.imgbtn_add);
	}

	private void initListen() {
		// TODO Auto-generated method stub
		imgbtnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new CommomDialog(context, R.style.dialog, "确定合成选定音频文件？\n*mp3与wav文件无法一起合成", new CommomDialog.OnCloseListener() {
					@Override
					public void onClick(Dialog dialog, boolean confirm) {
						// TODO Auto-generated method stub
						if(confirm){
							if(data.size()>1){
								loading = new Loading_view(context,R.style.CustomDialog);
							    loading.show();
							    loadTv=loading.GetLoadText();
							    loadTv.setText("拼接ing。。。");
								new Handler().postDelayed(new Runnable() {  
						             @Override  
						             public void run() {  
						            	 beOneMusic();
						             }  
						         }, 1000);
								
							}
				               dialog.dismiss();
				         }
					}
				})
				        .setTitle("提示").show();
				
			}
		});
		imgbtnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
                Message msg = MainActivity.mainHand.obtainMessage();
				msg.obj = null;
				msg.what = 9;
				MainActivity.mainHand.sendMessage(msg);// 结果返回
			}
		});
		
	}

	private void beOneMusic(){
		String oldtemp;
		String temp=data.get(0);
		for(int i=1;i<data.size();i++){
			try {
				oldtemp=temp;
				switch (synthesisType) {
				case WavType:
					temp =CaoZuoWavUtils.fenLiData(data.get(i),temp,"interim"+i);
					break;
				case Mp3Type:
					temp =CaoZuoMp3Utils.heBingMp3(data.get(i),temp,"interim"+i);
					break;
				}
				
				File filed=new File(oldtemp);  
		           if(filed.exists())  
		                filed.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message msg = MainActivity.mainHand.obtainMessage();
				msg.obj = null;
				msg.what = 12;
				MainActivity.mainHand.sendMessage(msg);// 结果返回
			}
			
		}
		loading.dismiss();
		data.clear();
		adapter.notifyDataSetChanged();
		Message msg = MainActivity.mainHand.obtainMessage();
		msg.obj = temp;
		msg.what = 11;
		MainActivity.mainHand.sendMessage(msg);// 结果返回
	}


	class SynthesisHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
				int vis =(Integer)msg.obj;
				MusicType fileType=MusicType.Null;
				String temp=SearchMainActivity.frequencyList.get(vis);
				if(temp.substring(temp.lastIndexOf(".")+1).equals("wav")){
					fileType=MusicType.WavType;
				}else if(temp.substring(temp.lastIndexOf(".")+1).equals("mp3")){
					fileType=MusicType.Mp3Type;
				}
				if(synthesisType!=MusicType.Null && synthesisType!=fileType){
					Message msgg = MainActivity.mainHand.obtainMessage();
					msgg.obj = null;
					msgg.what = 10;
					MainActivity.mainHand.sendMessage(msgg);// 结果返回
				}else{
					synthesisType=fileType;
					data.add(temp);
					adapter.notifyDataSetChanged();  
				}
			}
			
			
			super.handleMessage(msg);
		}
	
	}
	class ListItemMove implements DragItemChangeListener{

		@Override
		public void onDragItemChange(int dragSrcPosition, int dragPosition) {
			// TODO Auto-generated method stub
			String map= data.get(dragSrcPosition);  
			 //HashMap<String, Object> map 
		        data.remove(dragSrcPosition);  
		        data.add(dragPosition, map);  
		        adapter.notifyDataSetChanged();  
		}
		
	}
	
	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		data.remove(Integer.parseInt(v.getTag().toString()));
		if(data.size()<=0){
			synthesisType=MusicType.Null;
		}
        adapter.notifyDataSetChanged(); 
	}

	
}
