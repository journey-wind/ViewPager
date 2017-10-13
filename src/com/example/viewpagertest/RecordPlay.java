package com.example.viewpagertest;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordPlay implements OnClickListener{
	/**
	 * 按钮
	 */
	private Button bt_exit;
	private Button bt_star;
	/**
	 * AudioRecord 写入缓冲区大小
	 */
	protected int m_in_buf_size;
	/**
	 * 录制音频对象
	 */
	private AudioRecord m_in_rec;
	/**
	 * 录入的字节数组
	 */
	private byte[] m_in_bytes;
	/**
	 * 存放录入字节数组的大小
	 */
	private LinkedList<byte[]> m_in_q;
	/**
	 * AudioTrack 播放缓冲大小
	 */
	private int m_out_buf_size;
	/**
	 * 播放音频对象
	 */
	private AudioTrack m_out_trk;
	/**
	 * 播放的字节数组
	 */
	private byte[] m_out_bytes;
	/**
	 * 录制音频线程
	 */
	private Thread record;
	/**
	 * 播放音频线程
	 */
	private Thread play;
	/**
	 * 让线程停止的标志
	 */
	private boolean flag = true;
	/**
	 * 音频绘画线程
	 */
	private Thread draw;
	/**
	 * 绘画数组
	 */
	private byte[] m_draw_bytes;
	
	
	private Paint mPaint;
	private SurfaceView sfv;
	private View view;
	
	public RecordPlay(View view) {
		// TODO Auto-generated constructor stub
		this.view=view;
		mPaint = new Paint();    
        mPaint.setColor(Color.GREEN);// 画笔为绿色    
        mPaint.setStrokeWidth(2);// 设置画笔粗细   
        bt_star = (Button) view.findViewById(R.id.button1);
		bt_exit = (Button) view.findViewById(R.id.button3);
		bt_star.setOnClickListener(this);
		bt_exit.setOnClickListener(this);
		
	}
	
	public void recordOnPlay(){
		init();
		flag=true;
		record = new Thread(new recordSound());
		play = new Thread(new playRecord());
		draw = new DrawThread();
		// 启动录制线程
		record.start();
		// 启动播放线程
		play.start();
		draw.start();
	}
	private void init()
	{
		
		// AudioRecord 得到录制最小缓冲区的大小
		m_in_buf_size = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// 实例化播放音频对象
		m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);
		// 实例化一个字节数组，长度为最小缓冲区的长度
		m_in_bytes = new byte[m_in_buf_size];
		// 实例化一个链表，用来存放字节组数
		m_in_q = new LinkedList<byte[]>();

		// AudioTrack 得到播放最小缓冲区的大小
		m_out_buf_size = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// 实例化播放音频对象
		m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
				AudioTrack.MODE_STREAM);
		// 实例化一个长度为播放最小缓冲大小的字节数组
		m_out_bytes = new byte[m_out_buf_size];
		
        
	}

	/*
	 录音线程
	 */
	class recordSound implements Runnable
	{
		@Override
		public void run()
		{
			byte[] bytes_pkg;
			// 开始录音
			m_in_rec.startRecording();

			while (flag)
			{
				m_in_rec.read(m_in_bytes, 0, m_in_buf_size);
				bytes_pkg = m_in_bytes.clone();
				
				if (m_in_q.size() >= 2)
				{
					m_in_q.removeFirst();
				}
				m_in_q.add(bytes_pkg);
			}
		}

	}

	/*
	 播放线程
	 */
	class playRecord implements Runnable
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			
			byte[] bytes_pkg = null;
			// 开始播放
			m_out_trk.play();

			while (flag)
			{
				try
				{
					
					m_out_bytes = m_in_q.getFirst();
					bytes_pkg = m_out_bytes.clone();
					m_draw_bytes=bytes_pkg.clone();
					m_out_trk.write(bytes_pkg, 0, bytes_pkg.length);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.button3:
			flag = false;
			m_in_rec.stop();
			m_in_rec = null;
			m_out_trk.stop();
			m_out_trk = null;
			record=null;
			play=null;
			draw=null;
			if(sfv!=null){
				Canvas canvas = sfv.getHolder().lockCanvas(  
	                    new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));// 关键:获取画布  
            	canvas.drawColor(Color.BLACK);// 清除背景  
            	sfv.getHolder().unlockCanvasAndPost(canvas);	
			}
			break;
		case R.id.button1:
			recordOnPlay();
			break;
			
		}
	}
	
	class DrawThread extends Thread {  

        private SurfaceHolder  sfh;   //surfaceView的 控制器  
        private int width;
        private int height;
        
        public DrawThread() {  
        	sfv = (SurfaceView) view.findViewById(R.id.SurfaceView01);
        	sfh=sfv.getHolder();
        	width=sfv.getWidth();
        	height=sfv.getHeight();
        }  
        public void run() {  
        	float def;
        	byte[] bytes_pkg = null;
            while (flag) {  
            	try
				{
            		if(m_draw_bytes == null){
            			continue;
            		}
            		bytes_pkg = m_draw_bytes.clone();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
            	if(bytes_pkg==null){
            		continue;
            	}
            	def=(width)/(float)bytes_pkg.length;
            	Canvas canvas = sfh.lockCanvas(  
	                    new Rect(0, 0, width, height));// 关键:获取画布  
            	canvas.drawColor(Color.BLACK);// 清除背景  
            	for (int i = 1; i < bytes_pkg.length; i++) {
            		if(i >= width){
            			break;
            		}
            		if(i%2==1){
            			continue;
            		}
            		canvas.drawLine((int)(def*i), (bytes_pkg[i-1]+height/2), (int)(def*i), height, mPaint);
            	}
            	
            	sfh.unlockCanvasAndPost(canvas);	
            	
            }  
        }  
	}  
        
	

}
