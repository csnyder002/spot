package com.IntelligentWaves.xmltest;

import android.util.Base64;
import android.util.Log;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * This program demonstrates how to encrypt/decrypt input
 * using the Blowfish Cipher with the Java Cryptograhpy.
 *
 */
public class BlowfishEncrypt {
    private final static String TAG = "BlowfishEncrypt.java";
    private final static String ALGORITM = "Blowfish";
    private final static String KEY = "2356a3a42ba5781f80a72dad3f90aeee8ba93c7637aaf218a8b8c18c";
    private final static String PLAIN_TEXT = "here is your text";

    public static byte[] encrypt(String key, String plainText) throws GeneralSecurityException {
        SecretKey secret_key = new SecretKeySpec(key.getBytes(), ALGORITM);
        Cipher cipher = Cipher.getInstance(ALGORITM);
        cipher.init(Cipher.ENCRYPT_MODE, secret_key);
        return cipher.doFinal(plainText.getBytes());
    }

    public static String decrypt(String key, byte[] encryptedText) throws GeneralSecurityException {
        SecretKey secret_key = new SecretKeySpec(key.getBytes(), ALGORITM);
        Cipher cipher = Cipher.getInstance(ALGORITM);
        cipher.init(Cipher.DECRYPT_MODE, secret_key);
        byte[] decrypted = cipher.doFinal(encryptedText);
        return new String(decrypted);
    }

    public static String encryptToString(String key, String plaintext) {
        try {
            return base64Encode(encrypt(key, plaintext));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static String decryptFromString(String key, String str) {
        try {
            return decrypt(key, base64Decode(str));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] base64Decode(String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }
}