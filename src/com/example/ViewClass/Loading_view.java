package com.example.ViewClass;

import com.example.viewpagertest.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class Loading_view extends ProgressDialog{

	private TextView loadText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init(getContext());
	}

	private void init(Context context) {
	    setCancelable(true);
	    setCanceledOnTouchOutside(false);
	    setContentView(R.layout.progress_layout);//loadingµÄxmlÎÄ¼þ
	    loadText=(TextView)findViewById(R.id.tv_load_dialog);
	    WindowManager.LayoutParams params = getWindow().getAttributes();
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
	    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    getWindow().setAttributes(params);
	  }
	public TextView GetLoadText(){
		return loadText;
	}
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	public Loading_view(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public Loading_view(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

}
