package com.example.viewpagertest;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter{

	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView(mListViews.get(position)); 
	}

	public List<View> mListViews;   
	
	public MyPagerAdapter(List<View> mListViews) {   
		this.mListViews = mListViews;   
	}   

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager) container).addView(mListViews.get(position), 0);   
		return mListViews.get(position);   
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;   
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListViews.size();  
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == (arg1);   
	}

}
