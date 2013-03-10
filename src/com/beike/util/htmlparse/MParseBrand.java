package com.beike.util.htmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

public class MParseBrand {

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
	 * 获取滚动品牌
	 * 
	 * @param path
	 * @param city
	 * @param fileName
	 * @return
	 */
	public static Map<String, String> getBrandInfo(String path, String city,
			String fileName) {
		Map<String, String> brandMap = new LinkedHashMap<String, String>();
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
			NodeFilter divFilter = new NodeClassFilter(Div.class);
			NodeList classList = parser.extractAllNodesThatMatch(divFilter);
			NodeList hrefList = null;
			NodeList imgList = null;
			Node picNode = null;
			Node hrefNode = null;
			Node imgNode = null;
			String classStr = "";
			String hrefStr = "";
			String imgStr = "";
			String imgClass = "";
			for (int i = 0; i < classList.size(); i++) {
				picNode = classList.elementAt(i);
				classStr = ((Div) picNode).getAttribute("class");
				if ("business_list_pic".equalsIgnoreCase(classStr)) {
					hrefList = picNode.getChildren();
					for (int j = 0; j < hrefList.size(); j++) {
						hrefNode = hrefList.elementAt(j);
						if (hrefNode instanceof LinkTag) {
							hrefStr = ((LinkTag) hrefNode).getLink();// 有用品牌id，获取到id
							hrefStr = MParseBrand.getBrandId(hrefStr);
							imgList = hrefNode.getChildren();
							for (int k = 0; k < imgList.size(); k++) {
								imgNode = imgList.elementAt(k);
								if (imgNode instanceof ImageTag) {
									imgClass = ((ImageTag) imgNode)
											.getAttribute("class");
									if (null != imgClass) {
										imgStr = ((ImageTag) imgNode)
												.getAttribute("src");
										if (null == imgStr) {
											imgStr = ((ImageTag) imgNode)
													.getAttribute("original");
										}
									}
								}

							}
							brandMap.put(hrefStr, imgStr);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return brandMap;
	}

	public static String getBrandId(String url) {
		String brandId = "";
		url = url.replaceAll(":", "").replaceAll("/", "").replaceAll(".html",
				"").replaceAll("\\.", "");
		Pattern p = Pattern.compile("([\\w&&[\\D]]+)([\\d]+)");
		Matcher m = p.matcher(url);
		if (m.matches()) {
			brandId = m.group(2);
		}
		return brandId;
	}

	// Unit Testing
	public static void main(String[] args) {
		Map<String, String> brandMap = MParseBrand.getBrandInfo("", "beijing",
				"brand_mainlist");
		for (String key : brandMap.keySet()) {
			System.out.println("key=" + key + "---value=" + brandMap.get(key));
		}
	}
}
