package innosiloco.demo.utils;

import java.util.List;

/**
 * <ul>
 * 	 <li>byte与 int,short之间的转化的工具类</li>
 *   <li>数组排序 都是 高位在前</li>
 * </ul> 
 */
public class ByteUtils {

	/**
	 * <p>List 转化为 数组</p>
	 * @param list  in
	 * @param bytes out
	 */
	public static void bytes2List(List <Byte>list,byte[] bytes )
	{
		for (int i = 0; i < bytes.length; i++) {
			list.add(bytes[i]);
		}
	}
	
	/**
	 * <p>byte数组转化为int</p>
	 * @param source byte数组
	 * @param  begin
	 * @return
	 */
	public static int byte2Int(byte[] source,int begin) {
	    int iOutcome = 0;
	    iOutcome |= ( (source[begin++] & 0xFF) << 24);
	    iOutcome |= ( (source[begin++] & 0xFF) << 16);
	    iOutcome |= ( (source[begin++] & 0xFF) << 8);
	    iOutcome |= (source[begin++] & 0xFF);
	    return iOutcome;
	}
	
	/**
	 * <p>byte数组转化为long</p>
	 * @param source byte数组
	 * @return
	 */
	public static long byte2long(byte[] source,int begin) {
	    long iOutcome = 0;
	    iOutcome |= ( (source[begin++] & 0xFF) << 56);
	    iOutcome |= ( (source[begin++] & 0xFF) << 48);
	    iOutcome |= ( (source[begin++] & 0xFF) << 40);
	    iOutcome |= ( (source[begin++] & 0xFF) << 32);
	    iOutcome |= ( (source[begin++] & 0xFF) << 24);
	    iOutcome |= ( (source[begin++] & 0xFF) << 16);
	    iOutcome |= ( (source[begin++] & 0xFF) << 8);
	    iOutcome |= (source[begin++] & 0xFF);
	    return iOutcome;
	}
	
	/**
	 * <p>int转化为byte</p>
	 * @param source
	 * @return
	 */
	public static byte[] int2byte(int source)
	{
		byte[] bLocalArr = new byte[4];
		long lSource = source & 0xFFFFFFFFL;
	    int i = 0;
	    bLocalArr[i++] = (byte)((lSource >> 24) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 16) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 8) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 0) & 0xFF);
	    return bLocalArr;
	}
	
	/**
	 * <p>int转化为byte</p>
	 * @param source
	 * @return
	 */
	public static byte[] long2byte(long source)
	{
		byte[] bLocalArr = new byte[8];
		long lSource = source & 0xFFFFFFFFL;
	    int i = 0;
	    bLocalArr[i++] = (byte)((lSource >> 56) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 48) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 40) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 32) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 24) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 16) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 8) & 0xFF);
	    bLocalArr[i++] = (byte)((lSource >> 0) & 0xFF);
	    return bLocalArr;
	}
	
	/**
	 * <p>short转化为字节数组</p>
	 * @param source
	 * @return
	 */
	public static byte[] short2byte(short source)
	{
		int iSource = source & 0xFFFF;
		byte[] bb = new byte[2];
		bb[0] = (byte)((iSource >> 8) & 0xFF);
		bb[1] =(byte)((iSource >> 0) & 0xFF);
		return bb;
	} 
	
	/**
	 * <p>byte数组 转化为 short</p>
	 * @param bb
	 * @param index
	 * @return
	 */
	public static short byte2short(byte[] bb,int index)
	{
		short sOutcome = 0;
		sOutcome |= ( (bb[index++] & 0xFF) << 8);
		sOutcome |= ( (bb[index++] & 0xFF) << 0);
		
		return sOutcome;
	}
	
	/**
	 * <h3>把String转化为byte类型</h3>
	 * <ul>
	 *   <li>由于str.getBytes 不包含结尾符号0</li>
	 * </ul> 
	 * @param str
	 * @return
	 */
	public static byte[] Str2Byte(String str)
	{
		byte[] bb = str.getBytes();
		byte [] ids = new byte[bb.length + 1];
		System.arraycopy(bb, 0, ids, 0, bb.length);
		return ids;
	}
}
