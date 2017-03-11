package innosiloco.demo.mvp_view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EventDownLine;
import innosiloco.demo.beans.FileBean;
import innosiloco.demo.beans.SecretKeyBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.TalkListBean;
import innosiloco.demo.mvp_view.iview.FileSelectActivity;
import innosiloco.demo.utils.AESKeyUitl;
import innosiloco.demo.utils.AccRecord;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.BitmapUtils;
import innosiloco.demo.utils.FileUtils;
import innosiloco.demo.utils.Mp3Util;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.TalkHelper;

import static android.R.id.message;

/**
 * Created by ron on 2017/2/25.
 */
public class SpeedActivity extends BaseActivity implements View.OnClickListener{

    private ListView listView;
    private Button sendMsgBtn;
    private TextView titleView;
    public static final String TalkFromID = "TalkFromID";

    public static final String TalkFromNick = "TalkFromNick";

    private List<TalkBean> talks;

    /********************
     * 发送者的ID号
     */
    private byte fromID;

    /*********************
     * 发送者的名字
     */
    private String fromNick;

    /****************
     * 客户端的Nick
     */
    private String myNick;
    public static final int REQUEST_PICTURE_LOCAL = 1;
    private final int REQUEST_FILE_LOCAL=3;
    public static final int  	AUDIO_STATUS=2;
    private Handler handler=new Handler() {
        public void handleMessage(android.os.Message msg) {

        }
    };
    /*****************
     * 编辑聊天内容
     */
    private EditText editText;


    private Mp3Util mp3Util;

    @Override
    public void findViews()
    {
        listView = (ListView) findViewById(R.id.list_talk);
        sendMsgBtn = (Button) findViewById(R.id.send_msg_btn);
        sendMsgBtn.setOnClickListener(this);
        findViewById(R.id.press_to_speak_btn).setOnTouchListener(new PressToSpeackTouchListener());
        editText = (EditText) findViewById(R.id.message_edt);
        titleView = (TextView)findViewById(R.id.tv_head_title);
    }

    @Override
    public void initViews()
    {
        mp3Util = new Mp3Util();
        talks = new ArrayList<>();
        listView.setAdapter(baseAdapter);
        fromID = getIntent().getByteExtra(TalkFromID,(byte)-1);
        fromNick = getIntent().getStringExtra(TalkFromNick);
        TalkListBean talkListBean = TalkHelper.getSingle().getOnceTalk(fromID);
        if(talkListBean != null && talkListBean.talks!= null)
        {
            talks.addAll(talkListBean.talks);
        }


        myNick = AppConfig.userNick;

        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).
                newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        initTitle(!TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()));
    }

    private void  notifyData()
    {
        baseAdapter.notifyDataSetChanged();
        listView.setSelection(baseAdapter.getCount() -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void exit()
    {
        boolean clearSuccess = TalkHelper.getSingle().clearTalk(fromID);
        RonLog.LogE("clearSucess:" + clearSuccess);
        Intent in =new Intent();
        in.putExtra("clearId",fromID);
        setResult(RESULT_OK,in);
        super.exit();
    }

    @Override
    public void initLisenter()
    {

    }

    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.select_picture:// 选择照片
                selectPicFromLocal();
                break;
            case R.id.send_file://发送文件
                selectFileFromLocal();
                break;
            case R.id.send_msg_btn:
                sendMsg();
                break;
        }
    }

    public void sendMsg()
    {
        if(TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
        {
            dialogCreatUtil.showSingleBtnDialog(null,getString(R.string.keyIsloss),this);
            return;
        }
                TalkBean talkBean = new TalkBean();
        talkBean.sendID = AppConfig.clientId;
        talkBean.toID = fromID;
        talkBean.talkContent = editText.getText().toString().trim();
        if(TextUtils.isEmpty(talkBean.talkContent))
        {
            talkBean.talkContent = " ";
        }
        MyApp.getSingleApp().mySocket.sendTalk(talkBean);
        TalkHelper.getSingle().addMySelfTalk(talkBean);

        talks.add(talkBean);

        notifyData();
        editText.setText("");
    }

    public void sendFileMsg(String path)
    {
        TalkBean talkBean = new TalkBean();
        talkBean.sendID = AppConfig.clientId;
        talkBean.toID = fromID;
        talkBean.talkContent=path;
        talkBean.fileType = FileUtils.fliePath2Type(path);
        MyApp.getSingleApp().mySocket.sendFileTalk(talkBean);
        TalkHelper.getSingle().addMySelfTalk(talkBean);
        talks.add(talkBean);
        notifyData();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_speed;
    }

    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return talks.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final TalkViewHolder talkViewHolder;
            if(convertView  == null )
            {
                convertView = LayoutInflater.from(SpeedActivity.this)
                        .inflate(R.layout.item_talk,null);
                talkViewHolder = new TalkViewHolder();
                convertView.setTag(talkViewHolder);
                talkViewHolder.linearLayout=(LinearLayout)convertView.findViewById(R.id.ll_talk_content);
                talkViewHolder.name = (TextView) convertView.findViewById(R.id.tv_talk_name);
                talkViewHolder.talk_content = (TextView) convertView.findViewById(R.id.tv_talk_content);
                talkViewHolder.headLeft = (ImageView)convertView.findViewById(R.id.img_talk_head_left);
                talkViewHolder.headRight = (ImageView)convertView.findViewById(R.id.img_talk_head_right);
                talkViewHolder.chat_voice = (ImageView)convertView.findViewById(R.id.chat_voice);
                talkViewHolder.bg = convertView.findViewById(R.id.ll_bg);
                talkViewHolder.imgTalk = (ImageView) convertView.findViewById(R.id.iv_img);
            }else
            {
                talkViewHolder = (TalkViewHolder) convertView.getTag();
            }
            final TalkBean talkBean = talks.get(position);
            if( talkBean.toID == fromID )
            {
                talkViewHolder.name.setText(myNick);
                talkViewHolder.linearLayout.setGravity(Gravity.RIGHT);
                talkViewHolder.talk_content.setText(talkBean.talkContent);
                talkViewHolder.bg.setBackgroundResource(R.drawable.bg_talk_content_right);
                talkViewHolder.headLeft.setVisibility(View.INVISIBLE);
                talkViewHolder.headRight.setVisibility(View.VISIBLE);


            }else
            {
                talkViewHolder.name.setText(fromNick);
                talkViewHolder.linearLayout.setGravity(Gravity.LEFT);
                talkViewHolder.talk_content.setText(talkBean.talkContent);
                talkViewHolder.bg.setBackgroundResource(R.drawable.bg_talk_content_left);
                talkViewHolder.headLeft.setVisibility(View.VISIBLE);
                talkViewHolder.headRight.setVisibility(View.INVISIBLE);
            }
            talkViewHolder.talk_content.setVisibility(View.GONE);
            talkViewHolder.imgTalk.setVisibility(View.GONE);
            talkViewHolder.chat_voice.setVisibility(View.GONE);

            switch (talkBean.fileType)
            {
                case FileBean.isAAC:
                    talkViewHolder.chat_voice.setVisibility(View.VISIBLE);
                    break;
                case FileBean.isJPE:
                case FileBean.isPNG:
                    talkViewHolder.imgTalk.setVisibility(View.VISIBLE);
                    if(TextUtils.isEmpty(talkBean.talkContent))
                    {
                        talkViewHolder.imgTalk.setImageResource(R.drawable.dud);
                    }else
                    {
                        Bitmap bmp = BitmapFactory.decodeFile(talkBean.talkContent);
                        talkViewHolder.imgTalk.setImageBitmap(bmp);
                    }

                    break;
                default:
                    talkViewHolder.talk_content.setVisibility(View.VISIBLE);
                    break;
            }

            talkViewHolder.imgTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SpeedActivity.this,ImageActivity.class);
                    intent.putExtra(ImageActivity.PATH,talkBean.talkContent);
                    startActivity(intent);
                }
            });

            talkViewHolder.chat_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return convertView;
        }

    };

    /*******************
     * 有新的聊天记录
     * @param talkBean
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void talkUpdate(TalkBean talkBean)
    {
        RonLog.LogE("聊天记录:" + talkBean.sendID + "," + talkBean.talkContent);
        if(talkBean != null && talkBean.sendID == fromID)
        {
            talks.add(talkBean);
        }
       notifyData();
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void userOnline(EventDownLine eventDownLine)
    {
        RonLog.LogE("收到下线通知：" + eventDownLine.clientId + "," + eventDownLine.isDownLine);
        if(!AppConfig.isServce)
        {
//            if(eventDownLine.isDownLine && eventDownLine.clientId == AppConfig.clientId )
            {//用户自己的客户端
                if(dialogCreatUtil != null )
                {
                    dialogCreatUtil.showSingleBtnDialog(null,"连接服务器失败",SpeedActivity.this);
                }
            }
        }
    }

    private static class TalkViewHolder
    {
        TextView name;
        LinearLayout linearLayout;
        TextView talk_content;
        ImageView headLeft;
        ImageView headRight;
        ImageView chat_voice;
        View bg;
        ImageView imgTalk;
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void secretKeyChange(SecretKeyBean secretKeyBean)
    {
       initTitle(secretKeyBean.secretKeyIsOnLine);
    }

    private void initTitle(boolean secretKeyIsOnLine)
    {
        if(!secretKeyIsOnLine)
        {
            titleView.setText(R.string.keyIsloss);
            titleView.setTextColor(Color.RED);
        }else
        {
            setTitle("talk with:" + fromNick);
            titleView.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_PICTURE_LOCAL);
    }
    /**
     * 选择文件
     */
    private void selectFileFromLocal() {
        Intent intent = null;
        try{
            intent=new Intent(this,FileSelectActivity.class);
            startActivityForResult(intent,REQUEST_FILE_LOCAL);
        }catch(Exception e){
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }
    private PowerManager.WakeLock wakeLock;
    /**
     * 按住说活
     */
    private class PressToSpeackTouchListener implements View.OnTouchListener {
        private  long         startTime=System.currentTimeMillis();
        private  boolean      isHavePrivate=true;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action=event.getAction() & MotionEvent.ACTION_MASK;
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_UP:
                    return false;
                case MotionEvent.ACTION_DOWN:
                    isHavePrivate=true;
                    mp3Util.recorderErr();
                    if(System.currentTimeMillis()-startTime<800){
                        startTime=System.currentTimeMillis();
                        return false;
                    }
                    startTime=System.currentTimeMillis();
                    if (!FileUtils.isExitsSdcard()) {
                        String str = getResources().getString(R.string.sd_not_exist);
                        Toast.makeText(SpeedActivity.this, str, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        view.setPressed(true);
                        wakeLock.acquire();
//                        if (PlayVoice.isPlaying)
//                            PlayVoice.currentPlayListener.stopPlayVoice();
                        mp3Util.beginRecorder();
                    } catch (Exception e) {
                        e.printStackTrace();
                        view.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        mp3Util.recorderErr();
                        Toast.makeText(SpeedActivity.this, R.string.recoding_fail,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    if(isHavePrivate==false)
                        return false;
                    if(System.currentTimeMillis()-startTime<300){
                        startTime=System.currentTimeMillis();
                        mp3Util.recorderErr();
                        view.setPressed(false);
                        return false;
                    }
                    startTime=System.currentTimeMillis();
                    view.setPressed(false);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        startTime=0;
                        mp3Util.recorderErr();
                    }else {
                        String tooShort = getResources().getString(R.string.The_recording_time_is_too_short);
                        try {
                            startTime=0;
                            int length = (int)mp3Util.stopRecorder();
                            if (length > 0) {
                                sendVoice(mp3Util.getSavaPath(),length);
                            } else if (length ==0) {
                                Toast.makeText(getApplicationContext(), tooShort,Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), tooShort,Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SpeedActivity.this, tooShort,Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    if (mp3Util != null)
                         mp3Util.recorderErr();
                    return false;
            }
        }
    }
    /**
     * 发送音频消息
     */
    private void sendVoice(String filePath,int voiceLength){
        TalkBean talkBean = new TalkBean();
        talkBean.sendID = AppConfig.clientId;
        talkBean.toID = fromID;
        talkBean.talkContent=filePath;
        MyApp.getSingleApp().mySocket.sendFileTalk(talkBean);
        TalkHelper.getSingle().addMySelfTalk(talkBean);
        talks.add(talkBean);
        notifyData();
    }

    /**
     * 处理Activity 返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICTURE_LOCAL:
                if (data == null)
                    break;
                Uri imageUri = data.getData();
                String path = BitmapUtils.getAbsolutePath(imageUri,this);
                sendFileMsg(path);
        }
    }
}
