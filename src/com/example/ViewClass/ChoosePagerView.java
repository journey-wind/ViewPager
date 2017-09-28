package com.example.ViewClass;

import com.example.viewpagertest.R;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChoosePagerView extends ViewPager{

	private boolean noScroll;
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (noScroll)
            return false;
        else
        	return super.onTouchEvent(arg0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int curPosition = this.getCurrentItem();
//		if(curPosition==1){
//			getParent().requestDisallowInterceptTouchEvent(true);
//			
//		}
		px2dip(this.getContext(),(float)1000);
		if(ev.getY()>=60&&ev.getY()<=this.getWidth()-120
					&& curPosition==1){
			getParent().requestDisallowInterceptTouchEvent(true);
			noScroll=true;
		}
		else{
			getParent().requestDisallowInterceptTouchEvent(false);
			noScroll=false;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (noScroll)
            return false;
        else
        	return super.onInterceptTouchEvent(arg0);
	}

	public ChoosePagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  

}
