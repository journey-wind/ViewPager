package com.example.ViewClass;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainPageView extends ViewPager{
	private float downX,downY;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		 int curPosition;
		 float k;

	        switch (ev.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                getParent().requestDisallowInterceptTouchEvent(true);
//	                downX=ev.getX();
//	                downY=ev.getY();
	                break;
	            case MotionEvent.ACTION_MOVE:
//	            	k=(downX-ev.getX())/(downY-ev.getY());
	                curPosition = this.getCurrentItem();
	                int count = this.getAdapter().getCount();
	                // 当当前页面在最后一页和第0页的时候，由父亲拦截触摸事件
	                if (curPosition == count - 1|| curPosition==0 ) {
	                    getParent().requestDisallowInterceptTouchEvent(false);
	                } else {//其他情况，由孩子拦截触摸事件
	                    getParent().requestDisallowInterceptTouchEvent(true);
	                }
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
