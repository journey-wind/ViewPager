package com.example.viewpagertest;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.widget.Toast;

public class RecordPlayer {
	private static MediaPlayer mediaPlayer;

    private Context mcontext;

    public RecordPlayer(Context context) {
        this.mcontext = context;
    }

    // ����¼���ļ�
    public void playRecordFile(File file) {
        
            if (mediaPlayer == null) {
            	try {
            		mediaPlayer=new MediaPlayer();
					mediaPlayer.setDataSource(file.getAbsolutePath());
					mediaPlayer.prepare();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            	  
            }
            mediaPlayer.start();

            //����MediaPlayer�������
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer paramMediaPlayer) {
                    // TODO Auto-generated method stub
                    //������ʾ
                    Toast.makeText(mcontext,
                            "�����ļ��������",
                            Toast.LENGTH_SHORT).show();
                }
            });

        
    }

    // ��ͣ����¼��
    public void pausePalyer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }

    }

    // ֹͣ����¼��
    public void stopPalyer() {
        // ���ﲻ����stop()������seekto(0),�Ѳ��Ž��Ȼ�ԭ���ʼ
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);

        }
    }
}
