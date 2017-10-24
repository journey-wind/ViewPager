package com.example.ViewClass;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.viewpagertest.MainActivity;
import com.example.viewpagertest.R;
import com.example.viewpagertest.SocialMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainPageView extends ViewPager{
	private float downX,downY;
	private ImageView iv;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		 int curPosition=this.getCurrentItem();;
		 float k;
	        switch (ev.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                getParent().requestDisallowInterceptTouchEvent(true);
	                
//	                downX=ev.getX();
//	                downY=ev.getY();
	                break;
	            case MotionEvent.ACTION_MOVE:
//	            	k=(downY-ev.getY());
	                curPosition = this.getCurrentItem();
	                int count = this.getAdapter().getCount();
	                // 当当前页面在最后一页和第0页的时候，由父亲拦截触摸事件
	                if (curPosition == count - 1|| curPosition==0 ) {
	                    getParent().requestDisallowInterceptTouchEvent(false);
	                } else {//其他情况，由孩子拦截触摸事件
	                    getParent().requestDisallowInterceptTouchEvent(true);
	                }
	               
	                break;
	            case MotionEvent.ACTION_UP:
	            	
	    			break;
	        }
	        
	        return super.dispatchTouchEvent(ev);
	}

	public MainPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
	}


	public static int dip2px(Context context, float dpValue) {
		 final float scale = context.getResources().getDisplayMetrics().density;
		 return (int) (dpValue * scale + 0.5f);
		}
	
}
