package com.example.viewpagertest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.example.Record.CaoZuoMp3Utils;
import com.example.Record.CaoZuoWavUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SynthesisListViewAdapter extends BaseAdapter implements OnClickListener{

	static class ViewHolder
	{
	    public ImageButton imgbtn_delete;
	    public TextView tv_name;
	    public TextView tv_type;
	    public TextView tv_wavDetail;
	}
	private Callback mCallback;
	private ArrayList<String> data;
	private LayoutInflater mInflater;
	
	public SynthesisListViewAdapter(ArrayList<String> data, Context context,Callback callback){
		this.data=data;
		this.mInflater=LayoutInflater.from(context);
		mCallback = callback;
	}
	
	public interface Callback {
        public void click(View v);
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

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
	    if(arg1 == null)
	    {
	        holder = new ViewHolder();
	        arg1 = mInflater.inflate(R.layout.item_synthesis_music, null);
	        holder.imgbtn_delete = (ImageButton)arg1.findViewById(R.id.imgbtn_delete);
	        holder.tv_name = (TextView)arg1.findViewById(R.id.tv_synthesisName);
	        holder.tv_wavDetail = (TextView)arg1.findViewById(R.id.tv_wavDetail);
	        holder.tv_type = (TextView)arg1.findViewById(R.id.tv_synthesistype);
	        holder.imgbtn_delete.setOnClickListener(this);
	        holder.imgbtn_delete.setTag(arg0);
	        arg1.setTag(holder);
	    }else
	    {
	        holder = (ViewHolder)arg1.getTag();
	    }
	    if (0 == arg0 % 2) {
	    	arg1.setBackgroundResource(R.drawable.synthesis_item1);
        } else {
        	arg1.setBackgroundResource(R.drawable.synthesis_item2);
        }
	    String temp = data.get(arg0);
	    int start=temp.lastIndexOf("/")+1;
	    int end =temp.lastIndexOf(".");
	    holder.tv_name.setText(temp.substring(start, end));
        holder.tv_type.setText(temp.substring(end+1)+"文件");
        if(temp.substring(end+1).equals("wav")){
        	holder.tv_wavDetail.setVisibility(View.VISIBLE);
        	byte[] bt =new byte[44];
        	FileInputStream fis;
			try {
				fis = new FileInputStream(temp);
				fis.read(bt, 0, 44);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				holder.tv_wavDetail.setText("文件无法读取");
				return arg1;
			} 
        	
        	long longSampleRate=CaoZuoWavUtils.bytesToInt(bt,24);
    		long channels=CaoZuoWavUtils.bytesToInt2(bt,22);
    		holder.tv_wavDetail.setText("采样频率："+longSampleRate+"声道数："+channels);
        }
		return arg1;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		mCallback.click(arg0);
	}

}
