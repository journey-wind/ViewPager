package com.example.viewpagertest;

import java.util.ArrayList;
import java.util.List;

import com.example.ViewClass.ChoosePagerView;

import android.R.color;
import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainChooseActivity extends ViewGroup {

	
	private ViewPager mPager;//Ò³¿¨ÄÚÈÝ
    private List<View> listViews; // TabÒ³ÃæÁÐ±í
    private ImageView cursor;// ¶¯»­Í¼Æ¬
    private TextView t1, t2, t3,t4;// Ò³¿¨Í·±ê
    private int offset = 0;// ¶¯»­Í¼Æ¬Æ«ÒÆÁ¿
    private int currIndex = 0;// µ±Ç°Ò³¿¨±àºÅ
    private int bmpW;// ¶¯»­Í¼Æ¬¿í¶È
	private Context context;
	private View view;
	DrawWaveForm dwf;
	public static Handler changeFile;
	
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
		changeFile=new ChooseHandle();
	}

	 private void InitLayout() {   
		 mPager=new ViewPager(context);
		 cursor=new ImageView(context);
		 t1=new TextView(context);
		 t2=new TextView(context);
		 t3=new TextView(context);
		 t4=new TextView(context);
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
		t4 = (TextView) view.findViewById(R.id.text7);   
		t1.setTextColor(getResources().getColor(R.color.topcolor));
		t2.setTextColor(getResources().getColor(R.color.silver2));
		t3.setTextColor(getResources().getColor(R.color.silver2));
		t4.setTextColor(getResources().getColor(R.color.silver2));
		t1.setOnClickListener(new MyOnClickListener(0));   
		t2.setOnClickListener(new MyOnClickListener(1));   
		t3.setOnClickListener(new MyOnClickListener(2));   
		t4.setOnClickListener(new MyOnClickListener(3));   
	}  
	

	private void InitViewPager() {   
		mPager = (ViewPager) view.findViewById(R.id.vPager2);   
		
		listViews = new ArrayList<View>();   
		LayoutInflater mInflater = ((Activity) context).getLayoutInflater();   
		listViews.add(mInflater.inflate(R.layout.lay1, null));
		listViews.add(mInflater.inflate(R.layout.lay2, null));
		listViews.add(mInflater.inflate(R.layout.lay3, null));
		listViews.add(mInflater.inflate(R.layout.lay4, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));   
		mPager.setCurrentItem(0);  
		String path = MainActivity.firstMisicPath;
//		SoundRecord tt =new SoundRecord(listViews.get(0).getContext(), null,listViews.get(0));
		RecordPlay tt =new RecordPlay(listViews.get(0));
		dwf =new DrawWaveForm(listViews.get(1).getContext(), null,listViews.get(1),path);
		synthesisMusic sm=new synthesisMusic(listViews.get(2).getContext(),listViews.get(2));
		MusicMix mm=new MusicMix(listViews.get(3).getContext(),listViews.get(3));
		//t2 =new DrawWaveForm(listViews.get(1).getContext(), null,listViews.get(1),"/storage/emulated/0/bbbb.mp3");
		mPager.setOnPageChangeListener(new MyOnPageChangeListener()); 
		
		
		}
	
	private void InitImageView() {   
		cursor = (ImageView) view.findViewById(R.id.cursor); 
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.btnbase)   
		.getWidth();// »ñÈ¡Í¼Æ¬¿í¶È   
		DisplayMetrics dm = new DisplayMetrics();   
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);   
		int screenW = dm.widthPixels;// »ñÈ¡·Ö±æÂÊ¿í¶È   
		LayoutParams para;
        para = cursor.getLayoutParams();
        para.width = screenW / 4-10;
        cursor.setLayoutParams(para);
		offset = (screenW / 4 - bmpW) / 2;// ¼ÆËãÆ«ÒÆÁ¿   
		Matrix matrix = new Matrix();   
		matrix.postTranslate(offset, 0);   
		cursor.setImageMatrix(matrix);// ÉèÖÃ¶¯»­³õÊ¼Î»ÖÃ   
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


			int one = offset * 2 + bmpW;// Ò³¿¨1 -> Ò³¿¨2 Æ«ÒÆÁ¿   
			int two = one * 2;// Ò³¿¨1 -> Ò³¿¨3 Æ«ÒÆÁ¿   
			int three = one * 3;
			
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
				} else if (currIndex == 3) {   
				animation = new TranslateAnimation(three, 0, 0, 0);   
				}   
				break;   
				case 1:   
				if (currIndex == 0) {   
				animation = new TranslateAnimation(offset, one, 0, 0);   
				} else if (currIndex == 2) {   
				animation = new TranslateAnimation(two, one, 0, 0);   
				} else if (currIndex == 3) {   
				animation = new TranslateAnimation(three, one, 0, 0);   
				}  
				break;   
				case 2:   
				if (currIndex == 0) {   
				animation = new TranslateAnimation(offset, two, 0, 0);   
				} else if (currIndex == 1) {   
				animation = new TranslateAnimation(one, two, 0, 0);   
				} else if (currIndex == 3) {   
				animation = new TranslateAnimation(three, two, 0, 0);   
				}  
				break; 
				case 3:   
				if (currIndex == 0) {   
				animation = new TranslateAnimation(offset, three, 0, 0);   
				} else if (currIndex == 1) {   
				animation = new TranslateAnimation(one, three, 0, 0);   
				} else if (currIndex == 2) {   
				animation = new TranslateAnimation(two, three, 0, 0);   
				}  
				break;
				}   
				currIndex = arg0;   
				animation.setFillAfter(true);// True:Í¼Æ¬Í£ÔÚ¶¯»­½áÊøÎ»ÖÃ   
				animation.setDuration(300);   
				cursor.startAnimation(animation);   
				
				switch (currIndex) {
				case 0:
					t1.setTextColor(getResources().getColor(R.color.topcolor));
					t2.setTextColor(getResources().getColor(R.color.silver2));
					t3.setTextColor(getResources().getColor(R.color.silver2));
					t4.setTextColor(getResources().getColor(R.color.silver2));
					break;
				case 1:
					t2.setTextColor(getResources().getColor(R.color.topcolor));
					t1.setTextColor(getResources().getColor(R.color.silver2));
					t3.setTextColor(getResources().getColor(R.color.silver2));
					t4.setTextColor(getResources().getColor(R.color.silver2));
					break;
				case 2:
					t3.setTextColor(getResources().getColor(R.color.topcolor));
					t2.setTextColor(getResources().getColor(R.color.silver2));
					t1.setTextColor(getResources().getColor(R.color.silver2));
					t4.setTextColor(getResources().getColor(R.color.silver2));
					break;
				case 3:
					t4.setTextColor(getResources().getColor(R.color.topcolor));
					t3.setTextColor(getResources().getColor(R.color.silver2));
					t2.setTextColor(getResources().getColor(R.color.silver2));
					t1.setTextColor(getResources().getColor(R.color.silver2));
					break;
				}
			}

		}

		@Override
		protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
			// TODO Auto-generated method stub
			
		}
		class ChooseHandle extends Handler{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Bundle b = msg.getData(); 
				int vis = b.getInt("FileChange");
				if(vis>=0){
					dwf.FileChange(vis);
				}
			}
			
		}
}
	

