package com.example.Record;



import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
* Created by senshan_wang on 2016/3/31.
*/
public class AudioCodec {

private static final String TAG = "AudioCodec";
private String encodeType;
private String srcPath;
//private String dstPath;
private MediaCodec mediaDecode;
private MediaCodec mediaEncode;
private MediaExtractor mediaExtractor;
private ByteBuffer[] decodeInputBuffers;
private ByteBuffer[] decodeOutputBuffers;
private ByteBuffer[] encodeInputBuffers;
private ByteBuffer[] encodeOutputBuffers;
private MediaCodec.BufferInfo decodeBufferInfo;
private MediaCodec.BufferInfo encodeBufferInfo;
//private FileOutputStream fos;
//private BufferedOutputStream bos;
private FileInputStream fis;
private BufferedInputStream bis;
private ArrayList<byte[]> chunkPCMDataContainer;//PCM���ݿ�����
private OnCompleteListener onCompleteListener;
private OnProgressListener onProgressListener;
private long fileTotalSize;
private long decodeSize;
public long mSampleRate;
public int channel;
public long duration;


	public static AudioCodec newInstance() {
		return new AudioCodec();
	}
	
	/**
	* ���ñ���������
	* @param encodeType
	*/
	public void setEncodeType(String encodeType) {
		this.encodeType=encodeType;
	}
	
	/**
	* ������������ļ�λ��
	* @param srcPath
	* @param dstPath
	*/
	public void setIOPath(String srcPath) {
		this.srcPath=srcPath;
		
	}
	
	/**
	* �����Ѿ�����װ
	* ����prepare���� ���ʼ��Decode ��Encode ����������� ��һЩ�в���
	*/
	public void prepare(boolean isMix) {
	
		if (encodeType == null) {
		    throw new IllegalArgumentException("encodeType can't be null");
		}
		
		if (srcPath == null) {
		    throw new IllegalArgumentException("srcPath can't be null");
		}
		
//		if (dstPath == null) {
//		    throw new IllegalArgumentException("dstPath can't be null");
//		}
		
		//		    fos = new FileOutputStream(new File(dstPath));
		//		    bos = new BufferedOutputStream(fos,200*1024);
				    File file = new File(srcPath);
				    fileTotalSize=file.length();
		chunkPCMDataContainer= new ArrayList<byte[]>();
		initMediaDecode(isMix);//������
		
		if (encodeType == MediaFormat.MIMETYPE_AUDIO_AAC) {
//		    initAACMediaEncode();//AAC������
		}else if (encodeType == MediaFormat.MIMETYPE_AUDIO_MPEG) {
		    initMPEGMediaEncode();//mp3������
		}
		
	}
	
	/**
	* ��ʼ��������
	*/
	private void initMediaDecode(boolean isMix) {
		try {
		    mediaExtractor=new MediaExtractor();//����ɷ�����Ƶ�ļ����������Ƶ���
		    mediaExtractor.setDataSource(srcPath);//ý���ļ���λ��
		    for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {//����ý���� �˴����Ǵ��������Ƶ�ļ�������Ҳ��ֻ��һ�����
		        MediaFormat format = mediaExtractor.getTrackFormat(i);
		        String mime = format.getString(MediaFormat.KEY_MIME);
		        if (mime.startsWith("audio")) {//��ȡ��Ƶ���
		//            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 200 * 1024);
		        	mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
		        	channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
		        	duration = format.getLong(MediaFormat.KEY_DURATION);
		        	if(isMix){
			            mediaExtractor.selectTrack(i);//ѡ�����Ƶ���
			            mediaDecode = MediaCodec.createDecoderByType(mime);//����Decode������
			            mediaDecode.configure(format, null, null, 0);
		        	}
		            break;
		        }
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		if (mediaDecode == null) {
		    Log.e(TAG, "create mediaDecode failed");
		    return;
		}
		mediaDecode.start();//����MediaCodec ���ȴ���������
		decodeInputBuffers=mediaDecode.getInputBuffers();//MediaCodec�ڴ�ByteBuffer[]�л�ȡ��������
		decodeOutputBuffers=mediaDecode.getOutputBuffers();//MediaCodec�����������ݷŵ���ByteBuffer[]�� ���ǿ���ֱ����������õ�PCM����
		decodeBufferInfo=new MediaCodec.BufferInfo();//������������õ���byte[]���ݵ������Ϣ
//		showLog("buffers:" + decodeInputBuffers.length);
	}
	
	
	/**
	* ��ʼ��AAC������
	*/
//	private void initAACMediaEncode() {
//		try {
//		    MediaFormat encodeFormat = MediaFormat.createAudioFormat(encodeType, 44100, 2);//������Ӧ-> mime type�������ʡ�������
//		    encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);//������
//		    encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
//		    encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
//		    mediaEncode = MediaCodec.createEncoderByType(encodeType);
//		    mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
//		
//		if (mediaEncode == null) {
//		    Log.e(TAG, "create mediaEncode failed");
//		    return;
//		}
//		mediaEncode.start();
//		encodeInputBuffers=mediaEncode.getInputBuffers();
//		encodeOutputBuffers=mediaEncode.getOutputBuffers();
//		encodeBufferInfo=new MediaCodec.BufferInfo();
//	}
	
	/**
	* ��ʼ��MPEG������
	*/
	private void initMPEGMediaEncode() {
	
	}
	
	private boolean codeOver = false;
	/**
	* ��ʼת��
	* ��Ƶ����{@link #srcPath}�Ƚ����PCM  PCM�����ڱ������Ҫ�õ���{@link #encodeType}��Ƶ��ʽ
	* mp3->PCM->aac
	*/
	public void startAsync() {
		showLog("start");
		
		new Thread(new DecodeRunnable()).start();
//		new Thread(new EncodeRunnable()).start();
	
	}
	
	/**
	* ��PCM���ݴ���{@link #chunkPCMDataContainer}
	* @param pcmChunk PCM���ݿ�
	*/
	private void putPCMData(byte[] pcmChunk) {
		synchronized (AudioCodec.class) {//�ǵü���
		    chunkPCMDataContainer.add(pcmChunk);
		}
	}
	public ArrayList<byte[]> getPCMbyte(){
		return chunkPCMDataContainer;
	}
	
	/**
	* ��Container��{@link #chunkPCMDataContainer}ȡ��PCM����
	* @return PCM���ݿ�
	*/
	private byte[] getPCMData() {
		synchronized (AudioCodec.class) {//�ǵü���
		    showLog("getPCM:"+chunkPCMDataContainer.size());
		    if (chunkPCMDataContainer.isEmpty()) {
		        return null;
		    }
		
		    byte[] pcmChunk = chunkPCMDataContainer.get(0);//ÿ��ȡ��index 0 ������
		    chunkPCMDataContainer.remove(pcmChunk);//ȡ���󽫴�����remove�� ���ܱ�֤PCM���ݿ��ȡ��˳�� ���ܼ�ʱ�ͷ��ڴ�
		    return pcmChunk;
		}
	}
	
	
	/**
	* ����{@link #srcPath}��Ƶ�ļ� �õ�PCM���ݿ�
	* @return �Ƿ��������������
	*/
	private void srcAudioFormatToPCM() {
		for (int i = 0; i < decodeInputBuffers.length-1; i++) {
		int inputIndex = mediaDecode.dequeueInputBuffer(-1);//��ȡ���õ�inputBuffer -1����һֱ�ȴ���0��ʾ���ȴ� ����-1,���ⶪ֡
		if (inputIndex < 0) {
		    codeOver =true;
		    return;
		}
		
		ByteBuffer inputBuffer = decodeInputBuffers[inputIndex];//�õ�inputBuffer
		inputBuffer.clear();//���֮ǰ����inputBuffer�ڵ�����
		int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);//MediaExtractor��ȡ���ݵ�inputBuffer��
		if (sampleSize <0) {//С��0 �������������Ѷ�ȡ���
		        codeOver=true;
		    }else {
		        mediaDecode.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);//֪ͨMediaDecode����ոմ��������
		        mediaExtractor.advance();//MediaExtractor�ƶ�����һȡ����
		        decodeSize+=sampleSize;
		    }
		}
		
		//��ȡ����õ���byte[]���� ����BufferInfo�����ѽ��� 10000ͬ��Ϊ�ȴ�ʱ�� ͬ��-1����һֱ�ȴ���0�����ȴ����˴���λΪ΢��
		//�˴����鲻Ҫ��-1 ��Щʱ��û�������������ô���ͻ�һֱ������ �ȴ�
		int outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);
		
		//showLog("decodeOutIndex:" + outputIndex);
		ByteBuffer outputBuffer;
		byte[] chunkPCM;
		while (outputIndex >= 0) {//ÿ�ν�����ɵ����ݲ�һ����һ���³� ������whileѭ������֤�������³���������
		    outputBuffer = decodeOutputBuffers[outputIndex];//�õ����ڴ��PCM���ݵ�Buffer
		    chunkPCM = new byte[decodeBufferInfo.size];//BufferInfo�ڶ����˴����ݿ�Ĵ�С
		    outputBuffer.get(chunkPCM);//��Buffer�ڵ�����ȡ�����ֽ�������
		    outputBuffer.clear();//����ȡ����һ���ǵ���մ�Buffer MediaCodec��ѭ��ʹ����ЩBuffer�ģ�������´λ�õ�ͬ��������
		    putPCMData(chunkPCM);//�Լ�����ķ����������������ڵ��̻߳�ȡ����,�������������
		    mediaDecode.releaseOutputBuffer(outputIndex, false);//�˲���һ��Ҫ������ȻMediaCodec�������е�Buffer�� �����������������
		    outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);//�ٴλ�ȡ���ݣ����û�����������outputIndex=-1 ѭ������
		}
		int a;
		a=0;
	}
	
	/**
	* ����PCM���� �õ�{@link #encodeType}��ʽ����Ƶ�ļ��������浽{@link #dstPath}
	*/
//	private void dstAudioFormatFromPCM() {
//		
//		int inputIndex;
//		ByteBuffer inputBuffer;
//		int outputIndex;
//		ByteBuffer outputBuffer;
//		byte[] chunkAudio;
//		int outBitSize;
//		int outPacketSize;
//		byte[] chunkPCM;
//		
//		//showLog("doEncode");
//		for (int i = 0; i < encodeInputBuffers.length-1; i++) {
//		    chunkPCM=getPCMData();//��ȡ�����������߳���������� �����߻�����
//		    if (chunkPCM == null) {
//		        break;
//		    }
//		    inputIndex = mediaEncode.dequeueInputBuffer(-1);//ͬ������
//		    inputBuffer = encodeInputBuffers[inputIndex];//ͬ������
//		    inputBuffer.clear();//ͬ������
//		    inputBuffer.limit(chunkPCM.length);
//		    inputBuffer.put(chunkPCM);//PCM��������inputBuffer
//		    mediaEncode.queueInputBuffer(inputIndex, 0, chunkPCM.length, 0, 0);//֪ͨ������ ����
//		}
//	
//	    outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 10000);//ͬ������
//	    while (outputIndex >= 0) {//ͬ������
//	
//	        outBitSize=encodeBufferInfo.size;
//	        outPacketSize=outBitSize+7;//7ΪADTSͷ���Ĵ�С
//	        outputBuffer = encodeOutputBuffers[outputIndex];//�õ����Buffer
//	        outputBuffer.position(encodeBufferInfo.offset);
//	        outputBuffer.limit(encodeBufferInfo.offset + outBitSize);
//	        chunkAudio = new byte[outPacketSize];
//	        addADTStoPacket(chunkAudio,outPacketSize);//���ADTS ������������
//	        outputBuffer.get(chunkAudio, 7, outBitSize);//������õ���AAC���� ȡ����byte[]�� ƫ����offset=7 �㶮��
//	        outputBuffer.position(encodeBufferInfo.offset);
//	//        showLog("outPacketSize:" + outPacketSize + " encodeOutBufferRemain:" + outputBuffer.remaining());
//	        try {
//	            bos.write(chunkAudio,0,chunkAudio.length);//BufferOutputStream ���ļ����浽�ڴ濨�� *.aac
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	
//	        mediaEncode.releaseOutputBuffer(outputIndex,false);
//	        outputIndex = mediaEncode.dequeueOutputBuffer(encodeBufferInfo, 10000);
//	
//	    }
//	}
	
	/**
	* ���ADTSͷ
	* @param packet
	* @param packetLen
	*/
//	private void addADTStoPacket(byte[] packet, int packetLen) {
//		int profile = 2; // AAC LC
//		int freqIdx = 4; // 44.1KHz
//		int chanCfg = 2; // CPE
//		
//		
//		//fill in ADTS data
//		packet[0] = (byte) 0xFF;
//		packet[1] = (byte) 0xF9;
//		packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
//		packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
//		packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
//		packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
//		packet[6] = (byte) 0xFC;
//	}
	
	/**
	* �ͷ���Դ
	*/
	public void release() {
//		try {
//		    if (bos != null) {
//		        bos.flush();
//		    }
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}finally {
//		    if (bos != null) {
//		        try {
//		            bos.close();
//		        } catch (IOException e) {
//		            e.printStackTrace();
//		        }finally {
//		            bos=null;
//		        }
//		    }
//		}
//		
//		try {
//		    if (fos != null) {
//		        fos.close();
//		    }
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}finally {
//		    fos=null;
//		}
		
		if (mediaEncode != null) {
		    mediaEncode.stop();
		    mediaEncode.release();
		    mediaEncode=null;
		}
		
		if (mediaDecode != null) {
		    mediaDecode.stop();
		    mediaDecode.release();
		    mediaDecode=null;
		}
		
		if (mediaExtractor != null) {
		    mediaExtractor.release();
		    mediaExtractor=null;
		}
		
		if (onCompleteListener != null) {
		    onCompleteListener=null;
		}
		
		if (onProgressListener != null) {
		    onProgressListener=null;
		}
		showLog("release");
	}
	
	/**
	* �����߳�
	*/
	private class DecodeRunnable implements Runnable{
	
	@Override
		public void run() {
		    while (!codeOver) {
		        srcAudioFormatToPCM();
		    }
		    onCompleteListener.completed();
		}
	}
	
	/**
	* �����߳�
	*/
//	private class EncodeRunnable implements Runnable {
//	
//	@Override
//	public void run() {
//	    long t=System.currentTimeMillis();
//	    while (!codeOver || !chunkPCMDataContainer.isEmpty()) {
//	        dstAudioFormatFromPCM();
//	    }
//	    if (onCompleteListener != null) {
//	        onCompleteListener.completed();
//	    }
//	    showLog("size:"+fileTotalSize+" decodeSize:"+decodeSize+"time:"+(System.currentTimeMillis()-t));
//		}
//	}
	
	
	/**
	* ת����ɻص��ӿ�
	*/
	public interface OnCompleteListener{
		void completed();
	}
	
	/**
	* ת����ȼ�����
	*/
	public interface OnProgressListener{
		void progress();
	}
	
	/**
	* ����ת����ɼ�����
	* @param onCompleteListener
	*/
	public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
		this.onCompleteListener=onCompleteListener;
	}
	
	public void setOnProgressListener(OnProgressListener onProgressListener) {
		this.onProgressListener = onProgressListener;
	}
	
	private void showLog(String msg) {
		Log.e("AudioCodec", msg);
	}
}
