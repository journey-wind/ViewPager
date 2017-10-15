package com.example.ViewClass;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.viewpagertest.DrawWaveForm;
import com.example.viewpagertest.MainActivity;
import com.example.viewpagertest.R;
import com.example.viewpagertest.SocialMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
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
		int dirButtom=dip2px(this.getContext(),(float)124);
		int dirTop=dip2px(this.getContext(),(float)50);
		if(ev.getY()>=dirTop&&ev.getY()<=this.getHeight()-dirButtom
					&& curPosition==1 && !DrawWaveForm.isFirst){
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
	
	public static int dip2px(Context context, float dpValue) {
		 final float scale = context.getResources().getDisplayMetrics().density;
		 return (int) (dpValue * scale + 0.5f);
		}
	
	public int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  

}
