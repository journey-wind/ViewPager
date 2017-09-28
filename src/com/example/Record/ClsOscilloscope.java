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
    private boolean isRecording = false;// �߳̿��Ʊ��  
    private File audioFile;
    /** 
     * X����С�ı��� 
     */  
    public int rateX = 4;  
    /** 
     * Y����С�ı��� 
     */  
    public int rateY = 4;  
    /** 
     * Y����� 
     */  
    public int baseLine = 0;  
    /** 
     * ��ʼ�� 
     */  
    public void initOscilloscope(int rateX, int rateY, int baseLine,File audioFile) {  
        this.rateX = 8;  
        this.rateY = rateY;  
        this.baseLine = baseLine;  
        this.audioFile=audioFile;
    }  
    /** 
     * ��ʼ 
     *  
     * @param recBufSize 
     *            AudioRecord��MinBufferSize 
     */  
    public void Start(AudioRecord audioRecord, int recBufSize, SurfaceView sfv,  
            Paint mPaint) {  
    	
        isRecording = true;  
        new RecordThread(audioRecord, recBufSize).start();// ��ʼ¼���߳�  
        new DrawThread(sfv, mPaint).start();// ��ʼ�����߳�  
    }  
    /** 
     * ֹͣ 
     */  
    public void Stop() {  
        isRecording = false;  
        inBuf.clear();// ���  
    }  
    /** 
     * �����MIC�������ݵ�inBuf 
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
                audioRecord.startRecording();// ��ʼ¼��  
                while (isRecording) {  
                	int x=0;
                    // ��MIC�������ݵ�������  
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
                        inBuf.add(tmpBuf);// �������  
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
     * �������inBuf�е����� 
     *  
     * @author GV 
     *  
     */  
    class DrawThread extends Thread {  
        private int oldX = 0;// �ϴλ��Ƶ�X����  
        private int oldY = 0;// �ϴλ��Ƶ�Y����  
        private SurfaceView sfv;// ����  
        private int X_index;// ��ǰ��ͼ������ĻX�������  
        private Paint mPaint;// ����  
        private int Wheight;
        private SurfaceHolder  sfh;   //surfaceView�� ������  
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
//                    buf = (ArrayList<short[]>) inBuf.clone();// ����  
//                    inBuf.clear();// ���  
//                }  
                for (int i = 0; i < inBuf.size(); i++) {  
                    short[] tmpBuf = inBuf.get(i);  
                    SimpleDraw(X_index, tmpBuf, rateY, baseLine);// �ѻ��������ݻ�����  
                    //X_index = X_index + tmpBuf.length;  
//                    if (X_index > sfv.getWidth()) {  
//                        X_index = 0;  
//                    }  
                }  
            }  
        }  
        /** 
         * ����ָ������ 
         *  
         * @param start 
         *            X�Ὺʼ��λ��(ȫ��) 
         * @param buffer 
         *            ������ 
         * @param rate 
         *            Y��������С�ı��� 
         * @param baseLine 
         *            Y����� 
         */  
        void SimpleDraw(int start, short[] buffer, int rate, int baseLine) { 
        	 int y=0;  
        	  
             for (int i = 0; i < buffer.length; i++) {// �ж��ٻ�����  
            	 int x = start; 
            	 if(i%5==0){
             		Canvas canvas = sfh.lockCanvas(  
	                    new Rect(0, 0, start, Wheight));// �ؼ�:��ȡ����  
             		canvas.drawColor(Color.BLACK);// �������  
             		
             		 y = buffer[i]  + baseLine;
	                //canvas.drawLine(oldX-3, oldY, x, y, mPaint);  
	                // ������С���������ڻ�׼��  
	                points.add(new Point(x, y));
	                points.get(0).X=points.get(0).X-3;
	                for(int j=1;j<points.size();j++){
	                	points.get(j).X=points.get(j).X-3;
	                	canvas.drawLine(points.get(j-1).X, points.get(j-1).Y, points.get(j).X, points.get(j).Y, mPaint);
	                }
	                
	                
            		//canvas.drawColor(Color.BLACK);// ������� 
	               
	                
	                if(points.get(0).X<0){
	                	points.remove(0);
	                }
	                
	             // �����������ύ���õ�ͼ��  
	                sfh.unlockCanvasAndPost(canvas);
            	 }
             	
            }  
            
        }  
    }  
}  