package com.beike.dao.mobile;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.mobile.LbsGoodsInfo;
import com.beike.entity.mobile.LbsMerchantInfo;

public interface LbsMerchantGoodsDao extends GenericDao<LbsMerchantInfo, Long> {
	
	/**
	 * 根据分店的id集合获取分店的信息
	 * @param merids  分店id的集合
	 * @return
	 */
	public List<Map<String, Object>> getMerInfoByMerIds(String merids);
	
	/**
	 * 获取商品下的分类标识
	 * @param goodsidstr
	 * @return
	 */
	public List<Map<String, Object>> getTypeTagesByGoodids(String goodsidstr);
	
	/**
	 * 获取分店下的商品ID和分类标签
	 * @param merchantIds  分店ID集合
	 * @return
	 */
	public List<Map<String, Object>> getGoodIdsAndTypeTages(String merchantIds);
	
	/**
	 * 查询分店下的商圈集合
	 * @return
	 */
	public List<Map<String, Object>> getBranchRegions(String merchantIds);
	
	/**
	 * 获取分店信息
	 * @param dataCount 分页数据量
	 * @param lastMaxId 上次最大分店ID，下次查询从ID>lastMaxId开始查询数据
	 * @return
	 */
	public List<LbsMerchantInfo> getLbsMerchantInfo(int dataCount, Long lastMaxId);
	
	/**
	 * 获取商品信息
	 * @param dataCount 分页数据量
	 * @param lastMaxMerchantId 上次最大分店ID
	 * @param lastMaxGoodsId 上次最大商品ID
	 * @return
	 */
	public List<LbsGoodsInfo> getLbsGoodsInfo(int dataCount, Long lastMaxMerchantId, Long lastMaxGoodsId);

	/**
	 * 根据商品ID集合查询商品信息
	 * @param goodids  商品ID的集合
	 * @return
	 */
	public List<Map<String, Object>> getGoodsByGids(String goodids);

	/**
	 * 获取分店下的扩展信息
	 * @param string
	 * @return
	 */
	public List<Map<String, Object>> getMerExpands(String merchantIds);
}
