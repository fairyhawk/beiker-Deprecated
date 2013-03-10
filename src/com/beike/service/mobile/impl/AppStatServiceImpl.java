package com.beike.service.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.mobile.AppStatsDao;
import com.beike.model.lucene.APPRegion;
import com.beike.model.lucene.APPTag;
import com.beike.model.lucene.AppSearchQuery;
import com.beike.service.mobile.AppStatService;
import com.beike.service.mobile.SearchParamV2;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

@Service("appStatService")
public class AppStatServiceImpl implements AppStatService {

	@Autowired
	private AppStatsDao appStatsDao;
	MemCacheService memCacheService = MemCacheServiceImpl.getInstance();


	@Override
	public Map<String, Object> getAppCat(SearchParamV2 query) {
		logger.info(query);
		String cityid = query.getAreaid().toString();
		Map<String, Object> results_map = null;
		synchronized (this) {
			results_map = (Map<String, Object>) memCacheService.get(cityid + query.hashCode());
			if(results_map != null){
				return results_map;
			}
		}
		
		
		logger.info("城市ID:" + cityid);
		// 最终返回的分类列表
		List<APPTag> tag_results = new ArrayList<APPTag>();
		// 最终返回的商圈列表
		List<APPRegion> region_results = new ArrayList<APPRegion>();
		// 根据城市查询所有的一级分类
		List<APPTag> tags = appStatsDao.getAvailableTag(new Integer(cityid), 0);
		// 根据城市查询所有的一级商圈
		List<APPRegion> regions = appStatsDao.getAvailableRegion(new Integer(
				cityid), 0);
		AppSearchQuery appSearchQuery = new AppSearchQuery();
		appSearchQuery.setCityid(new Integer(cityid));
		// LJW(null,null,null,null)
		if (SearchParamV2.noSelected(query)) {
			// tag
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				APPTag tag = tags.get(tagindex);
				appSearchQuery.clear();
				appSearchQuery.setTagid(tag.getId());
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));;
				tag_results.add(tag);
				// 查询当前一级分类下的所有二级分类列表
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// tagext
					tag_results.add(tagext);
				}
			}
			// region
			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				APPRegion region = regions.get(regionindex);
				appSearchQuery.clear();
				appSearchQuery.setRegionid(region.getId());
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// region
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}
			// LJW (tag,null,null,null)
		} else if (SearchParamV2.isSelected(query.getTagid())
				&& !SearchParamV2.isSelected(query.getTagextid())
				&& !SearchParamV2.isSelected(query.getRegionid())
				&& !SearchParamV2.isSelected(query.getRegionextid())) {
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				appSearchQuery.clear();
				APPTag tag = tags.get(tagindex);
				appSearchQuery.setTagid(tag.getId());
				// tag
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());

				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// tagext
					tag_results.add(tagext);
				}
			}

			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				APPRegion region = regions.get(regionindex);
				appSearchQuery.clear();
				appSearchQuery.setTagid(new Integer(query.getTagid().toString()));
				appSearchQuery.setRegionid(region.getId());
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				//region
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}
			// LJW (tag,tagext,null,null)
		} else if (SearchParamV2.isSelected(query.getTagid())
				&& SearchParamV2.isSelected(query.getTagextid())
				&& !SearchParamV2.isSelected(query.getRegionid())
				&& !SearchParamV2.isSelected(query.getRegionextid())) {
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				APPTag tag = tags.get(tagindex);
				appSearchQuery.clear();
				appSearchQuery.setTagid(tag.getId());
				// tag
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());

				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// tagext
					tag_results.add(tagext);
				}
			}

			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				appSearchQuery.clear();
				appSearchQuery.setTagid(new Integer(query.getTagid().toString()));
				appSearchQuery.setTagextid(new Integer(query.getTagextid().toString()));
				APPRegion region = regions.get(regionindex);
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());

				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}

			// LJW (tag,tagextid,region,null)
		} else if (SearchParamV2.isSelected(query.getTagid())
				&& SearchParamV2.isSelected(query.getTagextid())
				&& SearchParamV2.isSelected(query.getRegionid())
				&& !SearchParamV2.isSelected(query.getRegionextid())) {
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				APPTag tag = tags.get(tagindex);
				appSearchQuery.clear();
				appSearchQuery.setTagid(tag.getId());
				appSearchQuery.setRegionid(new Integer(query.getRegionid().toString()));
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// tag
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					// tagext
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					tag_results.add(tagext);
				}
			}

			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				appSearchQuery.clear();
				appSearchQuery.setTagid(new Integer(query.getTagid().toString()));
				appSearchQuery.setTagextid(new Integer(query.getTagextid().toString()));
				APPRegion region = regions.get(regionindex);
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}

			// LJW (tag,tagextid,region,regionext)
		} else if (SearchParamV2.isSelected(query.getTagid())
				&& SearchParamV2.isSelected(query.getTagextid())
				&& SearchParamV2.isSelected(query.getRegionid())
				&& SearchParamV2.isSelected(query.getRegionextid())) {
			
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				APPTag tag = tags.get(tagindex);
				appSearchQuery.clear();
				appSearchQuery.setRegionid(new Integer(query.getRegionid().toString()));
				appSearchQuery.setRegionextid(new Integer(query.getRegionextid().toString()));
				appSearchQuery.setTagid(tag.getId());
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// tag
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					// tagext
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					tag_results.add(tagext);
				}
			}

			
			
			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				APPRegion region = regions.get(regionindex);
				appSearchQuery.clear();
				appSearchQuery.setTagid(new Integer(query.getTagid().toString()));
				appSearchQuery.setTagextid(new Integer(query.getTagextid().toString()));
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}
			// LJW(null,null,region,regionext)
		} else if (!SearchParamV2.isSelected(query.getTagid())
				&& !SearchParamV2.isSelected(query.getTagextid())
				&& SearchParamV2.isSelected(query.getRegionid())
				&& SearchParamV2.isSelected(query.getRegionextid())) {
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				APPTag tag = tags.get(tagindex);
				appSearchQuery.clear();
				appSearchQuery.setRegionid(new Integer(query.getRegionid().toString()));
				appSearchQuery.setRegionextid(new Integer(query.getRegionextid().toString()));
				appSearchQuery.setTagid(tag.getId());
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// tag
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					// tagext
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					tag_results.add(tagext);
				}
			}
			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				APPRegion region = regions.get(regionindex);
				appSearchQuery.clear();
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}
			// LJW(tag,null,region,null)
		} else if (SearchParamV2.isSelected(query.getTagid())
				&& !SearchParamV2.isSelected(query.getTagextid())
				&& SearchParamV2.isSelected(query.getRegionid())
				&& !SearchParamV2.isSelected(query.getRegionextid())) {
			appSearchQuery.clear();
			appSearchQuery.setRegionid(new Integer(query.getRegionid().toString()));
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				APPTag tag = tags.get(tagindex);
				appSearchQuery.setTagid(tag.getId());
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// tag
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					// tagext
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					tag_results.add(tagext);
				}
			}
			
			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				APPRegion region = regions.get(regionindex);
				appSearchQuery.clear();
				appSearchQuery.setTagid(new Integer(query.getTagid().toString()));
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					// regionext
					region_results.add(regionext);
				}
			}
			// LJW(tag,null,region,regionext)
		} else if (SearchParamV2.isSelected(query.getTagid())
				&& !SearchParamV2.isSelected(query.getTagextid())
				&& SearchParamV2.isSelected(query.getRegionid())
				&& SearchParamV2.isSelected(query.getRegionextid())) {

			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				appSearchQuery.clear();
				appSearchQuery.setRegionid(new Integer(query.getRegionid().toString()));
				appSearchQuery.setRegionextid(new Integer(query.getRegionextid().toString()));
				APPTag tag = tags.get(tagindex);
				appSearchQuery.setTagid(tag.getId());
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// tag
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					// tagext
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					logger.info(appSearchQuery);
					tag_results.add(tagext);
				}
			}
			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				appSearchQuery.clear();
				appSearchQuery.setTagid(new Integer(query.getTagid().toString()));
				APPRegion region = regions.get(regionindex);
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					// regionext
					region_results.add(regionext);
				}
			}
			// LJW (null,null,region,null)
		} else if (!SearchParamV2.isSelected(query.getTagid())
				&& !SearchParamV2.isSelected(query.getTagextid())
				&& SearchParamV2.isSelected(query.getRegionid())
				&& !SearchParamV2.isSelected(query.getRegionextid())) {
			for (int tagindex = 0; tagindex < tags.size(); tagindex++) {
				appSearchQuery.clear();
				appSearchQuery.setRegionid(new Integer(query.getRegionid().toString()));
				APPTag tag = tags.get(tagindex);
				tag.setCount(appStatsDao.getRegionStats(appSearchQuery));
				logger.info(appSearchQuery);
				// tag
				tag_results.add(tag);
				List<APPTag> tagexts = appStatsDao.getAvailableTag(new Integer(
						cityid), tag.getId());
				for (int tagextindex = 0; tagextindex < tagexts.size(); tagextindex++) {
					APPTag tagext = tagexts.get(tagextindex);
					appSearchQuery.setTagextid(tagext.getId());
					// tagext
					tagext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					tag_results.add(tagext);
				}
			}
			for (int regionindex = 0; regionindex < regions.size(); regionindex++) {
				APPRegion region = regions.get(regionindex);
				appSearchQuery.clear();
				appSearchQuery.setRegionid(region.getId());
				// region
				region.setCount(appStatsDao.getRegionStats(appSearchQuery));
				region_results.add(region);
				List<APPRegion> regionexts = appStatsDao.getAvailableRegion(
						new Integer(cityid), region.getId());
				for (int regionextindex = 0; regionextindex < regionexts.size(); regionextindex++) {
					APPRegion regionext = regionexts.get(regionextindex);
					appSearchQuery.setRegionextid(regionext.getId());
					regionext.setCount(appStatsDao.getRegionStats(appSearchQuery));
					// regionext
					region_results.add(regionext);
				}
			}
		}
		results_map = new HashMap<String, Object>();
		results_map.put("tags", tag_results);
		results_map.put("regions", region_results);
        memCacheService.set(cityid + query.hashCode(), results_map);
		return results_map;
	}

	static final Log logger = LogFactory.getLog(AppStatServiceImpl.class);


	

}
