package com.beike.util;


/**
 * <p>Title: 加解密功能接口</p>
 * <p>Description: 提供单向加密、加解密功能</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @author ye.tian
 * @version 1.0
 */
public interface CryptUtil {
	
	/**
	 * 按照Triple-DES算法，对数据进行加密
	 * @param source - 明文数据
	 * @param key - 密钥
	 * @return - 密文数据
	 */
	String cryptDes(String source,String key);
	
	/**
	 * 按照Triple-DES算法，对数据进行解密
	 * @param des - 密文数据
	 * @param key - 密钥
	 * @return - 明文数据 
	 */
	String decryptDes(String des,String key);
	
	/**
	 * 按照Triple-DES算法，使用系统固定的密钥"a1b2c3d4e5f6g7h8i9j0klmn"，对数据进行加密
	 * @param source - 明文数据
	 * @return - 密文数据
	 */
	String cryptDes(String source);
	
	/**
	 * 按照Triple-DES算法，使用系统固定的密钥"a1b2c3d4e5f6g7h8i9j0klmn"，对数据进行解密
	 * @param des - 密文数据
	 * @return - 明文数据
	 */
	String decryptDes(String des);
	
	/**
	 * 对数据进行MD5签名
	 * @param source - 待签名数据 
	 * @param key - 密钥
	 * @return - 数据签名结果
	 */
	String cryptMd5(String source,String key);	
}
