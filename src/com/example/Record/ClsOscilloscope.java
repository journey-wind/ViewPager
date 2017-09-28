package com.example.Record;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ClsOscilloscope {  
    private ArrayList<short[]> inBuf = new ArrayList<short[]>();  
    private boolean isRecording = false;// 线程控制标记  
    private File audioFile;
    /** 
     * X轴缩小的比例 
     */  
    public int rateX = 4;  
    /** 
     * Y轴缩小的比例 
     */  
    public int rateY = 4;  
    /** 
     * Y轴基线 
     */  
    public int baseLine = 0;  
    /** 
     * 初始化 
     */  
    public void initOscilloscope(int rateX, int rateY, int baseLine,File audioFile) {  
        this.rateX = 8;  
        this.rateY = rateY;  
        this.baseLine = baseLine;  
        this.audioFile=audioFile;
    }  
    /** 
     * 开始 
     *  
     * @param recBufSize 
     *            AudioRecord的MinBufferSize 
     */  
    public void Start(AudioRecord audioRecord, int recBufSize, SurfaceView sfv,  
            Paint mPaint) {  
    	
        isRecording = true;  
        new RecordThread(audioRecord, recBufSize).start();// 开始录制线程  
        new DrawThread(sfv, mPaint).start();// 开始绘制线程  
    }  
    /** 
     * 停止 
     */  
    public void Stop() {  
        isRecording = false;  
        inBuf.clear();// 清除  
    }  
    /** 
     * 负责从MIC保存数据到inBuf 
     *  
     * @author GV 
     *  
     */  
    class RecordThread extends Thread {  
        private int recBufSize;  
        private AudioRecord audioRecord;  
        public RecordThread(AudioRecord audioRecord, int recBufSize) {  
            this.audioRecord = audioRecord;  
            this.recBufSize = recBufSize;  
        }  
        public void run() {  
        	BytesTransUtil bytesTransUtil = BytesTransUtil.getInstance();
            try {  
            	DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));
                byte[] buffer = new byte[recBufSize];  
                audioRecord.startRecording();// 开始录制  
                while (isRecording) {  
                	int x=0;
                    // 从MIC保存数据到缓冲区  
                    int bufferReadResult = audioRecord.read(buffer, 0,  
                            recBufSize);  
                    if(bufferReadResult>0){
                    	dos.write(buffer);
                    
                    	short[] tmpBuf = new short[bufferReadResult / rateX];  
//                    for (int i = 0, ii = 0; i < tmpBuf.length; i++, x++ ,ii = i  
//                            * rateX) {  
//                    	dos.writeShort(buffer[x]);
                        tmpBuf = bytesTransUtil.Bytes2Shorts(buffer);
                        //dos.writeShort(tmpBuf[x]);
                    
//                    }  
//                    synchronized (inBuf) {//  
                        inBuf.add(tmpBuf);// 添加数据  
                    }
//                    }  
                }  
                audioRecord.stop();  
                dos.close();
            } catch (Throwable t) {  
            }  
        }  
    };  
    
    class Point{
    	public int X;
    	public int Y;
    	public Point(int x,int y){
    		X=x;
    		Y=y;
    	}
    }
    /** 
     * 负责绘制inBuf中的数据 
     *  
     * @author GV 
     *  
     */  
    class DrawThread extends Thread {  
        private int oldX = 0;// 上次绘制的X坐标  
        private int oldY = 0;// 上次绘制的Y坐标  
        private SurfaceView sfv;// 画板  
        private int X_index;// 当前画图所在屏幕X轴的坐标  
        private Paint mPaint;// 画笔  
        private int Wheight;
        private SurfaceHolder  sfh;   //surfaceView的 控制器  
        private ArrayList<Point> points;
        public DrawThread(SurfaceView sfv, Paint mPaint) {  
            this.sfv = sfv;  
            X_index=sfv.getWidth()-50;
            Wheight =sfv.getHeight();
            sfh=sfv.getHolder();
            this.mPaint = mPaint;  
            points=new ArrayList<Point>(1200);
        }  
        public void run() {  
            while (isRecording) {  
                ArrayList<short[]> buf = new ArrayList<short[]>();  
//                synchronized (inBuf) {  
                    if (inBuf.size() == 0)  
                        continue;  
//                    buf = (ArrayList<short[]>) inBuf.clone();// 保存  
//                    inBuf.clear();// 清除  
//                }  
                for (int i = 0; i < inBuf.size(); i++) {  
                    short[] tmpBuf = inBuf.get(i);  
                    SimpleDraw(X_index, tmpBuf, rateY, baseLine);// 把缓冲区数据画出来  
                    //X_index = X_index + tmpBuf.length;  
//                    if (X_index > sfv.getWidth()) {  
//                        X_index = 0;  
//                    }  
                }  
            }  
        }  
        /** 
         * 绘制指定区域 
         *  
         * @param start 
         *            X轴开始的位置(全屏) 
         * @param buffer 
         *            缓冲区 
         * @param rate 
         *            Y轴数据缩小的比例 
         * @param baseLine 
         *            Y轴基线 
         */  
        void SimpleDraw(int start, short[] buffer, int rate, int baseLine) { 
        	 int y=0;  
        	  
             for (int i = 0; i < buffer.length; i++) {// 有多少画多少  
            	 int x = start; 
            	 if(i%5==0){
             		Canvas canvas = sfh.lockCanvas(  
	                    new Rect(0, 0, start, Wheight));// 关键:获取画布  
             		canvas.drawColor(Color.BLACK);// 清除背景  
             		
             		 y = buffer[i]  + baseLine;
	                //canvas.drawLine(oldX-3, oldY, x, y, mPaint);  
	                // 调节缩小比例，调节基准线  
	                points.add(new Point(x, y));
	                points.get(0).X=points.get(0).X-3;
	                for(int j=1;j<points.size();j++){
	                	points.get(j).X=points.get(j).X-3;
	                	canvas.drawLine(points.get(j-1).X, points.get(j-1).Y, points.get(j).X, points.get(j).Y, mPaint);
	                }
	                
	                
            		//canvas.drawColor(Color.BLACK);// 清除背景 
	               
	                
	                if(points.get(0).X<0){
	                	points.remove(0);
	                }
	                
	             // 解锁画布，提交画好的图像  
	                sfh.unlockCanvasAndPost(canvas);
            	 }
             	
            }  
            
        }  
    }  
}  