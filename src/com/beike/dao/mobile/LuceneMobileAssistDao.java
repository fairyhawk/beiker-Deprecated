package com.beike.dao.mobile;

import java.util.List;
import java.util.Map;

public interface LuceneMobileAssistDao {

	/**
	 * 
	 * janwen
	 * @param brandids
	 * @return 品牌分店总数
	 *
	 */
	public List<Map<String,Object>> getBranchOfBrand(List<Long> brandids);
	
	
	/**
	 * 
	 * janwen
	 * @param goodsids
	 * @return 分店对应品牌id
	 *
	 */
	public List<Map<String,Object>> getBrandofGoods(List<Long> goodsids);
	
	
	/**
	 * 分店商品
	 * janwen
	 * @param branchid
	 * @return
	 *
	 */
	public List<Map<String,Object>> getBranchGoods(List<Long> querybranchid);
	
	
	public Map<Long,String> getCityEnName();
	
	/**
	 * 
	 * @param goodsidList
	 * @return 商品信息
	 * 4:50:36 PM
	 * janwen
	 *
	 */
	public List<Map> getGoodsList(List<Long> goodsidList);
	
	public List<Map<String,Object>> getindexdata();
	
	/**
	 * 
	 * janwen
	 * @param querygoodsid
	 * @return  商品销售量
	 *
	 */
	public List<Map<String,Object>> getGoodsSale(List<Long> querygoodsid);
	
	/**
	 * 
	 * janwen
	 * @param querygoodsid
	 * @return 商品现价
	 *
	 */
	public List<Map<String,Object>> getGoodsCurrentPrice(List<Long> querygoodsid);
	
	/**
	 * 
	 * @param goodsidList
	 * @return 商品品牌
	 * 6:02:29 PM
	 * janwen
	 *
	 */
	public List<Map> getGoodsBrand(boolean first,List<Long> goodsidList);
	
	/**
	 * 
	 * @param goodsidList
	 * @param fir 是不是一级商圈
	 * @return 商品分店
	 * 6:02:37 PM
	 * janwen
	 *
	 */
	public List<Map> getGoodsBranch(boolean fir,List<Long> goodsidList);
	
	/**
	 * 
	 * janwen
	 * @param brandids
	 * @return 品牌对应所有分店
	 *
	 */
	public List<Map> getBranchesByBrandID(List<Long> brandids);
	/**
	 * 品牌列表
	 * @param brandidList
	 * @return
	 * 4:50:23 PM
	 * janwen
	 *
	 */
	public List<Map> getBrandList(List<Long> brandidList);
	
	/**
	 * 
	 * @param brandList
	 * @return 品牌基本信息
	 * 10:25:04 AM
	 * janwen
	 *
	 */
	public List<Map> getBrandBasic(List<Long> brandidList);
	/**
	 * 
	 * @param brandid
	 * @return 品牌所属商品
	 * 5:58:58 PM
	 * janwen
	 *
	 */
	public List<Long> getBrandGoodsIDList(Long brandid);
	/**
	 * 
	 * @param goodsidList
	 * @return 品牌所属商品详情
	 * 5:59:54 PM
	 * janwen
	 *
	 */
	public List<Map> getBrandGoodsDeatil(List<Long> goodsidList);
	/**
	 * 
	 * @param brandid
	 * @return 品牌图片列表
	 * 5:21:37 PM
	 * janwen
	 *
	 */
	public List<Map> getBrandPhoto(Long brandid);
	/**
	 * 
	 * @param brandidList
	 * @param fir 一级商圈
	 * @return 品牌分店
	 * 10:25:44 AM
	 * janwen
	 *
	 */
	public List<Map> getBrandBranches(boolean fir,List<Long> brandidList);
	
	/**
	 * 
	 * @param brandidList
	 * @return 品牌分类
	 * 10:26:23 AM
	 * janwen
	 *
	 */
	public List<Map> getBrandCat(boolean fir,List<Long> brandidList);
	
	/**
	 * 
	 * @param fir 是否一级商圈
	 * @param goodsidList
	 * @return 商品分类
	 */
	public List<Map> getGoodsCat(boolean fir,List<Long> goodsidList);
	
	/**
	 * 
	 * @param goodsid
	 * @return 品牌介绍
	 */
	public List<String> getBrandIntrByGoodsID(Long goodsid);
}
