package innosiloco.demo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;

import com.sinaapp.bashell.VoAACEncoder;

import innosiloco.demo.mvp_view.SpeedActivity;

/**
 * 
 * @ClassName: AccRecord
 * @Description: 录制aac 格式录音文件
 * @author:ZCS
 * @date: 2015年11月13日 下午5:37:50
 */
public class AccRecord {
	static final String  PREFIX = "voice";
	static final String  EXTENSION = ".aac";
	private boolean      isRecording = false;
	private long 		 startTime;
	private String       voiceFilePath = null;
	private String       voiceFileName = null;
	private File         file;
	private Handler      handler;
	
	private AudioRecord 		 recordInstance;
	private FileOutputStream  fos;
	private int SAMPLERATE = 8000;

	public AccRecord(Handler paramHandler) {
		this.handler = paramHandler;
	}

	public String startRecording(String paramString1, String paramString2,Context paramContext) {
		this.voiceFileName=getVoiceFileName(paramString2);
		this.voiceFilePath = getVoiceFilePath();
		isRecording=true;
		new Thread(new Runnable() {
			public void run() {
				try {
					VoAACEncoder vo = new VoAACEncoder();
					vo.Init(SAMPLERATE, 16000, (short) 1, (short) 1);
					fos = new FileOutputStream(voiceFilePath);
					
					int min = AudioRecord.getMinBufferSize(SAMPLERATE,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
					min=min<2048?2048:min;
					byte[] tempBuffer = new byte[2048];
					recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLERATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, min);
					recordInstance.startRecording();
					
					while (isRecording) {
						int bufferRead = recordInstance.read(tempBuffer, 0, 2048);
						byte[] ret = vo.Enc(tempBuffer);
						if (bufferRead > 0) 
							fos.write(ret);
						
//						//通知录音界面进行更新
//						Message localMessage = new Message();
//						localMessage.what= SpeedActivity.AUDIO_STATUS;
//						localMessage.arg1 =(int) getVolumeMax(bufferRead, tempBuffer);
//						AccRecord.this.handler.sendMessage(localMessage);
						SystemClock.sleep(100L);
					}
					recordInstance.stop();
					recordInstance.release();
					recordInstance = null;
					vo.Uninit();
					fos.close();
				} catch (Exception localException) {
					RonLog.LogD("voice", localException.toString());
				}
			}
		}).start();
		this.startTime = new Date().getTime();
		return this.file == null ? null : this.file.getAbsolutePath();
	}
	
	//获取音量
	private double getVolumeMax(int r, byte[] bytes_pkg) {
        int mShortArrayLenght = r / 2;
        short[] short_buffer = byteArray2ShortArray(bytes_pkg, mShortArrayLenght);
        int max = 0;
        if (r > 0) {
            for (int i = 0; i < mShortArrayLenght; i++) {
                if (Math.abs(short_buffer[i]) > max) {
                    max = Math.abs(short_buffer[i]);
                }
            }
        }
        return Math.min(((double)max/32767)*13,13);
    }

    private short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

	public void discardRecording() {
		if (this.recordInstance != null) {
			try {
				this.recordInstance.stop();
				this.recordInstance.release();
				this.recordInstance = null;
				if ((this.file != null) && (this.file.exists())&& (!this.file.isDirectory())) {
					this.file.delete();
				}
			} catch (IllegalStateException localIllegalStateException) {
			} catch (RuntimeException localRuntimeException) {
			}
			this.isRecording = false;
		}
	}

	public int stopRecoding() {
		if (this.recordInstance != null) {
			this.isRecording = false;
			file=new File(voiceFilePath);
			if ((this.file == null) || (!this.file.exists())|| (!this.file.isFile())) 
				return 0;
			
			if (this.file.length() == 0) {
				this.file.delete();
				return 0;
			}
			int i = (int) (new Date().getTime() - this.startTime) / 1000;
			RonLog.LogD("voice", "voice recording finished. seconds:" + i+ " file length:" + this.file.length());
			return i;
		}
		return 0;
	}


	public String getVoiceFileName(String paramString) {
		Time localTime = new Time();
		localTime.setToNow();
		return paramString + localTime.toString().substring(0, 15) + ".aac";
	}

	public boolean isRecording() {
		return this.isRecording;
	}

	public String getVoiceFilePath() {
		return FileUtils.getVoicePath() + this.voiceFileName;
	}
}
