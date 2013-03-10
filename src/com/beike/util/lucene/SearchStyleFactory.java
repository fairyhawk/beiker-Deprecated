package com.beike.util.lucene;

public class SearchStyleFactory {

	public static SearchStyle searchGoods() {
		SearchStyle searchStyle = new SearchStyle();
		searchStyle.setSearch_action(LuceneSearchConstants.SEARCH_ACTION_GOODS);
		searchStyle
				.setCurrent_search_goods(LuceneSearchConstants.CURRENT_SEARCH_STYLE);
		return searchStyle;
	}

	public static SearchStyle searchCoupon() {
		SearchStyle searchStyle = new SearchStyle();
		searchStyle
				.setSearch_action(LuceneSearchConstants.SEARCH_ACTION_COUPON);
		searchStyle.setCurrent_search_coupon(LuceneSearchConstants.CURRENT_SEARCH_STYLE);
		return searchStyle;
	}

	public static SearchStyle searchBrand() {
		SearchStyle searchStyle = new SearchStyle();
		searchStyle
				.setSearch_action(LuceneSearchConstants.SEARCH_ACTION_BRAND);
		searchStyle
				.setCurrent_search_brand(LuceneSearchConstants.CURRENT_SEARCH_STYLE);
		return searchStyle;
	}
}
