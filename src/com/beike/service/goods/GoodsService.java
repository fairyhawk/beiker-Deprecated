package com.beike.service.goods;

import java.util.List;
import java.util.Map;
import com.beike.common.exception.BaseException;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.goods.GoodKindly;
import com.beike.entity.goods.Goods;
import com.beike.form.CommendGoodsForm;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.service.GenericService;

/**
 * <p>
 * Title:商品Service
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
 * @date May 16, 2011
 * @author ye.tian
 * @version 1.0
 */

public interface GoodsService extends GenericService<Goods, Long> {

	/**
	 * 根据商品id 查找
	 * @param goodId
	 * @return
	 */
	public Goods findById(Long id);

	/**
	 * 根据多商品id 批量查找
	 * @param goodId
	 * @return
	 */
	public List<Goods> findByIdList(String goodId);

	/**
	 * 增加商品form
	 * @param form
	 */
	public void addGood(GoodsForm form) throws BaseException;

	/**
	 * 根据商品id 查找最上级的商家
	 * @param goodId 商品id
	 * @return 商户信息
	 */
	public MerchantForm getMerchantById(Long goodId);

	/**
	 * 根据商品id 查找地图需要信息
	 * @param goodId 商品id
	 * @return 商户信息
	 */
	public List<MerchantForm> getGoodsMapMerchant(Long goodId, Pager pager);

	/**
	 * 根据某个商品ID 获得 包含的路径
	 * @param goodId
	 * @param profileName
	 * @return
	 */
	public String getGoodDetailIncliudeUrl(Long goodId);

	/**
	 * 临时需要 查询top5条
	 * @return
	 */
	public List<GoodsForm> getTopGoodsForm();

	/**
	 * 某商品销售量
	 * @param goodId 商品ID
	 * @return
	 */
	public String salesCount(Long goodId);

	/**
	 * 根据品牌id 查询商品
	 * @param merchantId 品牌id
	 * @return
	 */
	public Goods getGoodsByBrandId(Long merchantId);

	/**
	 * 分店的商品总数
	 */
	public int getGoodsCount(List<MerchantForm> listForm);

	/**
	 * 分店id查询商品详情
	 * @param listForm
	 * @return
	 */
	public List<GoodsForm> getGoodsFormByChildId(List<Long> listForm);

	/**
	 * 分店id 下的所有商品id
	 * @param idsCourse
	 * @return
	 */
	public List<Long> getGoodsCountIds(String idsCourse, Pager pager);

	public GoodsCatlog searchGoodsRegionById(Long goodsId);

	/**
	 * 分页数量
	 * @param goodId
	 * @return
	 */
	public int getAllGoodsMerchantCount(Long goodId);

	/**
	 * 补充方法：
	 * 根据商品ID, 来获取商品的虚拟购买数量
	 * Add by zx.liu
	 */
	public Long getGoodsVirtualCount(Long goodsId);

	public List<Map<String, Object>> getGoodsInfoByGoodsIds(Long... goodsid);

	public List<Map<String, Object>> getMiaoShaInfoByGoodsIds(Long... miaoshaid);

	public List<Goods> getGoodsDaoByIdList(String goodsid);

	/**
	 * 商铺宝分店在售商品数量
	 * @param listForm
	 * @param filterFlag
	 * @return
	 */

	public int getShopsBaoGoodsCount(List<MerchantForm> listForm, String filterFlag);

	/**
	 * 商铺宝分店在售商品查询
	 * @param listForm
	 * @param filterFlag
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Long> getShopsBaoGoodsCountIds(List<MerchantForm> listForm, String filterFlag, Pager pager);

	/**
	 * 最佳搭配商品Ids
	 * @param regionid 一级区域id
	 * @param regionextid 二级区域ids
	 * @param tagid 一级分类id
	 * @param cityid 城市id
	 * @param count 数量
	 * @param excludeIds 排除商品ids
	 * @return
	 * @author qiaowb 2011-11-12
	 */
	public List<Long> getSaleWithGoodsIds(Long regionid, String regionextids, Long tagid, Long cityid, Long count, String excludeIds);

	/**
	 * 推荐商品Ids
	 * @param regionid 一级区域id
	 * @param regionextid 二级区域ids
	 * @param tagid 一级分类id
	 * @param cityid 城市id
	 * @param count 数量
	 * @param excludeIds 排除商品ids
	 * @return
	 * @author qiaowb 2011-11-12
	 */
	public List<Long> getRecommendGoodsIds(Long regionid, String regionextids, Long tagid, Long cityid, Long count, String excludeIds);

	/**
	 * 获取商品地域、分类信息
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getGoodsRegionIds(Long goodsId);

	/**
	 * 获取优惠券地域、分类信息
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getCouponRegionIds(Long couponId);

	/**
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getGoodsFirstRegionById(Long goodsId);

	/**
	 * 获取指定商品ids中售价最高的商品id、城市
	 * @param goodsIds
	 * @return
	 */
	public List<Map<String, Object>> getMostExpGoodsId(String goodsIds);

	/**
	 * 查询最低价商品
	 * @param cityId
	 * @return
	 */
	public List<Long> getLowestGoods(Long cityId, String ids);

	/**
	 * 查询同品牌下销量前N的商品ID
	 * @param merchantId 品牌ID
	 * @param topCount 数量
	 * @param excGoodsIds 排除商品IDs
	 * @return
	 */
	public List<Long> getTopGoodsByMerchantId(Long merchantId, int topCount, String excGoodsIds);

	/**
	 * @date Mar 29, 2012
	 * @description 分类查询最低价商品
	 * @param cityId
	 * @param countNum
	 * @param ids 排除的商品Id
	 * @return List<Long>
	 * @throws
	 */
	public List<Long> getPartLowestGoods(Long cityId, Long countNum, String ids);

	/**
	 * @date Mar 29, 2012
	 * @description 查询商品数量最多的一级商圈
	 * @param cityId
	 * @param countNum
	 * @return List<Long> 商圈id（regionId）
	 * @throws
	 */
	public List<Long> getTopRegionCatlogId(Long cityId, Long countNum);

	/**
	 * @Description: 查询city城市下的type类型的有效商品数
	 * @author wenjie.mai
	 * @return 返回类型 int
	 * @throws
	 */
	public int getGoodCountByCity(String city, String type);

	/**
	 * 查询低价商品
	 * @param catlogid
	 * @param cityid
	 * @return
	 */
	public List<Long> getLowestPriceGood(Long catlogid, Long cityid);

	/**
	 * 查询该商品的温馨提示信息
	 * @param goodId
	 * @return
	 */
	public List<GoodKindly> getGoodKindlyById(Long goodId);

	/**
	 * @description:获得推荐商品Id(同品类热销，周边人气，网站热销)
	 * @param params
	 * @return Map<String,List<Long>>
	 * @throws
	 */
	public Map<String, List<Long>> getCommendGoodsId(Map<String, String> params) throws Exception;

	/**
	 * @description：获取订单量
	 * @param map
	 * @return List<CommendGoodsForm>
	 * @throws
	 */
	public List<CommendGoodsForm> getOrderCount(Map<Long, CommendGoodsForm> map);

	/**
	 * @description:同一个品牌商品只取一件
	 * @param map
	 * @return Map<Long,CommendGoodsForm>
	 * @throws
	 */
	public Map<Long, CommendGoodsForm> removeSameBrandGoods(Map<Long, CommendGoodsForm> map);

	public List<Map<String, Object>> getHuodongGoodsId(Long goodsId);

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
	 * edm你可能喜欢推荐规则
	 *@param areaId  商品所在城市ID
	 *@param days    几天内
	 *@return
	 */
	public List<GoodsForm> getEmailRecommendGoodIdsByAreaId(Long areaId, int days);
	
	/** 
	 * @description:获取该商品所属的所有分店的Id
	 * @param goodId
	 * @return List<Long>
	 * @throws 
	 */
	public List<Long> getBranchIdByGoodsId(Long goodId);
	
	/**
	 * 
	 * @Title: getTopGoodsWithFlagShip
	 * @Description: 查询旗舰店对应的推荐商品
	 * @param 
	 * @return Goods
	 * @author wenjie.mai
	 */
	public List<GoodsForm> getTopGoodsWithFlagShip(List<Long> merchantIdList);
	
	/**
	 * 
	 * @Title: getMaxSaleCountWithFlagShip
	 * @Description: 查询品牌旗舰店对应销量最多的商品
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List<GoodsForm> getMaxSaleCountWithFlagShip(List<Long> merchantIdList);
	
	/**
	 * 
	 * @Title: getMerchantIdByGoodId
	 * @Description: 通过商品ID查询品牌ID
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public Long getMerchantIdByGoodId(Long goodId);
}
