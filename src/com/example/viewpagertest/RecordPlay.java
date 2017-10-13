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
	 * ��ť
	 */
	private Button bt_exit;
	private Button bt_star;
	/**
	 * AudioRecord д�뻺������С
	 */
	protected int m_in_buf_size;
	/**
	 * ¼����Ƶ����
	 */
	private AudioRecord m_in_rec;
	/**
	 * ¼����ֽ�����
	 */
	private byte[] m_in_bytes;
	/**
	 * ���¼���ֽ�����Ĵ�С
	 */
	private LinkedList<byte[]> m_in_q;
	/**
	 * AudioTrack ���Ż����С
	 */
	private int m_out_buf_size;
	/**
	 * ������Ƶ����
	 */
	private AudioTrack m_out_trk;
	/**
	 * ���ŵ��ֽ�����
	 */
	private byte[] m_out_bytes;
	/**
	 * ¼����Ƶ�߳�
	 */
	private Thread record;
	/**
	 * ������Ƶ�߳�
	 */
	private Thread play;
	/**
	 * ���߳�ֹͣ�ı�־
	 */
	private boolean flag = true;
	/**
	 * ��Ƶ�滭�߳�
	 */
	private Thread draw;
	/**
	 * �滭����
	 */
	private byte[] m_draw_bytes;
	
	
	private Paint mPaint;
	private SurfaceView sfv;
	private View view;
	
	public RecordPlay(View view) {
		// TODO Auto-generated constructor stub
		this.view=view;
		mPaint = new Paint();    
        mPaint.setColor(Color.GREEN);// ����Ϊ��ɫ    
        mPaint.setStrokeWidth(2);// ���û��ʴ�ϸ   
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
		// ����¼���߳�
		record.start();
		// ���������߳�
		play.start();
		draw.start();
	}
	private void init()
	{
		
		// AudioRecord �õ�¼����С�������Ĵ�С
		m_in_buf_size = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// ʵ����������Ƶ����
		m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);
		// ʵ����һ���ֽ����飬����Ϊ��С�������ĳ���
		m_in_bytes = new byte[m_in_buf_size];
		// ʵ����һ��������������ֽ�����
		m_in_q = new LinkedList<byte[]>();

		// AudioTrack �õ�������С�������Ĵ�С
		m_out_buf_size = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// ʵ����������Ƶ����
		m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
				AudioTrack.MODE_STREAM);
		// ʵ����һ������Ϊ������С�����С���ֽ�����
		m_out_bytes = new byte[m_out_buf_size];
		
        
	}

	/*
	 ¼���߳�
	 */
	class recordSound implements Runnable
	{
		@Override
		public void run()
		{
			byte[] bytes_pkg;
			// ��ʼ¼��
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
	 �����߳�
	 */
	class playRecord implements Runnable
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			
			byte[] bytes_pkg = null;
			// ��ʼ����
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
	                    new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));// �ؼ�:��ȡ����  
            	canvas.drawColor(Color.BLACK);// �������  
            	sfv.getHolder().unlockCanvasAndPost(canvas);	
			}
			break;
		case R.id.button1:
			recordOnPlay();
			break;
			
		}
	}
	
	class DrawThread extends Thread {  

        private SurfaceHolder  sfh;   //surfaceView�� ������  
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
	                    new Rect(0, 0, width, height));// �ؼ�:��ȡ����  
            	canvas.drawColor(Color.BLACK);// �������  
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
