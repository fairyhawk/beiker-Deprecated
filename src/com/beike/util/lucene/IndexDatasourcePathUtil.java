package com.beike.util.lucene;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/*
 * 通过配置文件获取索引路径/数据源路径
 */
public class IndexDatasourcePathUtil {

	private IndexDatasourcePathUtil() {
	}

	public static Map<String, String[]> getAlertEmail(String city_en_name,
			String type, String keyword) {
		Properties properties = new Properties();
		try {
			properties.load(IndexDatasourcePathUtil.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		synchronized (IndexDatasourcePathUtil.class) {
			Map<String, String[]> alermail = new HashMap<String, String[]>();
			alermail.put("alertaddress", properties.getProperty("alertemail")
					.split(","));
			String[] emailparams = new String[] { new Date().toLocaleString() + " 异常发生机器IP:" + LuceneAlertUtil.getIP(),
					city_en_name, type, keyword };
			alermail.put("emailparams", emailparams);
			return alermail;
		}

	}

	private static IndexDatasourcePathUtil instance = null;

	public static IndexDatasourcePathUtil getInstance() {
		if (instance == null) {
			instance = new IndexDatasourcePathUtil();
		}
		return instance;
	}

	private static Logger logger = Logger
			.getLogger(IndexDatasourcePathUtil.class);

	/**
	 * 
	 * @param tomcathome
	 *            tomcat安装路径
	 * @param city
	 * @param type
	 * @return 索引文件目录路径(主)
	 */
	public static String getIndexDir(String city, String type) {
		Properties properties = new Properties();
		try {
			properties.load(IndexDatasourcePathUtil.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String indexDir = null;
		// FIXME
		synchronized (IndexDatasourcePathUtil.class) {
			indexDir = MessageFormat.format(properties
					.getProperty("luceneIndexDir"),  city, type);
		}

		logger.info("lucene索引文件主路径=" + indexDir);
		return indexDir;
	}
	
	
	
	public static String getIndexDir4Mobile(String city, String type) {
		Properties properties = new Properties();
		try {
			properties.load(IndexDatasourcePathUtil.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String indexDir = null;
		// FIXME
		synchronized (IndexDatasourcePathUtil.class) {
			indexDir = MessageFormat.format(properties
					.getProperty("luceneIndexMobileDir"),  city, type);
		}

		logger.info("lucene手机客户端索引文件主路径=" + indexDir);
		return indexDir;
	}
	
	
	public static String getSpellCheckerIndexDir() {
		Properties properties = new Properties();
		try {
			properties.load(IndexDatasourcePathUtil.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {

			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String indexDir = null;
		synchronized (IndexDatasourcePathUtil.class) {
			indexDir = properties
					.getProperty("spellcheckerIndexDir");
		}

		logger.info("seo spellchecker index 文件主路径=" + indexDir);
		return indexDir;
	}
	/**
	 * 
	 * @param tomcathome
	 * @param city
	 * @param type
	 * @return 索引备份目录
	 */
	public static String getIndexDirBak(String city, String type) {
		Properties properties = new Properties();
		try {
			properties.load(IndexDatasourcePathUtil.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {
			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String indexDir = null;
		// FIXME
		synchronized (IndexDatasourcePathUtil.class) {
			indexDir = MessageFormat.format(properties
					.getProperty("luceneIndexDirBak"),  city, type);
		}
		logger.info("lucene索引文件备份路径=" + indexDir);
		return indexDir;
	}

	

	

	
	public static String[] getAvailableCity() {
		Properties properties = new Properties();
		try {
			properties.load(IndexDatasourcePathUtil.class
					.getResourceAsStream("/lucene.properties"));
		} catch (IOException e) {
			logger.error("lucene配置文件路径不对,请检查lucene.properties是否存在");
			throw new RuntimeException(
					"lucene配置文件路径不对,请检查lucene.properties是否存在");
		}
		String citylist = null;
		synchronized (IndexDatasourcePathUtil.class) {
			citylist = properties.getProperty("citylist");
		}
		return citylist.split(",");
	}
}
