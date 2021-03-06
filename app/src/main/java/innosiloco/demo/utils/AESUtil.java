package innosiloco.demo.utils;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/2/28.
 */
public class AESUtil
{
    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed);
        RonLog.LogE("rawKey:"+ rawKey.length);
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

  /*  public static byte[] encryptArgByte(byte[] seed, byte[] cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed);
        RonLog.LogE("rawKey:"+ rawKey.length);
        byte[] result = encrypt(rawKey,cleartext);
        return result;
    }*/

 /*   public static byte[] decryptArgByte(byte[] seed, byte[] encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed);
        RonLog.LogE("rawKey:"+ rawKey.length);
        byte[] result = decrypt(rawKey, encrypted);
        return result;
    }*/

    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed);
        RonLog.LogE("rawKey:"+ rawKey.length);
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(String password) throws Exception
    {
        /* User types in their password: */

   /* Store these things on disk used to derive key later: */
        int iterationCount = 1000;
        int saltLength = 32; // bytes; should be the same size as the output (256 / 8 = 32)
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc
      //  byte[] salt; // Should be of saltLength

   /* When first creating the key, obtain a salt with this: */
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);
   /* Use this to derive the key from the password: */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
       /* SecretKey key = new SecretKeySpec(keyBytes, "AES");
        byte[] raw = key.getEncoded();*/
        return  keyBytes;


        /*KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = null;
        if (android.os.Build.VERSION.SDK_INT >=  17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);
        kgen.init(256, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;*/
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }


    public static String toHex(byte[] buf,int length) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*length);
        for (int i = 0; i <length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
