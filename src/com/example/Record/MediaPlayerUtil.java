package com.example.Record;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.example.viewpagertest.MainActivity;
import com.example.viewpagertest.R;
import com.example.viewpagertest.MsgListViewAdapter.ViewHolder;
import com.example.viewpagertest.SocialMessage;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MediaPlayerUtil implements OnBufferingUpdateListener,  
OnCompletionListener, OnPreparedListener{

	public static MediaPlayerUtil mInstance;
	public SeekBar skbProgress;
	public MediaPlayer mediaPlayer;
	private Timer mTimer;
	private TextView time;
	ViewHolder holder;
	
	public synchronized static MediaPlayerUtil getInstance(ViewHolder holders){
        if(mInstance == null){
            mInstance = new MediaPlayerUtil(holders);
        }
        mInstance.ChangeSeekBar(holders);
        return mInstance;
    }
	
	public MediaPlayerUtil(ViewHolder holders){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setLooping(true);  
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		ChangeSeekBar(holders);
		  
	}
	
	
    
    Handler handleProgress = new Handler() {  
        public void handleMessage(Message msg) {  
  
            int position = mediaPlayer.getCurrentPosition();  
            int duration = mediaPlayer.getDuration();  

		    String minute=String.format("%02d", position/1000/60);
		    String second=String.format("%02d", position/1000%60);
            if (duration > 0) {  
            	time.setText(minute+":"+second);
                long pos = (long) (skbProgress.getMax() * ((double)position / duration));  
                skbProgress.setProgress((int) pos);  
            }  
        };  
    };  
    
	public void Stop(){
		if(holder!=null){
			holder.isplay=true;
			holder.isfirst=true;
			holder.iv_play.setImageResource(R.drawable.play_small);
		}
		if (mediaPlayer != null) {   
            mediaPlayer.stop();  
        }  
		if(this.skbProgress!=null){
			skbProgress.setProgress(0);  
			this.skbProgress.setOnSeekBarChangeListener(null);
		}
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
		if(time!=null){
			time.setText("00:00");
		}
	}
	
	public void ChangeSeekBar(ViewHolder holder){
		Stop();
		this.holder=holder;
		this.time=holder.tv_musicPoint;
		this.skbProgress=holder.sb_music;
		skbProgress.setOnSeekBarChangeListener(new MySeekbar());
	}

	public boolean Prepared(){
		mTimer=new Timer();
		TimerTask mTimerTask = new TimerTask() {  
	        @Override  
	        public void run() {  
	            if(mediaPlayer==null)  
	                return;  
	            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false ) {  
	                handleProgress.sendEmptyMessage(0);  
	            }  
	        }  
	    };  
		boolean isError=false;
		String aa = "";
		mTimer.schedule(mTimerTask, 0, 200);
		 try {  
	            mediaPlayer.reset();  
	            mediaPlayer.setDataSource(holder.musicPath);
	            mediaPlayer.prepare();//prepare之后自动播放  
	            
	        } catch (Exception e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	            aa=e.getMessage();
	            isError=true;
	        }
		 if(isError){
			 Message msgg = MainActivity.mainHand.obtainMessage();
			msgg.obj=aa;
			msgg.what = 4;
			MainActivity.mainHand.sendMessage(msgg);// 结果返回
			return false;
		 }
		 return true;
	}

	class MySeekbar implements OnSeekBarChangeListener {  
		int progress;  
        public void onProgressChanged(SeekBar seekBar, int progress,  
                boolean fromUser) {  
        	this.progress = progress * mediaPlayer.getDuration()  
                    / seekBar.getMax();  
        }  
  
        public void onStartTrackingTouch(SeekBar seekBar) {  
  
        }  
  
        public void onStopTrackingTouch(SeekBar seekBar) {  
        		mediaPlayer.seekTo(progress);  
        }  
	
	}
	
	
	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		arg0.start();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		holder.iv_play.setImageResource(R.drawable.play_small); 
		skbProgress.setProgress(0);  
		holder.isplay=false;
		//this.skbProgress.setOnSeekBarChangeListener(null);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		// TODO Auto-generated method stub
		skbProgress.setSecondaryProgress(bufferingProgress);  
        int currentProgress=skbProgress.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();  
	}  

}
