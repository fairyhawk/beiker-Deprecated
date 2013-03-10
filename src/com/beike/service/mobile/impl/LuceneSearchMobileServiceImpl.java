package com.beike.service.mobile.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.spatial.tier.DistanceFieldComparatorSource;
import org.apache.lucene.spatial.tier.DistanceQueryBuilder;
import org.apache.lucene.spatial.tier.projections.CartesianTierPlotter;
import org.apache.lucene.spatial.tier.projections.IProjector;
import org.apache.lucene.spatial.tier.projections.SinusoidalProjector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.mobile.LuceneMobileAssistDao;
import com.beike.model.lucene.AppSearchedGoods;
import com.beike.service.mobile.LuceneSearchMobileService;
import com.beike.service.mobile.SearchParam;
import com.beike.service.mobile.SearchParamV2;
import com.beike.util.StringUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.lucene.APPSearchConstants;
import com.beike.util.lucene.AppIndexFileUtils;
import com.beike.util.lucene.IndexDatasourcePathUtil;
import com.beike.util.lucene.LuceneConstants;
import com.beike.util.lucene.LuceneUtil;
import com.beike.util.lucene.SortFieldMap;

@Service("appSearchService")
public class LuceneSearchMobileServiceImpl implements LuceneSearchMobileService {

	private static final Logger logger = Logger.getLogger(LuceneSearchMobileServiceImpl.class);
	private static IProjector projector;
	private static CartesianTierPlotter ctp;
	public static final double RATE_MILE_TO_KM = 1.609344; // 英里和公里的比率
	private static final double MAX_RANGE = 50.0; // 索引支持的最大范围，单位是千米
	private static final double MIN_RANGE = 3.0; // 索引支持的最小范围，单位是千米
	private static int startTier;
	private static int endTier;

	public LuceneSearchMobileServiceImpl() {
		projector = new SinusoidalProjector();
		ctp = new CartesianTierPlotter(0, projector, CartesianTierPlotter.DEFALT_FIELD_PREFIX);
		startTier = ctp.bestFit(MAX_RANGE / RATE_MILE_TO_KM);
		endTier = ctp.bestFit(MIN_RANGE / RATE_MILE_TO_KM);
	}

	@Autowired
	private LuceneMobileAssistDao assistDao;
	MemCacheService cacheService = MemCacheServiceImpl.getInstance();
	Analyzer analyzer = LuceneConstants.QP_ANALYZER;

	@Override
	public Map<String, Object> getListBrand(SearchParam param) {
		Map<Long, String> city_cache = (Map<Long, String>) cacheService.get("app_lucene_city");
		if (city_cache == null || city_cache.get(param.getAreaid()) == null) {
			city_cache = assistDao.getCityEnName();
			cacheService.set("app_lucene_city", city_cache);
		}
		List<Long> searchedBrandid = new ArrayList<Long>();

		Map<String, Object> results = new HashMap<String, Object>();
		String indexpath = IndexDatasourcePathUtil.getIndexDir4Mobile(city_cache.get(param.getAreaid()).toLowerCase(), "brand");
		Directory directory = null;
		IndexSearcher indexSearcher = null;
		IndexReader indexReader = null;
		try {
			directory = FSDirectory.open(new File(indexpath));
			indexReader = IndexReader.open(directory, true);
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery bq = new BooleanQuery();
			if (param.getKeyword() != null && !"".equals(param.getKeyword().trim())) {
				MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, APPSearchConstants.goodsField, analyzer);
				Query kwq = queryParser.parse(param.getKeyword().trim());
				bq.add(kwq, Occur.MUST);
			}
			if (param.getRegionid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGION, param.getRegionid().toString()));
				bq.add(tq, Occur.MUST);
			}
			if (param.getRegionextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGIONEXTID, param.getRegionextid().toString()));
				bq.add(tq, Occur.MUST);
			}

			if (param.getTagid() != null) {
				String[] tagids = param.getTagid().split(",");
				for (int i = 0; i < tagids.length; i++) {
					TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGID, tagids[i]));
					if (tagids.length == 1) {
						bq.add(tq, Occur.MUST);
					} else {
						bq.add(tq, Occur.SHOULD);
					}
				}

			}
			if (param.getTagextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGEXTID, param.getTagextid().toString()));
				bq.add(tq, Occur.MUST);
			}
			SortField sortField = new SortField(null, SortField.SCORE, false);
			if (param.getSt() != null) {
				sortField = SortFieldMap.getSortMap().get(param.getSt());
			}
			TopFieldDocs docs = indexSearcher.search(bq, param.getRequestno() + param.getStart(), new Sort(sortField));
			ScoreDoc[] sd = docs.scoreDocs;
			results.put("total", docs.totalHits);
			Map<Long, String> sales_count_map = new HashMap<Long, String>();
			Map<Long, String> brand_star_map = new HashMap<Long, String>();
			for (int i = param.getStart(); i < param.getStart() + param.getRequestno(); i++) {
				if (i == sd.length) {
					break;
				}
				Long brandid = new Long(indexSearcher.doc(sd[i].doc).get("brandID"));
				searchedBrandid.add(brandid);
				sales_count_map.put(brandid, formatSales(indexSearcher.doc(sd[i].doc).get("salescount")));
				brand_star_map.put(brandid, formatSales(indexSearcher.doc(sd[i].doc).get("star")));
			}
			//销量使用索引数据
			results.put("brand_sales_map", sales_count_map);
			//品牌星级
			results.put("brand_star_map", brand_star_map);
		} catch (Exception e) {
			logger.error("App品牌搜索异常,异常信息如下:");
			e.printStackTrace();
			throw new RuntimeException("App品牌搜索异常,Method:getListBrand");
		} finally {
			try {
				indexSearcher.close();
				indexReader.close();
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		results.put("searchedids", searchedBrandid);

		return results;
	}

	@Override
	public Map<String, Object> getListGoods(SearchParam param) {
		Map<Long, String> city_cache = (Map<Long, String>) cacheService.get("app_lucene_city");
		if (city_cache == null || city_cache.get(param.getAreaid()) == null) {
			city_cache = assistDao.getCityEnName();
			cacheService.set("app_lucene_city", city_cache);
		}
		List<Long> searchedGoodsid = new ArrayList<Long>();
		Map<String, Object> results = new HashMap<String, Object>();
		String indexpath = IndexDatasourcePathUtil.getIndexDir4Mobile(city_cache.get(param.getAreaid()).toLowerCase(), "goods");
		Directory directory = null;
		IndexSearcher indexSearcher = null;
		IndexReader indexReader = null;
		try {
			directory = FSDirectory.open(new File(indexpath));
			indexReader = IndexReader.open(directory, true);
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery bq = new BooleanQuery();
			if (param.getKeyword() != null && !"".equals(param.getKeyword().trim())) {
				MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, APPSearchConstants.goodsField, analyzer);
				Query kwq = queryParser.parse(param.getKeyword().trim());
				bq.add(kwq, Occur.MUST);
			}
			if (param.getRegionid() != null) {

				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGION, param.getRegionid().toString()));
				bq.add(tq, Occur.MUST);
			}
			if (param.getRegionextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGIONEXTID, param.getRegionextid().toString()));
				bq.add(tq, Occur.MUST);
			}

			if (param.getTagid() != null) {

				//一级分类多条件查询
				String[] tagids = param.getTagid().split(",");
				BooleanQuery tagbq = new BooleanQuery();
				for (int i = 0; i < tagids.length; i++) {
					TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGID, tagids[i]));
					if (tagids.length == 1) {
						bq.add(tq, Occur.MUST);
					} else {
						bq.add(tq, Occur.SHOULD);
					}
				}
				//bq.add(tagbq,Occur.MUST);

			}

			if (param.getTagextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGEXTID, param.getTagextid().toString()));
				bq.add(tq, Occur.MUST);
			}
			SortField sortField = new SortField(null, SortField.SCORE, false);
			if (param.getSt() != null) {
				sortField = SortFieldMap.getSortMap().get(param.getSt());
			}
			TopFieldDocs docs = indexSearcher.search(bq, param.getRequestno() + param.getStart(), new Sort(sortField));
			ScoreDoc[] sd = docs.scoreDocs;
			results.put("total", docs.totalHits);
			Map<Long, String> goods_sales_map = new HashMap<Long, String>();
			Map<Long, String> goods_price_map = new HashMap<Long, String>();
			if (sd.length > 0) {
				for (int i = param.getStart(); i < param.getStart() + param.getRequestno(); i++) {
					if (i > sd.length || i == sd.length) {
						break;
					}
					searchedGoodsid.add(new Long(indexSearcher.doc(sd[i].doc).get("goodsID")));
					goods_sales_map.put(new Long(indexSearcher.doc(sd[i].doc).get("goodsID")), formatSales(indexSearcher.doc(sd[i].doc).get("salescount")));
					goods_price_map.put(new Long(indexSearcher.doc(sd[i].doc).get("goodsID")), formatPrice(indexSearcher.doc(sd[i].doc).get("price")));

				}
				//销量,价钱从索引返回
				results.put("sales_count_map", goods_sales_map);
				results.put("goods_price_map", goods_price_map);
			}
		} catch (Exception e) {
			logger.error("App商品搜索异常,异常信息如下 ");
			e.printStackTrace();
			throw new RuntimeException("App商品搜索异常Method:getListGoods");
		} finally {

			try {

				indexSearcher.close();
				indexReader.close();
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		results.put("searchedids", searchedGoodsid);

//		logger.info(searchedGoodsid);
		return results;
	}

	private static String formatPrice(String price) {
		for (int i = 0; i < price.length(); i++) {
			if ((price.charAt(i) != '0' && price.charAt(i) != '.')) {
				return price.substring(i, price.length());
			} else if (((price.charAt(i) != '0' && price.charAt(i) == '.'))) {
				return "0" + price.substring(i, price.length());
			}
		}
		return price;
	}

	private static String formatSales(String sales) {

		int endindex = sales.indexOf(".");

		for (int i = 0; i < sales.length(); i++) {
			if (sales.charAt(i) != '0' && sales.charAt(i) != '.') {
				return sales.substring(i, endindex);
			} else if (sales.charAt(i) != '0' && sales.charAt(i) == '.') {
				return "0";
			}
		}
		return sales;
	}

	private static final String SAME_CITY = "beijing";//app search index在同一个目录 janwen

	@Override
	public Map<String, Object> getListBranch(SearchParam param) {
		List<Map<String, Object>> searchedBranch = new ArrayList<Map<String, Object>>();

		Map<String, Object> results = new HashMap<String, Object>();
		String indexpath = IndexDatasourcePathUtil.getIndexDir4Mobile(SAME_CITY, "branch");
		Directory directory = null;
		IndexSearcher indexSearcher = null;
		IndexReader indexReader = null;
		try {
			directory = FSDirectory.open(new File(indexpath));
			indexReader = IndexReader.open(directory, true);
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery bq = new BooleanQuery();
			DistanceQueryBuilder dq = new DistanceQueryBuilder(param.getLat(), param.getLng(), LuceneUtil.km2Mile(param.getDistance()), "latitude", "longitude", CartesianTierPlotter.DEFALT_FIELD_PREFIX, true, startTier, endTier);
			DistanceFieldComparatorSource dsort = new DistanceFieldComparatorSource(dq.getDistanceFilter());

			Sort sort = new Sort();
			if ("dorder:asc".equals(param.getSt())) {
				sort = new Sort(new SortField("geo_distance", dsort));
			}

			if (param.getTagid() != null) {
				String tagids[] = param.getTagid().split(",");
				for (int i = 0; i < tagids.length; i++) {
					TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGID, tagids[i]));
					if (tagids.length == 1) {
						//只有一个tagid,MUST
						bq.add(tq, Occur.MUST);
					} else {
						bq.add(tq, Occur.SHOULD);
					}

				}
			}

			if (param.getTagextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGEXTID, param.getTagextid().toString()));
				bq.add(tq, Occur.MUST);

			}

			if (param.getRegionid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGION, param.getRegionid().toString()));
				bq.add(tq, Occur.MUST);
			}
			//什么条件都没有,返回经纬度范围内的所有满足条件的结果			
			if (param.getTagid() == null && param.getTagextid() == null && param.getRegionid() == null && param.getRegionextid() == null) {
				Query distanceQuery = new MatchAllDocsQuery();
				bq.add(distanceQuery, Occur.SHOULD);
			}
			TopFieldDocs docs = indexSearcher.search(bq, dq.getFilter(), param.getRequestno() + param.getStart(), sort);
			//获得各条结果相对应的距离  
			Map<Integer, Double> distances = dq.getDistanceFilter().getDistances();
			ScoreDoc[] sd = docs.scoreDocs;
			results.put("total", docs.totalHits);
			List<String> brand_ids = new ArrayList<String>();
			List<String> branch_ids = new ArrayList<String>();
			for (int i = param.getStart(); i < param.getStart() + param.getRequestno(); i++) {
				if (i == sd.length) {
					break;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				Document doc = indexSearcher.doc(sd[i].doc);
				map.put("storeid", doc.get("branchid"));
				branch_ids.add(doc.get("branchid"));
				map.put("storename", doc.get("branchName"));
				map.put("storeaddr", doc.get("branchAddr"));
				map.put("coord", doc.get("coord"));
				map.put("totalBranches", doc.get("totalBranches"));
				String[] regionfirarray = doc.getValues("regionid");
				map.put("storeregionid", StringUtils.arrayToString(regionfirarray, ","));
				String[] regionsecarray = doc.getValues("regionextid");
				map.put("storeregionextid", StringUtils.arrayToString(regionsecarray, ","));
				map.put("brandid", doc.get("brandID"));
				brand_ids.add(doc.get("brandID"));
				map.put("brandname", doc.get("brandName"));
				map.put("imgurl", doc.get("brandlogo"));
				String[] tagidarray = doc.getValues("tagid");
				String[] tagextidarray = doc.getValues("tagextid");
				map.put("branchcatfir", StringUtils.arrayToString(tagidarray, ","));
				map.put("branchcatsec", StringUtils.arrayToString(tagextidarray, ","));
				map.put("updatetime", doc.get("updatetime").substring(0, doc.get("updatetime").lastIndexOf(".0")));
				map.put("distance", LuceneUtil.mile2KM(distances.get(docs.scoreDocs[i].doc)));
				map.put("isvip", doc.get("isvip"));
				searchedBranch.add(map);
			}
			results.put("branchids", branch_ids);
			results.put("brandids", brand_ids);
			results.put("searchedbranch", searchedBranch);

		} catch (Exception e) {
			logger.error("App分店搜索异常,异常信息如下:");
			e.printStackTrace();
			throw new RuntimeException("App分店搜索异常,Method:getListBranch");
		} finally {
			try {
				indexSearcher.close();
				indexReader.close();
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	@Override
	public Map<String, Object> getListBranch(List<Long> querybranchid, Double lat, Double lng) {
		List<Map<String, Object>> searchedBranch = new ArrayList<Map<String, Object>>();

		Map<String, Object> results = new HashMap<String, Object>();
		String indexpath = AppIndexFileUtils.getBranchIndexV2Dir();
		Directory directory = null;
		IndexSearcher indexSearcher = null;
		IndexReader indexReader = null;
		try {
			directory = FSDirectory.open(new File(indexpath));
			indexReader = IndexReader.open(directory, true);
			indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery bq = new BooleanQuery();

			Sort sort = new Sort();
			TopFieldDocs docs = null;
			for (int i = 0; i < querybranchid.size(); i++) {
				bq.add(new TermQuery(new Term("branchid", querybranchid.get(i).toString())), Occur.SHOULD);
			}
			if (lat != null && lat > 0 && lng != null && lat > 0) {
				DistanceQueryBuilder dq = new DistanceQueryBuilder(lat, lng, LuceneUtil.km2Mile(45), "latitude", "longitude", CartesianTierPlotter.DEFALT_FIELD_PREFIX, true, startTier, endTier);
				DistanceFieldComparatorSource dsort = new DistanceFieldComparatorSource(dq.getDistanceFilter());
				sort = new Sort(new SortField("geo_distance", dsort));
				docs = indexSearcher.search(bq, dq.getFilter(), querybranchid.size(), sort);
			} else {
				docs = indexSearcher.search(bq, querybranchid.size(), sort);
			}
			ScoreDoc[] sd = docs.scoreDocs;
			for (int i = 0; i < sd.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				Document doc = indexSearcher.doc(sd[i].doc);
				map.put("storeid", doc.get("branchid"));
				map.put("storename", doc.get("branchname"));
				map.put("storeaddr", doc.get("addr"));
				map.put("coord", doc.get("coord"));
				map.put("opentime", doc.get("opentime"));
				String[] regionfirarray = doc.getValues("regionid");
				map.put("storeregionid", StringUtils.arrayToString(regionfirarray, ","));
				String[] regionsecarray = doc.getValues("regionextid");
				map.put("storeregionextid", StringUtils.arrayToString(regionsecarray, ","));
				map.put("brandid", doc.get("brandid"));
				map.put("brandname", doc.get("brandname"));
				map.put("logourl", "/jsp/uploadimages/" + doc.get("logourl"));
				map.put("brandtel", doc.get("brandtel"));
				map.put("branchtel", doc.get("branchtel"));
				map.put("totalbranch", doc.get("totalbranch"));
				searchedBranch.add(map);
			}
			results.put("searchedbranch", searchedBranch);

		} catch (Exception e) {
			logger.error("App分店搜索异常,异常信息如下:");
			e.printStackTrace();
			throw new RuntimeException("App分店搜索异常,Method:getListBranch");
		} finally {
			try {
				indexSearcher.close();
				indexReader.close();
				directory.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	@Override
	public Map<String, Object> searchGoods(SearchParamV2 param) {
		logger.info(param);
		Map<Long, String> city_cache = (Map<Long, String>) cacheService.get("app_lucene_city");
		if (city_cache == null || city_cache.get(param.getAreaid()) == null) {
			city_cache = assistDao.getCityEnName();
			cacheService.set("app_lucene_city", city_cache);
		}
		List<AppSearchedGoods> searchedGoods = new ArrayList<AppSearchedGoods>();
		Map<String, Object> results = new HashMap<String, Object>();
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = AppIndexSearcherFactory.getInstance().getGoodsIndexSearcher(city_cache.get(param.getAreaid()).toLowerCase());
			BooleanQuery bq = new BooleanQuery();
			if (param.getKeyword() != null && !"".equals(param.getKeyword().trim())) {
				MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, APPSearchConstants.goodsField, analyzer);
				Query kwq = queryParser.parse(param.getKeyword().trim());
				bq.add(kwq, Occur.MUST);
			}
			if (param.getRegionid() != null) {

				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGION, param.getRegionid().toString()));
				bq.add(tq, Occur.MUST);
			}
			if (param.getRegionextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGIONEXTID, param.getRegionextid().toString()));
				bq.add(tq, Occur.MUST);
			}

			if (param.getTagid() != null) {
				//一级分类
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGID, param.getTagid().toString()));
				bq.add(tq, Occur.MUST);

			}

			if (param.getTagextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGEXTID, param.getTagextid().toString()));
				bq.add(tq, Occur.MUST);
			}
			DistanceQueryBuilder dq = null;
			DistanceFieldComparatorSource dsort = null;
			if (param.getLat() != 0 && param.getLng() != 0) {
				dq = new DistanceQueryBuilder(param.getLat(), param.getLng(), LuceneUtil.km2Mile(param.getDistance()), "latitude", "longitude", CartesianTierPlotter.DEFALT_FIELD_PREFIX, true, startTier, endTier);
				dsort = new DistanceFieldComparatorSource(dq.getDistanceFilter());
			}

			Sort sort = null;
			if ("dorder:asc".equals(param.getSt()) && dsort != null) {
				sort = new Sort(new SortField("geo_distance", dsort));
			} else if (param.getSt() != null) {
				sort = new Sort(SortFieldMap.getSortMap().get(param.getSt()));
			} else {
				sort = new Sort(new SortField(null, SortField.SCORE, false));
			}

			//什么条件都没有,返回经纬度范围内的所有满足条件的结果			
			if (param.getTagid() == null && param.getTagextid() == null && param.getRegionid() == null && param.getRegionextid() == null) {
				Query distanceQuery = new MatchAllDocsQuery();
				bq.add(distanceQuery, Occur.SHOULD);
			}
			TopFieldDocs docs = null;
			if (dq != null && dsort != null) {
				//带经纬度搜索
				docs = indexSearcher.search(bq, dq.getFilter(), param.getRequestno() + param.getStart(), sort);
			} else {
				docs = indexSearcher.search(bq, param.getRequestno() + param.getStart(), sort);
			}

			ScoreDoc[] sd = docs.scoreDocs;
			results.put("total", docs.totalHits);
			if (sd.length > 0) {
				for (int i = param.getStart(); i < param.getStart() + param.getRequestno(); i++) {
					if (i > sd.length || i == sd.length) {
						break;
					}
					AppSearchedGoods asg = new AppSearchedGoods();
					asg.setGoodsid(indexSearcher.doc(sd[i].doc).get("goodsid"));
					asg.setBranchid(indexSearcher.doc(sd[i].doc).get("branchid"));
					searchedGoods.add(asg);
				}
			}
		} catch (Exception e) {
			logger.error("App商品搜索异常,异常信息如下 ");
			e.printStackTrace();
			throw new RuntimeException("App商品搜索异常searchGoods");
		}
		results.put("searchedgoods", searchedGoods);

//		logger.info(searchedGoodsid);
		return results;
	}

	@Override
	public Map<String, Object> searchBranch(SearchParamV2 param) {
		logger.info(param);
		List<Long> searchedbranchid = new ArrayList<Long>();
		Map<String, Object> results = new HashMap<String, Object>();
		IndexSearcher indexSearcher = null;
		try {
			indexSearcher = AppIndexSearcherFactory.getInstance().getBranchIndexSearcher();
			BooleanQuery bq = new BooleanQuery();
			if (param.getRegionid() != null) {

				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGION, param.getRegionid().toString()));
				bq.add(tq, Occur.MUST);
			}
			if (param.getRegionextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_REGIONEXTID, param.getRegionextid().toString()));
				bq.add(tq, Occur.MUST);
			}

			if (param.getTagid() != null) {

				//一级分类
				Long tagid = param.getTagid();
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGID, tagid.toString()));
				bq.add(tq, Occur.MUST);

			}

			if (param.getTagextid() != null) {
				TermQuery tq = new TermQuery(new Term(APPSearchConstants.APP_TAGEXTID, param.getTagextid().toString()));
				bq.add(tq, Occur.MUST);
			}
			DistanceQueryBuilder dq = null;
			DistanceFieldComparatorSource dsort = null;
			if (param.getLat() != 0 && param.getLng() != 0) {
				dq = new DistanceQueryBuilder(param.getLat(), param.getLng(), LuceneUtil.km2Mile(param.getDistance()), "latitude", "longitude", CartesianTierPlotter.DEFALT_FIELD_PREFIX, true, startTier, endTier);
				dsort = new DistanceFieldComparatorSource(dq.getDistanceFilter());
			}
			Sort sort = new Sort();
			if ("dorder:asc".equals(param.getSt()) && dsort != null) {
				sort = new Sort(new SortField("geo_distance", dsort));
			} else if (param.getSt() != null && !"dorder:asc".equals(param.getSt())) {
				sort = new Sort(SortFieldMap.getSortMap().get(param.getSt()));
			} else {
				sort = new Sort(new SortField(null, SortField.SCORE, false));
			}

			TopFieldDocs docs = null;
			//什么条件都没有,返回经纬度范围内的所有满足条件的结果			
			if (param.getTagid() == null && param.getTagextid() == null && param.getRegionid() == null && param.getRegionextid() == null) {
				Query distanceQuery = new MatchAllDocsQuery();
				bq.add(distanceQuery, Occur.SHOULD);
			}
			Map<Integer, Double> distances = null;
			if (dq != null && dsort != null) {
				//带经纬度搜索
				docs = indexSearcher.search(bq, dq.getFilter(), param.getRequestno() + param.getStart(), sort);
				//获得各条结果相对应的距离  
				distances = dq.getDistanceFilter().getDistances();
			} else {
				docs = indexSearcher.search(bq, param.getRequestno() + param.getStart(), sort);
			}

			ScoreDoc[] sd = docs.scoreDocs;
			results.put("total", docs.totalHits);
			if (sd.length > 0) {
				for (int i = param.getStart(); i < param.getStart() + param.getRequestno(); i++) {
					if (i > sd.length || i == sd.length) {
						break;
					}
					if (distances != null) {
						logger.info(LuceneUtil.mile2KM(distances.get(docs.scoreDocs[i].doc)));
					}
					searchedbranchid.add(new Long(indexSearcher.doc(sd[i].doc).get("branchid")));
				}
			}
		} catch (Exception e) {
			logger.error("App分店搜索异常,异常信息如下 ");
			e.printStackTrace();
			throw new RuntimeException("App分店搜索异常searchBranch");
		}
		results.put("searchedbranchid", searchedbranchid);

//		logger.info(searchedGoodsid);
		return results;
	}

}
