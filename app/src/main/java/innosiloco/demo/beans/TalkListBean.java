package innosiloco.demo.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ron on 2017/2/27.
 * 针对冒个朋友的聊天记录
 */
public class TalkListBean
{
    /*************
     * 发送端
     */
    public byte meID;

    /*************
     * 接收端
     */
    public byte friendID;

    /**********
     * 聊天记录
     */
    public List<TalkBean> talks;

    public TalkListBean(byte meID,byte friendID)
    {
        this.meID = meID;
        this.friendID = friendID;
        talks = new ArrayList<>();
    }

    /*****************
     * 添加聊天记录
     * @param talkBean
     */
    public void addTalk(TalkBean talkBean)
    {
        if(talkBean != null)
        {
            talks.add(talkBean);
        }
    }
}
