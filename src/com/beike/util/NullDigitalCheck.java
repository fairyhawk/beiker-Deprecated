package com.beike.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 评价映射类
 * @author janwen
 * Mar 16, 2012
 */
public class NullDigitalCheck {

	public final static Map<String, Integer> rate_score_map = new HashMap<String, Integer>();

	public static final String RATE_BEST_ENUM = "best";
	public static final String RATE_BETTER_ENUM = "better";
	public static final String RATE_BAD_ENUM = "bad";
	
	public static final Integer RATE_BEST_SCORE = 20;
	public static final Integer RATE_BETTER_SCORE = 5;
	public static final Integer RATE_BAD_SCORE = -15;

	static {
		rate_score_map.put(RATE_BEST_ENUM, RATE_BEST_SCORE);
		rate_score_map.put(RATE_BETTER_ENUM, RATE_BETTER_SCORE);
		rate_score_map.put(RATE_BAD_ENUM, RATE_BAD_SCORE);
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 * 1:30:41 PM
	 * janwen
	 *
	 */
	public static boolean checkParamNull(List<Object> params){
		for(Object param:params){
			if(param.toString() == null || "".equals(param)){
				return false;
			}
		}
		return true;
		
	}
	public static boolean checkDigital(List<Object> params){
		for(Object param : params){
			if(!Pattern.matches("\\d*", param.toString())){
				return false;
			}
			
		}
		return true;
	}
}
