package com.beike.util;
/**   
 * @Title: 一维码生成工具
 * @Description:
 * @author ye.tian  
 * @date Apr 25, 2011
 * @version V1.0   
 */
public class BarCodeUtils {
	
	private static BarCodeUtils barCode;
	private static Object lock=new Object();
	
	private final String PRE_FIX="BEIKER";
	
	public static  BarCodeUtils instance() {
		if (barCode == null) {
			synchronized(lock){
				if(barCode==null)
					barCode = new BarCodeUtils();
			}
		}
		return barCode;
	}
	
	/**
	 * 根据订单号生成一维码
	 * @param requestId  订单号
	 * @return  一维码
	 */
	public String generateBarCodeSecret(String requestId){
		String password="";
		String generateOrder=MobilePurseSecurityUtils.generateOrderNumber(PRE_FIX);
		
		String cryptResult=MobilePurseSecurityUtils.getCrypt().cryptDes(requestId+System.currentTimeMillis());
		String secret=MobilePurseSecurityUtils.hmacSign(cryptResult, generateOrder);
		password=secret.toUpperCase().substring(0, 10);
		return password;
	}
}
