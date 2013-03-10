	package com.beike.service.lucene.recommend.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.beike.dao.unionpage.UnionKWDao;
import com.beike.service.lucene.recommend.LuceneRecommendService;

/**
 * 
 * @author janwen May 17, 2012
 */
@Service("luceneRecommendService")
public class LuceneRecommendServiceImpl implements LuceneRecommendService {

	private static final IKAnalyzer ikAnalyzer = new IKAnalyzer(false);
	@Autowired
	private UnionKWDao kwDao;

	private static final Logger logger = Logger
			.getLogger(LuceneRecommendServiceImpl.class);

	@Override
	public String[] getSilimarWords(String originalword, int numSug)
			throws IOException {
		SpellCheckSingleton spCheckSingleton = SpellCheckSingleton
				.getInstance(false);
		SpellChecker spellChecker = spCheckSingleton.getSpellChecker();
		String new_update_time = kwDao.getKWUpdateTime();
		if (SpellCheckSingleton.getLast_update_time() == null
				|| !new_update_time.equals(SpellCheckSingleton
						.getLast_update_time())) {
			spCheckSingleton.setLast_update_time(new_update_time);
			StringReader dataReader = new StringReader(getKWData("0"));
			try {
				spellChecker.clearIndex();
				spellChecker.indexDictionary(
						new PlainTextDictionary(dataReader));
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				dataReader.close();
			}
		}
		
		return spellChecker.suggestSimilar(originalword,
				numSug);
	}

	private Long getPageSize(Long count) {
		Long pages = 0L;
		if (count % 1000L == 0) {
			pages = count / 1000L;
		} else {
			pages = (count / 1000L) + 1L;
		}
		return pages;
	}

	@Override
	public String getKWData(String isused) {
		StringBuilder kw_data = new StringBuilder();
		Long pages = getPageSize(kwDao.getKWCount(isused));
		// 分页查询数据库
		for (Long begin = 0L; begin < pages; begin++) {
			List<String> kw = kwDao.getUsedKW(isused, begin);
			for (int i = 0; i < kw.size(); i++) {
				kw_data.append(kw.get(i)).append("\n");
			}
		}
		return kw_data.toString();
	}

	private static List<String> wordSegmentation(String source) {
		TokenStream tokenStream = ikAnalyzer.tokenStream("ingore",
				new StringReader(source));
		List<String> tokens = new ArrayList<String>();
		CharTermAttribute charTermAttribute = tokenStream
				.getAttribute(CharTermAttribute.class);
		try {
			while (tokenStream.incrementToken()) {
				String term = charTermAttribute.toString();

				if (term.length() > 1 && !term.matches("\\d+")) {
					tokens.add(term.toUpperCase());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return tokens;
		}
		return tokens;
	}

	@Override
	public String[] getRecommend(String goodsname, int numRec)
			throws IOException {

		String new_update_time = kwDao.getKWUpdateTime();
		boolean isInital = (IKDictionaryConfig.getLast_update_time() == null);
		Set<String> similar_kw_set = new HashSet<String>();
		List<String> use_data_list = new ArrayList<String>();
		List<String> unuse_data_list = new ArrayList<String>();
		if (isInital
				|| !new_update_time.equals(IKDictionaryConfig
						.getLast_update_time())) {
			String original_use_data = getKWData("0");
			String original_unuse_data = getKWData("1");
			if (!"".equals(original_use_data)) {
				String[] use_data = original_use_data.split("\\n");
				use_data_list = Arrays.asList(use_data);
			}
			if (!"".equals(original_unuse_data)) {
				String[] unuse_data = original_unuse_data.split("\\n");
				unuse_data_list = Arrays.asList(unuse_data);
			}
		}
		IKDictionaryConfig
				.getInstance(isInital, use_data_list, unuse_data_list);
		IKDictionaryConfig.setLast_update_time(new_update_time);
		List<String> tokens = wordSegmentation(goodsname);
		// token 返回
		similar_kw_set.addAll(tokens);
		for (int i = 0; i < tokens.size(); i++) {
			similar_kw_set.addAll(Arrays.asList(getSilimarWords(tokens.get(i),
					numRec)));
		}

		return similar_kw_set.toArray(new String[] {});
	}

}
