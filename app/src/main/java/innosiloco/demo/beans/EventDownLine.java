package innosiloco.demo.beans;

/**
 * Created by ron on 2017/2/26.
 * 下线
 */
public class EventDownLine
{
    /**********
     * ture:下线
     * false:上线
     */
    public boolean isDownLine;

    public byte clientId;

    public EventDownLine(byte clientId)
    {
        isDownLine = true;
        this.clientId = clientId;
    }

    public EventDownLine(boolean isDownLine,byte clientId)
    {
        this.isDownLine = isDownLine;
        this.clientId = clientId;
    }

}
