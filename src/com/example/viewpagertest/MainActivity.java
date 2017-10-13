package com.example.viewpagertest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.example.Record.BackService;
import com.example.Record.MsgTypeUtil;
import com.example.ViewClass.Loading_view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private ViewPager mPagerMain;//页卡内容
     private List<View> listViewsMain; // Tab页面列表
     private TextView t1Main,t2Main;// 页卡头标
     private MainChooseActivity mca;
     public static String firstMisicPath;
     public static BackService msgServer;
     public static Handler mainHand;
     private ImageView imgUser;
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InitTextView();
		InitViewPager();
		msgServer=new BackService();
		mainHand=new MainHandler();
		imgUser=(ImageView)findViewById(R.id.imgUser);
		
		imgUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
				Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();;
			}else if(msg.what == 2){
				Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 3){
				Toast.makeText(getApplicationContext(), "无法连接服务器", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 4){
				String aa= (String)msg.obj;
				Toast.makeText(getApplicationContext(), aa, Toast.LENGTH_SHORT).show();
			}else if(msg.what == 5){
				Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 6){

			}
			super.handleMessage(msg);
		}
		
	};
	
	public static Activity getGlobleActivity() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException{
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        for(Object activityRecord:activities.values()){
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if(!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
      }
	
	private void InitTextView() {   
		
		t1Main = (TextView) findViewById(R.id.text1);   
		t2Main = (TextView) findViewById(R.id.text2);
		
		t1Main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPagerMain.setCurrentItem(0);  
			}
		});   
		t2Main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPagerMain.setCurrentItem(1);  
			}
		});   
		
	}  
	
	private void InitViewPager() {   
		mPagerMain = (ViewPager) findViewById(R.id.vPager1);  
		
		listViewsMain = new ArrayList<View>();   
		LayoutInflater mInflaterMain = getLayoutInflater();   
		
		listViewsMain.add(mInflaterMain.inflate(R.layout.main_choose, null));  
		listViewsMain.add(mInflaterMain.inflate(R.layout.main_messages, null));  
		mPagerMain.setAdapter(new MyPagerAdapter(listViewsMain));   
		mPagerMain.setCurrentItem(0);   
		mca=new MainChooseActivity(listViewsMain.get(0).getContext(), null,listViewsMain.get(0));
		SocialMessage sm =new SocialMessage(listViewsMain.get(1).getContext(),listViewsMain.get(1));
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(); 
		     intent.setClass(MainActivity.this, SearchMainActivity.class);
		     //MainActivity.this.startActivity(intent);
		     intent.putExtra("from", "Main");
	            startActivity(intent);  
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
	


