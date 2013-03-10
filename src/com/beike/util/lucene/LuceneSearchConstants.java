package com.beike.util.lucene;

/**
 * 
 * @author janwen
 * 
 */
public class LuceneSearchConstants {

	// 搜索类型
	public static final String SEARCH_TYPE_GOODS = "goods";
	public static final String SEARCH_TYPE_BRAND = "brand";
	public static final String SEARCH_TYPE_COUPON = "coupon";
	public static final String SEARCH_TYPE_BRANCH = "branch";
	// 不同搜索结果分页大小
	public static final int GOODS_PAGE_SIZE = 40;
	public static final int COUPON_PAGE_SIZE = 48;
	public static final int BRAND_PAGE_SIZE = 60;

	// 搜索样式,搜索结果页控制
	public final static String SEARCH_ACTION_GOODS = "/search/searchGoods.do";
	public final static String SEARCH_ACTION_BRAND = "/search/searchBrand.do";
	public final static String SEARCH_ACTION_COUPON = "/search/searchCoupon.do";

	public final static String CURRENT_SEARCH_STYLE = "current_search";
	public static final int CITY_SEARCH_UNION_PAGE_SIZE = 36;
	public static final int UNION_PAGE_SIZE = 3;

   //搜索结果key值
   public final static String SEARCH_RESULTS_COUNT = "searchedResults";
   public final static String SEARCH_RESULT_NEXTPAGE_ID = "nextpageid";
   
   public final static String SEARCHED_RESULT_ID = "searchedid";
   
   //搜索field
	public static final String[] goodsField = {"goodsName","goodsID","regioncat","category","brandName","merchantName","address", "extel","tel"};
	public static final String[] brandField = {"brandName","brandDesc"};
	
   

	// 订餐分店搜索字段
	public static final String[] BRANCHFIELD = { "branchAddr", "branchName",
			"tel" };

	public static final String DINGCAN = "dingcan";

	public final static String SEARCHED_BRANCHID = "searchedbranchid";

	public static final String WAIMAI = "waimai";

	// indexsearcher cache key

	public static final String KEY_GOODS = "cache_goods_";

	public static final String KEY_BRAND = "cache_brand_";

	public static final String KEY_BRANCH = "cache_branch_";
}
