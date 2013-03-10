package com.beike.util.htmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.Region;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

public class MParseRegion {
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_PATH);
	private static String ENCODE = "ISO-8859-1";
	private static final String PATH = propertyUtil.getProperty("ONLINE_IMAGE_PATH");
	private static final String INCLUDE = "/include/";
	private static final String STUFF = ".html";

	/**
	 * 
	 * @param szFileName
	 * @return
	 */
	public static String openFile(String szFileName) {
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(szFileName)), ENCODE));
			String szContent = "";
			String szTemp;

			while ((szTemp = bis.readLine()) != null) {
				szContent += szTemp + "\n";
			}
			bis.close();
			return szContent;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取热门地标
	 * 
	 * @param path
	 * @param city
	 * @param fileName
	 * @return
	 */
	public static String getRegionInfo(String path, String city,
			String fileName) {
		StringBuilder region = new StringBuilder("");
		try {
			StringBuilder filePath = new StringBuilder();
			filePath.append(PATH);
			filePath.append(city);
			filePath.append(INCLUDE);
			filePath.append(fileName);
			filePath.append(STUFF);
			
			// 开始解析
			Parser parser = new Parser(filePath.toString());
			// 过滤出<a></a>标签
			NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
			NodeList spanList = parser.extractAllNodesThatMatch(linkFilter);
			Node linkNode = null;
			for(int i=0;i<spanList.size()-1;i++){
				linkNode = spanList.elementAt(i);
				if(linkNode instanceof LinkTag){
					if("".equals(region.toString())){
						region.append("'").append(MParseRegion.getRegionEnName(((LinkTag)linkNode).getLink())).append("'");
					}else{
						region.append(",").append("'").append(MParseRegion.getRegionEnName(((LinkTag)linkNode).getLink())).append("'");
					}
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return region.toString();
	}
	
	public static String getRegionEnName(String url) {
		String regionName = "";
		url = url.replaceAll(":", "").replaceAll("/", "").replaceAll(".jsp","").replaceAll(".html",
				"").replaceAll("\\.", "");
		if(url.indexOf("x-")<0){
			regionName = url.split("goods")[1].split("-")[0];
		}else{
			regionName = url.split("x-")[1].split("-")[0];
		}
		return regionName;
	}


	// Unit Testing
	public static void main(String[] args) {
		//MParseRegion.getRegionEnName("");
		String region = MParseRegion.getRegionInfo("",
				"guangzhou", "hotcircle");
		System.out.println(region);
		/*
		 * for (String key : couponMap.keySet()) {
			System.out.println("key=" + key);
			System.out.println("value=" + couponMap.get(key));
		}*/

	}
}
