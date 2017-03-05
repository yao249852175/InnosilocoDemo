package innosiloco.demo.mvp_view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EventDownLine;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.TalkListBean;
import innosiloco.demo.utils.AESKeyUitl;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.TalkHelper;

/**
 * Created by ron on 2017/2/25.
 */
public class SpeedActivity extends BaseActivity {

    private ListView listView;
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

    /*****************
     * 编辑聊天内容
     */
    private EditText editText;
    @Override
    public void findViews()
    {
        listView = (ListView) findViewById(R.id.list_talk);
        editText = (EditText) findViewById(R.id.edit_talk);
    }

    @Override
    public void initViews()
    {   talks = new ArrayList<>();
        listView.setAdapter(baseAdapter);
        fromID = getIntent().getByteExtra(TalkFromID,(byte)-1);
        fromNick = getIntent().getStringExtra(TalkFromNick);
        TalkListBean talkListBean = TalkHelper.getSingle().getOnceTalk(fromID);
        if(talkListBean != null && talkListBean.talks!= null)
        {
            talks.addAll(talkListBean.talks);
        }


        myNick = AppConfig.userNick;
        setTitle("talk with:" + fromNick);

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
        if(true)
        {
            sendFileMsg();
            return;
        }
        if(TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
        {
            dialogCreatUtil.showSingleBtnDialog(null,getString(R.string.keyIsloss),this);
            return;
        }



        view.setEnabled(false);
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
        view.setEnabled(true);
        editText.setText("");
    }

    public void sendFileMsg()
    {
        TalkBean talkBean = new TalkBean();
        talkBean.sendID = AppConfig.clientId;
        talkBean.toID = fromID;
        talkBean.talkContent="/sdcard/plane1.png";
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
            TalkViewHolder talkViewHolder;
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
            }else
            {
                talkViewHolder = (TalkViewHolder) convertView.getTag();
            }
            TalkBean talkBean = talks.get(position);
            if( talkBean.toID == fromID )
            {
                talkViewHolder.name.setText(myNick);
                talkViewHolder.linearLayout.setGravity(Gravity.RIGHT);
                talkViewHolder.talk_content.setText(talkBean.talkContent);
                talkViewHolder.talk_content.setBackgroundResource(R.drawable.bg_talk_content_right);
                talkViewHolder.headLeft.setVisibility(View.INVISIBLE);
                talkViewHolder.headRight.setVisibility(View.VISIBLE);
            }else
            {
                talkViewHolder.name.setText(fromNick);
                talkViewHolder.linearLayout.setGravity(Gravity.LEFT);
                talkViewHolder.talk_content.setText(talkBean.talkContent);
                talkViewHolder.talk_content.setBackgroundResource(R.drawable.bg_talk_content_left);
                talkViewHolder.headLeft.setVisibility(View.VISIBLE);
                talkViewHolder.headRight.setVisibility(View.INVISIBLE);
            }
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
