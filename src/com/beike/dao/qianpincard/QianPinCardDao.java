package com.beike.dao.qianpincard;

import java.util.List;
import java.util.Set;

import com.beike.dao.GenericDao;
import com.beike.entity.goods.Goods;

/**  
* @Title:  千品卡Dao
* @Package com.beike.dao.qianpincard
* @Description: TODO
* @author wenjie.mai  
* @date Feb 29, 2012 10:26:42 AM
* @version V1.0  
*/
public interface QianPinCardDao extends GenericDao<Goods, Long> {
	/**
	 * 获取指定城市下所有在售商超卡商品ID
	 * @param areaId
	 * @return
	 */
	public List<Long> getCardGoodsIdsByCityId(String areaId);
	
	/**
	 * 获取指定时间范围内销售量最好的N款商品，如指定时间范围内不足N款,则取最后上线的商品
	 * @param beginTime 开始时间
	 * @param endTime	结束时间
	 * @param iCount	数量，为0时不做数量限制，返回传入商品IDs的排序：销量逆序 and 上线时间逆序
	 * @param goodsIds	商品IDs
	 * @return
	 */
	public List<Long> getTopSaleGoods(String beginTime, String endTime, int iCount, List<Long> lstInGoods);
	
	/**
	 * 
	 * @author janwen
	 * @time Feb 29, 2012 3:46:51 PM
	 *
	 * @param topcategoryID
	 * @param cityid TODO
	 * @return 一级分类24小时内销量最好的4款商品,不包括现金券
	 */
   public List getGoodsByCategoryID(int cityid);	
   
   /**
    * 
    * @author janwen
    * @time Feb 29, 2012 4:17:54 PM
    *
    * @param cityid
    * @return 二级分类前五个商圈
    */
   public List getSecondCategoryRank(int tagid,int cityid);
   
   /**
    * 
    * @author janwen
    * @time Feb 29, 2012 5:03:40 PM
    *
    * @param cityid
    * @return 当前城市分类商品总数,包括现金券
    */
   public Long getGoodsCountByCity(int tagid,int cityid);
   
   /**
	 * 查询某城市在售现金券
	 * @param cityId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Long> getCouponCashForCityId(Long cityId);
	
	/**
	 * 每个品牌取销量最大的现金券
	 * @param ids
	 * @return
	 */
	public List getCheckGoodIdOneMerchant(String ids);
		
	/**
	 * 查找现金券信息
	 * @param ids
	 * @return
	 */
	public List getCouponCashById(Set<Long> idsList);
	
	/**
	 * 按照上线时间逆序排列商品ID
	 * @param areaId
	 * @return
	 */
	public List<Long> getGoodsIdsOrderOntime(List<Long> lstInGoods);
}
	