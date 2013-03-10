package com.beike.util.lucene;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class QueryWordFilter {

	
	private QueryWordFilter(){}
	
	private static final QueryWordFilter queryWordFilter = new QueryWordFilter();
	//防止js/html注入
	private static String regex_unvalid_char = "[<|>|\"]";
	//过滤掉lucene不支持符合
	private final static String regex_not_supported = "[\\+|\\-|&|区|!|(|)|{|}|\\[|\\]|\\^|\"|~|*|\\?|:|\\\\]";
	public static QueryWordFilter getInstance(){
		return queryWordFilter;
	}
	
	/**
	 * 
	 * @author janwen
	 * @time Oct 27, 2011 8:45:54 PM
	 *
	 * @param keyword
	 * @return 过滤特殊字符,后续需增强过滤
	 * FIXME janwen
	 * @throws UnsupportedEncodingException
	 */
	public static String filterQueryWord(String keyword) throws UnsupportedEncodingException{
		keyword = URLDecoder.decode(keyword,"utf-8");
		return keyword.replaceAll(regex_not_supported, ",");
	}
	
	
   /**
    * 
    * @author janwen
    * @time Oct 27, 2011 8:45:39 PM
    *
    * @param keyword
    * @return 转码
    * @throws UnsupportedEncodingException
    */
	public static String decodeQueryWord(String keyword) throws UnsupportedEncodingException{
		 keyword = URLDecoder.decode(keyword,"utf-8");
		 return keyword.replaceAll(regex_unvalid_char, "");
		
	}
}
