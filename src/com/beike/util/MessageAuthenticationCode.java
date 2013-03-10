package com.beike.util;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESKeySpec;
//import java.io.UnsupportedEncodingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;


/**
 * ANSI X9.9 MAC校验算法
 *
 * DES加密结果：
 *   des key    = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
 *   des data   = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
 *   des result = 1b 18 b9 7a 85 f9 67 e9
 *
 * MAC加密结果：
 *   mac key    = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
 *   mac data   = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
 *   mac result = a1 e8 02 aa 02 74 74 bb
 *
 * <p>Title: Excel Report Util </p>
 * <p>Description: report utilitity using JXL</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @author not attributable
 * @version 1.0
 */
public class MessageAuthenticationCode {
//  private static final String Algorithm = "DES"; //���� �����㷨,���� DES,DESede,Blowfish

  /**
   * ����MAC���?����ANSI X9.9��׼
   * @param key  --  ��Կ������8�ֽڣ�
   * @param data -- ��Ҫ��MAC�����
   * @return
   */
  public static byte[] mac(byte[] key, byte[] data) throws
      NoSuchAlgorithmException, NoSuchPaddingException,
      NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
      IllegalBlockSizeException, IllegalStateException {

    return mac(key, data, 0, data.length);
  }

  /**
   * ����MAC���?����ANSI X9.9��׼
   * @param key  --  ��Կ������8�ֽڣ�
   * @param data -- ��Ҫ��MAC�����
   * @param offset -- data����ʼλ��
   * @param len -- ��ҪMAC����ݳ���
   * @return
   */
  public static byte[] mac(byte[] key, byte[] data, int offset, int len) throws
      NoSuchAlgorithmException, NoSuchPaddingException,
      NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
      IllegalBlockSizeException, IllegalStateException {
    final String Algorithm = "DES"; //���� �����㷨,���� DES,DESede,Blowfish

    //�����Կ
    SecretKey deskey = new SecretKeySpec(key, Algorithm);

    // ����
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.ENCRYPT_MODE, deskey);

    byte buf[] = {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    for (int i = 0; i < len; ) {
      for (int j = 0; j < 8 && i < len; i++, j++) {
        buf[j] ^= data[offset + i];
      }
      buf = c1.update(buf);
    }
    c1.doFinal();
    return buf;
  }

  /**
   * DES���ܴ���
   * ע�⣺�㷨����ʹ��"DES/ECB/NoPadding"���ܲ���8bytes�ļ��ܽ��
   * @param key
   * @param data
   * @return
   * @throws NoSuchAlgorithmException
   * @throws java.lang.Exception
   */
  public static byte[] desEncryption(byte[] key, byte[] data) throws
      NoSuchAlgorithmException, Exception {
    final String Algorithm = "DES/ECB/NoPadding"; //���� �����㷨,���� DES,DESede,Blowfish

    if (key.length != DESKeySpec.DES_KEY_LEN || data.length != 8)
      throw new IllegalArgumentException("key or data's length != 8");

    //�����Կ
    DESKeySpec desKS = new DESKeySpec(key);
    SecretKeyFactory  skf = SecretKeyFactory.getInstance("DES");
    SecretKey deskey = skf.generateSecret(desKS);

    // ����
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.ENCRYPT_MODE, deskey);

    byte buf[];
    // ���ֱ��ʹ��doFinal()��������16�ֽڵĽ��ͷ8���ֽ���update��ͬ
    buf = c1.doFinal(data);

    // ����8�ֽڵ�des���
    byte[] enc_data = new byte[8];
    System.arraycopy(buf, 0, enc_data, 0, 8);
    return enc_data;
  }


  /**
   * DES���ܴ���
   * ע�⣺�㷨����ʹ��"DES/ECB/NoPadding"���ܲ���8bytes�ļ��ܽ��
   * @param key
   * @param data
   * @return
   * @throws NoSuchAlgorithmException
   * @throws java.lang.Exception
   */
  public static byte[] desDecryption(byte[] key, byte[] data) throws
      NoSuchAlgorithmException, Exception {
    final String Algorithm = "DES/ECB/NoPadding"; //���� �����㷨,���� DES,DESede,Blowfish

    if (key.length != DESKeySpec.DES_KEY_LEN || data.length != 8)
      throw new IllegalArgumentException("key's len != 8 or data's length != 8");

    //�����Կ
    SecretKey deskey = new SecretKeySpec(key, "DES");

    // ����
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.DECRYPT_MODE, deskey);

    byte decrypted [];

    // ���ֱ��ʹ��doFinal()��������16�ֽڵĽ��ͷ8���ֽ���update��ͬ
    decrypted = c1.doFinal(data);
//    System.out.println("decrypted = " + StringArrayUtil.byte2hex(decrypted));

    return decrypted;
  }

  /**
   ��DES��������������ֽ�
   bytKey��Ϊ8�ֽڳ����Ǽ��ܵ�����
   */
  protected byte[] encryptByDES(byte[] bytP, byte[] bytKey) throws Exception {
    DESKeySpec desKS = new DESKeySpec(bytKey);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
    SecretKey sk = skf.generateSecret(desKS);
    Cipher cip = Cipher.getInstance("DES");
    cip.init(Cipher.ENCRYPT_MODE, sk);
    return cip.doFinal(bytP);
  }

  /**
   ��DES��������������ֽ�
   bytKey��Ϊ8�ֽڳ����ǽ��ܵ�����
   */
  protected byte[] decryptByDES(byte[] bytE, byte[] bytKey) throws Exception {
    DESKeySpec desKS = new DESKeySpec(bytKey);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
    SecretKey sk = skf.generateSecret(desKS);
    Cipher cip = Cipher.getInstance("DES");
    cip.init(Cipher.DECRYPT_MODE, sk);
    return cip.doFinal(bytE);
  }

  /**
   * �ַ� DESede(3DES) ����
   * @param key - Ϊ24�ֽڵ���Կ��3��x8�ֽڣ�
   * @param data - ��Ҫ���м��ܵ���ݣ�8�ֽڣ�
   * @return
   */
  public static byte[] des3Encryption(byte[] key, byte[] data) throws
      NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException, IllegalStateException {
    final String Algorithm = "DESede"; // ���� �����㷨,���� DES,DESede,Blowfish

    //�����Կ
    SecretKey deskey = new SecretKeySpec(key, Algorithm);

    //����
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.ENCRYPT_MODE, deskey);
    return c1.doFinal(data);
  }


  /**
   * �ַ� DESede(3DES) ����
   * @param key - Ϊ24�ֽڵ���Կ��3��x8�ֽڣ�
   * @param data - ��Ҫ���н��ܵ���ݣ�8�ֽڣ�
   * @return
   */
  public static byte[] des3Decryption(byte[] key, byte[] data) throws
      NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException, IllegalStateException {
    final String Algorithm = "DESede"; // ���� �����㷨,���� DES,DESede,Blowfish

    //�����Կ
    SecretKey deskey = new SecretKeySpec(key, Algorithm);

    //����
    Cipher c1 = Cipher.getInstance(Algorithm);
    c1.init(Cipher.DECRYPT_MODE, deskey);
    return c1.doFinal(data);
  }
  
  /**
   * ����ݽ���3DES����
   * @param key - 24 bytes����Կ�����飩
   * @param iv - �������ʹ�õ�random��(8 bytes)
   * @param data - ����ܵ����
   * @return ���ܺ�����
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws IllegalStateException
   * @throws InvalidAlgorithmParameterException
   * @throws InvalidKeySpecException
   */
  public static byte[] des3Encryption(byte[] key, byte[]iv, byte[] data) throws
  	NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
  	BadPaddingException, IllegalBlockSizeException, IllegalStateException, InvalidAlgorithmParameterException, InvalidKeySpecException {
	  final String Algorithm = "DESede/CBC/PKCS5Padding"; // ���� �����㷨,���� DES,DESede,Blowfish
	  //�����Կ
	  SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(
      "DESede");
	  DESedeKeySpec spec = new DESedeKeySpec(key);
	  SecretKey deskey = keyFactory.generateSecret(spec);
	  //SecretKey deskey = new SecretKeySpec(key, Algorithm);
	  // iv
	  IvParameterSpec tempIv = new IvParameterSpec(iv);
	  //����
	  Cipher c1 = Cipher.getInstance(Algorithm);
	  c1.init(Cipher.ENCRYPT_MODE, deskey, tempIv);
	  return c1.doFinal(data);
	 
  }
  
  /**
   * ����ݽ���3DES����
   * @param key - 24 bytes����Կ�����飩
   * @param iv - �������ʹ�õ�random��(8 bytes)
   * @param data - ����ܵ���ݣ�8�ı���
   * @return - ���ܺ�����
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws IllegalStateException
   * @throws InvalidAlgorithmParameterException
   * @throws InvalidKeySpecException
   */
  public static byte[] des3Decryption(byte[] key, byte[]iv, byte[] data) throws
	NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
	BadPaddingException, IllegalBlockSizeException, IllegalStateException, InvalidAlgorithmParameterException, InvalidKeySpecException {
	  final String Algorithm = "DESede/CBC/PKCS5Padding"; // ���� �����㷨,���� DES,DESede,Blowfish
	  //�����Կ
	  SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
	  DESedeKeySpec spec = new DESedeKeySpec(key);
	  SecretKey deskey = keyFactory.generateSecret(spec);
	  //SecretKey deskey = new SecretKeySpec(key, Algorithm);
	  // iv
	  IvParameterSpec tempIv = new IvParameterSpec(iv);
	  //����
	  Cipher c1 = Cipher.getInstance(Algorithm);
	  c1.init(Cipher.DECRYPT_MODE, deskey, tempIv);
	  return c1.doFinal(data);
	 
}
  
  public static void main(String[]args) 
  	throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, 
  		BadPaddingException, IllegalBlockSizeException, IllegalStateException, 
  		InvalidAlgorithmParameterException, InvalidKeySpecException, UnsupportedEncodingException{
	  String key = "6647b807e97889fca8b60047e85d9186e380d3c234f71566";
	  byte[] keyb = StringArrayUtil.hex2byte(key, key.length());
	  String iv = "9df131b13df6bdfe";
	  byte[] ivb = StringArrayUtil.hex2byte(iv, iv.length());
	  String value = "2032309250345045,500";
	  //byte[]temp_value =StringArrayUtil.hex2byte(value, value.length());
	  byte[] temp_bytes = des3Encryption(keyb, ivb, value.getBytes("UTF-8"));
	  
	  System.out.println("en="+StringArrayUtil.byte2hex(temp_bytes));
	  
	  byte[] decrptBytes = des3Decryption(keyb, ivb, StringArrayUtil.hex2byte("da0dde2d75cef459faa40a068232cd2eaddf0c7bcd7ef53d", "da0dde2d75cef459faa40a068232cd2eaddf0c7bcd7ef53d".length()));
	  System.out.println("de="+new String(decrptBytes, "UTF-8"));
	  
	  String md5key = "66ea3f65-382a-44f6-97e4-15b0a873332f";
	  
	  String tempMD5 = "PICODE=PI00001CARDDATA="+StringArrayUtil.byte2hex(temp_bytes)+md5key;
	  System.out.println(tempMD5);
	  
	  String md5 = Digest.hmacSign(tempMD5);
	  System.out.println(md5);
	  
  }
}