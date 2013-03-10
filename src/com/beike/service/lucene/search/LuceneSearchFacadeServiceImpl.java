package com.beike.service.lucene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.catlog.service.BrandCatlogService;
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.lucene.LuceneSearchDao;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.lucene.IndexDatasourcePathUtil;
import com.beike.util.lucene.LuceneSearchConstants;

@Service("luceneSearchFacadeService")
public class LuceneSearchFacadeServiceImpl implements LuceneSearchFacadeService {

	@Autowired
	private GoodsCatlogService goodsCatlogService;
	@Autowired
	private LuceneSearchService luceneSearchService;

	@Autowired
	private BrandCatlogService brandCatlogService;
	@Autowired
	private GoodsDao goodsDao;
	
	@Autowired
	private LuceneSearchDao luceneSearchDao;

	private MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	public static int nextPageOffset(int currentPage, int pagesize, int hits) {
		int searchedhits = hits;
		int nextPageOffset = currentPage * pagesize;
		if (nextPageOffset >= searchedhits) {
			nextPageOffset = searchedhits;
		}
		return nextPageOffset;
	}

	@Override
	public Map<String, Object> getSearchGoodsMap(String goods_keyword,
			String city_en_name, int currentPage, int pagesize) {
		Map<String, Object> tempMap = luceneSearchService.getSearchGoodsMap(
				goods_keyword, city_en_name, currentPage, pagesize);
		List<Long> searchedid = (List<Long>) tempMap
				.get(LuceneSearchConstants.SEARCHED_RESULT_ID);
		List<Long> normalid = new ArrayList<Long>();
		List<Long> validid = new ArrayList<Long>();
		List<Long> nextpageid = new ArrayList<Long>();
		Integer status = null;
		Long goodsid;

		for (int i = 0; i < searchedid.size(); i++) {
			goodsid = searchedid.get(i);
			
			status = (Integer) memCacheService.get("lucene_goods" + goodsid);
			if (status != null && status >= 0) {
				// (status==0)?(validid.add(goodsid)):(normalid.add(goodsid));
				if (status == 0) {
					validid.add(goodsid);
				} else {
					normalid.add(goodsid);
				}
			} else {
				status = goodsDao.getLuceneGoodsById(goodsid);
				memCacheService.set("lucene_goods" + goodsid, status, 3600);
				if (status == 0) {
					validid.add(goodsid);
				} else {
					normalid.add(goodsid);
				}
			}
		}
		// 重新组合所有goodsid
		normalid.addAll(validid);
		int nextPageOffset = nextPageOffset(currentPage, pagesize, normalid
				.size());
		// 返回当前页的goodsid
		for (int i = ((currentPage - 1) * pagesize); i < nextPageOffset; i++) {
			nextpageid.add(normalid.get(i));
		}
		tempMap
				.put(LuceneSearchConstants.SEARCH_RESULT_NEXTPAGE_ID,
						nextpageid);
		return tempMap;
	}

	@Override
	public List<GoodsForm> getSearchGoodsResult(List<Long> nextPageid) {
		List<GoodsForm> goodsResult = goodsCatlogService
				.getGoodsFormFromId(nextPageid);
		if (goodsResult == null || nextPageid == null || nextPageid.size() <= 0
				|| goodsResult.size() <= 0) {
			return goodsResult = new ArrayList<GoodsForm>();
		}
		return goodsResult;
	}

	@Override
	public Map<String, Object> getSearchBrandMap(String brand_keyword,
			String city_en_name, int currentPage, int pagesize) {
		Map<String, Object> tempMap = luceneSearchService.getSearchBrandMap(
				brand_keyword, city_en_name, currentPage, pagesize);
		List<Long> searchedid = (List<Long>) tempMap
				.get(LuceneSearchConstants.SEARCHED_RESULT_ID);
		List<Long> normalid = new ArrayList<Long>();
		List<Long> nextpageid = new ArrayList<Long>();
		Long brandid;
		// 0不可用、1可用
		Integer status;
		for (int i = 0; i < searchedid.size(); i++) {
			brandid = searchedid.get(i);

			status = (Integer) memCacheService.get("lucene_brand" + brandid);
			if (status != null) {
				if (status == 1) {
					normalid.add(brandid);
				}
			} else {
				if (brandCatlogService.checkMerchantStatus(brandid)) {
					memCacheService.set("lucene_brand" + brandid, 1, 3600);
					normalid.add(brandid);
				} else {
					memCacheService.set("lucene_brand" + brandid, 0, 3600);
				}
			}
		}
		int nextPageOffset = nextPageOffset(currentPage, pagesize, normalid
				.size());
		// 返回当前页的brandid
		for (int i = ((currentPage - 1) * pagesize); i < nextPageOffset; i++) {
			nextpageid.add(normalid.get(i));
		}
		tempMap
				.put(LuceneSearchConstants.SEARCH_RESULT_NEXTPAGE_ID,
						nextpageid);
		tempMap.put(LuceneSearchConstants.SEARCH_RESULTS_COUNT, normalid.size());
		return tempMap;
	}

	@Override
	public List<MerchantForm> getSearchBrandResult(List<Long> nextPageid) {
		List<MerchantForm> merchantResult = brandCatlogService
				.getGoodsFromIds(nextPageid);
		if (nextPageid == null || nextPageid.size() <= 0
				|| merchantResult.size() <= 0) {
			return merchantResult = new ArrayList<MerchantForm>();
		}
		return merchantResult;
	}



	@Override
	public List<Long> getSearchBranchMap(String keyword,
			String citypinyin, String type) {
		String indexdir = IndexDatasourcePathUtil.getIndexDir(citypinyin
				.toLowerCase(), LuceneSearchConstants.SEARCH_TYPE_BRANCH);
		return luceneSearchDao.queryBranchResult(indexdir, keyword, citypinyin, type);
	}

}
