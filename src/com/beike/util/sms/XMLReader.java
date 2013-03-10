package com.beike.util.sms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Copyright: Copyright (c)2010 Company: YeePay.com Description:
 * 
 * @author: wenhua.cheng
 * @version: 1.0 Create at: 2011-4-20
 */
public class XMLReader {

	private static void parseURL(String strURL) {
		try {
			// System.out.println("+++++++++++++");
			// System.out.println(strURL);
			InputStream urlStream = new ByteArrayInputStream(strURL.getBytes());
			String[] strLID = null;
			// URL url = new URL(strURL);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder doc_builder = dbf.newDocumentBuilder();
			System.out.println(strURL.getBytes());
			Document doc = doc_builder.parse(new InputSource(
					new InputStreamReader(urlStream, "GBK")));

			System.out.println(doc);
			// Get root node
			Element rootElement = doc.getDocumentElement();
			if (rootElement == null) {
				return;
			}

			NodeList paramNodeLists = doc.getElementsByTagName("LID");
			int iNodeCount = paramNodeLists.getLength();
			strLID = new String[iNodeCount];
			for (int i = 0; i < iNodeCount; i++) {
				strLID[i] = paramNodeLists.item(i).getFirstChild()
						.getNodeValue().trim();
				System.out.println("[" + strLID[i] + "]");
			}

			// this.strLID = strLID;
		} catch (Exception e) {
			// this.strLID = null;
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {

		parseURL("<?xml version='1.0' encoding='gbk'?><response><code>01</code><message><desmobile>13900000000"
				+ "</desmobile><msgid>200811041234253654785</msgid></message><message>"
				+ "<desmobile>13400000000</desmobile>	<msgid>200811041234253654786</msgid>"
				+ "</message></response>");
	}

}
