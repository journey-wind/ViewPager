package com.example.viewpagertest;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainChooseActivity extends ViewGroup {

	
	private ViewPager mPager;//ҳ������
    private List<View> listViews; // Tabҳ���б�
    private ImageView cursor;// ����ͼƬ
    private TextView t1, t2, t3;// ҳ��ͷ��
    private int offset = 0;// ����ͼƬƫ����
    private int currIndex = 0;// ��ǰҳ�����
    private int bmpW;// ����ͼƬ����
	private Context context;
	private View view;
	
	 public MainChooseActivity(Context context, AttributeSet attrs,View view) {
		 super(context, attrs);
			//setContentView(R.layout.main_choose);
		this.context=context;
		this.view=view;
		inflate(context, R.layout.main_choose, this);
		//InitLayout();
		InitTextView();
		InitImageView();
		InitViewPager();
	}

	 private void InitLayout() {   
		 mPager=new ViewPager(context);
		 cursor=new ImageView(context);
		 t1=new TextView(context);
		 t2=new TextView(context);
		 t3=new TextView(context);
		 LinearLayout lly1=new LinearLayout(context);
		 LinearLayout lly2=new LinearLayout(context);
		 
		 lly1.setOrientation(LinearLayout.VERTICAL);
		 lly2.setPadding(0, 8, 0, 5);
		 Color cl=new Color();
		
	 }
	private void InitTextView() {   
		t1 = (TextView) view.findViewById(R.id.text4);   
		t2 = (TextView) view.findViewById(R.id.text5);   
		t3 = (TextView) view.findViewById(R.id.text6);   
		 
		t1.setOnClickListener(new MyOnClickListener(0));   
		t2.setOnClickListener(new MyOnClickListener(1));   
		t3.setOnClickListener(new MyOnClickListener(2));   
	}  
	

	private void InitViewPager() {   
		mPager = (ViewPager) view.findViewById(R.id.vPager2);   
		
		listViews = new ArrayList<View>();   
		LayoutInflater mInflater = ((Activity) context).getLayoutInflater();   
		listViews.add(mInflater.inflate(R.layout.lay1, null));
		listViews.add(mInflater.inflate(R.layout.lay2, null));
		listViews.add(mInflater.inflate(R.layout.lay3, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));   
		mPager.setCurrentItem(0);   
		
		SoundRecord tt =new SoundRecord(listViews.get(0).getContext(), null,listViews.get(0));
		DrawWaveForm t2 =new DrawWaveForm(listViews.get(1).getContext(), null,listViews.get(1));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener()); 
		
		
		}
	
	private void InitImageView() {   
		cursor = (ImageView) view.findViewById(R.id.cursor); 
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.btnbase)   
		.getWidth();// ��ȡͼƬ����   
		DisplayMetrics dm = new DisplayMetrics();   
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);   
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ���   
		LayoutParams para;
        para = cursor.getLayoutParams();
        para.width = screenW / 3-10;
        cursor.setLayoutParams(para);
		offset = (screenW / 3 - bmpW) / 2;// ����ƫ����   
		Matrix matrix = new Matrix();   
		matrix.postTranslate(offset, 0);   
		cursor.setImageMatrix(matrix);// ���ö�����ʼλ��   
		}  
	
	
	public class MyOnClickListener implements View.OnClickListener {   
		private int index = 0;   
		  
		public MyOnClickListener(int i) {   
			index = i;   
		}   
		  
		@Override  
		public void onClick(View v) {   
		mPager.setCurrentItem(index);   
		
		}   
		};
	
		public class MyOnPageChangeListener implements OnPageChangeListener {


			int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����   
			int two = one * 2;// ҳ��1 -> ҳ��3 ƫ����   
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Animation animation = null;   
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Animation animation = null;   
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Animation animation = null;   
				switch (arg0) {   
				case 0:   
				if (currIndex == 1) {   
				animation = new TranslateAnimation(one, 0, 0, 0);   
				} else if (currIndex == 2) {   
				animation = new TranslateAnimation(two, 0, 0, 0);   
				}   
				break;   
				case 1:   
				if (currIndex == 0) {   
				animation = new TranslateAnimation(offset, one, 0, 0);   
				} else if (currIndex == 2) {   
				animation = new TranslateAnimation(two, one, 0, 0);   
				}   
				break;   
				case 2:   
				if (currIndex == 0) {   
				animation = new TranslateAnimation(offset, two, 0, 0);   
				} else if (currIndex == 1) {   
				animation = new TranslateAnimation(one, two, 0, 0);   
				}   
				break;   
				}   
				currIndex = arg0;   
				animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��   
				animation.setDuration(300);   
				cursor.startAnimation(animation);   
			}

		}

		@Override
		protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
			// TODO Auto-generated method stub
			
		}
}
	
