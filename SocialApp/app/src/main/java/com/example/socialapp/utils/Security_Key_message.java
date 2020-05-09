package com.example.socialapp.utils;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Security_Key_message {

    public  String encrypt(String cleartext, byte[] session_key) throws Exception {
        byte[] rawKey = session_key;
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public  String decrypt(String encrypted, byte[] session_key) throws Exception {
        byte[] rawKey = session_key;
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    public  byte[] getPrivateSecretKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        System.out.println( "TAG in Key genetrate with toString() "+ raw.toString());
        return raw;
    }

    public byte[] getSessionKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        Log.d( "TAG in Key generate: ", skey.toString());
        byte[] raw = skey.getEncoded();
        System.out.println( "TAG in Key generate with toString() "+ raw.toString());
        return raw;
    }

    public  byte[] getGroupKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        System.out.println( "TAG in Key generate with toString() "+ raw.toString());
        return raw;
    }

    public String encypt_keys(SecretKey sk, SecretKey pk) throws Exception
    {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        byte[] raw = cipher.doFinal(sk.getEncoded());
        return Base64.encodeToString(raw, Base64.DEFAULT);
    }



    public  byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public  byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public String encrypt_With_publicKey(byte[] sess_key, PublicKey key) throws Exception {
        Cipher cipher = null;
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(sess_key);
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    public byte[] decrept_With_privateKey(String sess_key, PrivateKey key) throws Exception {
        Cipher cipher = null;
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedSessKey = Base64.decode(sess_key, Base64.DEFAULT);
        byte[] cipherText = cipher.doFinal(decodedSessKey);//.getBytes());
        return cipherText;
    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("MD5withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes());

        byte[] signature = privateSignature.sign();

        return java.util.Base64.getEncoder().encodeToString(signature);
    }




    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
