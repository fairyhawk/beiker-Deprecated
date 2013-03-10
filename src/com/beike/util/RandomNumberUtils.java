package com.beike.util;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;



/**
 * <p>Title: 产生数字随机码</p>
 * <p>Description: 
 * <p>Copyright: </p>
 * <p>Company: Sinobo</p>
 * @author ye.tian
 * @version 1.0
 */

public class RandomNumberUtils {
	private static long suff = 100;
	
	public static List<Integer> getRandomNumberx(int i,int randomsize){
		List<Integer> listCount=new ArrayList<Integer>();
		for(int j=0;j<i;j++){
			int randomNumber=(int)(Math.random()*randomsize);
			System.out.println(randomNumber);
			listCount.add(randomNumber);
		}
		
		return listCount;
	}
	public static int getRandomNumber(int i){
		int randomNumber=(int)(Math.random()*i);
		return randomNumber;
	}
	/**
	 * 产生几位随机码
	 * @param i	 位数
	 * @return		随机码
	 */
	public static String getRandomNumbers(int i){
		StringBuffer sb=new StringBuffer();
		
		for (int j = 0; j < i; j++) {
			Random random=new Random();
			sb.append(random.nextInt(9));
		}
		return sb.toString();
	}
	
	/**
	 * 生成随机数
	 * @return
	 */
	public static String createPicName(){
		//鐢熸垚璁㈠崟鍙�
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);//鑾峰彇骞翠唤
		int month=ca.get(Calendar.MONTH) + 1;//鑾峰彇鏈堜唤 
		int day=ca.get(Calendar.DATE);//鑾峰彇鏃�
		int minute=ca.get(Calendar.MINUTE);//鍒� 
		int hour=ca.get(Calendar.HOUR) + 12;//灏忔椂 
		int second=ca.get(Calendar.SECOND);//绉�
		String p2_Order = "" + year+month+day+hour+minute+second;
		p2_Order += RandomNumberUtils.getRandomNumbers(4);
		return p2_Order;
		
	}
	
	public static String createOrderId(){
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month=ca.get(Calendar.MONTH) + 1;//鑾峰彇鏈堜唤 
		int day=ca.get(Calendar.DATE);//鑾峰彇鏃�
		int minute=ca.get(Calendar.MINUTE);//鍒� 
		int hour=ca.get(Calendar.HOUR) + 12;//灏忔椂 
		int second=ca.get(Calendar.SECOND);//绉�
		String p2_Order = "" + year+month+day+hour+minute+second;
		p2_Order += RandomNumberUtils.getRandomNumbers(4);
		return p2_Order;
		
	}
	
	public static synchronized String genId(){
		suff += 1;
		if ( suff > 999)
			suff = 100;
		String tem = ("" + new Date().getTime() ) + suff;
		return  tem;
	}
	
	public static final String getRandString(int size){
		
		if ( size < 0 || size > 1000){
			size = 6;
		}
		 // 随机生成一个size位字符长的的字符串。
	      String tempStr = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	      int length = tempStr.length();
	      Random random = new Random();
	      String result = "";
	      for(int i = 0; i < size; i++){
	         int randomInt = random.nextInt(length);
	         result += tempStr.substring(randomInt, randomInt + 1);
	      }
	      
	      return result;
	}

	/**
	 * 计算页数
	 * @param sum
	 * @param size
	 * @return
	 */
	public static int calculatePage(int sum,int size){
		BigDecimal sumDecimal = new BigDecimal(sum);   
		BigDecimal sizeDecimal = new BigDecimal(size);   
		BigDecimal page = sumDecimal.divide(sizeDecimal,0,java.math.RoundingMode.UP);
		return page.intValue();
	}
	
	/**
	 * 计算评价分数
	 * @param score
	 * @return
	 */
	public static int mulScore(String score){
		BigDecimal old = new BigDecimal(score);
		BigDecimal ji = old.multiply(new BigDecimal(2));
		BigDecimal record = ji.divide(new BigDecimal(1), 0, RoundingMode.DOWN);
		return record.intValue();
	}
}
