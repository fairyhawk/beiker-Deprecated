package com.beike.util.htmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

public class MParseBrandScroll {

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
	public static Map<Integer, Map<String, String>> getBrandInfo(String path,
			String city, String fileName) {
		Map<Integer, Map<String, String>> randomMap = new LinkedHashMap<Integer, Map<String, String>>();
		Map<String, String> brandMap = null;
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
			NodeList hrefList = parser.extractAllNodesThatMatch(linkFilter);
			NodeList imgNodeList = null;
			Node hrefNode = null;
			Node imgNode = null;
			String brandId = "";
			String hrefStr = null;
			for (int i = 0; i < hrefList.size(); i++) {
				hrefNode = hrefList.elementAt(i);
				brandId = MParseBrandScroll.getBrandId(((LinkTag) hrefNode)
						.getLink());
				imgNodeList = hrefNode.getChildren();
				brandMap = new HashMap<String, String>();
				for (int j = 0; j < imgNodeList.size(); j++) {
					imgNode = imgNodeList.elementAt(j);
					if (imgNode instanceof ImageTag) {
						hrefStr = ((ImageTag) imgNode).getAttribute("src");
						if (null == hrefStr) {
							hrefStr = ((ImageTag) imgNode)
									.getAttribute("original");
						}
						brandMap.put(brandId, hrefStr);
					}
				}
				randomMap.put(i, brandMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return randomMap;
	}

	public static String getBrandId(String url) {
		if(url.indexOf("?")>0){
			url = url.substring(0, url.indexOf("?"));
		}
		if(url.indexOf("?")<0&&url.indexOf("#")>0){
			url = url.substring(0, url.indexOf("#"));
		}
		String brandId = "";
		url = url.replaceAll(":", "").replaceAll("/", "").replaceAll(".jsp",
		"").replaceAll(".html","").replaceAll("\\.", "");
		Pattern p = Pattern.compile("([\\w&&[\\D]]+)([\\d]+)");
		Matcher m = p.matcher(url);
		if (m.matches()) {
			brandId = m.group(2);
		}
		return brandId;
	}

	private static Random rnd = null;

	/**
	 * 初始化随机数发生器。
	 */
	private static void initRnd() {
		if (rnd == null)
			rnd = new Random();
	}

	public static int[] randomNum(int size) {
		int min = 0;
		int max = size;
		final int LEN = max - min; // 种子个数
		initRnd(); // 初始化随机数发生器
		int[] seed = new int[LEN]; // 种子数组
		for (int i = 0, n = min; i < LEN;)
			seed[i++] = n++; // 初始化种子数组
		for (int i = 0, j = 0, t = 0; i < 3; ++i) {
			j = rnd.nextInt(LEN - i) + i;
			t = seed[i];
			seed[i] = seed[j];
			seed[j] = t;
		}
		int[] la = Arrays.copyOf(seed,3);
		return la;
	}

	// Unit Testing
	public static void main(String[] args) {
		Map<Integer, Map<String, String>> brandMap = MParseBrandScroll
				.getBrandInfo("", "beijing", "recommendBrand");

		int size = brandMap.size();
		int[] la = MParseBrandScroll.randomNum(size);
		for (int v : la) {
			Map<String, String> bMap = brandMap.get(v);
			for (String key : bMap.keySet()) {
				System.out.print("key=" + key + "-value=" + bMap.get(key));
			}

			System.out.println();
		}


	}
}
