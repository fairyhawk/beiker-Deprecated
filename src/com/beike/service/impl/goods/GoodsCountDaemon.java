package com.beike.service.impl.goods;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.entity.background.area.Area;
import com.beike.service.background.area.AreaService;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.constants.GoodsRelatedConstants;

/**      
 * project:beiker  
 * Title:定时刷新分类商品数量
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 19, 2012 3:33:23 PM     
 * @version 1.0
 */
@Service("goodsCountDaemon")
public class GoodsCountDaemon {
	
	private static final Logger logger =Logger.getLogger(GoodsCountDaemon.class);
	
	private static int iFlag = 1;
	
	@Autowired
	private GoodsCatlogService goodsCatlogService;
	
	@Autowired
	private AreaService areaService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	public void calculateGoodsCount() {
		logger.info("GoodsCountDaemon calculateGoodsCount begin===");
		try {
			List<Area> lstOnlineArea = areaService.queryOnlineArea();
			if(lstOnlineArea!=null){
				for(Area area : lstOnlineArea){
					if(area!=null){
						logger.info("GoodsCountDaemon calculateGoodsCount ===" + area.getAreaId());
						Map<String, Integer> mapCatlogCount = goodsCatlogService.getGoodsCatlogGroupCount(new Long(area.getAreaId()));
						//cache有效期360天
						if(iFlag==0){
							memCacheService.set(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + area.getAreaId(), 
									mapCatlogCount, 60*60*24);
							logger.info("GoodsCountDaemon calculateGoodsCount no b ===" + area.getAreaId());
						}else if(iFlag==1){
							memCacheService.set(GoodsRelatedConstants.MEMCAHCECACHE_KEY_CITY_GOODS_COUNT + area.getAreaId() + "_b", 
									mapCatlogCount, 60*60*24);
							logger.info("GoodsCountDaemon calculateGoodsCount b ===" + area.getAreaId());
						}
					}
				}
				if(iFlag==0){
					iFlag = 1;
				}else{
					iFlag = 0;
				}
				
			}
			logger.info("GoodsCountDaemon calculateGoodsCount end===");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}