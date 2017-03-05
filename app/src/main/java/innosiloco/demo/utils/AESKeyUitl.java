package innosiloco.demo.utils;

/**
 * Created by Administrator on 2017/3/1.
 */
public class AESKeyUitl
{
    private static AESKeyUitl aesKeyUitl;

    public final int myvid=1155,mypid=22336;

    private String decode_key = "ron";

    private String encode_key = "ron";

    public String getDecode_key() {
        return decode_key;
    }

    public void setDecode_key(String decode_key) {
        this.decode_key = decode_key;
    }

    public String getEncode_key() {
        return encode_key;
    }

    public void setEncode_key(String encode_key) {
        this.encode_key = encode_key;
    }



    private AESKeyUitl()
    {

    }

    public static AESKeyUitl getSingleton()
    {
        if(aesKeyUitl == null )
        {
            aesKeyUitl = new AESKeyUitl();
        }
        return aesKeyUitl;
    }


}
