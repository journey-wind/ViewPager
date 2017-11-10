package com.example.viewpagertest;

import java.util.ArrayList;

import com.example.Record.AudioCodec;
import com.example.Record.CaoZuoWavUtils;
import com.example.viewpagertest.synthesisMusic.MusicType;

import android.content.Context;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicMix {

	private Context context;
	private View view;
	private ImageButton musicOne,musicTwo;
	private ImageButton musicMix;
	private TextView musicOneName,musicTwoName;
	private TextView musicOneStatue,musicTwoStatue;
	public static Handler myHander;
	private String onePath,twoPath;
	private long oneSampleRate;
	private int oneChannel;
	private long twoSampleRate;
	private int twoChannel;
	

	public MusicMix(Context context, View view){
		this.context=context;
		this.view=view;
		myHander=new musicMixHandler();
		findListenView();
		initTextView();
		
	}
	private void findListenView() {
		// TODO Auto-generated method stub
		musicOne =(ImageButton)view.findViewById(R.id.btn_lay4_musicOne);
		musicTwo =(ImageButton)view.findViewById(R.id.btn_lay4_musicTwo);
		musicMix =(ImageButton)view.findViewById(R.id.btn_lay4_musicMix);
		musicOneName =(TextView)view.findViewById(R.id.tv_lay4_musicOneName);
		musicOneStatue =(TextView)view.findViewById(R.id.tv_lay4_musicOneStatue);
		musicTwoName =(TextView)view.findViewById(R.id.tv_lay4_musicTwoName);
		musicTwoStatue =(TextView)view.findViewById(R.id.tv_lay4_musicTwoStatue);
		musicOne.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Message msg = MainActivity.mainHand.obtainMessage();
				msg.obj = null;
				msg.what = 13;
				MainActivity.mainHand.sendMessage(msg);// 结果返回
			}
		});
		musicTwo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Message msg = MainActivity.mainHand.obtainMessage();
				msg.obj = null;
				msg.what = 14;
				MainActivity.mainHand.sendMessage(msg);// 结果返回
			}
		});
		musicMix.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//musicMix.setBackgroundResource(R.drawable.progressbar_mix);
				if(oneChannel==twoChannel && oneSampleRate==twoSampleRate && oneSampleRate!=-1){
					musicMix.setImageResource(R.drawable.progressbar_mix);
					musicMix.setEnabled(false);
					musicOne.setEnabled(false);
					musicTwo.setEnabled(false);
					new Thread(new MusicMixRun(oneSampleRate, oneChannel,"/storage/emulated/0/ccdd.wav")).start();
				}else{
					Message msg = MainActivity.mainHand.obtainMessage();
					msg.obj = null;
					msg.what = 17;
					MainActivity.mainHand.sendMessage(msg);// 结果返回
				}
			}
		});
	}
	private boolean changeTextView(String path,int num) {
		// TODO Auto-generated method stub
		int start=path.lastIndexOf("/")+1;
		AudioCodec audioCodec=AudioCodec.newInstance();
		audioCodec.setEncodeType(MediaFormat.MIMETYPE_AUDIO_MPEG);
		audioCodec.setIOPath(path);
		audioCodec.prepare(false);
		audioCodec.release();
		if(num==1){
			onePath=new String(path);
			musicOneName.setText(path.substring(start));
			oneSampleRate=audioCodec.mSampleRate;
			oneChannel=audioCodec.channel;
			musicOneStatue.setText(audioCodec.mSampleRate+"Hz "+audioCodec.channel+"Channel");
		}else if(num==2){
			twoPath=new String(path);
			musicTwoName.setText(path.substring(start));
			twoSampleRate=audioCodec.mSampleRate;
			twoChannel=audioCodec.channel;
			musicTwoStatue.setText(audioCodec.mSampleRate+"Hz "+audioCodec.channel+"Channel");
		}
		return true;
		
	}
	private void initTextView() {
		// TODO Auto-generated method stub
		onePath=null;
		twoPath=null;
		oneSampleRate=-1;
		twoSampleRate=-1;
		oneChannel=0;
		twoChannel=0;
		musicMix.setImageResource(R.drawable.type_music);
		musicOne.setEnabled(true);
		musicTwo.setEnabled(true);
		musicMix.setEnabled(true);
		musicOneName.setText("暂无");
		musicTwoName.setText("暂无");
		musicOneStatue.setText("0Hz 0Channel");
		musicTwoStatue.setText("0Hz 0Channel");
	}
	
	class musicMixHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
				int vis =(Integer)msg.obj;
				String path=SearchMainActivity.frequencyList.get(vis);
				changeTextView(path,1);
			}else if (msg.what == 2) {
				int vis =(Integer)msg.obj;
				String path=SearchMainActivity.frequencyList.get(vis);
				changeTextView(path,2);
			}else if (msg.what == 3) {
				boolean vis =(boolean)msg.obj;
				Message msgg = MainActivity.mainHand.obtainMessage();
				msgg.obj = vis;
				msgg.what = 15;
				MainActivity.mainHand.sendMessage(msgg);// 结果返回
			}else if (msg.what == 4) {
				boolean vis =(boolean)msg.obj;
				Message msgg = MainActivity.mainHand.obtainMessage();
				msgg.obj = vis;
				msgg.what = 16;
				MainActivity.mainHand.sendMessage(msgg);// 结果返回
			}else if (msg.what == 5) {
				initTextView();
			}
			super.handleMessage(msg);
		}
	
	}
	
	class MusicMixRun implements Runnable{
		ArrayList<byte[]> musicPCMone;
		ArrayList<byte[]> musicPCMtwo;
		private boolean musicOneOk=false,musicTwoOk=false;
		private long longSampleRate;
		private int channels;
		private String totalpath;
		public MusicMixRun(long longSampleRate,int channels,String Path){
			this.longSampleRate=longSampleRate;
			this.channels=channels;
			totalpath=Path;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int oneStart=onePath.lastIndexOf(".")+1;
			int twoStart=twoPath.lastIndexOf(".")+1;
			if(onePath.substring(oneStart).equals("mp3")){
				mp3data(1,onePath);
			}else if(onePath.substring(oneStart).equals("wav")){
				musicOneOk=wavdata(1,onePath);
				sendToHander(3,musicOneOk);
			}
			
			if(twoPath.substring(twoStart).equals("mp3")){
				mp3data(2,twoPath);
			}else if(twoPath.substring(twoStart).equals("wav")){
				musicTwoOk=wavdata(2,twoPath);
				sendToHander(4,musicTwoOk);
			}
			while(!(musicOneOk && musicTwoOk)){
				
			}
			musicPCMone=mixMusic(musicPCMone,musicPCMtwo);
			CaoZuoWavUtils.convertWaveFile(totalpath, musicPCMone, longSampleRate, channels);
			Message msg = MainActivity.mainHand.obtainMessage();
			msg.obj = null;
			msg.what = 18;
			MainActivity.mainHand.sendMessage(msg);// 结果返回
			sendToHander(5,true);

		}
		private void sendToHander(int num,boolean musicOk){
			Message msg = myHander.obtainMessage();
			msg.obj = musicOk;
			msg.what = num;
			myHander.sendMessage(msg);// 结果返回
		}
		private void mp3data(int number,String path){
			final AudioCodec audioCodec=AudioCodec.newInstance();
			final int num=number;
			audioCodec.setEncodeType(MediaFormat.MIMETYPE_AUDIO_MPEG);
			audioCodec.setIOPath(path);
			audioCodec.prepare(true);
			audioCodec.startAsync();
			audioCodec.setOnCompleteListener(new AudioCodec.OnCompleteListener() {

				@Override
			    public void completed() {
					if(num==1){
						musicPCMone = audioCodec.getPCMbyte();
						musicOneOk=true;
						sendToHander(3,musicOneOk);
					}else if(num==2){
						musicPCMtwo = audioCodec.getPCMbyte();
						musicTwoOk=true;
						sendToHander(4,musicTwoOk);
					}
					
			        audioCodec.release();
			    }
			});
		}
		private boolean wavdata(int num,String path){
			if(num==1){
				musicPCMone=CaoZuoWavUtils.fenLiPathData(path);
			}else if(num==2){
				musicPCMtwo=CaoZuoWavUtils.fenLiPathData(path);
			}
			return true;
		}
		
	}
	
	private ArrayList<byte[]> mixMusic(ArrayList<byte[]> one,ArrayList<byte[]>two){
		 ArrayList<byte[]> temp =new ArrayList<byte[]>();
		 byte[] temp_t;
		 int index_one=2,index_two=2;
		 int index_one_byte=0,index_two_byte=0;
		 int data1;
		 int data2;
		 double date_mix;
		 temp.add(one.get(0));
		 temp.add(one.get(1));
		while(true){
			if(index_one>=one.size()||index_two>=two.size()){
				break;
			}
			temp_t=new byte[4096];
			for(int i=0;i<4096;i++){
				if(index_one_byte>=one.get(index_one).length){
					index_one++;
					if(index_one>=one.size()){
						break;
					}
					index_one_byte=0;
				}
				if(index_two_byte>=two.get(index_two).length){
					index_two++;
					if(index_two>=two.size()){
						break;
					}
					index_two_byte=0;
				}
				data1=one.get(index_one)[index_one_byte];
				data2=two.get(index_two)[index_two_byte];
				if( data1 < 0 && data2 < 0) { 
				    date_mix = (data1+data2 - (data1 * data2 / -(Math.pow(2,8-1)-1)));  
				}else if(data1 == 0 && data2 == 0) {
					if(data1 == 0){
						date_mix=data2;
					}else{
						date_mix=data1;
					}
					
				}else{
				    date_mix = (data1+data2 - (data1 * data2 / (Math.pow(2,8-1)-1)));
				}
				temp_t[i]=(byte)date_mix;
				index_one_byte++;
				index_two_byte++;

			}
			temp.add(temp_t);
		}
		
		return temp;
	}
	
}
