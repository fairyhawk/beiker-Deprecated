package com.beike.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DesString {

	private static Log log=LogFactory.getLog(DesString.class);

	public DES getDes(String strKey){
		DES des = null;
		try{
			des = new DES(strKey);
		}catch (Exception e){
			 log.error("创建des错误"+e);
		}
		return des;
	}
		
	/**
	 * 加密
	 * @param te（明文）
	 * @return 密文
	 */
	public String encrypt(String te,String strKey){
		return getDes(strKey).encrypt(te);
	}
	
	/**
	 * 解密
	 * @param te  密文
	 * @return 明文
	 */
	public String decrypt(String te,String strKey){
		return getDes(strKey).decrypt(te);
	}

}
