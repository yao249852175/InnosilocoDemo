package innosiloco.demo.utils;

import java.util.ArrayList;
import java.util.List;

import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.beans.TalkListBean;

/**
 * Created by ron on 2017/2/27.
 * <p>聊天记录的帮助类</p>
 */
public class TalkHelper
{

    public volatile List<TalkListBean> data;

    private static TalkHelper talkHelper;

    private TalkHelper()
    {
        data = new ArrayList<>();
    }

    /*******************
     * <p>单例模式</p>
     * @return
     */
    public static TalkHelper getSingle()
    {
        if(talkHelper == null )
        {
            talkHelper = new TalkHelper();
        }
        return talkHelper;
    }

    /***************************
     * 添加一条聊天记录 对方发送的聊天记录
     * @param talkBean
     */
    public void addTalk(TalkBean talkBean)
    {
        for (TalkListBean bean:data)
        {
            if(talkBean.sendID == bean.friendID)
            {
                bean.addTalk(talkBean);
                return;
            }
        }

        TalkListBean talkListBean = new TalkListBean(talkBean.toID,talkBean.sendID);
        talkListBean.addTalk(talkBean);
        data.add(talkListBean);
    }

    /***************************
     * 添加一条聊天记录 (当前用户的聊天)
     * @param talkBean
     */
    public void addMySelfTalk(TalkBean talkBean)
    {
        for (TalkListBean bean:data)
        {
            if(talkBean.toID == bean.friendID)
            {
                bean.addTalk(talkBean);
                return;
            }
        }

        TalkListBean talkListBean = new TalkListBean(talkBean.sendID,talkBean.toID);
        talkListBean.addTalk(talkBean);
        data.add(talkListBean);
    }

    /*************************************
     * 获取指定 好友的最后一条记录
     * @param fromId
     * @return
     */
    public TalkBean getLastTalk(byte fromId)
    {
        for (TalkListBean talkListBean:data)
        {
            if(talkListBean.friendID == fromId)
            {
                if(talkListBean.talks.size()> 0 )
                {
                    return talkListBean.talks.get(talkListBean.talks.size() -1);
                }else
                    return null;
            }
        }
        return null;
    }

    /*****************
     * 清理掉一次聊天记录
     * @param fromId
     */
    public boolean clearTalk(byte fromId)
    {
        for (TalkListBean talkListBean:data)
        {
            if(talkListBean.friendID == fromId)
            {
                data.remove(talkListBean);
                return true;
            }
        }
        return  false;
    }

    /****************
     * 获取一次所有的聊天记录
     * @param fromId
     * @return
     */
    public TalkListBean getOnceTalk(byte fromId)
    {
        for (TalkListBean talkListBean:data)
        {
            if(talkListBean.friendID == fromId)
            {

                return talkListBean;
            }
        }

        return null;
    }

}
