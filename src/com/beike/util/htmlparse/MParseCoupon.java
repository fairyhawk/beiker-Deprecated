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

public class MParseCoupon {

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
	public static Map<String, String> getCouponInfo(String path, String city,
			String fileName) {
		Map<String, String> couponMap = new LinkedHashMap<String, String>();
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
			if(null!=classList){
				for (int i = 0; i < 8; i++) {// 此处需要注意8，为4的2倍 classList.size();
					picNode = classList.elementAt(i);
					classStr = ((Div) picNode).getAttribute("class");
					if ("business_list_pic".equalsIgnoreCase(classStr)) {
						hrefList = picNode.getChildren();
						for (int j = 0; j < hrefList.size(); j++) {// 
							hrefNode = hrefList.elementAt(j);
							if (hrefNode instanceof LinkTag) {
								hrefStr = ((LinkTag) hrefNode).getLink();// 有用优惠券id
								hrefStr = MParseCoupon.getCouponId(hrefStr);
								imgList = hrefNode.getChildren();
								for (int k = 0; k < imgList.size(); k++) {
									imgNode = imgList.elementAt(k);

									if (imgNode instanceof ImageTag) {
										imgStr = ((ImageTag) imgNode)
												.getAttribute("src");// 有用优惠券图片
										if (null == imgStr) {
											imgStr = ((ImageTag) imgNode)
													.getAttribute("original");// 有用优惠券图片
										}
									}

								}
								couponMap.put(hrefStr, imgStr);
							}
						}
					}
					System.out.println();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return couponMap;
	}

	public static String getCouponId(String url) {
		String couponId = "";
		url = url.replaceAll(":", "").replaceAll("/", "").replaceAll(".html",
				"").replaceAll("\\.", "");
		Pattern p = Pattern.compile("([\\w&&[\\D]]+)([\\d]+)");
		Matcher m = p.matcher(url);
		if (m.matches()) {
			couponId = m.group(2);
		}
		return couponId;
	}

	// Unit Testing
	public static void main(String[] args) {
		Map<String, String> couponMap = MParseCoupon.getCouponInfo("",
				"beijing", "coupon_list");
		for (String key : couponMap.keySet()) {
			System.out.println("key=" + key + "--value=" + couponMap.get(key));
		}
	}
}
