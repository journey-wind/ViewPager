package com.example.Record;

import java.io.IOException;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MediaPlayerUtil implements OnBufferingUpdateListener,  
OnCompletionListener, OnPreparedListener{

	public static MediaPlayerUtil mInstance;
	public SeekBar skbProgress;
	private MediaPlayer mediaPlayer;
	
	public synchronized static MediaPlayerUtil getInstance(SeekBar skbProgress){
        if(mInstance == null){
            mInstance = new MediaPlayerUtil(skbProgress);
        }
        mInstance.ChangeSeekBar(skbProgress);
        return mInstance;
    }
	
	public MediaPlayerUtil(SeekBar skbProgress){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		ChangeSeekBar(skbProgress);
	}
	
	public void Stop(){
		if (mediaPlayer != null) {   
            mediaPlayer.stop();  
        }  
	}
	
	public void ChangeSeekBar(SeekBar skbProgress){
		if (mediaPlayer != null) {   
            mediaPlayer.stop();  
        }  
		this.skbProgress=skbProgress;
		skbProgress.setOnSeekBarChangeListener(new MySeekbar());
	}
	
	public void Prepared(String url){
		 try {  
	            mediaPlayer.reset();  
	            mediaPlayer.setDataSource(url);  
	            mediaPlayer.prepare();//prepare之后自动播放  
	        } catch (IllegalArgumentException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IllegalStateException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
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
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		// TODO Auto-generated method stub
		skbProgress.setSecondaryProgress(bufferingProgress);  
        int currentProgress=skbProgress.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();  
	}  

}
