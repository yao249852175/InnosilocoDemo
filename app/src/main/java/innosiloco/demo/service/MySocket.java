package innosiloco.demo.service;

import java.security.Key;

import innosiloco.demo.beans.ICallback;
import innosiloco.demo.beans.KeyBean;
import innosiloco.demo.beans.QuestionBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.UserBean;

/**
 * Created by Administrator on 2017/2/26.
 */
public interface MySocket
{
    public void start(ICallback iCallback);

    public void stop();

    /*************
     * <p>上传重新设置的用户信息</p>
     * @param bean
     */
    public void updateUser(UserBean bean);

    /**********************
     * 发送Talk到服务器
     * @param talkBean
     */
    public void sendTalk(TalkBean talkBean);

    public void sendFileTalk(TalkBean talkBean);

    public void sendKey2ServerCheckKey(KeyBean keyBean);

    public void sendCheckKeyResult(KeyBean keyBean);

    public void sendQuestion(QuestionBean questionBean);

}
