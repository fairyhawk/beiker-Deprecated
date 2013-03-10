package com.beike.common.bean.trx;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Copyright: Copyright (c)2010 Company: YeePay.com Description:
 * 
 * @author: renli.yu
 * @version: 1.0 Create at: 2011-9-1
 */

public class SAXXMLReaderAli extends DefaultHandler {
	private static final String IS_SUCCESS = "is_success";
	private static final String SIGN = "sign";
	private static final String SIGN_TYPE = "sign_type";
	private static final String BUYER_EMAIL = "buyer_email";
	private static final String BUYER_ID = "buyer_id";
	private static final String IS_TOTAL_FEE_ADJUST = "is_total_fee_adjust";
	private static final String PRICE = "price";
	private static final String OUT_TRADE_NO = "out_trade_no";
	private static final String TRADE_NO = "trade_no";
	private static final String ERROR = "error";
	private static final String TRADE_STATUS = "trade_status";
	/*private static final String GMT_LAST_MODIFIED_TIME = "gmt_last_modified_time";
	private static final String GMT_PAYMENT = "gmt_payment";
	private static final String ELEMENT_MSGID = "buyer_email";
	private static final String ELEMENT_RCODE = "gmt_last_modified_time";*/
	private String tagValue;
	private long starttime;
	private long endtime;
	private static Map<String, String> xmlMap = new HashMap<String, String>();

	public  static Map<String, String> parseSms(String strXmlContent) {

		parseXML(strXmlContent);

		// 将list中的导入到MAP中

		return xmlMap;
	}

	public static void parseXML(String strXmlContent) {

		// String filename = "J:/smstest.xml";
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new ByteArrayInputStream(strXmlContent.getBytes()),
					new SAXXMLReaderAli());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 开始解析XML文件
	@Override
	public void startDocument() throws SAXException {
		// 可以在此初始化变量等操作
		System.out.println("~~~~解析文档开始~~~");

		starttime = System.currentTimeMillis();
	}

	// 结束解析XML文件
	@Override
	public void endDocument() throws SAXException {

		endtime = System.currentTimeMillis();
		System.out.println("~~~~解析文档结束~~~");
		System.out.println("共用" + (endtime - starttime) + "毫秒");

	}

	/**
	 * 在解析到一个开始元素时会调用此方法.但是当元素有重复时可以自己写算法来区分
	 * 
	 */
	@Override
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
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (IS_SUCCESS.equals(qName)) {
			System.out.println("IS_SUCCESS=" + tagValue);
			xmlMap.put(qName, tagValue);
		}
		if (SIGN.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("SIGN=" + tagValue);
		}
		if (SIGN_TYPE.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("SIGN_TYPE=" + tagValue);
		}
		if (BUYER_EMAIL.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("BUYER_EMAIL=" + tagValue);
		}
		if (BUYER_ID.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("BUYER_ID=" + tagValue);
		}
		if (IS_TOTAL_FEE_ADJUST.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("IS_TOTAL_FEE_ADJUST=" + tagValue);
		}
		if (PRICE.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("PRICE=" + tagValue);
		}
		
		if (OUT_TRADE_NO.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("OUT_TRADE_NO=" + tagValue);
		}
		if (TRADE_NO.equals(qName)) {
			xmlMap.put(qName, tagValue);
			System.out.println("TRADE_NO=" + tagValue);
		}
		if(ERROR.equals(qName)){
			xmlMap.put(qName, tagValue);
			System.out.println("ERROR=" + tagValue);
		}
		if(TRADE_STATUS.equals(qName)){
			xmlMap.put(qName, tagValue);
			System.out.println("ERROR=" + tagValue);
		}
		
		// System.out.print("endElement-value:");
		// System.out.println(tagValue);
	}

	/**
	 * 所有的XML文件中的字符会放到ch[]中
	 */
	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		tagValue = new String(ch, start, length).trim();
	}




}