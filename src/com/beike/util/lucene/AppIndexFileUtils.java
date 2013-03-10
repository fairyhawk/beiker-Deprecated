package com.beike.util.lucene;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * app index util
 * @author janwen
 * Oct 29, 2012
 */
public class AppIndexFileUtils {

	private AppIndexFileUtils() {
	}

	public static Map<String, String[]> getAlertEmail(String city_en_name,
			String type, String keyword) {
		Properties properties = new Properties();
		try {
			properties.load(AppIndexFileUtils.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		synchronized (AppIndexFileUtils.class) {
			Map<String, String[]> alermail = new HashMap<String, String[]>();
			alermail.put("alertaddress", properties.getProperty("alertemail")
					.split(","));
			String[] emailparams = new String[] { Calendar.getInstance().getTime().toString(),
					city_en_name, type, keyword };
			alermail.put("emailparams", emailparams);
			return alermail;
		}

	}

	private static AppIndexFileUtils instance = null;

	public static AppIndexFileUtils getInstance() {
		if (instance == null) {
			instance = new AppIndexFileUtils();
		}
		return instance;
	}

	private static Logger logger = Logger
			.getLogger(AppIndexFileUtils.class);

	
	/**
	 * 
	 * janwen
	 * @param citypinyin
	 * @return 商品索引目录
	 *
	 */
	public static String getGoodsIndexV2Dir(String citypinyin) {
		Properties properties = new Properties();
		try {
			properties.load(AppIndexFileUtils.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String indexDir = null;
		synchronized (AppIndexFileUtils.class) {
			indexDir = MessageFormat.format(properties
					.getProperty("luceneIndexMobileV2Dir"),  citypinyin, "goods");
		}

		logger.info("app lucene V2 商品索引文件路径=" + indexDir);
		return indexDir;
	}
	
	
	public static String getBranchIndexV2Dir() {
		Properties properties = new Properties();
		try {
			properties.load(AppIndexFileUtils.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String indexDir = null;
		synchronized (AppIndexFileUtils.class) {
			indexDir = MessageFormat.format(properties
					.getProperty("luceneIndexMobileV2Dir"),  "branch", "");
		}

		logger.info("app lucene V2 分店索引文件路径=" + indexDir);
		return indexDir;
	}
	
	
	

	

	
}
