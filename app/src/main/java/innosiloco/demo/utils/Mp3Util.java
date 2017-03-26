package innosiloco.demo.utils;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/11.
 */
public class Mp3Util
{
    private String savaPath;

    private MediaRecorder mRecorder;

    private MediaPlayer mPlayer;
    private ImageView img_anim;

    private AnimationDrawable anim;

    public Mp3Util(ImageView img_anim)
    {
        this.img_anim = img_anim;
    }

    private void beginAnim()
    {
        if(img_anim  == null ) return;
        img_anim.setVisibility(View.VISIBLE);
        anim = (AnimationDrawable) img_anim.getBackground();
        anim.start();

    }


    private void stopAnim()
    {
        if(anim != null )
        {
            anim.stop();
            anim = null;
            img_anim.setVisibility(View.GONE);
        }
    }

    /***********
     * 开始录像
     */
    public void beginRecorder()
    {
        createSavePath();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setMaxDuration(600000);

//设置最大录制的大小 单位，字节
        mRecorder.setMaxFileSize(90*1024);
        mRecorder.setOutputFile(savaPath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            RonLog.LogE("prepare() failed");
        }
        mRecorder.start();
        beginAnim();
    }

    /***********
     * 获取刚才录制音频的地址
     * @return
     */
    public String getSavaPath()
    {
        return savaPath;
    }

    /*************
     * 停止录音，并反馈录音的保存地址
     * @return 录制的文件大小
     */
    public long stopRecorder()
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        File file = new File(savaPath);
        stopAnim();
        if(file.exists())
            return file.length();
        return 0;
    }

    /**************
     * 录制失败
     */
    public void recorderErr()
    {
        if(mRecorder != null)
        {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        if(!TextUtils.isEmpty(savaPath))
        {
            File file = new File(savaPath);
            if(file.exists())
                file.delete();
        }
        stopAnim();
    }

    private AnimationDrawable playAnim;

    private void playAnimBegin(ImageView imageView)
    {
        if(playAnim != null)
        {
            playAnim.stop();
        }
        playAnim = (AnimationDrawable) imageView.getDrawable();
        playAnim.start();
    }

    private void stopPlayAnim()
    {
        if(playAnim != null )
        {
            playAnim.stop();
            playAnim = null;
        }
    }

    /*********
     * 播放mp3的文件
     * @param path
     */
    public void playMp3(String path,ImageView imageView)
    {
        if(mPlayer == null )
        {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            try{
                mPlayer.setDataSource(path);
                mPlayer.prepare();
                mPlayer.start();
                mPlayer.setVolume(1f,1f);
            }catch(IOException e){
                RonLog.LogE("播放失败");
            }
        }else
        {
            mPlayer.reset();
            try {
                mPlayer.setDataSource(path);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        playAnimBegin(imageView);
        mPlayer.setOnCompletionListener(onCompletionListener);
    }

    /*************
     * 监听完成，播放完成
     */
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(mPlayer != null )
            {
                stopMp3();
            }
            RonLog.LogD("播放完成");
            stopPlayAnim();
        }
    };

    public void stopMp3()
    {
        if(mPlayer != null )
        {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

    }

    /****************
     * 录音的时候，保存录音的地址
     */
    private void  createSavePath()
    {
        savaPath = AppConfig.BaseDirectory + System.currentTimeMillis() + ".mp3";
    }
}
