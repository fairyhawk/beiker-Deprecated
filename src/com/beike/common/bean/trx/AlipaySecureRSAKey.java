package com.beike.common.bean.trx;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.beike.common.exception.BaseException;
import com.beike.util.Configuration;
import com.beike.util.StringUtils;
/**   
 * @title: AlipaySecureRSAKey.java
 * @package com.beike.common.bean.trx
 * @description: 安全支付RSA公、私钥
 * @author wangweijie  
 * @date 2012-6-19 下午06:02:19
 * @version v1.0   
 */
public class AlipaySecureRSAKey {
	private static final Log log = LogFactory.getLog(AlipaySecureRSAKey.class);
	
	private static final String AlipaySecure_PUBLIC_KEY_PATH = Configuration.getInstance().getValue("alipaySecurePublicKeyPath");		//获得公钥所在路径
	private static final String AlipaySecure_PRIVATE_KEY_PATH = Configuration.getInstance().getValue("alipaySecurePrivateKeyPath");		//获得私钥所在路径

	private static String PUBLIC_KEY = null;
	private static String PRIVATE_KEY = null;
	
	private static String KEYFILE_ECODING = "UTF-8";		//密钥文件编码为UTF-8
	
	
	/**
	 * 获得公钥
	 * @return String
	 * @throws BaseException    
	 */
	public static String getPublicKey() throws BaseException{
		if(StringUtils.isEmpty(PUBLIC_KEY)){
			String content = getFileContent(AlipaySecure_PUBLIC_KEY_PATH);
			if(StringUtils.isEmpty(content)){
				throw new BaseException(BaseException.KEY_VALUE_NOT_FOUND); //
			}
			PUBLIC_KEY = content.trim();
			log.info("Alipay secure public key:******");
		}
		return PUBLIC_KEY;
	}
	
	/**
	 * 获得私钥
	 * @return String
	 * @throws BaseException    
	 */
	public static String getPrivateKey() throws BaseException{
		if(StringUtils.isEmpty(PRIVATE_KEY)){
			String content = getFileContent(AlipaySecure_PRIVATE_KEY_PATH);
			if(StringUtils.isEmpty(content)){
				throw new BaseException(BaseException.KEY_VALUE_NOT_FOUND); //
			}
			PRIVATE_KEY = content.trim();
			log.info("Alipay secure private key:*******" );
		}
		return PRIVATE_KEY;
	}
	
	
	/**
	 * 从文件获得文件内容
	 * @param path
	 * @return
	 */
	private static String getFileContent(String path){
		ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();  
        Resource[] source;
		StringBuilder sb=new StringBuilder("");
		try {
			source = resourceLoader.getResources(path);
			for (int i = 0; i < source.length; i++) {
				Resource resource = source[i];
				BufferedReader in=null;
				try {
					InputStreamReader read = new InputStreamReader(new FileInputStream(resource.getFile()),KEYFILE_ECODING);
					 in = new BufferedReader(read);
					String str;
					while ((str = in.readLine()) != null) {
						sb.append(StringUtils.toTrim(str));
					}
					
				} catch (IOException e) {
					e.getStackTrace();
				}finally{
					if(in!=null){
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}
