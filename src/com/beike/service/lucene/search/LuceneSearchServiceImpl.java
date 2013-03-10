package com.beike.service.lucene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.lucene.LuceneSearchDao;

@Service("luceneSearchService")
public class LuceneSearchServiceImpl implements LuceneSearchService {

	Logger logger = Logger.getLogger(LuceneSearchServiceImpl.class);
	@Autowired
	private LuceneSearchDao luceneSearchDao;

	@Override
	public Map<String, Object> getSearchGoodsMap(String goods_keyword,
			String city_en_name, int currentPage, int pagesize) {
		return luceneSearchDao.queryGoodsResult(goods_keyword, city_en_name, currentPage, pagesize);
	}


	@Override
	public List<Long> getNextPageID(List<Long> searchedids, int currentpage,
			int pagesize) {
		if (searchedids.size() <= 0) {
			return null;
		}
		List<Long> nextPageIds = new ArrayList<Long>();
		int searchedidSize = searchedids.size();
		int nextPageOffset = currentpage * pagesize;
		if (nextPageOffset > searchedidSize) {
			nextPageOffset = searchedidSize;
		}
		for (int i = ((currentpage - 1) * pagesize) + 1; i < nextPageOffset; i++) {
			nextPageIds.add(searchedids.get(i));
		}
		return nextPageIds;
	}

	@Override
	public Map<String, Object> getSearchBrandMap(String brand_keyword,
			String city_en_name, int currentPage, int pagesize) {
		return luceneSearchDao.queryBrandResult(brand_keyword, city_en_name, currentPage, pagesize);
	}



}
