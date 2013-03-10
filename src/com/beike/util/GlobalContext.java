package com.beike.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Title: GlobalContext
 * </p>
 * <p>
 * Description: GlobalContext
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @author ye.tian
 * @version 1.0
 */
public class GlobalContext {
	private static final Log logger = LogFactory.getLog(GlobalContext.class);
	public static String FILESEPARATOR = System.getProperty("file.separator");
	private static Map<String, String> context = new HashMap<String, String>();
	private static ResourceBundle systemProperties;
	static {
		try {
			systemProperties = ResourceBundle.getBundle("project");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("no systemCfg.properties found!!" + e.getMessage());
		}
	}

	public static String getAppPath(String path) {
		String apphome = getAppHome();
		if (apphome == null || path == null) {
			return null;
		}
		if (apphome.endsWith(FILESEPARATOR) || path.startsWith(FILESEPARATOR)) {
			return apphome + path;
		} else {
			return apphome + FILESEPARATOR + path;
		}
	}

	public static String getAppHome() {
		return context.get(Thread.currentThread().getContextClassLoader()
				.toString());
	}

	public static void setAppHome(String appHome) {
		context.put(Thread.currentThread().getContextClassLoader().toString(),
				appHome);
	}

	public static String getProperty(String name) {
		try {
			return systemProperties.getString(name);
		} catch (Exception e) {
			logger.error("get property fail! name[" + name + "] exception["
					+ e.getMessage() + "]");
			e.printStackTrace();
			return null;
		}
	}
}
