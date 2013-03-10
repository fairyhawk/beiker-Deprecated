package com.beike.service.lucene.search;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.beike.util.StringUtils;

public class IndexSearcherFactory {

	private IndexSearcherFactory() {}

	Logger logger = Logger.getLogger(IndexSearcherFactory.class);

	// indexsearcher缓存
	private static Map<String, IndexSearcher> indexSearcherCache = new HashMap<String, IndexSearcher>();

	// 索引修改时间
	private static Map<String, Long> indexModifyTime = new HashMap<String, Long>();

	private static IndexSearcherFactory factory = null;

	private synchronized Long getIndexModifyTime(String city_en_name) {
		return indexModifyTime.get(city_en_name);
	}

	public synchronized static IndexSearcherFactory getInstance() {
		if (factory == null) {
			factory = new IndexSearcherFactory();
		}
		return factory;
	}

	/**
	 * 设置indexsearch缓存,索引创建时间
	 * @author janwen
	 * @time Nov 24, 2011 11:04:44 AM
	 * @param key
	 * @param indexSearcher
	 */
	private synchronized void setIndexSearcher(String cache_key, IndexSearcher indexSearcher, Long modifyTime) {
		indexSearcherCache.put(cache_key, indexSearcher);
		indexModifyTime.put(cache_key, modifyTime);
		logger.info("create web indexsearcher " + cache_key);
	}

	private synchronized IndexSearcher createIndexSearcher(String indexdir, String cache_key) throws Exception {
		IndexSearcher indexSearcher = indexSearcherCache.get(cache_key);
		Long indexCreateTime = null;
		if (indexSearcher == null) {
			File index = new File(indexdir);
			Directory directory = FSDirectory.open(index);
			IndexReader indexReader = IndexReader.open(directory, true);
			indexSearcher = new IndexSearcher(indexReader);
			indexCreateTime = index.lastModified();
			setIndexSearcher(cache_key, indexSearcher, indexCreateTime);
			logger.info("reload indexsearcher " + cache_key);
		} else {
			// 判断索引创建时间
			File indextime = new File(indexdir);
			indexCreateTime = indextime.lastModified();

			if (indexCreateTime > getIndexModifyTime(cache_key)) {
				//关闭旧indexsearcher
				indexSearcher.close();
				File index = new File(indexdir);
				Directory directory = FSDirectory.open(index);
				IndexReader indexReader = IndexReader.open(directory, true);
				indexSearcher = new IndexSearcher(indexReader);
				indexCreateTime = index.lastModified();
				setIndexSearcher(cache_key, indexSearcher, indexCreateTime);
			} else {
				logger.info("cached web indexsearcher " + cache_key);
			}
		}

		return indexSearcher;
	}

	/**
	 * @author janwen
	 * @time Nov 24, 2011 2:24:10 PM
	 * @param key
	 * @param indexdir
	 *        索引目录
	 * @param indexdirbak
	 *        索引备份目录
	 * @return IndexSearcher
	 * @throws Exception
	 */
	public synchronized IndexSearcher getIndexSearcher(String cache_key, String indexdir, String indexdirbak) throws Exception {

		try {
			return createIndexSearcher(indexdir, cache_key);
		} catch (Exception e) {
			// 发生异常尝试备份索引
			if (StringUtils.validNull(indexdirbak)) {
				return createIndexSearcher(indexdirbak, cache_key);
			}
			return null;
		}
	}
}
