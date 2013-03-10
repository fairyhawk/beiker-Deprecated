package com.beike.util.htmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Bullet;
import org.htmlparser.util.NodeList;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

public class MParseGoods {
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

	public static Map<String, String> getGoodsMes(String path, String city,
			String fileName) {
		Map<String, String> goodsIds = new LinkedHashMap<String, String>();
		try {
			StringBuilder filePath = new StringBuilder();
			filePath.append(PATH);
			filePath.append(city);
			filePath.append(INCLUDE);
			filePath.append(fileName);
			filePath.append(STUFF);
			Parser parser = new Parser(filePath.toString());
			Node liNode = null;
			NodeFilter nodeFilter = new TagNameFilter("li");
			NodeList liList = parser.extractAllNodesThatMatch(nodeFilter);
			NodeList phoneList = null;
			NodeList imgList = null;
			Node linkNode = null;
			Node imgNode = null;
			String linkUrl = null;
			String imgStr = null;
			String goodId = "";
			if(null!=liList){
				for(int i=0;i<liList.size();i++){
					liNode = liList.elementAt(i);
					String listPhoto = ((Bullet) liNode).getAttribute("class");
					
					if("list_photo".equals(listPhoto)){
						phoneList = liNode.getChildren();
						for(int j=0;j<phoneList.size();j++){
							linkNode = phoneList.elementAt(j);
							if(linkNode instanceof LinkTag){
								linkUrl = ((LinkTag)linkNode).getLink();
								goodId = MParseGoods.getGoodsId(linkUrl);
								imgList = linkNode.getChildren();
								for(int k=0;k<imgList.size();k++){
									imgNode = imgList.elementAt(k);
									if (imgNode instanceof ImageTag) {
										imgStr = ((ImageTag) imgNode).getAttribute("src");
										if (null == imgStr) {
											imgStr = ((ImageTag) imgNode).getAttribute("original");
										}
									}
								}
							}
						}
						goodsIds.put(goodId, imgStr);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
		return goodsIds;
	}

	public static String getGoodsId(String url) {
		if(url.indexOf("?")>0){
			url = url.substring(0, url.indexOf("?"));
		}
		if(url.indexOf("?")<0&&url.indexOf("#")>0){
			url = url.substring(0, url.indexOf("#"));
		}
		String goodsId = "";
		url = url.replaceAll(":", "").replaceAll("/", "").replaceAll(".jsp","").replaceAll(".html",
				"").replaceAll("\\.", "");
		Pattern p = Pattern.compile("([\\w&&[\\D]]+)([\\d]+)");
		Matcher m = p.matcher(url);
		if (m.matches()) {
			goodsId = m.group(2);
		}
		return goodsId;
	}

	public static void main(String[] args) {
		Map<String, String> goodsList = MParseGoods.getGoodsMes("", "beijing",
				"mainlistone");
		for (String key : goodsList.keySet()) {
			System.out.print("key=" + key);
			System.out.println("=value=" + goodsList.get(key));
		}
		
	}

}
