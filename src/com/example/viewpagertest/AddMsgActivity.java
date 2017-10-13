package com.example.viewpagertest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.Record.MsgTypeUtil;
import com.example.Record.SocThread;
import com.example.Record.SocThread.ReceiveType;
import com.example.ViewClass.Loading_view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddMsgActivity extends Activity {

	private TextView sendText;
	private TextView musicText;
	private TextView wordNumText;
	private EditText messageEdit;
	private ImageView exitImage;
	private LinearLayout selectLayout;
	public static String selectMusic;
	public static Handler selectHand;
	//private ProgressDialog pd;
	Loading_view loading;
	TextView loadTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmsg_layout);
		
		selectMusic="";
		musicText=(TextView)findViewById(R.id.textMusic);
		sendText=(TextView)findViewById(R.id.textSend);
		wordNumText=(TextView)findViewById(R.id.textWoridNum);
		messageEdit=(EditText)findViewById(R.id.editMessage);
		exitImage=(ImageView)findViewById(R.id.imageExit);
		selectLayout=(LinearLayout)findViewById(R.id.layoutselect);

		//SocThread.mInstance.conn();
		selectHand=new AddMsgHandler();
		messageEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
		initClick();
	}

	private void initClick() {
		// TODO Auto-generated method stub
		sendText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading = new Loading_view(AddMsgActivity.this,R.style.CustomDialog);
			    loading.show();
			    loadTv=loading.GetLoadText();
				 
				DateFormat df = new SimpleDateFormat("yyMMddHHmmss");  
		        String date= df.format(new Date());  
				String str = date +"/user/"+messageEdit.getText().toString()+"/";
				String path="";
				if(!(selectMusic==null || selectMusic.equals(""))){
					 String type=selectMusic.substring(selectMusic.lastIndexOf("."));
					 path = date+type;
				}
				str=str+path+"/<<1>>";
				//stu.SendMessage(str, path);
				//SocialMessage.socketTh.Send(str,ReceiveType.PushMessage);
				
				
				if(!MainActivity.msgServer.sendMsg(str)){
					
					Message msg = MainActivity.mainHand.obtainMessage();
					msg.obj = null;
					msg.what = 1;
					MainActivity.mainHand.sendMessage(msg);// 结果返回
					finish();
				}
				
			}
		});
		selectLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();  
	            intent.setClass(AddMsgActivity.this, SearchMainActivity.class);//从一个activity跳转到另一个activity  
	            intent.putExtra("from", "AddMsg");//给intent添加额外数据，key为“str”,key值为"Intent Demo"  
	            startActivity(intent);  
			}
		});
		exitImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		messageEdit.addTextChangedListener(new TextWatcher() {
			CharSequence temp;
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				temp=arg0;
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				wordNumText.setText(temp.length()+"/140");
				
			}
		});
	}

	class AddMsgHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bundle b = msg.getData(); 
			int vis = b.getInt("FileChange");
			if(vis>=0){
				selectMusic=SearchMainActivity.frequencyList.get(vis);
				SongMetadataReader metadataReader = new SongMetadataReader(AddMsgActivity.this, selectMusic);
				musicText.setText("音频："+metadataReader.mTitle); 
			}else if(vis==-101){
			    loading.dismiss();
				Message msgg = MainActivity.mainHand.obtainMessage();
				msgg.obj = null;
				msgg.what = 2;
				MainActivity.mainHand.sendMessage(msgg);// 结果返回
				finish();
			}else if(vis==-101){
				loading.dismiss();
				Toast.makeText(getApplicationContext(), "文件发送失败", Toast.LENGTH_SHORT).show();
			}
			super.handleMessage(msg);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_msg, menu);
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
}
