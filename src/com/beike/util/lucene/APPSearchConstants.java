package com.beike.util.lucene;

public class APPSearchConstants {

	public final String Goods_FILED_ADDRESS = "address";

	public final String GOODS_FIELD_BRANDNAME = "brandName";

	public final String GOODS_FIELD_CATEGORY = "category";
	public final String GOODS_FIELD_EXTEL = "extel";
	public final String GOODS_FIELD_GOODSNAME = "goodsName";
	public final String GOODS_FIELD_TEL = "tel";
	
	
	public final static String GOODS_ID = "goodsID";
	public final static String COUPON_ID = "couponID";
	public final static String BRAND_ID = "brandID";
	//搜索商品的字段
	public static final String[] goodsField = {"goodsname","tagname","regionid","regionextid","tagid","tagextid","brandname","branchname","address", "brandtel","branchtel"};
	//搜索优惠券字段
	public static final String[] couponField = {"couponName","category", "regioncat","brandName","address"};
    //搜索品牌字段
	public static final String[] brandField = {"brandName","brandDesc"};
	

	public static final String APP_GOODS_PRICE = "price";
	public static final String APP_GOODS_ONTIME = "ontime";
	public static final String  APP_SALES_COUNT = "salescount";
	
	public static final String APP_TAGID = "tagid";
	
	public static final String APP_TAGEXTID = "tagextid";
	
	public static final String APP_REGION = "regionid";
	
	public static final String APP_REGIONEXTID = "regionextid";
	
	
	public static final String APP_LONGITUDE = "longitude";
	
	public static final String APP_LATITUDE = "latitude";
	
	
	public static final String APPV2_INDEXSEARCHER_CACHEKEY_GOODS = "APPV2_INDEXSEARCHER_GOODS_";

	public static final String APPV2_INDEXSEARCHER_CACHEKEY_BRANCH = "APPV2_INDEXSEARCHER_BRANCH_";
}
