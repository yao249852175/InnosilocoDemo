package innosiloco.demo.mvp_view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.MyApp;
import innosiloco.demo.R;
import innosiloco.demo.beans.EventDownLine;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.service.ParseDataHelper;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;
import innosiloco.demo.utils.TalkHelper;

public class FriendsActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    /*************
     * 查看聊天记录
     */
    public final int LookTalks = 1;

    private ListView listView;
    @Override
    public void findViews() {
        listView = (ListView) findViewById(R.id.list_friends);
    }

    @Override
    public void initViews()
    {
        listView.setAdapter(friendListAdapter);
        setTitle("在线列表");
    }

    @Override
    public void initLisenter() {
        listView.setOnItemClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_friends;
    }


    private BaseAdapter friendListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return ParseDataHelper.onlineUser.size();
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
            FriendViewHolder friendViewHolder = null;
            UserBean userBean = ParseDataHelper.onlineUser.get(position);
            if(convertView == null )
            {
                convertView = LayoutInflater.from(FriendsActivity.this).inflate(R.layout.item_friend,null);
                friendViewHolder = new FriendViewHolder();
                friendViewHolder.head = (ImageView)convertView.findViewById(R.id.img_friend_head);
                friendViewHolder.name = (TextView) convertView.findViewById(R.id.tv_friend_name);
                friendViewHolder.speedNum = (TextView) convertView.findViewById(R.id.tv_friend_speedNum);
                friendViewHolder.lastSpeed = (TextView) convertView.findViewById(R.id.tv_friend_lastSpeed);
                convertView.setTag(friendViewHolder);
            }else
            {
                friendViewHolder = (FriendViewHolder) convertView.getTag();
            }
            friendViewHolder.name.setText(userBean.userNike);
            TalkBean talkBean = TalkHelper.getSingle().getLastTalk(userBean.userID);
            if(talkBean != null )
            {
                RonLog.LogE("lastContent:"+ talkBean.talkContent);
                friendViewHolder.speedNum.setVisibility(View.VISIBLE);
                friendViewHolder.lastSpeed.setText(talkBean.talkContent);
                friendViewHolder.speedNum.setText(""+TalkHelper.getSingle().getOnceTalk(userBean.userID).talks.size());
            }else
            {
                friendViewHolder.lastSpeed.setText("");
                friendViewHolder.speedNum.setVisibility(View.GONE);
            }

            return convertView;
        }
    };

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setUserInfoOver(EventFriendListUpdate eventFriendListUpdate)
    {
        /*for (UserBean userBean: ParseDataHelper.onlineUser)
        {
            if(userBean.clientIp.equals(AppConfig.clientIp))
            {
                AppConfig.clientId = userBean.userID;
            }
        }*/
        friendListAdapter.notifyDataSetChanged();

    }

    /*******************
     * 有新的聊天记录
     * @param talkBean
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void talkUpdate(TalkBean talkBean)
    {

        friendListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(ParseDataHelper.onlineUser.get(position).userID == AppConfig.clientId)
        {
            Toast.makeText(this,"不能自己和自己聊天",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,SpeedActivity.class);
        intent.putExtra(SpeedActivity.TalkFromNick,ParseDataHelper.onlineUser.get(position).userNike);
        intent.putExtra(SpeedActivity.TalkFromID,ParseDataHelper.onlineUser.get(position).userID);
        startActivityForResult(intent,LookTalks);
    }

    @Override
    protected void onResume() {
        super.onResume();
        friendListAdapter.notifyDataSetChanged();
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    friendListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( LookTalks == requestCode  && resultCode == RESULT_OK )
        {
            byte id = data.getByteExtra("clearId",(byte)-1);
            RonLog.LogE("清理的ID号："+ id);
            boolean clearSuccess = TalkHelper.getSingle().clearTalk(id);
            try
            {
                RonLog.LogE(TalkHelper.getSingle().getOnceTalk(id).talks.size()+",------------");
            }catch (Exception exception)
            {
                exception.printStackTrace();
            }
            friendListAdapter.notifyDataSetChanged();

        }
    }




    @Subscribe(threadMode = ThreadMode.MainThread)
    public void userOnline(EventDownLine eventDownLine)
    {
        RonLog.LogE("列表页收到下线通知");
        if(!AppConfig.isServce)
        {
//            if(eventDownLine.clientId == AppConfig.clientId)
//            {//用户自己的客户端
                if(dialogCreatUtil != null )
                {
                    dialogCreatUtil.showSingleBtnDialog("","连接服务器失败",FriendsActivity.this);
                }
//            }
        }
    }

    private class FriendViewHolder
      {
          ImageView head;
          TextView name;
          TextView speedNum;
          TextView lastSpeed;
      }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.getSingleApp().mySocket.stop();
        MyApp.getSingleApp().mySocket = null;
    }
}
