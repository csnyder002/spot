package com.IntelligentWaves.xmltest;


import android.util.Base64;
import android.util.Log;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AesEncryption {
    private static String TAG = "AesEncryption.java";
    public static String decrypt(String key, byte[] encrypted) {
        try {
            key = pad(key, 32);
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(encrypted));
            return decrypted;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return "";
        }
    }

    public static byte[] encrypt(String key, String message) {
        try {
            key = pad(key, 32);
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return encrypted;
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public static String encryptToString(String key, String message){
        return base64Encode(encrypt(key, message));
    }

    public static String decryptFromString(String key, String message){
        return decrypt(key, base64Decode(message));
    }

    static String pad(String s, int numDigits)
    {
        StringBuffer sb = new StringBuffer(s);
        int numZeros = numDigits - s.length();
        while(numZeros-- > 0) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] base64Decode(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }
}
