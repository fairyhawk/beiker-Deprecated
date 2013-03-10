package com.beike.service.mobile.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.beike.util.lucene.APPSearchConstants;
import com.beike.util.lucene.AppIndexFileUtils;

public class AppIndexSearcherFactory {

	
	private AppIndexSearcherFactory(){}
	
	private static AppIndexSearcherFactory instance = null;
	
	private static final Map<String, IndexSearcher> goodsindexsearchercache = new HashMap<String, IndexSearcher>();
	
	private static final Map<String, IndexSearcher> branchindexsearchercache = new HashMap<String, IndexSearcher>();

	private static final Map<String,Long>	goodsindexupdatetimecache = new HashMap<String, Long>();
	
	
	private static final Map<String,Long>	branchindexupdatetimecache = new HashMap<String, Long>();

	public synchronized static AppIndexSearcherFactory getInstance(){
		if(instance == null){
			instance = new AppIndexSearcherFactory();
		}
		
		return instance;
	}
	
	
	public static IndexSearcher getGoodsIndexSearcher(String city_pinyin) throws Exception{
		return createGoodsIndexsearcher(city_pinyin);
	}
	
	
	public static IndexSearcher getBranchIndexSearcher() throws Exception{
		return createBranchIndexsearcher();
	}
	private static IndexSearcher createBranchIndexsearcher() throws Exception{
		IndexSearcher indexSearcher = branchindexsearchercache.get(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_BRANCH);
		String indexpath = AppIndexFileUtils.getBranchIndexV2Dir();
			Long indexCreateTime = null;
			if (indexSearcher == null) {
				File index = new File(indexpath);
				Directory directory = FSDirectory.open(index);
				IndexReader indexReader = IndexReader.open(directory,false);
				indexSearcher = new IndexSearcher(indexReader);
				indexCreateTime = index.lastModified();
				updateBranchcache(indexSearcher, indexCreateTime);
			} else {
				// 判断索引创建时间
				File indextime = new File(indexpath);
				indexCreateTime = indextime.lastModified();
				
				if (indexCreateTime > branchindexupdatetimecache.get(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_BRANCH)) {
					//关闭旧indexsearcher
					indexSearcher.close();
					File index = new File(indexpath);
					Directory directory = FSDirectory.open(index);
					IndexReader indexReader = IndexReader.open(directory,false);
					indexSearcher = new IndexSearcher(indexReader);
					indexCreateTime = index.lastModified();
					updateBranchcache(indexSearcher, indexCreateTime);
				}else{
					logger.info("cached app v2 branch indexsearcher");
				}
			}
		return indexSearcher;
	}
	
	
	
	static final Log logger = LogFactory.getLog(AppIndexSearcherFactory.class);
	private synchronized static IndexSearcher createGoodsIndexsearcher(String city_pinyin) throws Exception{
		IndexSearcher indexSearcher = goodsindexsearchercache.get(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_GOODS + city_pinyin);
		String indexpath = AppIndexFileUtils.getGoodsIndexV2Dir(city_pinyin);
			Long indexCreateTime = null;
			if (indexSearcher == null) {
				File index = new File(indexpath);
				Directory directory = FSDirectory.open(index);
				IndexReader indexReader = IndexReader.open(directory,false);
				indexSearcher = new IndexSearcher(indexReader);
				indexCreateTime = index.lastModified();
				updategoodscache(city_pinyin, indexSearcher, indexCreateTime);
			} else {
				// 判断索引创建时间
				File indextime = new File(indexpath);
				indexCreateTime = indextime.lastModified();
				
				if (indexCreateTime > goodsindexupdatetimecache.get(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_GOODS + city_pinyin)) {
					//关闭旧indexsearcher
					indexSearcher.close();
					File index = new File(indexpath);
					Directory directory = FSDirectory.open(index);
					IndexReader indexReader = IndexReader.open(directory,false);
					indexSearcher = new IndexSearcher(indexReader);
					indexCreateTime = index.lastModified();
					updategoodscache(city_pinyin,indexSearcher, indexCreateTime);
				}else{
					logger.info("cached app v2 goods indexsearcher cache " + APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_GOODS + city_pinyin);
				}
			}
		return indexSearcher;
	}
	
	
	static void updategoodscache(String citypinyin,IndexSearcher indexSearcher,Long updatetime){
		goodsindexsearchercache.put(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_GOODS + citypinyin, indexSearcher);
		goodsindexupdatetimecache.put(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_GOODS + citypinyin, updatetime);
		logger.info("create app v2 goods indexsearcher cache");
	}
	
	
	static void updateBranchcache(IndexSearcher indexSearcher,Long updatetime){
		branchindexsearchercache.put(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_BRANCH, indexSearcher);
		branchindexupdatetimecache.put(APPSearchConstants.APPV2_INDEXSEARCHER_CACHEKEY_BRANCH, updatetime);
		logger.info("create app v2 branch indexsearcher cache");
	}
}
