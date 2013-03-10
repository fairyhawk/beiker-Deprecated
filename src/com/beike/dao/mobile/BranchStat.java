package com.beike.dao.mobile;

import java.util.List;

import com.beike.model.lucene.APPRegion;
import com.beike.model.lucene.APPTag;
import com.beike.service.mobile.SearchParamV2;

public interface BranchStat {

	
	public List<APPTag> getTagStats(SearchParamV2 query);
	
	
	public List<APPRegion> getRegionStats(SearchParamV2 query);
}
