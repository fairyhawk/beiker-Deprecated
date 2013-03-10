package com.beike.dao.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import com.beike.service.lucene.search.IndexSearcherFactory;
import com.beike.util.lucene.APPSearchConstants;
import com.beike.util.lucene.CJKAnalyzer;
import com.beike.util.lucene.IndexDatasourcePathUtil;
import com.beike.util.lucene.LuceneConstants;
import com.beike.util.lucene.LuceneSearchConstants;
import com.beike.util.luncene.rank.QPSimilarty;

@Component
public class LuceneSearchDaoImpl implements LuceneSearchDao {

	Analyzer chineseAnalyzer = new CJKAnalyzer(LuceneConstants.LUCENE_CURRENT_VERSION);
	File stopword = null;
	Logger logger = Logger.getLogger(LuceneSearchDaoImpl.class);

	@Override
	public Map<String, Object> queryGoodsResult(String goods_keyword, String city_en_name, int currentPage, int pagesize) {
		String indexdir = IndexDatasourcePathUtil.getIndexDir(city_en_name.toLowerCase(), LuceneSearchConstants.SEARCH_TYPE_GOODS);
		String indexdirbak = IndexDatasourcePathUtil.getIndexDirBak(city_en_name.toLowerCase(), LuceneSearchConstants.SEARCH_TYPE_GOODS);

		return queryGoodsResultProxy(indexdir, indexdirbak, goods_keyword, city_en_name, currentPage, pagesize);

	}

	@Override
	public Map<String, Object> queryBrandResult(String brand_keyword, String city_en_name, int currentPage, int pagesize) {
		String indexdir = IndexDatasourcePathUtil.getIndexDir(city_en_name.toLowerCase(), LuceneSearchConstants.SEARCH_TYPE_BRAND);

		String indexdirbak = IndexDatasourcePathUtil.getIndexDirBak(city_en_name.toLowerCase(), LuceneSearchConstants.SEARCH_TYPE_BRAND);
		return queryBrandResultProxy(indexdir, indexdirbak, brand_keyword, city_en_name, currentPage, pagesize);

	}

	@Override
	public Map<String, Object> queryCouponResultProxy(String indexdir, String coupon_keyword, String city_en_name, int currentPage, int pagesize) {
		List<Long> couponidList = new ArrayList<Long>();
		List<Integer> docid = new ArrayList<Integer>();
		Map<String, Object> searchedMap = new HashMap<String, Object>();
		try {
			Directory directory = FSDirectory.open(new File(indexdir));
			IndexSearcher indexSearcher = new IndexSearcher(directory);

			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, APPSearchConstants.couponField, chineseAnalyzer);
			queryParser.setDefaultOperator(Operator.AND);
			Query query = queryParser.parse(coupon_keyword);
			indexSearcher.setSimilarity(new QPSimilarty());

			TopDocs resultDoc = indexSearcher.search(query, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			ScoreDoc[] hits = resultDoc.scoreDocs;
			for (int idand = 0; idand < hits.length; idand++) {
				docid.add(hits[idand].doc);
			}
			// OR
			queryParser.setDefaultOperator(Operator.OR);
			query = queryParser.parse(coupon_keyword);

			resultDoc = indexSearcher.search(query, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			hits = resultDoc.scoreDocs;
			for (int idor = 0; idor < hits.length; idor++) {
				if (docid.contains(hits[idor].doc)) {
					continue;
				} else {
					docid.add(hits[idor].doc);
				}
			}
			// 返回所有docid
			for (int i = 0; i < docid.size(); i++) {
				String couponid = indexSearcher.doc(docid.get(i)).get(APPSearchConstants.COUPON_ID);
				couponidList.add(Long.parseLong(couponid));
			}
			searchedMap.put(LuceneSearchConstants.SEARCH_RESULTS_COUNT, couponidList.size());
			searchedMap.put(LuceneSearchConstants.SEARCHED_RESULT_ID, couponidList);
			indexSearcher.close();
		} catch (Exception e) {
			logger.info("优惠券搜索异常,搜索信息[" + city_en_name + coupon_keyword + "]");
			e.printStackTrace();
			throw new RuntimeException("优惠券搜索异常");
		}
		return searchedMap;
	}

	@Override
	public Map<String, Object> queryGoodsResultProxy(String indexdir, String indexdirbak, String goods_keyword, String city_en_name, int currentPage, int pagesize) {
		List<Long> goodsidList = new ArrayList<Long>();
		List<Integer> docidand = new ArrayList<Integer>();
		Map<String, Object> searchedMap = new HashMap<String, Object>();
		try {
			IndexSearcher indexSearcher = IndexSearcherFactory.getInstance().getIndexSearcher(LuceneSearchConstants.KEY_GOODS + city_en_name, indexdir, indexdirbak);
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, LuceneSearchConstants.goodsField, chineseAnalyzer);
			queryParser.setDefaultOperator(Operator.AND);
			Query query = queryParser.parse(goods_keyword);

			indexSearcher.setSimilarity(new QPSimilarty());
			TopDocs resultDoc = indexSearcher.search(query, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			ScoreDoc[] hits = resultDoc.scoreDocs;
			// {"goodsName","category","regioncat","brandName","merchantName","address",
			// "extel","tel"};
			for (int andi = 0; andi < hits.length; andi++) {
				docidand.add(hits[andi].doc);
			}
			// OR查询
			queryParser.setDefaultOperator(Operator.OR);
			Query query_or = queryParser.parse(goods_keyword);
			indexSearcher.setSimilarity(new QPSimilarty());
			resultDoc = indexSearcher.search(query_or, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			hits = resultDoc.scoreDocs;
			for (int ori = 0; ori < hits.length; ori++) {
				if (docidand.contains(hits[ori].doc)) {
					continue;
				} else {
					docidand.add(hits[ori].doc);
				}
			}
			// 返回当前所有搜索到的goodsid,上层进行过期商品排序处理
			for (int i = 0; i < docidand.size(); i++) {
				goodsidList.add(Long.parseLong(indexSearcher.doc(docidand.get(i)).get(APPSearchConstants.GOODS_ID)));
			}

			searchedMap.put(LuceneSearchConstants.SEARCH_RESULTS_COUNT, goodsidList.size());
			searchedMap.put(LuceneSearchConstants.SEARCHED_RESULT_ID, goodsidList);
			//indexSearcher.close();
		} catch (Exception e) {
			logger.info("商品搜索异常	,搜索信息[" + city_en_name + goods_keyword + "]");
			e.printStackTrace();
			throw new RuntimeException("商品搜索异常");
		}

		return searchedMap;
	}

	@Override
	public Map<String, Object> queryBrandResultProxy(String indexdir, String indexdirbak, String brand_keyword, String city_en_name, int currentPage, int pagesize) {
		List<Long> brandidList = new ArrayList<Long>();
		Map<String, Object> searchedMap = new HashMap<String, Object>();
		try {

			IndexSearcher indexSearcher = IndexSearcherFactory.getInstance().getIndexSearcher(LuceneSearchConstants.KEY_BRAND + city_en_name, indexdir, indexdirbak);
			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, APPSearchConstants.brandField, chineseAnalyzer);
			List<Integer> docid = new ArrayList<Integer>();
			queryParser.setDefaultOperator(Operator.AND);
			indexSearcher.setSimilarity(new QPSimilarty());

			Query query = queryParser.parse(brand_keyword);
			TopDocs resultDoc = indexSearcher.search(query, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			ScoreDoc[] hits = resultDoc.scoreDocs;
			for (int andi = 0; andi < hits.length; andi++) {
				docid.add(hits[andi].doc);
			}
			// OR
			queryParser.setDefaultOperator(Operator.OR);

			query = queryParser.parse(brand_keyword);
			resultDoc = indexSearcher.search(query, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			hits = resultDoc.scoreDocs;
			for (int orid = 0; orid < hits.length; orid++) {
				if (docid.contains(hits[orid].doc)) {
					continue;
				} else {
					docid.add(hits[orid].doc);
				}
			}
			// FIXME janwen
			for (int i = 0; i < docid.size(); i++) {
				brandidList.add(Long.parseLong(indexSearcher.doc(docid.get(i)).get(APPSearchConstants.BRAND_ID)));
			}
			searchedMap.put(LuceneSearchConstants.SEARCH_RESULTS_COUNT, brandidList.size());
			searchedMap.put(LuceneSearchConstants.SEARCHED_RESULT_ID, brandidList);
			//indexSearcher.close();
		} catch (Exception e) {
			logger.info("品牌搜索异常	,搜索信息[" + city_en_name + brand_keyword + "]");
			e.printStackTrace();
			throw new RuntimeException("品牌搜索异常");
		}
		return searchedMap;
	}

	@Override
	public List<Long> queryBranchResult(String indexdir, String keyword, String citypinyin, String type) {
		// 返回当前页的branchid
		List<Long> searchedbranchids = new ArrayList<Long>();
		try {

			IndexSearcher indexSearcher = IndexSearcherFactory.getInstance().getIndexSearcher(LuceneSearchConstants.KEY_BRANCH + citypinyin, indexdir, null);
			BooleanQuery bq = new BooleanQuery();

			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LuceneConstants.LUCENE_CURRENT_VERSION, LuceneSearchConstants.BRANCHFIELD, chineseAnalyzer);
			Query kwq = queryParser.parse(keyword);
			bq.add(kwq, Occur.MUST);
			if (LuceneSearchConstants.DINGCAN.equals(type)) {
				TermQuery tq = new TermQuery(new Term(LuceneSearchConstants.DINGCAN, "1"));
				bq.add(tq, Occur.MUST);
			} else if (LuceneSearchConstants.WAIMAI.equals(type)) {
				TermQuery tq = new TermQuery(new Term(LuceneSearchConstants.WAIMAI, "1"));
				bq.add(tq, Occur.MUST);
			}
			TopDocs resultDoc = indexSearcher.search(bq, indexSearcher.maxDoc(), new Sort(SortField.FIELD_SCORE));
			ScoreDoc[] hits = resultDoc.scoreDocs;

			for (int i = 0; i < hits.length; i++) {
				searchedbranchids.add(new Long(indexSearcher.doc(hits[i].doc).get("branchid")));
				//logger.info(indexSearcher.doc(hits[i].doc).get("waimai"));
				//logger.info(indexSearcher.doc(hits[i].doc).get("dingcan"));
			}

		} catch (Exception e) {
			logger.info("订餐分店搜索异常	,搜索信息[" + citypinyin + keyword + "]");
			e.printStackTrace();
			throw new RuntimeException("订餐分店搜索异常");
		}
		return searchedbranchids;
	}

}
