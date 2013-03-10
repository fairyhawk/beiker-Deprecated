package com.beike.util.sms;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Copyright: Copyright (c)2010 Company: YeePay.com Description:
 * 
 * @author: wenhua.cheng
 * @version: 1.0 Create at: 2011-4-20
 */

public class SAXXMLReader extends DefaultHandler {

	private static final String ELEMENT_CODE = "code";
	private static final String ELEMENT_MOBILE = "desmobile";
	private static final String ELEMENT_MSGID = "msgid";
	private static final String ELEMENT_RCODE = "rcode";
	private String tagValue;
	private long starttime;
	private long endtime;
	private static Map<String, String> xmlMap = new HashMap<String, String>();

	// 批量发送时手机号可能会有重复。但msgid不会，将msgid放入msgidList中，再导到MAP中作为key
	private static List<String> mobileList = new ArrayList<String>();
	private static List<String> msgidList = new ArrayList<String>();

	public  static Map<String, String> parseSms(String strXmlContent) {

		parseXML(strXmlContent);

		// 将list中的导入到MAP中

		int msgidListSize = msgidList.size();
		int mobileListSize = mobileList.size();

		if (msgidListSize > 0 && mobileListSize == msgidListSize) {

			for (int j = 0; j < msgidListSize; j++) {

				xmlMap.put(msgidList.get(j), mobileList.get(j));
			}
		}

		return xmlMap;
	}

	public static void   parseXML(String strXmlContent) {

		// String filename = "J:/smstest.xml";
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new ByteArrayInputStream(strXmlContent.getBytes()),
					new SAXXMLReader());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 开始解析XML文件
	public void startDocument() throws SAXException {
		// 可以在此初始化变量等操作
		System.out.println("~~~~解析文档开始~~~");

		starttime = System.currentTimeMillis();
	}

	// 结束解析XML文件
	public void endDocument() throws SAXException {

		endtime = System.currentTimeMillis();
		System.out.println("~~~~解析文档结束~~~");
		System.out.println("共用" + (endtime - starttime) + "毫秒");

	}

	/**
	 * 在解析到一个开始元素时会调用此方法.但是当元素有重复时可以自己写算法来区分
	 * 
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// System.out.println("startElement-key:"+qName);

		if (attributes != null && attributes.getLength() != 0) {
			System.out.print("--" + "该标签有属性值:");
			for (int i = 0; i < attributes.getLength(); i++) {
				System.out.print(attributes.getQName(i) + "=");
				System.out.print(attributes.getValue(i) + " ");
			}
			System.out.println();
		}

	}

	/**
	 * 在遇到结束标签时调用此方法
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (ELEMENT_CODE.equals(qName)) {

			System.out.println("ELEMENT_CODE=" + tagValue);
			xmlMap.put(qName, tagValue);
		}
		if (ELEMENT_RCODE.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("ELEMENT_RCODE=" + tagValue);
		}
		if (ELEMENT_MOBILE.equals(qName)) {

			mobileList.add(tagValue);
			System.out.println("ELEMENT_MOBILE=" + tagValue);

		}

		if (ELEMENT_MSGID.equals(qName)) {

			msgidList.add(tagValue);
			System.out.println("ELEMENT_MSGID=" + tagValue);
		}

		// System.out.print("endElement-value:");
		// System.out.println(tagValue);
	}

	/**
	 * 所有的XML文件中的字符会放到ch[]中
	 */
	public void characters(char ch[], int start, int length)
			throws SAXException {
		tagValue = new String(ch, start, length).trim();
	}




	public static void main(String[] args) {

		SAXXMLReader xmlReader = new SAXXMLReader();

		xmlReader
				.parseSms("<?xml version='1.0' encoding='gbk'?><response><code>01</code><message><desmobile>13900000000"
						+ "</desmobile><msgid>200811041234253654785</msgid></message><message>"
						+ "<desmobile>13400000000</desmobile>	<msgid>200811041234253654786</msgid>"
						+ "</message><message>"
						+ "<desmobile>13400000001</desmobile>	<msgid>2008110412342536547898</msgid>"
						+ "</message></response>");

		Set<String> keySet = (Set<String>) xmlMap.keySet();
		for (String itemKey : keySet) {

			String value = xmlMap.get(itemKey);

			System.out
					.println("key===" + itemKey + "+++++++++++value=" + value);
		}

	}
}