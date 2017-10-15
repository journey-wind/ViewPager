package com.example.viewpagertest;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.Record.*;
import com.example.viewpagertest.SearchListViewAdapter.Callback;

public class MsgListViewAdapter extends BaseAdapter implements OnClickListener{



	static class ViewHolder
	{
		public ImageView iv_play;
	    public ImageView iv_head;
	    public TextView tv_name;
	    public TextView tv_time;
	    public TextView tv_content;
	    public TextView tv_LisenNum;
	    public TextView tv_likeNum;
	    public SeekBar sb_music;
	    public String musicPath; 
	}
	private ArrayList<MsgTypeUtil> data;
	private LayoutInflater mInflater=null;
	private Callback mCallback;
	
	public interface Callback {
        public void click(View v);
     }
	
	public MsgListViewAdapter(ArrayList<MsgTypeUtil> data, Context context,Callback callback){
		this.data=data;
		this.mInflater=LayoutInflater.from(context);
		mCallback = callback;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	public void RefreshAndSave(){
		Editor editor = MainActivity.sharedPreferences.edit();//获取编辑器
		for(int i=0;i<data.size()&&i<5;i++){
			switch(i){
			case 0:
				editor.putString("LastMessageOne", data.get(i).toString());
				break;
			case 1:
				editor.putString("LastMessageTwo", data.get(i).toString());
				break;
			case 2:
				editor.putString("LastMessageThree", data.get(i).toString());
				break;
			case 3:
				editor.putString("LastMessageFour", data.get(i).toString());
				break;
			case 4:
				editor.putString("LastMessageFive", data.get(i).toString());
				break;
			case 5:
				editor.putString("LastMessageSix", data.get(i).toString());
				break;
			case 6:
				editor.putString("LastMessageSeven", data.get(i).toString());
				break;
			default:
				break;
			}
		}
		editor.commit();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		 ViewHolder holder;
	    if(arg1 == null)
	    {
	        holder = new ViewHolder();
	        arg1 = mInflater.inflate(R.layout.message_list, null);
	        //holder.iv_head = (ImageView)arg1.findViewById(R.id.iv_head);
	        holder.iv_play = (ImageView)arg1.findViewById(R.id.iv_play);
	        holder.sb_music = (SeekBar)arg1.findViewById(R.id.sb_music);
	        holder.tv_content = (TextView)arg1.findViewById(R.id.tv_content);
	        holder.tv_likeNum = (TextView)arg1.findViewById(R.id.tv_likeNum);
	        holder.tv_LisenNum = (TextView)arg1.findViewById(R.id.tv_LisenNum);
	        holder.tv_name = (TextView)arg1.findViewById(R.id.tv_name);
	        holder.tv_time = (TextView)arg1.findViewById(R.id.tv_time);
	        holder.iv_play.setOnClickListener(this);
	        holder.iv_play.setTag(holder);
//	        		new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					if(holder.musicPath!=null || holder.musicPath!=""){
//						MediaPlayerUtil.getInstance(holder.sb_music);
//						MediaPlayerUtil.mInstance.Prepared(holder.musicPath);
//					}
//				}
//			});
	        arg1.setTag(holder);
	    }else
	    {
	        holder = (ViewHolder)arg1.getTag();
	    }
	    //holder.iv_head.setVisibility(View.GONE);
	    //holder.sb_music.setVisibility(View.GONE);
	    String timeStr=compareToNowDate(data.get(arg0).time);
	   
	    holder.tv_name.setText(data.get(arg0).name);
	    holder.tv_time.setText(timeStr);
	    holder.tv_content.setText(data.get(arg0).context);
	    holder.tv_likeNum.setText(data.get(arg0).likeNum);
	    holder.musicPath=data.get(arg0).musicPath;
		
		return arg1;
	}
	
	
	@SuppressWarnings("deprecation")
	private String compareToNowDate(String date){  
		
        Date nowDate,passDate;
        long diff=-100l;
        String time_str="";
        int year,month,day;

        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");  
        String nowStr=formatter.format(new Date());  
        try {
			passDate = formatter.parse(date);
			nowDate = formatter.parse(nowStr);
			
			day=nowDate.getDate()-passDate.getDate();
			month=nowDate.getMonth()-passDate.getMonth();
			year=nowDate.getYear()-passDate.getYear();
			if(year!=0){
				if(month>6){
					year++;
				}
				time_str=year+"年前";
			}else if(month!=0){
				time_str=month+"个月前";
			}else if(day!=0){
				switch(day){
				case 1:
					time_str="昨天";
					break;
				case 2:
					time_str="前天";
					break;
				default:
					time_str=day+"天前";
					break;
				}
			}else{
				diff = passDate.getTime() - nowDate.getTime();     
				time_str=getStringForTime(diff);
				if(time_str==""){
					time_str=passDate.getHours()+":"+
							passDate.getMinutes();
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}  
        
        return time_str;
    }

	private String getStringForTime(long timeDif) {
		// TODO Auto-generated method stub
		long year,month,day,hour,minute,second;
		second=(timeDif/1000)%60;
		minute=timeDif/(1000 * 60);
		hour=timeDif/(1000 * 60 * 60);
		if(hour!=0 || minute!=0){
			return "";
		}
		return second+"秒前";
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		mCallback.click(arg0);
	}  
      
}
