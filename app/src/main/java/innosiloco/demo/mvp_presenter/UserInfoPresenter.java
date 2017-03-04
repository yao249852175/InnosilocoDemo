package innosiloco.demo.mvp_presenter;

import innosiloco.demo.beans.UserBean;
import innosiloco.demo.mvp_biz.UserInfoBiz;
import innosiloco.demo.mvp_view.iview.IUserInfo;

/**
 * Created by Administrator on 2017/2/26.
 */
public class UserInfoPresenter
{
    private IUserInfo iUserInfo;

    private UserInfoBiz userInfoBiz;

    public UserInfoPresenter(IUserInfo iUserInfo)
    {
        this.iUserInfo = iUserInfo;
        this.userInfoBiz = new UserInfoBiz();
    }

    public void setUserInfo(UserBean userInfo)
    {
        this.userInfoBiz.setUserInfo(userInfo);
        this.iUserInfo.sendUserInfo2Server();
    }
}
