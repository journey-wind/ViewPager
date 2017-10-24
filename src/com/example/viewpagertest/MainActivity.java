package com.example.viewpagertest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.example.Record.BackService;
import com.example.Record.MsgTypeUtil;
import com.example.ViewClass.Loading_view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(imageMove&&SocialMessage.msgAdd!=null){
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				
				break;
			case MotionEvent.ACTION_MOVE:
				
				break;
	
			default:
				break;
			}
		}
//		return super.onTouchEvent(event);
		return true;
	}

	public static SharedPreferences sharedPreferences;
	private ViewPager mPagerMain;//页卡内容
     private List<View> listViewsMain; // Tab页面列表
     private TextView t1Main,t2Main;// 页卡头标
     private MainChooseActivity mca;
     public static String firstMisicPath;
     public static BackService msgServer;
     public static Handler mainHand;
     private ImageView imgUser;
	private DrawerLayout mDrawerLayout;
	private TextView textName;
	private EditText editName;
	private Button btnSave;
	private Button btnModify;
	public static String personName;
     public static boolean isFirst=true;
     public boolean imageMove;
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("myshare",Activity.MODE_PRIVATE);
        personName = sharedPreferences.getString("personName", "");
        imageMove=false;
        InitTextView();
		InitViewPager();
		msgServer=new BackService();
		mainHand=new MainHandler();
		imgUser=(ImageView)findViewById(R.id.imgUser);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.dl_left);
		textName=(TextView)findViewById(R.id.textName); 
		textName.setText(personName);
		editName=(EditText)findViewById(R.id.editName); 
		editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		btnSave=(Button)findViewById(R.id.btnSave); 
		btnSave.setEnabled(false);
		btnModify=(Button)findViewById(R.id.btnModify); 
		btnModify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btnSave.setEnabled(true);
				btnModify.setEnabled(false);
				String name = textName.getText().toString();
				textName.setVisibility(View.GONE);
				editName.setText(name);
				editName.setVisibility(View.VISIBLE);
			}
		});
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btnModify.setEnabled(true);
				btnSave.setEnabled(false);
				String name = editName.getText().toString();
				if(name.equals("")){
					Toast.makeText(getApplicationContext(), "用户名不许为空", Toast.LENGTH_SHORT).show();
					return;
				}
				Editor editor =sharedPreferences.edit();//获取编辑器
				
				editor.putString("personName", name);
				editor.commit();
				editName.setVisibility(View.GONE);
				textName.setText(name);
				textName.setVisibility(View.VISIBLE);
			}
		});
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			
			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				editName.setVisibility(View.GONE);
				textName.setVisibility(View.VISIBLE);
				btnModify.setEnabled(true);
				btnSave.setEnabled(false);
			}
			
			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				editName.setVisibility(View.GONE);
				textName.setVisibility(View.VISIBLE);
				btnModify.setEnabled(true);
				btnSave.setEnabled(false);
			}
		});
		imgUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDrawerLayout.openDrawer(Gravity.START);
			}
		});
	}

	

	class MainHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {
				Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 2){
				Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 3){
				if(SocialMessage.swipeRefreshView!=null){
					SocialMessage.swipeRefreshView.setRefreshing(false);
				}
				Toast.makeText(getApplicationContext(), "无法连接服务器", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 4){
				String aa= (String)msg.obj;
				Toast.makeText(getApplicationContext(), aa, Toast.LENGTH_SHORT).show();
			}else if(msg.what == 5){
				Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 6){
				int i = (Integer)msg.obj;
//				Toast.makeText(getApplicationContext(), "共更新了"+i+"条信息", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 7){
				Intent intent = new Intent(); 
			     intent.setClass(MainActivity.this, SearchMainActivity.class);
			     //MainActivity.this.startActivity(intent);
			     intent.putExtra("from", "Main");
		         startActivity(intent);  
			}else if(msg.what == 8){
				Toast.makeText(getApplicationContext(), "切割失败", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 9){
				Intent intent = new Intent(); 
			     intent.setClass(MainActivity.this, SearchMainActivity.class);
			     //MainActivity.this.startActivity(intent);
			     intent.putExtra("from", "Synthesis");
		         startActivity(intent);  
			}else if(msg.what == 10){
				Toast.makeText(getApplicationContext(), "选择的文件类型不一样", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 11){
				String aa= (String)msg.obj;
				Toast.makeText(getApplicationContext(), "拼接成功,文件地址："+aa, Toast.LENGTH_SHORT).show();
			}else if(msg.what == 12){
				Toast.makeText(getApplicationContext(), "拼接失败", Toast.LENGTH_SHORT).show();
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
//				if(isFirst){
//					String temp1 = MainActivity.sharedPreferences.getString("LastMessageOne", "");
//	    			String temp2 = MainActivity.sharedPreferences.getString("LastMessageTwo", "");
//	    			String temp3 = MainActivity.sharedPreferences.getString("LastMessageThree", "");
//	    			String temp4 = MainActivity.sharedPreferences.getString("LastMessageFour", "");
//	    			String temp5 = MainActivity.sharedPreferences.getString("LastMessageFive", "");
//	    			String temp6 = MainActivity.sharedPreferences.getString("LastMessageSix", "");
//	    			String temp7 = MainActivity.sharedPreferences.getString("LastMessageSeven", "");
//	    			String sumTemp=temp1+temp2+temp3+temp4+temp5+temp6+temp7+"end";
//	    			if(sumTemp!=""){
//	    				String[] temp=sumTemp.split("<<3>>");
//	    				Message msg1 = SocialMessage.socialHandl.obtainMessage();
//	    				msg1.obj = temp;
//	    				msg1.what = 2;
//	    				SocialMessage.socialHandl.sendMessage(msg1);// 结果返回
//	    			}
//	    			if(sumTemp.equals("end")){
//	    					SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
//	    					String date= df.format(new Date());  
//	    					String stt ="GetNewMessage:"+date;
//	    					MainActivity.msgServer.sendMsg(stt);
//	    			}
//	    			isFirst=false;
//            	}
				
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
		mPagerMain.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0==1){
					imageMove=true;
					Message msg1 = SocialMessage.socialHandl.obtainMessage();
					msg1.obj = null;
					msg1.what = 3;
					SocialMessage.socialHandl.sendMessage(msg1);// 结果返回
            	}else{
            		imageMove=false;
            	}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
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
			Message msg = mainHand.obtainMessage();
			msg.obj = null;
			msg.what = 7;
			mainHand.sendMessage(msg);// 结果返回
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
	


