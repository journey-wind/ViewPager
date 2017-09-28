package com.example.viewpagertest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.Record.ClsOscilloscope;
import com.example.viewpagertest.MediaFile.MediaFileType;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.ZoomControls;

public class SoundRecord extends ViewGroup {
	
	private View view;
	private Context mcontext;
	private MediaRecorder mediaRecorder;
    // 以文件的形式保存
	private String outputFile = null;
    private Button start;
    private Button end;
    private Button play;
//    private RecordPlayer player; 
    private File audioFile;  
    private Uri fileUri;  
    private SimpleCursorAdapter mAdapter;
    

	SurfaceView sfv;  
    ZoomControls zctlX,zctlY;  
      
    ClsOscilloscope clsOscilloscope=new ClsOscilloscope();  
      
    static final int frequency = 16000;//分辨率  
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;  
    static final int xMax = 16;//X轴缩小比例最大值,X轴数据量巨大，容易产生刷新延时  
    static final int xMin = 8;//X轴缩小比例最小值  
    static final int yMax = 10;//Y轴缩小比例最大值  
    static final int yMin = 1;//Y轴缩小比例最小值  
      
    int recBufSize;//录音最小buffer大小  
    AudioRecord audioRecord;  
    Paint mPaint;  
    
	
	public SoundRecord( Context context, AttributeSet attrs,View view) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		final Context mcontext=context;
		this.view = view;
//		start = (Button) view.findViewById(R.id.button1);
//		end = (Button) view.findViewById(R.id.button3);
		play = (Button) view.findViewById(R.id.button2);
		File fpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/data/files/");  
        fpath.mkdirs();//创建文件夹  
        try {  
            //创建临时文件  
            audioFile = File.createTempFile("recording", ".pcm", fpath);  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
//		
		//查找音频文件
//		List<String> frequency = SeekFrequency();
//		outputFile = context.getApplicationContext().getFilesDir().getAbsolutePath()+ "/files/myrecording.amr";
//		start.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Log.v("record", "start_onClick");
//		        // 判断，若当前文件已存在，则删除
//		            //开始录制  
//		            //我们需要实例化一个MediaRecorder对象，然后进行相应的设置  
//				mediaRecorder = new MediaRecorder();  
//		            //指定AudioSource 为MIC(Microphone audio source ),这是最长用的  
//				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
//		            //指定OutputFormat,我们选择3gp格式  
//		            //其他格式，MPEG-4:这将指定录制的文件为mpeg-4格式，可以保护Audio和Video  
//		            //RAW_AMR:录制原始文件，这只支持音频录制，同时要求音频编码为AMR_NB  
//		            //THREE_GPP:录制后文件是一个3gp文件，支持音频和视频录制  
//				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
//		            //指定Audio编码方式，目前只有AMR_NB格式  
//				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//		            //接下来我们需要指定录制后文件的存储路径  
//		        File fpath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/data/files/");  
//		        fpath.mkdirs();//创建文件夹  
//		        try {  
//		            //创建临时文件  
//		            audioFile = File.createTempFile("recording", ".3gp", fpath);  
//		        } catch (IOException e) {  
//		            // TODO Auto-generated catch block  
//		            e.printStackTrace();  
//		        }  
//		              
//		            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());  
//		              
//		            //下面就开始录制了  
//		            try {  
//		            	mediaRecorder.prepare();  
//		            } catch (IllegalStateException e) {  
//		                // TODO Auto-generated catch block  
//		                e.printStackTrace();  
//		            } catch (IOException e) {  
//		                // TODO Auto-generated catch block  
//		                e.printStackTrace();  
//		            }  
//		              
//		            mediaRecorder.start();  
//
//		        }
//		});
		play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.v("record", "play_onClick");
				// TODO Auto-generated method stub
//				player = new RecordPlayer(mcontext);
//				player.playRecordFile(audioFile);
				PlayTask player = new PlayTask();
				player.execute();
			}
		});
//		end.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Log.v("record", "end_onClick");
//				mediaRecorder.stop(); 
//				mediaRecorder.release();  
//				
//				ContentValues values = new ContentValues();  
//	            values.put(MediaStore.Audio.Media.TITLE, "this is my first record-audio");  
//	            values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis());  
//	            values.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());  
//	            fileUri = mcontext.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);  
//	            //录制结束后，我们实例化一个MediaPlayer对象，然后准备播放  
//	              
//			}
//		});
//		ListView SoundList =new ListView(mcontext);
//		mAdapter = new SimpleCursorAdapter(
//                mcontext,
//                // Use a template that displays a text view
//                R.layout.sound_list,
//                // Give the cursor to the list adatper
//                null,
//                // Map from database columns...
//                new String[] {
//                    MediaStore.Audio.Media.ARTIST,
//                    MediaStore.Audio.Media.ALBUM,
//                    MediaStore.Audio.Media.TITLE},
//                    // To widget ids in the row layout...
//                    new int[] {
//                    R.id.row_artist,
//                    R.id.row_album,
//                    R.id.row_title});
//		SoundList.setAdapter(mAdapter);
		
		init();
	}
	
	private void init(){
		//录音组件
		recBufSize = AudioRecord.getMinBufferSize(frequency,  
                channelConfiguration, audioEncoding);  
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,  
                channelConfiguration, audioEncoding, recBufSize);  
        //按键  
        start = (Button) view.findViewById(R.id.button1);  
        start.setOnClickListener(new ClickEvent());  
        end = (Button) view.findViewById(R.id.button3);  
        end.setOnClickListener(new ClickEvent());  
		//画板和画笔  
        sfv = (SurfaceView) view.findViewById(R.id.SurfaceView01);   
        sfv.setOnTouchListener(new TouchEvent());  
        mPaint = new Paint();    
        mPaint.setColor(Color.GREEN);// 画笔为绿色    
        mPaint.setStrokeWidth(1);// 设置画笔粗细   
        //示波器类库  
        clsOscilloscope.initOscilloscope(xMax/2, yMax/2, sfv.getHeight()/2,audioFile);  
          
        //缩放控件，X轴的数据缩小的比率高些  
        zctlX = (ZoomControls)view.findViewById(R.id.zctlX);  
        zctlX.setOnZoomInClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(clsOscilloscope.rateX>xMin)  
                    clsOscilloscope.rateX--;  
//                	setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"  
//                        +","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");  
            }  
        });  
        zctlX.setOnZoomOutClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(clsOscilloscope.rateX<xMax)  
                    clsOscilloscope.rateX++;      
//                setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"  
//                        +","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");  
            }  
        });  
        zctlY = (ZoomControls)view.findViewById(R.id.zctlY);  
        zctlY.setOnZoomInClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(clsOscilloscope.rateY>yMin)  
                    clsOscilloscope.rateY--;  
//                setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"  
//                        +","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");  
            }  
        });  
          
        zctlY.setOnZoomOutClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(clsOscilloscope.rateY<yMax)  
                    clsOscilloscope.rateY++;      
//                setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"  
//                        +","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");  
            }  
        });  
	}
	
	
	 public List<String> SeekFrequency (){
		 List<String> fileList = new ArrayList<String>();  
		 List<String> InnerList = new ArrayList<String>();  
		 List<String> ExtList = new ArrayList<String>();  
		 
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

	 private  List<String> getAllFile(String path){  
        List<String> fileList = new ArrayList<String>();  
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
	     * 获取内置SD卡路径 
	     * @return 
	     */  
	    public String getInnerSDCardPath() {    
	        return Environment.getExternalStorageDirectory().getPath();    
	    }  
	  
	    /** 
	     * 获取外置SD卡路径 
	     * @return  应该就一条记录或空 
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
			ContentResolver cr = mcontext.getContentResolver();  
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
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	class ClickEvent implements View.OnClickListener {  
        @Override  
        public void onClick(View v) {  
            if (v == start) {  
            	
                clsOscilloscope.baseLine=sfv.getHeight()/2;  
                clsOscilloscope.Start(audioRecord,recBufSize,sfv,mPaint);  
            } else if (v == end) {  
                clsOscilloscope.Stop();  
            }  
        }  
    }  
    /** 
     * 触摸屏动态设置波形图基线 
     * @author GV 
     * 
     */  
    class TouchEvent implements OnTouchListener{  
       
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// TODO Auto-generated method stub
			clsOscilloscope.baseLine=(int)arg1.getY();  
            return true;
		}  
          
    }  
    
    class PlayTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
	    	int bufferSize = AudioTrack.getMinBufferSize(SoundRecord.frequency,
	    			SoundRecord.channelConfiguration, audioEncoding);
	    	short[] buffer = new short[bufferSize / 4];
	    	try {
		    	// 定义输入流，将音频写入到AudioTrack类中，实现播放
		    	DataInputStream dis = new DataInputStream(
		    			new BufferedInputStream(new FileInputStream(audioFile)));
		    	// 实例AudioTrack
		    	AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
		    			SoundRecord.frequency, SoundRecord.channelConfiguration, audioEncoding, bufferSize,
		    	AudioTrack.MODE_STREAM);
		    	// 开始播放
		    	track.play();
		    	// 由于AudioTrack播放的是流，所以，我们需要一边播放一边读取
		    	while (dis.available() > 0) {
			    	int i = 0;
			    	while (dis.available() > 0 && i < buffer.length) {
				    	buffer[i] = dis.readShort();
				    	i++;
			    	}
			    	// 然后将数据写入到AudioTrack中
			    	track.write(buffer, 0, buffer.length);
		    	}
		    	track.stop();
		    	dis.close();
		    } catch (Exception e) {
		    	// TODO: handle exception
		    }
		    	return null;
		}
	}
}