package com.beike.dao.goods;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.beike.dao.GenericDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.goods.Goods;
import com.beike.entity.goods.GoodsProfile;
import com.beike.form.GoodsForm;
import com.beike.page.Pager;
/**
 * <p>
 * Title:商品数据库相关操作
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 16, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface GoodsDao extends GenericDao<Goods, Long> {
	
	/**
	 *  手机接口: 根据购物车的goodsId查询商品信息
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> findGoodInfoByShopCartGoodsId(String goodsIds);
	

	/**
	 * lucene搜索缓存
	 * 
	 * @param id
	 * @return
	 */
	public int getLuceneGoodsById(Long id);

	/**
	 * 根据商品id查找商品
	 * 
	 * @param id
	 *            商品id
	 * @return 商品对象
	 */
	public Goods getGoodsDaoById(Long id);

	/**
	 * 增加一个商品
	 * 
	 * @param form
	 *            商品form
	 */
	public Long addGood(final GoodsForm form);

	/**
	 * 根据商品id 查找父类的商家
	 * 
	 * @param goodId
	 *            商品id
	 * @return 商户信息
	 */
	public Map<String, String> getMerchantByGoodId(Long goodId);

	/**
	 * 获得所有支持此商品的商家
	 * 
	 * @param goodId
	 *            商品ID
	 */
	public List<Map<String, String>> getAllGoodsMerchant(Long goodId,
			int start, int end);

	/**
	 * 根据商品id 查找扩展属性
	 * 
	 * @param goodsId
	 *            商品id
	 * @param profileName
	 *            扩展属性名称
	 * @return
	 */
	public GoodsProfile getGoodsProfile(Long goodsId, String profileName);

	/**
	 * 查询top几条数据
	 * 
	 * @param count
	 *            前N条数量
	 * @return
	 */
	public List<Map<String, String>> getTopGoods(int count);

	/**
	 * 增加商品扩展属性
	 * 
	 * @param goodsProfile
	 */
	public void addGoodsProfile(GoodsProfile goodsProfile);

	public List<GoodsForm> getGoodsByIds(String idsCourse);

	/**
	 * 分页查询
	 * 
	 * @param idsCourse
	 * @param start
	 * @param end
	 * @return
	 */
	public List<GoodsForm> getBrandGoodsByIds(String idsCourse);

	public List<GoodsForm> getSalesCountByIds(String ids);

	/**
	 * 根据品牌id 查询商品
	 * 
	 * @param merchantId
	 *            品牌id
	 * @return
	 */
	public Goods getTopGoodsByBrandId(Long merchantId);

	/**
	 * 分店的商品总数
	 */
	public int getGoodsCount(String idsCourse);

	/**
	 * 查出分店id 下的所有商品id
	 * 
	 * @param idsCourse
	 * @return
	 */
	public List<Long> getGoodsCountIds(String idsCourse, int start, int end);

	/**
	 * 查出商品区域
	 * 
	 * @param goodsId
	 * @return
	 */
	public GoodsCatlog searchGoodsRegionById(Long goodsId);

	public int getAllGoodsMerchantCount(Long goodId);

	/**
	 * 根据商品ID，查出商品缩微图和商家名字 add by wnehua.cheng
	 */
	public Map<String, String> findLogo3AndMerNameBygoodId(Long goodsId);

	/**
	 * 查询商品个人限购信息（ 交易相关，已挪到GoodsSoaDao下.代码解耦）若此字段相关有改动，请同步通知交易组。
	 * 
	 * @param goodsId
	 *            add by wenhua.cheng
	 * @return
	 */
	public Map<Long, Integer> getSingleCount(Long goodsId);

	/**
	 * 根据品牌id 查一个Goods
	 * 
	 * @param merchantId
	 * @return
	 */
	public Goods getOneGoodsByBrandId(Long merchantId);

	public void updateSalesCount(Long goodId, String salesCountStr);

	/**
	 * 根据多商品id 批量查找
	 * 
	 * @param
	 * @return
	 */
	public List<Goods> getGoodsDaoByIdList(String goodsid);

	/**
	 * 
	 * 根据商品ID, 来获取该 商品的虚拟购买数量
	 * 
	 * Add by zx.liu
	 * 
	 */
	public Long getGoodsVirtualCountById(Long goodsId);

	public List<Map<String, Object>> getGoodsInfoByGoodsIds(Long... goodsid);
	
	
	public List<Map<String, Object>> getMiaoshaInfoByGoodsIds(Long... miaoshaid);

	/**
	 * 商铺宝分店在售商品数量
	 * 
	 * @param idsCourse
	 * @param filterFlag
	 *            预留参数区分是否现金券
	 * @return
	 * @author qiaowb 2011-10-31
	 */
	public int getShopsBaoGoodsCount(String idsCourse, String filterFlag);

	/**
	 * 商铺宝分店在售商品查询
	 * 
	 * @param idsCourse
	 * @param filterFlag
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Long> getShopsBaoGoodsCountIds(String idsCourse,
			String filterFlag, int start, int end);

	/**
	 * 查询商铺宝的置顶商品
	 * 
	 * @Title: getTopGoodsForShopBao
	 * @Description: TODO
	 * @param @param merchantId
	 * @param @return
	 * @return Goods
	 * @throws
	 */
	public Goods getTopGoodsForShopBao(Long merchantId);

	/**
	 * 商铺宝的置顶商铺下架，则查询销量最大的线上商品
	 * 
	 * @Title: getOneGoodsByForShopBao
	 * @Description: TODO
	 * @param @param merchantId
	 * @param @return
	 * @return Goods
	 * @throws
	 */
	public Goods getOneGoodsByForShopBao(Long merchantId);

	/**
	 * 查找商品的置顶商品标志
	 * 
	 * @Title: getIsTopForShopBao
	 * @Description: TODO
	 * @param @param goodsid
	 * @param @return
	 * @return String
	 * @throws
	 */
	public String getIsTopForShopBao(Long goodsid);

	/**
	 * 查找商品所属区域
	 * 
	 * @Title: getGoodsRegion
	 * @Description: TODO
	 * @param @param goodsid
	 * @return void
	 * @throws
	 */
	public Set<String> getGoodsRegion(Long goodsid);

	/**
	 * 获取推荐商品ID集合
	 * 
	 * @param regionid
	 *            一级区域id
	 * @param regionextid
	 *            二级区域ids
	 * @param tagid
	 *            一级分类id
	 * @param count
	 *            数量
	 * @param excludeIds
	 *            排除商品ids
	 * @return
	 */
	public List<Long> getRecommendGoods(Long regionid, String regionextids,
			Long tagid, Long cityid, Long count, String excludeIds);

	/**
	 * 获取商品地域、分类信息
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getGoodsRegionIds(Long goodsId);

	/**
	 * 获取优惠券地域、分类信息
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getCouponRegionIds(Long couponId);

	/**
	 * 获取一级商圈
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getGoodsFirstRegionById(Long goodsId);

	public List<Long> getHotMerchantIDS(AbstractCatlog abstractCatlog);

	/**
	 * 获取指定商品ids中售价最高的商品id、城市
	 * 
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String, Object>> getMostExpGoodsId(String goodsIds);

	/**
	 * 更新商品销售数量(交易调用已挪到goodsSoaDao数据代理下，便于后期代码拆分和解耦)
	 * 
	 * @param map
	 *            ,key=商品ID，value=购买数量
	 */
	public void updateSalesCount(Map<Long, Integer> map);

	/**
	 * 查询属性分类
	 * 
	 * @author janwen
	 * @time Jan 5, 2012 2:20:07 PM
	 * 
	 * @return map<10101,美食>
	 */
	public Map<Long, String> getTagProperty();

	/**
	 * 查询地域属性
	 * 
	 * @author janwen
	 * @time Jan 5, 2012 2:20:51 PM
	 * 
	 * @return map<1010,朝阳/国贸>
	 */
	public Map<Long, String> getRegionProperty();

	/**
	 * 查询商家商品信息关联表
	 * 
	 * @param goodsid
	 * @return
	 */
	public List<Map<String, String>> getGoodsMerchant(String goodsid);

	/**
	 * 查询商家信息
	 * 
	 * @param merchantid
	 * @return
	 */
	public List<Map<String, String>> getMerchant(String merchantid);

	/**
	 * 查询商品信息表
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Goods> getGoodsById(String goodsId);

	/**
	 * 查询同品牌下销量前N的商品ID
	 * 
	 * @param merchantId
	 *            品牌ID
	 * @param topCount
	 *            数量
	 * @param excGoodsIds
	 *            排除商品IDs
	 * @return
	 */
	public List<Long> getTopGoodsByMerchantId(Long merchantId, int topCount,
			String excGoodsIds);

	/**
	 * 查看cityId城市的最低商品
	 * 
	 * @param cityId
	 * @return
	 */
	public List<Long> getLowestPriceByNow(Long cityId, String ids);

	/**
	 * 查询某个城市的低价商品
	 * 
	 * @param cityId
	 * @param countNum
	 * @param ids
	 * @return
	 */
	public List<Long> queryLowestPrice(Long cityId, Long countNum, String ids);
	
	/** 
	 * @date Mar 29, 2012
	 * @description 查询某个城市的低价商品
	 * @param cityId
	 * @param countNum
	 * @param ids
	 * @param tagid 商品分类Id
	 * @return List<Long>
	 * @throws 
	 */
	public List<Long> getPartLowestPrice(Long cityId,Long countNum,String ids,Long tagid);

	/** 
	 * @date Mar 29, 2012
	 * @description 查询商品数量最多的一级商圈
	 * @param cityId
	 * @param countNum
	 * @return List<Long>
	 * @throws 
	 */
	public List<Long> getTopRegionCatlogId(Long cityId,Long countNum);
	
	/**
	 * 
	* @Description: 查询city城市下的type类型的有效商品数
	* @author wenjie.mai
	* @return 返回类型 int    
	* @throws
	 */
	public List getGoodCountByCity(String city,String type);
	
	/**
	 * 查询cityid城市下，catlogid分类的3个价位最低的商品
	 * @param catlogid
	 * @param cityid
	 * @return
	 */
	public List getLowestPriceGoodById(Long catlogid,Long cityid);
	
	/**
	 * 查询该商家其它的可售商品
	 * @param merchantId
	 * @return
	 */
	public List<Long> getGoodIdWithMerchant(Long merchantId);
	
	/**
	 * 查询商品的温馨提示信息
	 * @param goodId
	 * @return
	 */
	public List getGoodKindlyByGoodId(Long goodId);
	/** 
	 * @description:通过主商品Id获得主商品所在所有分店卖的所有商品
	 * @param goodsid
	 * @return String
	 * @throws 
	 */
	public String getMerchantGoodsByMainId(Long goodsid);
	/** 
	 * @description:获得和当前商品不同商家的商品信息列表
	 * @param map
	 * @return List<Map<String,String>>
	 * @throws 
	 */
	public List<Map<String,Object>> getListGoodsInfo(Map<String,String> map);
	
	/** 
	 * @description:通过商品Ids查询订单量
	 * @param goodIds
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getOrderCount(String goodIds);
	

	/** 
	 * @description：获取城市的所有商品的销售量（排除主商品同店铺商品）
	 * @param goodsIds
	 * @param cityId
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getRealSalesCount(String goodsIds,String cityId);
	
	/** 
	 * @description:获取商品的品牌Id
	 * @param goodsId
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getGoodsBrandId(String goodsId);
	
	public List<Map<String,Object>> getHuodongGoodsId(Long goodsId);
	
	
	/**
	 * 按idlist查询商品总数
	 * @param goodsIdList
	 * @param scope
	 * @param cashOnly
	 * @return
	 */
	public int queryGoodsCountByIds(List<Long> goodsIdList, String scope,  boolean cashOnly);
	
	/**
	 * 按idlist分页查询商品
	 * @param pager
	 * @param goodsIdList
	 * @param scope
	 * @param sort
	 * @param cashOnly
	 * @return
	 */
	public List<GoodsForm> queryGoodsByIds(Pager pager, List<Long> goodsIdList, String scope, String sort, boolean cashOnly);

	/**
	 * 根据一级分类查询其下的商品ID
	 * @param firstCatIds   一级分类ID
	 * @return
	 */
	public List<Map<String, Object>> getGoodsIdsByFirstCatIds(String firstCatIds, Long areaId);
	
	/**
	 * 根据商品ID集合和一个时间点获取在此段时间内销量最好的n个商品
	 * @param goodIds        商品ID集合，以,分隔的字符串
	 * @param pointInTime    起始时间
	 * @param amount         要查询的个数
	 * @return
	 */
	public List<Map<String, Object>> getGoodIdsOfBestSellingWithinaPeriodOfTime(String goodIds, String pointInTime, int amount);
	
	/** 
	 * @description:获取该商品所属的所有分店的Id
	 * @param goodId
	 * @return List<Map<String,Object>>
	 * @throws 
	 */
	public List<Map<String,Object>> getBranchIdByGoodsId(Long goodId);
	
	/**
	 * 
	 * @Title: getTopGoodsWithFlagShip
	 * @Description: 查询旗舰店对应的推荐商品
	 * @param 
	 * @return Goods
	 * @author wenjie.mai
	 */
	public List getTopGoodsWithFlagShip(List<Long> merchantIdList);
	
	/**
	 * 
	 * @Title: getMaxSaleCountWithFlagShip
	 * @Description: 查询品牌旗舰店对应销量最多的商品
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getMaxSaleCountWithFlagShip(List<Long> merchantIdList);
	
	/**
	 * 
	 * @Title: getMerchantIdByGoodId
	 * @Description: 通过商品ID查询品牌ID
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getMerchantIdByGoodId(Long goodId);
 } 
