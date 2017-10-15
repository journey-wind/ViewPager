package com.example.ViewClass;

import com.example.viewpagertest.R;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class MySwipeRefresh extends SwipeRefreshLayout{

	private ListView mListView;
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		
		// 获取ListView,设置ListView的布局位置
		  if (mListView == null) {
		      // 判断容器有多少个孩子
		      if (getChildCount() > 0) {
		          // 判断第一个孩子是不是ListView
		          if (getChildAt(0) instanceof ListView) {
		              // 创建ListView对象
		              mListView = (ListView) getChildAt(0);

		              // 设置ListView的滑动监听
		              setListViewOnScroll();
		          }
		      }
		  }

		super.onLayout(changed, left, top, right, bottom);
	}
	
	/**
	   * 设置ListView的滑动监听
	   */
	  private void setListViewOnScroll() {

	      mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
	          @Override
	          public void onScrollStateChanged(AbsListView view, int scrollState) {
	              // 移动过程中判断时候能下拉加载更多
	              if (canLoadMore()) {
	                  // 加载数据
	                  loadData();
	              }
	          }

	          @Override
	          public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	          }
	      });
	  }

	  /**
	   * 在分发事件的时候处理子控件的触摸事件
	   *
	   * @param ev
	   * @return
	   */
	  private float mDownY, mUpY;
	private float mScaledTouchSlop=30;
	private OnLoadListener mOnLoadListener;  
	private boolean isLoading;
	private View mListViewFooter;
	  @Override
	  public boolean dispatchTouchEvent(MotionEvent ev) {

	      switch (ev.getAction()) {
	          case MotionEvent.ACTION_DOWN:
	              // 移动的起点
	              mDownY = ev.getY();
	              break;
	          case MotionEvent.ACTION_MOVE:
	        	  
	              // 移动过程中判断时候能下拉加载更多
	              if (canLoadMore()) {
	                  // 加载数据
	                  loadData();
	              }

	              break;
	          case MotionEvent.ACTION_UP:
	              // 移动的终点
	              
	              break;
	      }
	      return super.dispatchTouchEvent(ev);
	  }

	  /**
	   * 判断是否满足加载更多条件
	   *
	   * @return
	   */
	  private boolean canLoadMore() {
	      // 1. 是上拉状态
	      boolean condition1 = (mDownY - mUpY) >= mScaledTouchSlop;
	      if (condition1) {
	          System.out.println("是上拉状态");
	      }

	      // 2. 当前页面可见的item是最后一个条目
	      boolean condition2 = false;
	      if (mListView != null && mListView.getAdapter() != null) {
	          condition2 = mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
	      }

	      if (condition2) {
	          System.out.println("是最后一个条目");
	      }
	      // 3. 正在加载状态
	      boolean condition3 = !isLoading;
	      if (condition3) {
	          System.out.println("不是正在加载状态");
	      }
	      return condition1 && condition2 && condition3;
	  }

	  /**
	   * 处理加载数据的逻辑
	   */
	  private void loadData() {
	      System.out.println("加载数据...");
	      if (mOnLoadListener != null) {
	          // 设置加载状态，让布局显示出来
	          setLoading(true);
	          mOnLoadListener.onLoad();
	      }

	  }

	  /**
	   * 设置加载状态，是否加载传入boolean值进行判断
	   *
	   * @param loading
	   */
	  public void setLoading(boolean loading) {
	      // 修改当前的状态
	      isLoading = loading;
	      if (isLoading) {
	          // 显示布局
	          mListView.addFooterView(mListViewFooter);
	      } else {
	          // 隐藏布局
	          mListView.removeFooterView(mListViewFooter);

	          // 重置滑动的坐标
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
	}

	public static interface OnLoadListener {  
        public void onLoad();  
    }  

}
