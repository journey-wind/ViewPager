package com.example.ViewClass;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.viewpagertest.R;

/**
 * Created by zhaocheng on 2016/11/3.
 */

public class RecordView extends View {
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
    	switch(event.getAction()){
    	  case MotionEvent.ACTION_DOWN:
    		  postInvalidate();
    		  if(timeTimer1==null){
    			  start();
    			  timeTimer1 = new Timer(true);
	    		  timeTimer1.schedule(timeTask1 = new TimerTask() {
	                  public void run() {
	                      Message msg = new Message();
	                      msg.what = 1;
	                      handler.sendMessage(msg);
	                  }
	              }, 20, 20);
	    		  
    		  }
    		  
    		  break;
    	  case MotionEvent.ACTION_UP:
    		  Log.d(TAG,"MotionEvent.ACTION_UP");
    		  cancel();
    		  break;
          default:
        	  break;
    	}
		return true;
	}
	//ViewĬ����С���
    private static final int DEFAULT_MIN_WIDTH = 500;
    public final static int MODEL_PLAY = 2;
    public final static int MODEL_RECORD = 1;
    private final TypedArray typedArray;
    //Բ���ı߾�
    private int pandding = 10;
    //Բ���Ŀ��
    private int widthing = 5;
    private Context mContext;
    private Paint mPaint;
    private final String TAG = "RecordView";
    private int countdownTime = 9;//����ʱʱ�䣬Ĭ��ʱ��10��
    private int countdownTime2 = 9;//����ʱʱ�䣬Ĭ��ʱ��10��.���ǻ���
    private float progress = 0;//�ܽ���360
    private boolean canDrawProgress = false;
    private double r;
    private Timer timeTimer = new Timer(true);
    private Timer progressTimer = new Timer(true);
    private long lastTime = 0;
    private int lineSpeed = 100;
    private float translateX = 0;
    /**
     * Բ����ɫ
     * */
    private int[] doughnutColors = new int[]{0xFFDAF6FE,0xFF45C3E5,0xFF45C3E5,0xFF45C3E5,0xFF45C3E5};
    /**
     * Ĭ����¼��ģʽ
     * */
    private int model = MODEL_RECORD;
    /**
     * ��ʱ����ʾʱ��
     * */
    private String hintText = "";
    /**
     * �������յ�ͼƬ
     * */
    private Drawable progressDrawable;
    /**
     * ���
     */
    private float amplitude = 1;
    /**
     * ����
     */
    private float volume = 10;
    private int fineness = 1;
    private float targetVolume = 1;
    private float maxVolume = 100;
    private boolean isSet = false;
    /**
     * ������
     */
    private int sensibility = 4;
    private boolean canSetVolume = true;

    private TimerTask timeTask;
    private TimerTask progressTask;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                countdownTime2++;
                if(countdownTime2 == 0){
                    listener.onCountDown();
                    canSetVolume = false;
                    timeTask.cancel();
                    postInvalidate();
                }
            }else if(msg.what == 2){
                progress += 360.00/(countdownTime*950.00/5.00);
//                Log.d(TAG,"progress:"+progress);
                if(progress >360){
                	progress=0;
//                    targetVolume = 1;
//                    postInvalidate();
//                    progressTask.cancel();
                	postInvalidate();
                }else
                    postInvalidate();
            }
        }
    };
    private OnCountDownListener listener;
    private float textHintSize;
    private float middleLineHeight;
    private int middleLineColor;
    private int voiceLineColor;
    private ArrayList<Path> paths;
    private int timeTextColor;
    private String unit;
    private String playHintText;
    

    public RecordView(Context context) {
        this(context,null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        typedArray = context.obtainStyledAttributes(attrs,R.styleable.recordView);
        initAtts();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//�������
        mPaint.setStyle(Paint.Style.STROKE);
        this.countdownTime = 9;
        this.countdownTime2 = 0;
    }

    private void initAtts(){
        model = typedArray.getInt(R.styleable.recordView_model,MODEL_RECORD);
        hintText = typedArray.getString(R.styleable.recordView_hintText);
        progressDrawable = typedArray.getDrawable(R.styleable.recordView_progressSrc) == null?
                getResources().getDrawable(R.drawable.light_blue):typedArray.getDrawable(R.styleable.recordView_progressSrc);
        textHintSize = typedArray.getDimension(R.styleable.recordView_hintTextSize,15);
        middleLineColor = typedArray.getColor(R.styleable.recordView_middleLineColor, getResources().getColor(R.color.RoundFillColor));
        voiceLineColor = typedArray.getColor(R.styleable.recordView_middleLineColor, getResources().getColor(R.color.RoundFillColor));
        middleLineHeight = typedArray.getDimension(R.styleable.recordView_middleLineHeight, 2);
        timeTextColor = typedArray.getColor(R.styleable.recordView_timeTextColor, getResources().getColor(R.color.TimeTextColor));
        unit = typedArray.getString(R.styleable.recordView_unit);
        playHintText = typedArray.getString(R.styleable.recordView_playHintText);
        paths = new ArrayList<>(20);
        for (int i = 0; i <20; i++) {
            paths.add(new Path());
        }
    }
    /**
     * ������Ϊwrap_contentʱ����Ĭ�ϳ���
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }
    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if(model == MODEL_RECORD){
            drawDefaultView(canvas);
            drawVoiceLine(canvas);
//        }else{
//            drawDefaultForPlay(canvas);
//            drawVoiceLine2(canvas);
//        }
        //��߿�����������
        if(canDrawProgress){
            drawProgress(canvas);
        }
    }
//    private void drawDefaultForPlay(Canvas canvas){
//        /**
//        * �Ȼ�һ����Բ
//        */
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(dip2px(mContext, widthing));
//        mPaint.setColor(mContext.getResources().getColor(R.color.RoundColor));
//        RectF oval = new RectF( dip2px(mContext, pandding)
//                , dip2px(mContext, pandding)
//                , getWidth()-dip2px(mContext, pandding)
//                , getHeight()-dip2px(mContext, pandding));
//        canvas.drawArc(oval, 0, 360, false, mPaint);    //����Բ��
//
//        /**
//         * ���ŵĵ�
//         * */
//        drawImageDot(canvas);
//
//        /**
//         * ��ʱ��
//         * */
//        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint2.setTextSize(dip2px(mContext,14));
//        paint2.setColor(mContext.getResources().getColor(R.color.RoundFillColor));
//        paint2.setTextAlign(Paint.Align.CENTER);
//        if(playHintText == null){
//            playHintText = "���ڲ���¼��.";
//        }
//        canvas.drawText(playHintText, getWidth()/2, getHeight()*1/3, paint2);
//    }
    private void drawDefaultView(Canvas canvas){
        /**
         * ����ʾ������
         * */
        if(hintText!=null&&!hintText.equals("")){
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(dip2px(mContext,textHintSize));
            paint.setColor(mContext.getResources().getColor(R.color.RoundHintTextColor));
            // ����������ʵ��ˮƽ���У�drawText��Ӧ��Ϊ����targetRect.centerX()
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(hintText, getWidth()/2, getHeight()/2+50, paint);
        }else{
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(dip2px(mContext,textHintSize));
            paint.setColor(mContext.getResources().getColor(R.color.RoundHintTextColor));
            // ����������ʵ��ˮƽ���У�drawText��Ӧ��Ϊ����targetRect.centerX()
            paint.setTextAlign(Paint.Align.CENTER);
            if(timeTimer1==null){
            	canvas.drawText("��ס��ʼ¼��", getWidth()/2, getHeight()/2+50, paint);
            }else{
            	canvas.drawText("��¼��ʱ��", getWidth()/2, getHeight()/2+50, paint);
            }
        }

        /**
         * ��ʱ��
         * */
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setTextSize(dip2px(mContext,60));
        paint2.setColor(mContext.getResources().getColor(R.color.TimeTextColor));
        paint2.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(countdownTime2+"", getWidth()/2, getHeight()/2-20, paint2);

        /**
         * ����λ��Ĭ��s
         * */
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setTextSize(dip2px(mContext,40));
        paint1.setColor(mContext.getResources().getColor(R.color.TimeTextColor));
        paint1.setTextAlign(Paint.Align.CENTER);
        float timeWidth = getWidth()/2f+paint2.measureText(countdownTime2+"")*2/3;
        canvas.drawText(unit == null ?"s":unit,timeWidth, getHeight()/2-20, paint1);
        /**
         * ��һ����Բ(��ɫ)
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dip2px(mContext, widthing));
        mPaint.setColor(mContext.getResources().getColor(R.color.RoundColor));
        RectF oval1 = new RectF( dip2px(mContext, pandding)
                , dip2px(mContext, pandding)
                , getWidth()-dip2px(mContext, pandding)
                , getHeight()-dip2px(mContext, pandding));
        canvas.drawArc(oval1, progress, 360, false, mPaint);    //����Բ��
    }
    public void setModel(int model){
        //this.model = model;
        postInvalidate();
    }
    /**
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * ����Բ��
     * */
    private void drawProgress(Canvas canvas){

        /**
         * ��߻�����
         */
        if(progress > 90){
            mPaint.setColor(getResources().getColor(R.color.RoundFillColor));
            mPaint.setStrokeWidth(dip2px(mContext, widthing));
            RectF oval = new RectF( dip2px(mContext, pandding)
                    , dip2px(mContext, pandding)
                    , getWidth()-dip2px(mContext, pandding)
                    , getHeight()-dip2px(mContext, pandding));
            canvas.drawArc(oval, 0, progress-90, false, mPaint);    //����Բ��
            r = getHeight()/2f-dip2px(mContext,pandding);
        }
        /**
         * ��һ����Բ(����)
         */
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.rotate(-90, getWidth() / 2, getHeight() / 2);
        mPaint.setStrokeWidth(dip2px(mContext, widthing));
        mPaint.setShader(new SweepGradient(getWidth() / 2, getHeight() / 2, doughnutColors, null));
        RectF oval = new RectF( dip2px(mContext, pandding)
                , dip2px(mContext, pandding)
                , getWidth()-dip2px(mContext, pandding)
                , getHeight()-dip2px(mContext, pandding));
        //�������ʵ���ﵱprogress����90�Ժ��һֱֻ��0-90�ȵ�Բ��
        canvas.drawArc(oval, 0, progress<90?progress:90, false, mPaint);    //����Բ��
        canvas.rotate(90, getWidth() / 2, getHeight() / 2);
        mPaint.reset();

        drawImageDot(canvas);
    }
    private void drawImageDot(Canvas canvas){
        /**
         * ��һ���㣨ͼƬ��
         * */
        if(r>0){
            if(progress >360)
            	return;
            double hu = Math.PI*Double.parseDouble(String.valueOf(progress))/180.0;
            //Log.d(TAG,"hu: "+hu);
            double p = Math.sin(hu)*r;
            //Log.d(TAG,"p: "+p);
            double q = Math.cos(hu)*r;
            //Log.d(TAG,"q: "+q);
            float x = (float) ((getWidth()-progressDrawable.getIntrinsicWidth())/2f+p);
            //Log.d(TAG,"x: "+x);
            float y = (float) ((dip2px(mContext,pandding)-progressDrawable.getIntrinsicHeight()/2f)+r-q);
            //Log.d(TAG,"y: "+y);
            canvas.drawBitmap(((BitmapDrawable)progressDrawable).getBitmap(),x,y,mPaint);
        }
    }
    /**
     * ������(¼��)
     * */
    private void drawVoiceLine(Canvas canvas) {
        lineChange();
        mPaint.setColor(voiceLineColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        canvas.save();
        int moveY = getHeight()*3/4;
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).reset();
            paths.get(i).moveTo(getWidth()*5/6, getHeight() *3/4);
        }
        for (float j = getWidth()*5/6 - 1; j >= getWidth()/6; j -= fineness) {
            float i = j-getWidth()/6;
            //��߱��뱣֤��ʼ����յ��ʱ��amplitude = 0;
            amplitude = 5 * volume *i / getWidth() - 5 * volume * i / getWidth() * i/getWidth()*6/4;
            for (int n = 1; n <= paths.size(); n++) {
                float sin = amplitude * (float) Math.sin((i - Math.pow(1.22, n)) * Math.PI / 180 - translateX);
                paths.get(n - 1).lineTo(j, (2 * n * sin / paths.size() - 15 * sin / paths.size() + moveY));
            }
        }
        for (int n = 0; n < paths.size(); n++) {
            if (n == paths.size() - 1) {
                mPaint.setAlpha(255);
            } else {
                mPaint.setAlpha(n * 130 / paths.size());
            }
            if (mPaint.getAlpha() > 0) {
                canvas.drawPath(paths.get(n), mPaint);
            }
        }
        canvas.restore();
    }
    /**
     * �����ƣ����ţ�
     * */
//    private void drawVoiceLine2(Canvas canvas) {
//        lineChange();
//        mPaint.setColor(voiceLineColor);
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(2);
//        canvas.save();
//        int moveY = getHeight() / 2;
//        int pandY = getWidth()/12;
//        for (int i = 0; i < paths.size(); i++) {
//            paths.get(i).reset();
//            paths.get(i).moveTo(getWidth()-pandY, getHeight() / 2);
//        }
//        for (float j = getWidth()*11/12 - 1; j >= getWidth()/12; j -= fineness) {
//            float i = j-getWidth()/12;
//            //��߱��뱣֤��ʼ����յ��ʱ��amplitude = 0;
//            amplitude = 4 * volume *i / getWidth() - 4 * volume * i / getWidth() * i/getWidth()*12/10;
//            for (int n = 1; n <= paths.size(); n++) {
//                float sin = amplitude * (float) Math.sin((i - Math.pow(1.22, n)) * Math.PI / 180 - translateX);
//                paths.get(n - 1).lineTo(j, (2 * n * sin / paths.size() - 15 * sin / paths.size() + moveY));
//            }
//        }
//        for (int n = 0; n < paths.size(); n++) {
//            if (n == paths.size() - 1) {
//                mPaint.setAlpha(255);
//            } else {
//                mPaint.setAlpha(n * 130 / paths.size());
//            }
//            if (mPaint.getAlpha() > 0) {
//                canvas.drawPath(paths.get(n), mPaint);
//            }
//        }
//        canvas.restore();
//    }

    
    private TimerTask timeTask1;
    private Timer timeTimer1 = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int db = (int) (Math.random()*100);
            setVolume(db);
        }
    };
    
    public void start(){
            //���ü�ʱ����ʾ��ʱ��
            canSetVolume = true;
            canDrawProgress = true;
            progress = 0;
            countdownTime2 = 0;
            //������ʱ��
            
            timeTimer.schedule(timeTask = new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }, 1000, 1000);
            progressTimer.schedule(progressTask = new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            },0,5);
    }
    private void lineChange() {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            translateX += 5;
        } else {
            if (System.currentTimeMillis() - lastTime > lineSpeed) {
                lastTime = System.currentTimeMillis();
                translateX += 5;
            } else {
                return;
            }
        }
        if (volume < targetVolume && isSet) {
            volume += getHeight() / 30;
        } else {
            isSet = false;
            if (volume <= 10) {
                volume = 10;
            } else {
                if (volume < getHeight() / 30) {
                    volume -= getHeight() / 60;
                } else {
                    volume -= getHeight() / 30;
                }
            }
        }
    }
    public void setVolume(int volume) {
        if(volume >100)
            volume = volume/100;
        volume = volume*2/5;
        if(!canSetVolume)
            return;
        if (volume > maxVolume * sensibility / 30) {
            isSet = true;
            this.targetVolume = getHeight() * volume / 3 / maxVolume;
            //Log.d(TAG,"targetVolume: "+targetVolume);
        }
    }
    interface OnCountDownListener{
        void onCountDown();
    }
    public void setOnCountDownListener(OnCountDownListener listener){
        this.listener = listener;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = 9;
        this.countdownTime2 = 0;
        postInvalidate();
    }
    public void cancel(){
        if(timeTimer1!=null){
        	canDrawProgress=false;
        	if(listener!=null)
        		listener.onCountDown();
            canSetVolume = false;
            timeTask.cancel();
            targetVolume = 1;
            postInvalidate();
            progressTask.cancel();
	        timeTimer1.cancel();
	        timeTimer1=null;
        }
    }
}
