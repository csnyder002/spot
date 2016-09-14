package com.IntelligentWaves.xmltest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class TwofishEncrypt
{
    public static void main(String []args) throws Exception {
        String toEncrypt = "The shorter you live, the longer you're dead!";

        System.out.println("Encrypting...");
        String encrypted = encrypt(toEncrypt, "password");

        System.out.println("Decrypting...");
        String decrypted = decrypt(encrypted, "password");

        System.out.println("Decrypted text: " + decrypted);
    }

    public static String encrypt(String toEncrypt, String key) throws Exception {
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("twofish");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("twofish");

        // initialize the cipher with the key
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        // encrypt!
        byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());

        return encrypted.toString();
    }

    public static String decrypt(String todecrypt, String key) throws Exception {
        byte[] toDecrypt = todecrypt.getBytes();
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("twofish");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("twofish");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrypted = cipher.doFinal(toDecrypt);

        return new String(decrypted);
    }
}