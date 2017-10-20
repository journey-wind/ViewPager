package com.example.viewpagertest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ringdroid.soundfile.CheapSoundFile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.provider.ContactsContract.Contacts.Data;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchListViewAdapter extends BaseAdapter implements OnClickListener{

	private LayoutInflater mInflater=null;
	private ArrayList<String> data; 
	private ArrayList<String> queryData ; 
	private Callback mCallback;
	
	public interface Callback {
        public void click(View v);
     }
	
	public SearchListViewAdapter(Context context,ArrayList<String> temp,Callback callback){
		this.mInflater=LayoutInflater.from(context);
		data =temp;
		mCallback = callback;
		queryData =new ArrayList<String>();
		queryData.addAll(data);
	}
	
	 public void queryData(String query){  
	    queryData.clear();
	    if(query.equals("")){
	    	queryData.addAll(data);
	    }else{
	        for(String key : data){  
	        	String temp =key.substring(key.lastIndexOf("/"));
	            if(!TextUtils.isEmpty(key) && temp.toLowerCase()
	            		.substring(temp.lastIndexOf("/")+1)
	            		.contains(query.toLowerCase())){  
	                queryData.add(key);  
	            }  
	        }  
	    }
	    notifyDataSetChanged();  
	}  
	
	 
	public void setDataList(ArrayList<String> outData){
		data=null;
		data=outData;
		queryData.clear();
		queryData.addAll(data);
		notifyDataSetChanged();  
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return queryData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		arg1=mInflater.inflate(R.layout.search_list, null);
		TextView tv1 =(TextView)arg1.findViewById(R.id.textView1);
		TextView tv2 =(TextView)arg1.findViewById(R.id.searchAuthor);
		ImageView imgv =(ImageView)arg1.findViewById(R.id.imageView2);
		imgv.setOnClickListener(this);
		
		String mFilename=queryData.get(arg0);
		SongMetadataReader metadataReader = new SongMetadataReader((Activity) arg1.getContext(), mFilename);
		CheapSoundFile mSoundFile=null;
		
        String mTitle = metadataReader.mTitle;
        String mArtist = metadataReader.mFilename;
        mArtist=mArtist.substring(mArtist.lastIndexOf('.')+1);
        
        //int aa = metadataReader.mTime;
//        if(aa<=0){
//        	queryData.remove(arg0);
//        	notifyDataSetChanged();
//        }
        //mArtist=Integer.toString(aa);
		tv1.setText(mTitle);
		tv2.setText(mArtist+"нд╪Ч");
		Paint mp = new Paint();
		Typeface font = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
		tv1.setTypeface(font);
		imgv.setTag(arg0);
		return arg1;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mCallback.click(v);
	}

}
