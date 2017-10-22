package com.example.Record;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;

public class CaoZuoWavUtils {
	public static String fenLiData(String path,String path1,String name) throws IOException {
		// TODO Auto-generated method stub
		FileInputStream fisone=null;
		FileInputStream fistwo=null;
		byte[] temp =new byte[44];
		try {
			fisone =new FileInputStream(path);
			fistwo=new FileInputStream(path1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		fisone.read(temp, 0, 44);
		//fistwo.read(temp, 0, 44);
		String luJing="/storage/emulated/0/"+"HH���ֲ�����/�ϲ�/";  
		File f=new File(luJing);  
	    f.mkdirs();  
	    
		File file=new File(luJing+name+".pcm");  
		FileOutputStream out=new FileOutputStream(file);
		byte bs[]=new byte[1024*4];  
	    int len=0;  
	    //�ȶ���һ��  
	    while((len=fisone.read(bs))!=-1){  
	        out.write(bs,0,len);  
	    }  
	    fisone.close();  
	    out.close();  
	       //�ٶ��ڶ���  
	       
	    out=new FileOutputStream(file,true);//���ļ�β�������  
	    len=0;  
	    byte bs1[]=new byte[1024*4];  
	    while((len=fistwo.read(bs1))!=-1){  
	        out.write(bs1,0,len);  
	    }  
	    fistwo.close();  
	    out.close();
	    //����ͷ��  д��wav�ļ���
	    FileInputStream in = new FileInputStream(file);
	    File file1=new File(luJing+name+".wav");  
		out=new FileOutputStream(file1);
		long totalAudioLen = in.getChannel().size();
        //���ڲ�����RIFF��WAV
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate=bytesToInt(temp,24);
		long channels=bytesToInt2(temp,22);
		long byteRate=bytesToInt(temp,28);
		WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
        while (in.read(bs) != -1) {
            out.write(bs);
        }
        in.close();
        out.close();
        //ɾ���м��ļ�
        file=new File(luJing+name+".pcm");  
        if(file.exists()){
        	file.delete();
        }
		return file1.getAbsolutePath();
	}  
	
	public static long bytesToInt(byte[] src, int offset) {  
	    long value;    
	    value = (long) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)   
	            | ((src[offset+2] & 0xFF)<<16)   
	            | ((src[offset+3] & 0xFF)<<24));  
	    return value;  
	}  
	public static long bytesToInt2(byte[] src, int offset) {  
	    long value;    
	    value = (long) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8));
	    return value;  
	}
	
	private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
            long channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);//���ݴ�С
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';//WAVE
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		//FMT Chunk
		header[12] = 'f'; // 'fmt '
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';//�����ֽ�
		//���ݴ�С
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		//���뷽ʽ 10HΪPCM�����ʽ
		header[20] = 1; // format = 1
		header[21] = 0;
		//ͨ����
		header[22] = (byte) channels;
		header[23] = 0;
		//�����ʣ�ÿ��ͨ���Ĳ����ٶ�
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		//��Ƶ���ݴ�������,������*ͨ����*�������/8
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		// ȷ��ϵͳһ��Ҫ������ٸ������ֽڵ����ݣ�ȷ����������ͨ����*����λ��
		header[32] = (byte) (1 * 16 / 8);
		header[33] = 0;
		//ÿ������������λ��
		header[34] = 16;
		header[35] = 0;
		//Data chunk
		header[36] = 'd';//data
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}
	
}
