package com.beike.util.htmlparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;

public class MParseGoodsDetail {
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_PATH);
	private static String ENCODE = "ISO-8859-1";
	private static final String PATH = propertyUtil.getProperty("ONLINE_IMAGE_PATH_DETAIL");
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
	 * 获取商品ID
	 * 
	 * @param path
	 * @param city
	 * @param fileName
	 * @return
	 */
	public static Map<String,String> getGoodsIds(String fileName) {
		Map<String,String> goodsMap = new LinkedHashMap<String,String>();
		StringBuilder infoSb = null;
		try {
			StringBuilder filePath = new StringBuilder();
			filePath.append(PATH);
			filePath.append(fileName);
			filePath.append(STUFF);
			Parser parser = new Parser(filePath.toString());
			NodeFilter divFilter =  new NodeClassFilter(Div.class);
			NodeList divList = parser.extractAllNodesThatMatch(divFilter);
			NodeList infoList = null;
			Node divNode = null;
			Node infoNode = null;
			String infoStr = null;
			for (int i = 0; i < divList.size(); i++) {
				divNode = divList.elementAt(i);
				infoList = divNode.getChildren();
				if(null!=infoList&&infoList.size()>0){
					infoSb = new StringBuilder("");
					for(int j = 0; j<infoList.size();j++){
						infoNode = infoList.elementAt(j);
						infoStr = new String(infoNode.toHtml().getBytes("ISO-8859-1"),"UTF-8");
						infoSb.append(infoStr);
					}
				}
				
				goodsMap.put(String.valueOf(i), infoSb.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return goodsMap;
	}

	public static void main(String[] args) {
		Map<String,String> goodsMap = MParseGoodsDetail.getGoodsIds("16680");
		for(String key : goodsMap.keySet()){
			System.out.println("key="+key);
			System.out.println("value="+goodsMap.get(key));
			System.out.println("-------------------------");
		}
	}

}
