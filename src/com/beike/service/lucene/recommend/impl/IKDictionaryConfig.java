package com.beike.service.lucene.recommend.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.wltea.analyzer.cfg.DefualtConfig;
import org.wltea.analyzer.dic.Dictionary;

public class IKDictionaryConfig {

	public static String getLast_update_time() {
		if (instance == null) {
			return null;
		}
		return last_update_time;
	}

	public static void setLast_update_time(String last_update_time) {
		IKDictionaryConfig.last_update_time = last_update_time;
	}

	private static String last_update_time;

	/**
	 * 
	 * @param reload
	 * @param usedwords
	 *            不能为空
	 * @param unusedwords
	 *            不能为空
	 */
	private IKDictionaryConfig() {
		Dictionary.initial(DefualtConfig.getInstance());
	}

	private static IKDictionaryConfig instance = null;

	private static final Logger logger = Logger
			.getLogger(IKDictionaryConfig.class);

	public static IKDictionaryConfig getInstance(boolean isInital,
			List<String> usedwords, List<String> unusedwords) {
		if (isInital) {
			synchronized (IKDictionaryConfig.class) {
				instance = new IKDictionaryConfig();
			}
		}
		addWords(usedwords);
		disableWords(unusedwords);

		return instance;
	}

	private static void addWords(List<String> usedwords) {
		Dictionary.getSingleton().addWords(usedwords);
	}

	private static void disableWords(List<String> unusedwords) {
		Dictionary.getSingleton().disableWords(unusedwords);
	}
}
