package com.beike.util;

import java.io.File;
import java.io.FilenameFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**      
 * project:beiker  
 * Title:首页商品碎片轮换显示
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Jan 10, 2012 5:07:15 PM     
 * @version 1.0
 */
public class IndexHtmlRotation {
	public static String getHtmlIndex(HttpServletRequest request,
			HttpServletResponse response, String fileName, String city){
		//读取当前轮换索引
		String retNum = WebUtils.getCookieValue("INDEX_HTML_" + fileName + "_" + city, request);
		if(org.apache.commons.lang.StringUtils.isEmpty(retNum)){
			retNum = "0";
		}
		
		//获取碎片文件个数
		String htmlFile = request.getRealPath("") + "/jsp/templates/"+ city + "/include/";
		File dir = new File(htmlFile);
		FilenameFilterImpl filter = new FilenameFilterImpl(fileName + "_");
		String[] names = dir.list(filter);

		int fileCount = 0;
		if(names!=null){
			fileCount = names.length;
		}
		
		//历史遗留碎片处理
		if(fileCount == 0){
			retNum = "";
			return fileName + ".html";
		}else{
			int itmp = Integer.parseInt(retNum) + 1;
			if(fileCount == 1){
				itmp = fileCount;
			}else if(itmp > fileCount){
				itmp = itmp % fileCount;
			}
			retNum = String.valueOf(itmp);
			//记入cookie
			Cookie cookie = WebUtils.cookie("INDEX_HTML_" + fileName + "_" + city, retNum, -1);
			response.addCookie(cookie);
			return names[itmp-1];
		}
	}
	
	//通过文件名匹配
	private static class FilenameFilterImpl implements FilenameFilter {
		private String name;
		
		public FilenameFilterImpl(String name) {
			this.name = name;
		}
		
		public boolean accept(File dir, String file) {
			return file.startsWith(name);
		}
	}
}