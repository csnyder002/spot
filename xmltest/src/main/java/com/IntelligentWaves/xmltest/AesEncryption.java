package com.IntelligentWaves.xmltest;


import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AesEncryption {

    public static byte[] aesEncrypt(String key, String message) {
        try {
            key = pad(key, 32);
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            System.out.println("Encrypted text: " + new String(encrypted));
            return encrypted;
        } catch (Exception e) {
            System.out.println("!!!!! " + e.toString());
            return null;
        }
    }

    static String pad(String s, int numDigits)
    {
        StringBuffer sb = new StringBuffer(s);
        int numZeros = numDigits - s.length();
        while(numZeros-- > 0) {
            sb.insert(0, "0");
        }
        System.out.println("!!!!! " + sb.toString());
        return sb.toString();
    }
}
