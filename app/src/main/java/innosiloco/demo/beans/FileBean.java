package innosiloco.demo.beans;

/**
 * Created by Administrator on 2017/3/4.
 */
public class FileBean
{
    public FileBean(byte type,byte from,byte to)
    {
        this.type = type;
        this.fromID = from;
        this.send2ID = to;
    }

    /************
     * byte[]数据类型
     */
    public static final byte DataType = 0x01;

    /*****************
     * 文件数据类型
     */
    public static final byte FileType = 0x03;


    public static final byte isMp3 = 0x04;

    public static final byte isJPE = 0x05;

    public static final byte isPNG = 0x06;

    public static final byte isAAC = 0x07;

    /****************
     * 当前的数据类型
     */
    public byte type;

    /*************
     * 发给谁
     */
    public byte send2ID;

    /********
     * 谁发的
     */
    public byte fromID;

    public byte[] data;

    public String filePath;
}
