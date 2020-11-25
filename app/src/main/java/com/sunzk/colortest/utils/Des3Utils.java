package com.sunzk.colortest.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class Des3Utils {

    // 密钥
    //public final static String secretKey = "0535YANTAIJIANWANZHONG99";
    // 向量
    public final static String iv = "12345678";
    // 加解密统一使用的编码方式
    public final static String encoding = "utf-8";
    // 3des加密
    public static final String algorithm = "DESede";

    /**
     * @param str 需要加密的文字
     * @return 加密后的文字
     * @throws Exception 加密失败
     */
    public static String get3DES(final String str,String secretKey) throws Exception
    {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(getKeyBytes(secretKey));
        SecretKeyFactory keyfactory = SecretKeyFactory
                .getInstance(algorithm);
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] encryptData = cipher.doFinal(str.getBytes(encoding));
        //return Base64.encode(encryptData);
        return HexBytesUtils.bytesToHexString(encryptData);
    }

    private static byte[] getKeyBytes(String paramString)
    {
        byte[] arrayOfByte = new byte[24];
        byte[] paramStringArr = paramString.getBytes();
        int j = paramStringArr.length;
        int i = 0;
        while (i < j)
        {
            arrayOfByte[i] = paramStringArr[i];
            i += 1;
        }
        i = j;
        while (i < 24)
        {
            arrayOfByte[i] = 48;
            i += 1;
        }
        return arrayOfByte;
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode3DES(String encryptText,String secretKey) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(getKeyBytes(secretKey));
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(algorithm);
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, deskey);

        byte[] decryptData = cipher.doFinal(HexBytesUtils.hexStringToByte(encryptText));

        return new String(decryptData, encoding);
    }

}