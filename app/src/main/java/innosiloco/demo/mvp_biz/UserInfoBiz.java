package innosiloco.demo.mvp_biz;

import innosiloco.demo.MyApp;
import innosiloco.demo.beans.UserBean;

/**
 * Created by Administrator on 2017/2/26.
 */
public class UserInfoBiz
{
    /***************
     * <p>设置用户的信息</p>
     * @param userInfo
     */
    public void setUserInfo(UserBean userInfo)
    {
        MyApp.getSingleApp().mySocket.updateUser(userInfo);
    }
}
