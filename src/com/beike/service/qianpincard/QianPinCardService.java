package com.beike.service.qianpincard;

import java.util.List;
import java.util.Map;

import com.beike.entity.catlog.QPCardRegionCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.Goods;
import com.beike.form.GoodsForm;
import com.beike.service.GenericService;

/**  
* @Title:  千品卡Service
* @Package com.beike.service.qianpincard
* @Description: TODO
* @author wenjie.mai  
* @date Feb 29, 2012 10:31:17 AM
* @version V1.0  
*/
public interface QianPinCardService extends GenericService<Goods, Long> {
	/**
	 * 获取N天内销售量最好的N款商超卡，如指定时间范围内不足N款,则取最后上线的商超卡
	 * @param areaId	城市ID
	 * @param iCount	数量
	 * @param nDays		N天
	 * @return
	 */
	public List<Long> getTopSaleCardGoods(String areaId, int iCount, int nDays);
	
	/**
	 * 获取指定城市下所有在售商超卡，按上线时间逆序
	 * @param areaId
	 * @return
	 */
	public List<Long> getCardGoodsOrderOnTime(String areaId);
	
	/**
	 * 查询24小时内销量最好的现金券
	 * @param cityId
	 * @return
	 */
	public List<GoodsForm> getCouponCashFor24Hour(Long cityId);
	
	/**
	 * 
	 * @author janwen
	 * @time Feb 29, 2012 5:24:40 PM
	 *
	 * @param cityid
	 * @param tagid
	 * @return 剩余商品数量
	 */
	public Map<String,Long> getGoodsTotal(int cityid);
	
	/**
	 * 
	 * @author janwen
	 * @time Feb 29, 2012 6:20:32 PM
	 *
	 * @return 顶级分类
	 */
	public List<RegionCatlog> getTopCategory();
	/**
	 * 
	 * @author janwen
	 * @time Feb 29, 2012 6:21:48 PM
	 *
	 * @param cityid
	 * @param tagid
	 * @return 当前分类热门商圈
	 */
	public Map<String,List<QPCardRegionCatlog>> getHotRegion(int cityid);
	
	/**
	 * 
	 * @author janwen
	 * @time Feb 29, 2012 6:23:45 PM
	 *
	 * @param cityid
	 * @param tagid
	 * @return 热门分类商品
	 */
	public Map<Long,List<Long>> getHotGoods(int cityid);
	
}
