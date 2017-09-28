package com.example.ViewClass;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainPageView extends ViewPager{

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		 int curPosition;

	        switch (ev.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                getParent().requestDisallowInterceptTouchEvent(true);
	                break;
	            case MotionEvent.ACTION_MOVE:
	                curPosition = this.getCurrentItem();
	                int count = this.getAdapter().getCount();
	                // ����ǰҳ�������һҳ�͵�0ҳ��ʱ���ɸ������ش����¼�
	                if (curPosition == count - 1|| curPosition==0) {
	                    getParent().requestDisallowInterceptTouchEvent(false);
	                } else {//����������ɺ������ش����¼�
	                    getParent().requestDisallowInterceptTouchEvent(true);
	                }
	        }
	        return super.dispatchTouchEvent(ev);
	}

	public MainPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

}
