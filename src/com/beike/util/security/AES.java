package com.beike.util.security;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.beike.util.Base64;
  
/** 
 *  
 * @author wchun 
 *  
 * AES128 算法，加密模式为ECB，填充模式为 pkcs7（实际就是pkcs5） 
 *  
 * 
 */  
public class AES {  
      
    static final String algorithmStr="AES/ECB/PKCS5Padding";  
      
    static private KeyGenerator keyGen;  
      
    static private Cipher cipher;  
      
    static boolean isInited=false;  
      
    //初始化  
    static private void init()  
    {  
          
        //初始化keyGen  
        try {  
            keyGen=KeyGenerator.getInstance("AES");  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        keyGen.init(128);  
          
        //初始化cipher  
        try {  
            cipher=Cipher.getInstance(algorithmStr);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        }  
          
        isInited=true;  
    }  
      
	public static byte[] GenKey() {  
		//如果没有初始化过,则初始化 
		if (!isInited) {
            init();  
        }  
        return keyGen.generateKey().getEncoded();  
    }  
      
	public static byte[] Encrypt(byte[] content, byte[] keyBytes) {
        byte[] encryptedText=null;  
        // 为初始化
		if (!isInited) {
            init();  
        }  
          
        Key key=new SecretKeySpec(keyBytes,"AES");  
          
        try {  
            cipher.init(Cipher.ENCRYPT_MODE, key);  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        }  
          
        try {  
            encryptedText=cipher.doFinal(content);  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
          
        return encryptedText;  
    }  
      
    //解密为byte[]  
	public static byte[] DecryptToBytes(byte[] content, byte[] keyBytes) {
        byte[] originBytes=null;  
		
        if (!isInited) {  
            init();  
        }  
          
        Key key=new SecretKeySpec(keyBytes,"AES");  
          
        try {  
            cipher.init(Cipher.DECRYPT_MODE, key);  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        }  
          
        //解密  
        try {  
            originBytes=cipher.doFinal(content);  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
          
        return originBytes;  
    }
    
    public static void main(String[] args) {
    	try {
    		System.out.println(new String(Base64.encode(Encrypt("<Message xmlns=\"http://tuan.360buy.com/QueryTeamSellCountRequest\"><VenderTeamId>234</VenderTeamId><SpProdId>242351</SpProdId><SellCount>2</SellCount></Message>".getBytes("UTF-8"),"9987.tuan.360buy".getBytes()))));
			System.out.println(new String(DecryptToBytes(Base64.decode("azADvvCDojkTDGtYZgg13uevbfnVrDoAtpPBSJqbnPfzUEd4vMHOpPzXhJceEq/Mes55OKj6KvyVxlevaUjt0BbUjsVfzN839THvilnBBd0pCKb9gZqKKbJEoFxpHuYnbOfuKaDDVRdXlW8X60dYsG84PzZz6Ei6ZzxlpJ5wd7dEO0VDC8slHlLzVLTnUpNHBg7WMrvV8Q/Y8uTdIFsIPg==".getBytes("UTF-8")),"9987.tuan.360buy".getBytes())));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
}