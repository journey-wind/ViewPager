package com.example.viewpagertest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.Record.ClsOscilloscope;
import com.example.ViewClass.ClearEditText;
import com.example.viewpagertest.MediaFile.MediaFileType;
import com.example.viewpagertest.R.layout;
import com.example.viewpagertest.SearchListViewAdapter.Callback;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.Editable;
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

public class SearchMainActivity extends Activity implements Callback {

	public static Handler hand;
	private TimerCal timerT;
	private Thread MyThread;
	private boolean isPressImage;
	private boolean isLoopText;
	private String fileName;
	
	private TextView RecordTime;
	private ListView listView;
	private ClearEditText editSearch;
	private ArrayList<String> frequencyList;
	private SearchListViewAdapter historyAdapter;
	private RelativeLayout historyLayout;
//	private LinearLayout resultLayout;
	private ImageView imageExit;
	private ImageView soundAdd;
	
	private int sumSecond;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_main);
		isPressImage=false;
		isLoopText=false;
		hand=new MyHandler();
		
		RecordTime = (TextView)findViewById(R.id.SearchTime);
		soundAdd=(ImageView)findViewById(R.id.soundRecord);
		imageExit=(ImageView)findViewById(R.id.searchImageExit);
		historyLayout=(RelativeLayout)findViewById(R.id.historyLayout);
//		resultLayout=(LinearLayout)findViewById(R.id.resultLayout);
		editSearch=(ClearEditText)findViewById(R.id.Editsearch);
//		editSearch.setOnEditorActionListener(new returnKeyDeal());
		frequencyList = SeekFrequency();
		listView = (ListView)findViewById(R.id.listSearch);

		soundAdd.setOnTouchListener(new AddSoundClick());
		
        historyAdapter =new SearchListViewAdapter(this, frequencyList , this); 
        listView.setAdapter(historyAdapter);
        
        imageExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
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
				soundAdd.setVisibility(View.VISIBLE);
				break;
			case 1:
				soundAdd.setVisibility(View.GONE);
				break;
			case 2:
				RecordTime.setText(String.format("%02d��%02d", (Integer)(sumSecond/60),(Integer)(sumSecond%60)));
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
			case R.id.soundRecord:
					switch (arg1.getAction()) {
					case MotionEvent.ACTION_DOWN:
						timerT = new TimerCal();
						MyThread = new Thread(timerT);
						MyThread.start();
						RecordTime.setVisibility(View.VISIBLE);
						isPressImage=true;
						isLoopText=true;
						break;
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						RecordTime.setVisibility(View.GONE);
						isLoopText=false;
						isPressImage =false;
						RecordTime.setText("00��00");
						showInputDialog();
						break;
						
					default:
						break;
					}
				break;

			default:
				break;
			}
			if(isPressImage){
				if (arg1.getAction()==MotionEvent.ACTION_UP) {
					RecordTime.setVisibility(View.GONE);
					isLoopText=false;
					isPressImage=false;
					RecordTime.setText("00��00");
				}
			}
			return true;
		}
	}
	
	class TimerCal implements Runnable {
		
		private int startHour;
		private int startMinute;
		private int startSecond;
		private Message msg;
		private Bundle b;
		private Time t;
		public TimerCal(){
			t=new Time();
			 // or Time t=new Time("GMT+8"); ����Time Zone���ϡ�  
			t.setToNow(); // ȡ��ϵͳʱ�䡣  
			startHour=t.hour;
			startMinute=t.minute;
			startSecond=t.second;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isLoopText){
				
				t.setToNow(); // ȡ��ϵͳʱ�䡣  
				int hour = t.hour; // 0-23  
				int minute = t.minute;  
				int second = t.second;
				sumSecond = (hour-startHour)*3600+(minute-startMinute)*60+(second-startSecond);
				msg = new Message(); 
		        b = new Bundle();
		        b.putInt("viewControl", 2);
		        msg.setData(b);
		        hand.sendMessage(msg);
		        try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void showInputDialog() {
	    /*@setView װ��һ��EditView
	     */
		fileName ="��˹������";
	    final EditText editText = new EditText(SearchMainActivity.this);
	    editText.setText(fileName);
	    AlertDialog.Builder inputDialog = 
	        new AlertDialog.Builder(SearchMainActivity.this);
	    inputDialog.setTitle("�������ļ�������").setView(editText);
	    inputDialog.setPositiveButton("ȷ��", 
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            Toast.makeText(SearchMainActivity.this,
	            editText.getText().toString(), 
	            Toast.LENGTH_SHORT).show();
	        }
	    }).show();
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
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
	        View v = soundAdd;  
	        if (isShouldHideInput(v, ev)) {  
	  
	            soundAdd.setVisibility(View.GONE);
	        }  
	        return super.dispatchTouchEvent(ev);  
	    }  
	    // �ز����٣��������е������������TouchEvent��  
	    if (getWindow().superDispatchTouchEvent(ev)) {  
	          
	    }  
	    //return true;
	    return onTouchEvent(ev);  
//		return super.dispatchTouchEvent(ev);
	}
	
	public  boolean isShouldHideInput(View v, MotionEvent event) {  
	    if (v != null && (v instanceof ImageView)) {  
	        int[] leftTop = { 0, 0 };  
	        //��ȡ�����ǰ��locationλ��  
	        v.getLocationInWindow(leftTop);  
	        int left = leftTop[0];  
	        int top = leftTop[1];  
	        int bottom = top + v.getHeight();  
	        int right = left + v.getWidth();  
	        if (event.getX() > left && event.getX() < right  
	                && event.getY() > top && event.getY() < bottom) {  
	            // ���������������򣬱������EditText���¼�  
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
	            if(MediaFile.isAudioFileType(MimeType.fileType)){
	            	fileList.add(f.getAbsolutePath());  
	            }
       	}
         
       }  
   }
	
	 /** 
	     * ��ȡ����SD��·�� 
	     * @return 
	     */  
	    public String getInnerSDCardPath() {    
	        return Environment.getExternalStorageDirectory().getPath();    
	    }  
	  
	    /** 
	     * ��ȡ����SD��·�� 
	     * @return  Ӧ�þ�һ����¼��� 
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
		frequencyList.remove(Integer.parseInt(v.getTag().toString()));
		historyAdapter.notifyDataSetChanged();
	}
}