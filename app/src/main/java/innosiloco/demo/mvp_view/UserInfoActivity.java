package innosiloco.demo.mvp_view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.R;
import innosiloco.demo.beans.EventFriendListUpdate;
import innosiloco.demo.beans.UserBean;
import innosiloco.demo.dao.MyPreference;
import innosiloco.demo.mvp_presenter.UserInfoPresenter;
import innosiloco.demo.mvp_view.iview.IUserInfo;
import innosiloco.demo.service.ParseDataHelper;
import innosiloco.demo.utils.AppConfig;
import innosiloco.demo.utils.RonLog;

/**
 * Created by ron on 2017/2/26.
 */
public class UserInfoActivity extends BaseActivity implements IUserInfo
{
    private EditText editText;
    private View loading;

    private UserInfoPresenter userInfoPresenter;
    @Override
    public void findViews() {

        editText = (EditText) findViewById(R.id.edit_userinfo_nick);
        loading = findViewById(R.id.ll_loading);
    }

    @Override
    public void initViews() {
        setTitle("设置");
        userInfoPresenter = new UserInfoPresenter(this);
    }




    @Override
    public void initLisenter() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_userinfo;
    }


    public void onClick(View view)
    {
        String nick = editText.getText().toString().trim();
        if(TextUtils.isEmpty(nick))
        {
            Toast.makeText(this,"请输入昵称",Toast.LENGTH_SHORT).show();
            return;
        }
        MyPreference myPreference = new MyPreference(this);
        myPreference.saveStringValue(myPreference.Label_UserNick,nick);
        UserBean userBean = new UserBean();
        userBean.clientIp = AppConfig.clientIp;
        userBean.userNike = nick;
        userBean.userID = AppConfig.clientId;
        userInfoPresenter.setUserInfo(userBean);
    }



    @Override
    public void sendUserInfo2Server()
    {
        loading.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void setUserInfoOver(EventFriendListUpdate eventFriendListUpdate)
    {
        for (UserBean userBean: ParseDataHelper.onlineUser)
        {
            if(userBean.clientIp.equals(AppConfig.clientIp))
            {
                AppConfig.clientId = userBean.userID;
            }
        }
        RonLog.LogE("收到在想通知" + AppConfig.clientId);
        setUserInfoOver();
    }

    @Override
    public void setUserInfoOver()
    {

        Intent intent = new Intent(this,FriendsActivity.class);
        startActivity(intent);
        exit();
    }
}
