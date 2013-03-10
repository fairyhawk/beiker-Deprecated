package com.beike.util.lucene;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;


public class LuceneSearchHelper {

	private LuceneSearchHelper() {
	}

	private static LuceneSearchHelper indexHelper;

	public static LuceneSearchHelper getIndexHelper() {
		if (indexHelper == null) {
			indexHelper = new LuceneSearchHelper();
		}
		return indexHelper;
	}
	static Logger logger = Logger.getLogger(LuceneSearchHelper.class);
	
	public static String getIndexFilePath(String city_en_name,String type) throws IOException{
		Properties properties = new Properties();
		properties.load(LuceneSearchHelper.class.getResourceAsStream("/lucene.properties"));
		String indexDir = MessageFormat.format(properties.getProperty("luceneIndexDir"), city_en_name,type);
		logger.info("searche index dir=" + indexDir);
		return  indexDir;
	}
	
}
