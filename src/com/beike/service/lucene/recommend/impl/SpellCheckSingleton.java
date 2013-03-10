package com.beike.service.lucene.recommend.impl;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.beike.util.lucene.IndexDatasourcePathUtil;

public class SpellCheckSingleton {

	private static SpellChecker spellChecker = null;


	
	private static SpellCheckSingleton instance= null;

	public static String getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(String last_update_time) {
		SpellCheckSingleton.last_update_time = last_update_time;
	}

	private static String last_update_time;


	/**
	 * 构造spellchecker
	 */
	private SpellCheckSingleton() {
		
	}

	public SpellChecker getSpellChecker() {
		return spellChecker;
	}


	/**
	 * 
	 * janwen
	 * 
	 * @param isIK
	 *            是否加载ik
	 * @return
	 * 
	 */
	public static SpellCheckSingleton getInstance(boolean isIK) {
		if (instance == null) {
			synchronized (SpellCheckSingleton.class) {
				if (instance == null) {
					Directory spellcheckIndexDir = null;
					try {
						spellcheckIndexDir = FSDirectory.open(new File(
								IndexDatasourcePathUtil
										.getSpellCheckerIndexDir()));
						spellChecker = new SpellChecker(spellcheckIndexDir);
						instance = new SpellCheckSingleton();
						
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}

}
