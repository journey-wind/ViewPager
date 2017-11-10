package com.example.viewpagertest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.Record.AudioUtil;
import com.example.ViewClass.ClearEditText;
import com.example.ViewClass.CommomDialog;
import com.example.ViewClass.RecordView;
import com.example.viewpagertest.MediaFile.MediaFileType;
import com.example.viewpagertest.R.layout;
import com.example.viewpagertest.SearchListViewAdapter.Callback;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ZoomControls;

public class SearchMainActivity extends Activity implements Callback , OnItemClickListener{

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		sfl.setRefreshing(true);
		new Handler().postDelayed(new Runnable() {  
            @Override  
            public void run() {  
           	 	frequencyList = SeekFrequency();
           	 	historyAdapter =new SearchListViewAdapter(SearchMainActivity.this, frequencyList , SearchMainActivity.this); 
                   listView.setAdapter(historyAdapter);
                   listView.setOnItemClickListener(SearchMainActivity.this);
                   sfl.setRefreshing(false);
                   editSearch.setFocusable(true);
           		editSearch.setFocusableInTouchMode(true);
           		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);    
		        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0) ;
            }  
        }, 1200);  
	}
	public static Handler hand;
	//private TimerCal timerT;
	private Thread MyThread;
	private boolean isPressImage;
	private boolean isLoopText;
	private String fileName;
	
	private TextView RecordTime;
	private ListView listView;
	private ClearEditText editSearch;
	public static ArrayList<String> frequencyList;
	private SearchListViewAdapter historyAdapter;
	private RelativeLayout historyLayout;
//	private LinearLayout resultLayout;
	private ImageView imageExit;
	private RecordView soundAdd;
	private RelativeLayout soundAddLayout;
	private String fromStr;
	private int sumSecond;
	private SwipeRefreshLayout sfl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_main);
		isPressImage=false;
		isLoopText=false;
		
		Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
        	fromStr=bundle.getString("from");
        }
		
		hand=new MyHandler();
		AudioUtil.getInstance();
		//RecordTime = (TextView)findViewById(R.id.SearchTime);
		//soundAdd=(ImageView)findViewById(R.id.soundRecord);
		soundAdd = (RecordView) findViewById(R.id.recordView);
		soundAddLayout = (RelativeLayout) findViewById(R.id.RecordLayout);
		imageExit=(ImageView)findViewById(R.id.searchImageExit);
		historyLayout=(RelativeLayout)findViewById(R.id.historyLayout);
//		resultLayout=(LinearLayout)findViewById(R.id.resultLayout);
		editSearch=(ClearEditText)findViewById(R.id.Editsearch);
		editSearch.setFocusable(false);
		editSearch.setFocusableInTouchMode(false);
		 
		sfl=(SwipeRefreshLayout)findViewById(R.id.srlSearch);
//		sfl.setOnRefreshListener(new OnRefreshListener() {
//			
//			@Override
//			public void onRefresh() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
   	 	listView = (ListView)findViewById(R.id.listSearch);
//		editSearch.setOnEditorActionListener(new returnKeyDeal());\
	   	 if(frequencyList!=null&&frequencyList.size()>0){
	   		historyAdapter =new SearchListViewAdapter(SearchMainActivity.this, frequencyList , SearchMainActivity.this); 
            listView.setAdapter(historyAdapter);
            listView.setOnItemClickListener(SearchMainActivity.this);
		 }else{
			//sfl.measure(0,0);
		 }
	   	
		soundAdd.setOnTouchListener(new AddSoundClick());
        editSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if(historyAdapter!=null){
					if(arg0.length()>0){
						historyAdapter.queryData(arg0.toString().trim());
					}else{
						historyAdapter.queryData("");
					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        imageExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        sfl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	editSearch.setText("");
                    	frequencyList=null;
                    	frequencyList = SeekFrequency();
                    	historyAdapter.setDataList(frequencyList);
                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        sfl.setRefreshing(false);
                    }
                }, 1200);

            }
        });
        
	}
	
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			Bundle b = msg.getData(); 
			int vis =b.getInt("viewControl");
			switch (vis) {
			case 0:
				//soundAdd.setVisibility(View.VISIBLE);
				soundAddLayout.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);    
		        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0) ;  
				break;
			case 1:
				//soundAdd.setVisibility(View.GONE);
				soundAddLayout.setVisibility(View.GONE);
				break;
			case 2:
				//RecordTime.setText(String.format("%02d：%02d", (Integer)(sumSecond/60),(Integer)(sumSecond%60)));
				break;
			default:
				break;
			}
            
		}
		public MyHandler(){
			
		}

		
	};
	class AddSoundClick implements OnTouchListener{

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			
			switch (arg0.getId()) {
			case R.id.recordView:
					switch (arg1.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//timerT = new TimerCal();
						//MyThread = new Thread(timerT);
						//MyThread.start();
						//RecordTime.setVisibility(View.VISIBLE);
						isPressImage=true;
						isLoopText=true;
						AudioUtil.mInstance.startRecord();
						break;
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						//RecordTime.setVisibility(View.GONE);
						//soundAdd.setVisibility(View.GONE);
						soundAddLayout.setVisibility(View.GONE);
						isLoopText=false;
						isPressImage =false;
						//RecordTime.setText("00：00");
						showInputDialog();
						AudioUtil.mInstance.stopRecord();
						
						break;
						
					default:
						break;
					}
				break;

			default:
				break;
			}
//			if(isPressImage){
//				if (arg1.getAction()==MotionEvent.ACTION_UP) {
//					RecordTime.setVisibility(View.GONE);
//					isLoopText=false;
//					isPressImage=false;
//					RecordTime.setText("00：00");
//				}
//			}
			return false;
		}
	}
	
//	class TimerCal implements Runnable {
//		
//		private int startHour;
//		private int startMinute;
//		private int startSecond;
//		private Message msg;
//		private Bundle b;
//		private Time t;
//		public TimerCal(){
//			t=new Time();
//			 // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
//			t.setToNow(); // 取得系统时间。  
//			startHour=t.hour;
//			startMinute=t.minute;
//			startSecond=t.second;
//		}
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			while(isLoopText){
//				
//				t.setToNow(); // 取得系统时间。  
//				int hour = t.hour; // 0-23  
//				int minute = t.minute;  
//				int second = t.second;
//				sumSecond = (hour-startHour)*3600+(minute-startMinute)*60+(second-startSecond);
//				msg = new Message(); 
//		        b = new Bundle();
//		        b.putInt("viewControl", 2);
//		        msg.setData(b);
//		        hand.sendMessage(msg);
//		        try {
//					Thread.sleep(300);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		
//	}
	
	private void showInputDialog() {
	    /*@setView 装入一个EditView
	     */
		fileName ="test";
	    final EditText editText = new EditText(SearchMainActivity.this);
	    editText.setHint("请输入文件名字");
	    AlertDialog.Builder inputDialog = 
	        new AlertDialog.Builder(SearchMainActivity.this);
	    inputDialog.setView(editText);
	    inputDialog.setPositiveButton("确定", 
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	fileName=editText.getText().toString();
	        	AudioUtil.mInstance.SetFileName(fileName);
	        	AudioUtil.mInstance.convertWaveFile();
	            Toast.makeText(SearchMainActivity.this,
	            editText.getText().toString(), 
	            Toast.LENGTH_SHORT).show();
	            frequencyList.add(AudioUtil.mInstance.GetFileName());
	            SendToChoose(frequencyList.size()-1);
	            SearchMainActivity.this.finish();
	        }
	    });
	    inputDialog.setNegativeButton("取消",null).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean isGone;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
	        View v = soundAdd;  
	        //判断是否碰触在view上
	        if(soundAddLayout.getVisibility()!=View.GONE){
	        	if (isShouldHideInput(v, ev)) { 
		            //soundAdd.setVisibility(View.GONE);
		            soundAddLayout.setVisibility(View.GONE);
		            return true;
		        }  
	        	
	        }
	        return super.dispatchTouchEvent(ev);
	        //return true;
	    }  
	    // 必不可少，否则所有的组件都不会有TouchEvent了  
	    if (getWindow().superDispatchTouchEvent(ev)) {  
	          
	    }  
	    //return true;
	    return onTouchEvent(ev);  
//		return super.dispatchTouchEvent(ev);
	}
	
	public  boolean isShouldHideInput(View v, MotionEvent event) {  
	    if (v != null && (v instanceof RecordView)) {  
	        int[] leftTop = { 0, 0 };  
	        //获取输入框当前的location位置  
	        v.getLocationInWindow(leftTop);  
	        int left = leftTop[0];  
	        int top = leftTop[1];  
	        int bottom = top + v.getHeight();  
	        int right = left + v.getWidth();  
	        if (event.getX() > left && event.getX() < right  
	                && event.getY() > top && event.getY() < bottom) {  
	            // 点击的是输入框区域，保留点击EditText的事件  
	            return false;  
	        } else {  
	            return true;  
	        }  
	    }  
	    return false;  
	}  
	
//	public class returnKeyDeal implements OnEditorActionListener{
//
//		@Override
//		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//			// TODO Auto-generated method stub
//			if (event.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
//                // do something
//				historyLayout.setVisibility(View.GONE);
//				resultLayout.setVisibility(View.VISIBLE);
//            }
//			return false;
//		}
//
//	}
//	
	 public ArrayList<String> SeekFrequency (){
		 ArrayList<String> fileList = new ArrayList<String>();  
		 ArrayList<String> InnerList = new ArrayList<String>();  
		 ArrayList<String> ExtList = new ArrayList<String>();  
		 
		 String InnerSd =getInnerSDCardPath();
		 InnerList = getAllFile(InnerSd);
	     
	     List<String> ExtSd =getExtSDCardPath();
	     if(!(ExtSd.size()==0 || ExtSd.get(0).equals(""))){
	    	 ExtList = getAllFile(ExtSd.get(0));
	     }
	     fileList.addAll(InnerList);
	     fileList.addAll(ExtList);
	     
	     return fileList;  
	 }

	 private  ArrayList<String> getAllFile(String path){  
	   ArrayList<String> fileList = new ArrayList<String>();  
       File folder = new File(path);  
       addFile(fileList, folder);  
       return fileList;  
   }  
      
	 private  void addFile(List<String> fileList,File f){  
       if(f.isDirectory()){  
           File[] files = f.listFiles();  
           for(File f1 : files){  
               addFile(fileList, f1);  
           }  
       }else{  
           
       	MediaFileType MimeType = MediaFile.getFileType(f.getAbsolutePath());
       	if(MimeType != null){
	            if(MediaFile.isMP3FileType(MimeType.fileType)){
	            	fileList.add(f.getAbsolutePath());  
	            }
       	}
         
       }  
   }
	
	 /** 
	     * 获取内置SD卡路径 
	     * @return 
	     */  
	    public String getInnerSDCardPath() {    
	        return Environment.getExternalStorageDirectory().getPath();    
	    }  
	  
	    /** 
	     * 获取外置SD卡路径 
	     * @return  应该就一条记录或空 
	     */  
	    public List<String> getExtSDCardPath()  
	    {  
	        List<String> lResult = new ArrayList<String>();  
	        try {  
	            Runtime rt = Runtime.getRuntime();  
	            Process proc = rt.exec("mount");  
	            InputStream is = proc.getInputStream();  
	            InputStreamReader isr = new InputStreamReader(is);  
	            BufferedReader br = new BufferedReader(isr);  
	            String line;  
	            while ((line = br.readLine()) != null) {  
	                if (line.contains("extSdCard"))  
	                {  
	                    String [] arr = line.split(" ");  
	                    String path = arr[1];  
	                    File file = new File(path);  
	                    if (file.isDirectory())  
	                    {  
	                        lResult.add(path);  
	                    }  
	                }  
	            }  
	            isr.close();  
	        } catch (Exception e) {  
	        }  
	        return lResult;  
	    }  
	    public void SqlMedio(){
	    	List<String> fileName = new ArrayList<String>();
			ContentResolver cr = getContentResolver();  
			Cursor cursor = cr.query(  
	                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,  
	                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if (cursor.moveToFirst()) {  
	            do {  
	                String title = cursor.getString(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.TITLE));  
	                String singer = cursor.getString(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.ARTIST));  
	                String album = cursor.getString(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.ALBUM));  
	                long size = cursor.getLong(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.SIZE));  
	                long time = cursor.getLong(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.DURATION));  
	                String url = cursor.getString(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.DATA));  
	                int _id = cursor.getInt(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media._ID));  
	                String name = cursor.getString(cursor  
	                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));  
	                String sbr = name.substring(name.length() - 3,  
	                        name.length());  
	                fileName.add(url);
	            } while (cursor.moveToNext());  
			}
	    	
	    }
	 
	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		final int index=Integer.parseInt(v.getTag().toString());
		if(soundAddLayout.getVisibility()==View.GONE|| sfl.isRefreshing()){
			new CommomDialog(SearchMainActivity.this, R.style.dialog, "确定删除该文件？", new CommomDialog.OnCloseListener() {
				@Override
				public void onClick(Dialog dialog, boolean confirm) {
					// TODO Auto-generated method stub
					if(confirm){
						File ff =new File(frequencyList.get(index));
						if(ff.exists()){
							ff.delete();
						}
						frequencyList.remove(index);
						historyAdapter.notifyDataSetChanged();
					}
			        
				}
			}).show();
			
		}
		
	}
	private void SendToChoose(int index){
		
        if(fromStr.equals("Main")){
        	Message msg = new Message(); 
            Bundle b = new Bundle();
            b.putInt("FileChange", index);
            msg.setData(b);
        	MainChooseActivity.changeFile.sendMessage(msg);
        }else if(fromStr.equals("AddMsg")){
        	Message msg = new Message(); 
            Bundle b = new Bundle();
            b.putInt("FileChange", index);
            msg.setData(b);
        	AddMsgActivity.selectHand.sendMessage(msg);
        }else if(fromStr.equals("Synthesis")){
        	Message msg = synthesisMusic.myHander.obtainMessage();
        	msg.obj=index;
			msg.what = 1;
			synthesisMusic.myHander.sendMessage(msg);// 结果返回
        }else if(fromStr.equals("MixOne")){
        	Message msg = MainActivity.mainHand.obtainMessage();
        	msg.obj=index;
			msg.what = 1;
			MusicMix.myHander.sendMessage(msg);// 结果返回
        }else if(fromStr.equals("MixTwo")){
        	Message msg = MusicMix.myHander.obtainMessage();
        	msg.obj=index;
			msg.what = 2;
			MusicMix.myHander.sendMessage(msg);// 结果返回
        }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(soundAddLayout.getVisibility()==View.GONE || sfl.isRefreshing()){
			MainActivity.firstMisicPath=frequencyList.get(arg2);
			arg1.setBackgroundColor(getResources().getColor(R.color.silver));
			SendToChoose(arg2);
			this.finish();
		}
	}
}
