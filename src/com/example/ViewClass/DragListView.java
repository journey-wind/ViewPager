package com.example.ViewClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class DragListView extends ListView {  
	  
    private WindowManager              windowManager;         // windows���ڿ�����     
  
    private WindowManager.LayoutParams windowParams;          // ���ڿ�����ק�����ʾ�Ĳ���       
  
    private ImageView                  dragImageView;         // ����ק����(item)����ʵ����һ��ImageView     
  
    private int                        dragSrcPosition;       // ��ָ�϶���ԭʼ���б��е�λ��     
  
    private int                        dragPosition;          // ��ָ���׼���϶���ʱ��,��ǰ�϶������б��е�λ��.     
  
    private int                        dragPoint;             // �ڵ�ǰ�������е�λ��     
  
    private int                        dragOffset;            // ��ǰ��ͼ����Ļ�ľ���(����ֻʹ����y������)     
  
    private int                        upScrollBounce;        // �϶���ʱ�򣬿�ʼ���Ϲ����ı߽�     
  
    private int                        downScrollBounce;      // �϶���ʱ�򣬿�ʼ���¹����ı߽�     
  
    private final static int           step = 1;              // ListView ��������.     
  
    private int                        current_Step;          // ��ǰ����.     
  
    private int                        dragImageSourceId;  
  
    private DragItemChangeListener     dragItemChangeListener;  
    private int ImgBtnSourceId;
  
    /***  
     * ���췽��  
     *   
     * @param context  
     * @param attrs  
     */  
    public DragListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setDragImageSourceId(int dragImageSourceId,int imgbtn) {  
        this.dragImageSourceId = dragImageSourceId;  
        this.ImgBtnSourceId=imgbtn;
    }  
  
    public void setDragItemChangeListener(DragItemChangeListener dragItemChangeListener) {  
        this.dragItemChangeListener = dragItemChangeListener;  
    }  
  
    /***  
     * touch�¼�����  
     */  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        // ����     
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
            int x = (int) ev.getX();// ��ȡ�����ListView��x����     
            int y = (int) ev.getY();// ��ȡ��Ӧ��ListView��y����     
            dragSrcPosition = dragPosition = pointToPosition(x, y);  
            // ��Ч�����д���     
            if (dragPosition == AdapterView.INVALID_POSITION) {  
                return super.onInterceptTouchEvent(ev);  
            }  
  
            // ��ȡ��ǰλ�õ���ͼ(�ɼ�״̬)     
            ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());  
  
            // ��ȡ����dragPoint��ʵ����������ָ��item���еĸ߶�.     
            dragPoint = y - itemView.getTop();  
            // ���ֵ�ǹ̶���:��ʵ����ListView����ؼ�����Ļ����ľ��루һ��Ϊ������+״̬����.     
            dragOffset = (int) (ev.getRawY() - y);  
  
            // ��ȡ����ק��ͼ��     
            View dragger = itemView.findViewById(dragImageSourceId);  
            View right=itemView.findViewById(ImgBtnSourceId);  
            // x > dragger.getLeft() - 20��仰Ϊ�˸��õĴ�����-20����ʡ�ԣ�     
            if (dragger != null && x > dragger.getLeft() - 20 && x<right.getLeft()) {  
  
                upScrollBounce = getHeight() / 3;// ȡ�����Ϲ����ı߼ʣ����Ϊ�ÿؼ���1/3     
                downScrollBounce = getHeight() * 2 / 3;// ȡ�����¹����ı߼ʣ����Ϊ�ÿؼ���2/3     
  
                itemView.setDrawingCacheEnabled(true);// ����cache.     
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());// ����cache����һ���µ�bitmap����.     
                startDrag(bm, y);// ��ʼ��Ӱ��     
            }  
        }  
  
        return super.onInterceptTouchEvent(ev);  
    }  
  
    /**  
     * �����¼�����  
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent ev) {  
        // item��view��Ϊ�գ��һ�ȡ��dragPosition��Ч     
        if (dragImageView != null && dragPosition != INVALID_POSITION) {  
            int action = ev.getAction();  
            switch (action) {  
            case MotionEvent.ACTION_UP:  
                int upY = (int) ev.getY();  
                stopDrag();  
                onDrop(upY);  
                break;  
            case MotionEvent.ACTION_MOVE:  
                int moveY = (int) ev.getY();  
                onDrag(moveY);  
                break;  
            case MotionEvent.ACTION_DOWN:  
            	  
                break;  
            default:  
            	//stopDrag();  
                break;  
            }  
            return true;// ȡ��ListView����.     
        }  
  
        return super.onTouchEvent(ev);  
    }  
  
    /**  
     * ׼���϶�����ʼ���϶����ͼ��  
     *   
     * @param bm  
     * @param y  
     */  
    private void startDrag(Bitmap bm, int y) {  
        // stopDrag();     
        /***  
         * ��ʼ��window.  
         */  
        windowParams = new WindowManager.LayoutParams();  
        windowParams.gravity = Gravity.TOP;  
        windowParams.x = 0;  
        windowParams.y = y - dragPoint + dragOffset;  
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
  
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// �����ȡ����     
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// ������ܴ����¼�     
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// �����豸���������������Ȳ��䡣     
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// ����ռ��������Ļ��������Χ��װ�α߿�����״̬�������˴����迼�ǵ�װ�α߿�����ݡ�     
  
        windowParams.format = PixelFormat.TRANSLUCENT;// Ĭ��Ϊ��͸�����������͸��Ч��.     
        windowParams.windowAnimations = 0;// ������ʹ�õĶ�������     
  
        ImageView imageView = new ImageView(getContext());  
        imageView.setImageBitmap(bm);  
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);  
        windowManager.addView(imageView, windowParams);  
        dragImageView = imageView;  
  
    }  
  
    /**  
     * �϶�ִ�У���Move������ִ��  
     *   
     * @param y  
     */  
    public void onDrag(int y) {  
        int drag_top = y - dragPoint;// ��קview��topֵ���ܣ�0�����������.     
        if (dragImageView != null && drag_top >= 0) {  
            windowParams.alpha = 0.5f;// ͸����     
            windowParams.y = y - dragPoint + dragOffset;// �ƶ�yֵ.//�ǵ�Ҫ����dragOffset��windowManager�������������Ļ.(��������״̬����Ҫ����)     
            windowManager.updateViewLayout(dragImageView, windowParams);// ʱʱ�ƶ�.     
        }  
        // Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������     
        int tempPosition = pointToPosition(0, y);  
        if (tempPosition != INVALID_POSITION) {  
            dragPosition = tempPosition;  
  
        }  
        doScroller(y);  
    }  
  
    /***  
     * ListView���ƶ�.  
     * Ҫ�����ƶ�ԭ����ӳ���ƶ����¶˵�ʱ��ListView���ϻ�������ӳ���ƶ����϶˵�ʱ��ListViewҪ���»��������ú�ʵ�ʵ��෴.  
     *   
     */  
  
    public void doScroller(int y) {  
        // ListView��Ҫ�»�     
        if (y < upScrollBounce) {  
            current_Step = step + (upScrollBounce - y) / 10;// ʱʱ����     
        }// ListView��Ҫ�ϻ�     
        else if (y > downScrollBounce) {  
            current_Step = -(step + (y - downScrollBounce)) / 10;// ʱʱ����     
        } else {  
            current_Step = 0;  
        }  
  
        // ��ȡ����ק������λ�ü���ʾitem��Ӧ��view�ϣ�ע������ʾ���֣���position��     
        View view = getChildAt(dragPosition - getFirstVisiblePosition());  
        // ���������ķ���setSelectionFromTop()     
        setSelectionFromTop(dragPosition, view.getTop() + current_Step);  
  
    }  
  
    /**  
     * ֹͣ�϶���ɾ��Ӱ��  
     */  
    public void stopDrag() {  
        if (dragImageView != null) {  
            windowManager.removeView(dragImageView);  
            dragImageView = null;  
        }  
    }  
  
    /**  
     * �϶����µ�ʱ��  
     *   
     * @param y  
     */  
    public void onDrop(int y) {  
  
        // Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������     
        int tempPosition = pointToPosition(0, y);  
        if (tempPosition != INVALID_POSITION) {  
            dragPosition = tempPosition;  
        }  
  
        // �����߽紦��(������ϳ����ڶ���Top�Ļ�����ô�ͷ����ڵ�һ��λ��)     
        if (y < getChildAt(0).getTop()) {  
            // �����ϱ߽�     
            dragPosition = 0;  
            // ����϶��������һ������±���ô�ͷ�ֹ�����±�     
        } else if (y > getChildAt(getChildCount() - 1).getBottom()) {  
            // �����±߽�     
            dragPosition = getAdapter().getCount() - 1;  
        }  
  
        // ���ݽ���     
        if (dragPosition < getAdapter().getCount()) {  
  
            dragItemChangeListener.onDragItemChange(dragSrcPosition, dragPosition);  
        }  
  
    }  
    public static interface DragItemChangeListener {  
        public void onDragItemChange(int dragSrcPosition,int dragPosition);  
    }  
  
}  
