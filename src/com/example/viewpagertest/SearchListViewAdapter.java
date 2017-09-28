package com.example.viewpagertest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.provider.ContactsContract.Contacts.Data;
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
	private Callback mCallback;
	
	public interface Callback {
        public void click(View v);
     }
	
	public SearchListViewAdapter(Context context,ArrayList<String> temp,Callback callback){
		this.mInflater=LayoutInflater.from(context);
		data =temp;
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
		ImageView imgv =(ImageView)arg1.findViewById(R.id.imageView2);
		imgv.setOnClickListener(this);
		tv1.setText(data.get(arg0));
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
