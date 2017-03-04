package innosiloco.demo.beans;

/**
 * Created by ron on 2017/2/26.
 * [F0,01(Send2ID),01(CODEIDNEx),content,CRC8,END]
 */
public class FrameBean
{

    public byte send2ID;

    public byte cmdIndex;

    public byte[] content;

    public byte  crc8;
}
