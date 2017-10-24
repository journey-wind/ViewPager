package com.example.ViewClass;

import com.example.viewpagertest.R;
import com.example.viewpagertest.SocialMessage;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

public class MySwipeRefresh extends SwipeRefreshLayout{

	private ListView mListView;
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		
		// ��ȡListView,����ListView�Ĳ���λ��
		  if (mListView == null) {
		      // �ж������ж��ٸ�����
		      if (getChildCount() > 0) {
		          // �жϵ�һ�������ǲ���ListView
		          if (getChildAt(0) instanceof ListView) {
		              // ����ListView����
		              mListView = (ListView) getChildAt(0);

		              // ����ListView�Ļ�������
		              setListViewOnScroll();
		          }
		      }
		  }

		super.onLayout(changed, left, top, right, bottom);
	}
	
	/**
	   * ����ListView�Ļ�������
	   */
	  private void setListViewOnScroll() {

	      mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
	          @Override
	          public void onScrollStateChanged(AbsListView view, int scrollState) {
	              // �ƶ��������ж�ʱ�����������ظ���
	              if (canLoadMore()) {
	                  // ��������
	                  loadData();
	              }
	          }

	          @Override
	          public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	          }
	      });
	  }

	  /**
	   * �ڷַ��¼���ʱ�����ӿؼ��Ĵ����¼�
	   *
	   * @param ev
	   * @return
	   */
	  private float mDownY, mUpY;
	private float mScaledTouchSlop=30;
	private OnLoadListener mOnLoadListener;  
	private boolean isLoading;
	private View mListViewFooter;
	private float oldY;
	private boolean isUp=true;

	  @Override
	  public boolean dispatchTouchEvent(MotionEvent ev) {

	      switch (ev.getAction()) {
	          case MotionEvent.ACTION_DOWN:
	              // �ƶ������
	        	  oldY=ev.getY();
	              mDownY = ev.getY();
	              break;
	          case MotionEvent.ACTION_MOVE:
	        	  
	        	  float k=ev.getY()-oldY;
					oldY=ev.getY();
					if(SocialMessage.msgAdd!=null){
						if(k>2 && !isUp){
							SocialMessage.msgAdd.startAnimation(animImgUp);
							isUp=true;
						}else if(k<-2&&isUp){
							SocialMessage.msgAdd.startAnimation(animImgDown);
							isUp=false;
						}
					}
	        	  
	              // �ƶ��������ж�ʱ�����������ظ���
	              if (canLoadMore()) {
	                  // ��������
	                  loadData();
	              }

	              break;
	          case MotionEvent.ACTION_UP:
	              // �ƶ����յ�
	              
	              break;
	      }
	      return super.dispatchTouchEvent(ev);
	  }

	  
	  private AnimationSet animImgUp;
		private AnimationSet animImgDown;
		private void initAnimation() {
			// TODO Auto-generated method stub
			animImgUp = new AnimationSet(true);  
	        TranslateAnimation translateAnimation = new TranslateAnimation(  
	                //X���ʼλ��  
	                Animation.RELATIVE_TO_SELF, 0.0f,  
	                //X���ƶ��Ľ���λ��  
	                Animation.RELATIVE_TO_SELF,0.0f,  
	                //y�Ὺʼλ��  
	                Animation.RELATIVE_TO_SELF,2.0f,  
	                //y���ƶ���Ľ���λ��  
	                Animation.RELATIVE_TO_SELF,0.0f);  

	        translateAnimation.setDuration(1000);  
	        //���fillAfter��ֵΪ��Ļ������������󣬿ؼ�ͣ����ִ�к��״̬  
	        animImgUp.setFillAfter(true);  
	        animImgUp.addAnimation(translateAnimation);  
	        //down��������
	        animImgDown = new AnimationSet(true);  
	        translateAnimation = new TranslateAnimation(  
	                //X���ʼλ��  
	                Animation.RELATIVE_TO_SELF, 0.0f,  
	                //X���ƶ��Ľ���λ��  
	                Animation.RELATIVE_TO_SELF,0.0f,  
	                //y�Ὺʼλ��  
	                Animation.RELATIVE_TO_SELF,0.0f,  
	                //y���ƶ���Ľ���λ��  
	                Animation.RELATIVE_TO_SELF,-2.0f);  

	        translateAnimation.setDuration(1000);  
	        //���fillAfter��ֵΪ��Ļ������������󣬿ؼ�ͣ����ִ�к��״̬  
	        animImgDown.setFillAfter(true);  
	        animImgDown.addAnimation(translateAnimation); 
		}
	  
	  /**
	   * �ж��Ƿ�������ظ�������
	   *
	   * @return
	   */
	  private boolean canLoadMore() {
	      // 1. ������״̬
	      boolean condition1 = (mDownY - mUpY) >= mScaledTouchSlop;
	      if (condition1) {
	          System.out.println("������״̬");
	      }

	      // 2. ��ǰҳ��ɼ���item�����һ����Ŀ
	      boolean condition2 = false;
	      if (mListView != null && mListView.getAdapter() != null) {
	          condition2 = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
	      }

	      if (condition2) {
	          System.out.println("�����һ����Ŀ");
	      }
	      // 3. ���ڼ���״̬
	      boolean condition3 = !isLoading;
	      if (condition3) {
	          System.out.println("�������ڼ���״̬");
	      }
	      return condition1 && condition2 && condition3;
	  }

	  /**
	   * ����������ݵ��߼�
	   */
	  private void loadData() {
	      System.out.println("��������...");
	      if (mOnLoadListener != null) {
	          // ���ü���״̬���ò�����ʾ����
	          setLoading(true);
	          mOnLoadListener.onLoad();
	      }

	  }

	  /**
	   * ���ü���״̬���Ƿ���ش���booleanֵ�����ж�
	   *
	   * @param loading
	   */
	  public void setLoading(boolean loading) {
	      // �޸ĵ�ǰ��״̬
	      isLoading = loading;
	      if (isLoading) {
	          // ��ʾ����
	          mListView.addFooterView(mListViewFooter);
	      } else {
	          // ���ز���
	          mListView.removeFooterView(mListViewFooter);

	          // ���û���������
	          mDownY = 0;
	          mUpY = 0;
	      }
	  }
	  
	  public void setOnLoadListener(OnLoadListener loadListener) {  
	        mOnLoadListener = loadListener;  
	    }  
	  
	public MySwipeRefresh(Context context) {
		super(context,null);
		// TODO Auto-generated constructor stub
	}
	public MySwipeRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mListViewFooter = LayoutInflater.from(context).inflate(R.layout.pull_layout, null,  
                false);  
		initAnimation();
	}

	public static interface OnLoadListener {  
        public void onLoad();  
    }  

}
