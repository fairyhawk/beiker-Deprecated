package com.beike.wap.dao;

import java.util.Date;
import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MGoodsCatlog;

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
 * Company: qianpin.com
 * </p>
 * 
 * @date 2011-09-20
 * @author lvjx
 * @version 1.0
 */

public interface MGoodsDao extends GenericDao<MGoods, Long> {

	/**
	 * Description : 查询需要展示的信息
	 * 
	 * @param typeType
	 * @param typeFloor
	 * @param typePage
	 * @param currentDate
	 * @return
	 * @throws Exception
	 */
	public List<MGoods> queryIndexShowMes(int typeType, int typeFloor,
			int typePage, Date currentDate, String typeArea) throws Exception;

	/**
	 * Description: 查询最大日期
	 * @param typeType
	 * @param typeFloor
	 * @param typePage
	 * @param typeArea
	 * @return
	 * @throws Exception
	 */
	public Date queryMaxDate(int typeType, int typeFloor,
			int typePage, String typeArea) throws Exception;
	
	/**
	 * Description : 根据商品ID查询商品详细信息
	 * 
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public MGoods queryDetailShowMes(int goodsId) throws Exception;

	/**
	 * Description : 根据商品ID，当前日期，所属区域查询店铺信息
	 * 
	 * @param goodsId
	 * @param currentDate
	 * @param typeArea
	 * @return
	 * @throws Exception
	 */
	public MGoods getMerchantById(int goodsId)
			throws Exception;

	/**
	 * Description : 根据商品ID，当前日期，所属区域查询品牌信息
	 * 
	 * @param goodsId
	 * @param currentDate
	 * @param typeArea
	 * @return
	 * @throws Exception
	 */
	public List<MGoods> getBranchById(int goodsId) throws Exception;
	
	/**
	 * Description : 根据查询条件查询符合条件的商品id
	 * @param goodsCatlog
	 * @return
	 * @throws Exception
	 */
	public List<Long> queryGoodsIds(int page,MGoodsCatlog goodsCatlog) throws Exception; 
	
	/**
	 * Description : 根据查询条件查询符合条件的商品总数量
	 * @param goodsCatlog
	 * @return
	 * @throws Exception
	 */
	public int queryGoodsIdsSum(MGoodsCatlog goodsCatlog) throws Exception;
	
	/**
	 * Description : 根据商品id查询符合条件的信息
	 * @param goodsIds
	 * @return
	 * @throws Exception
	 */
	public List<MGoods> queryGoodsInfo(String goodsIds) throws Exception;

	/**
	 * Description : 根据品牌id查询品牌下的商品信息
	 * @param brandId
	 * @return
	 * @throws Exception
	 */
	public List<MGoods> queryGoodsByBrandId(String goodsIds) throws Exception;
	
	/**
	 * 根据品牌id查询商品id列表
	 * @param brandId
	 * @return
	 * @throws Exception
	 */
	public List<Long> queryGoodsIdByBrandId(Long brandId) throws Exception;
	
	/**
	 * Description : 将商品添加到购物车
	 * @param goodsid
	 * @param merchantId
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public boolean addShopItem(String goodsid, String merchantId,int buySum, String userid) throws Exception;
	
	/**
	 * 根据商品id查询商品
	 * @param id
	 * @return
	 */
	public MGoods findById(Long id)  throws Exception;
}
