package com.beike.service.sensitiveword;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.focustech.abiz.analysis.SWAnalyzer;
import com.focustech.abiz.analysis.entity.ForbiddenLevel;
import com.focustech.abiz.analysis.entity.Keyword;
import com.focustech.abiz.analysis.entity.PatternStyle;
import com.focustech.abiz.analysis.fragment.QPContainsFragment;
import com.focustech.abiz.analysis.fragment.QPFragment;

/**
 * 敏感过滤服务 敏感词位于classpath路径minganci.Dat 敏感词文件格式:一行一句 
 * ****** 
 * 共产党*
 * 江泽民*
 * ******
 * 
 * @author janwen
 * 
 */
public class SensitivewordFilterService {

	private static SensitivewordFilterService sensitivewordFilterService = null;

	private static SWAnalyzer mgcAnalyzer = new SWAnalyzer();
	private static Long timestamp = null;


	private SensitivewordFilterService() {
		File mgcfile = new File(SensitivewordFilterService.class.getResource(
				"/").getPath()
				+ "minganci.Dat");
		timestamp = mgcfile.lastModified();
		List<Keyword> mgcList = new ArrayList<Keyword>();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(SensitivewordFilterService.class
							.getResourceAsStream("/minganci.Dat"), "UTF-8"),
					512);
			String mgcWord = null;
			do {
				mgcWord = bufferedReader.readLine();
				if (mgcWord != null) {
					mgcList.add(new Keyword(mgcWord, ForbiddenLevel.FORBID,
							PatternStyle.FUZZY));
				}

			} while (mgcWord != null);
			mgcAnalyzer.addWord(mgcList.toArray(new Keyword[mgcList.size()]));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否包含改敏感词
	 * 
	 * @param word
	 * @return
	 */
	public static boolean containsWord(String word) {
		String afterWord = mgcAnalyzer.analysis(word, new QPContainsFragment());
		if(afterWord!=null && afterWord.contains(QPContainsFragment.CONTAINS_WORD)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 敏感词库是否更新
	 * 
	 * @return
	 */
	private static boolean update() {
		File mgcfile = null;

		mgcfile = new File(SensitivewordFilterService.class.getResource("/")
				.getPath()
				+ "minganci.Dat");
		if (mgcfile.lastModified() > timestamp) {
			return true;
		}

		return false;
	}

	public static synchronized SensitivewordFilterService getSingletonInstance() {
		if (sensitivewordFilterService == null) {
			sensitivewordFilterService = new SensitivewordFilterService();
		} else {
			if (update()) {
				sensitivewordFilterService = new SensitivewordFilterService();
			}
		}
		return sensitivewordFilterService;
	}

	private static final Log logger = LogFactory
			.getLog(SensitivewordFilterService.class);

	/**
	 * 
	 * @param text
	 * @return 加星处理结果
	 */
	public static String getFilterResult(String text) {
		return mgcAnalyzer.analysis(text, new QPFragment());
	}
}
