package com.beike.dao.trx.soa.proxy;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;

/**
 * @ClassName: GoodsSoaDao
 * @Description: TODO
 * @author yurenli
 * @date 2012-3-24 下午04:54:37
 * @version V1.0
 */
public interface GoodsSoaDao extends GenericDao<Object, Long> {

	public Map<String, Object> getGoodsProfile(Long goodsId);

	public Map<String, Object> getmaxCountById(Long id);

	public List<Map<String, Object>> getMaxcountByIdlist(String id);

	/**
	 * 获取总量限购商品已经购买量
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> getGoodsProfileByGoodsid(String goodsId);

	/**
	 * 查询商品个人限购信息。
	 * 
	 * @param goodsId
	 *            add by wenhua.cheng
	 * @return
	 */
	public Map<String, Object> getSingleCount(Long goodsId);

	/**
	 * 根据商品表主键查询商品简称
	 * 
	 * @param goodsId
	 *            add by wenhua.cheng
	 * @return
	 */
	public Map<String, Object> findGoodsTitleById(Long goodsId);

	/**
	 * 查询品牌
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<String, Object> findMerchantName(List<Long> goodsIdList);

	/**
	 * 查询商品URL
	 * 
	 * @param goodsId
	 * @return
	 */
	public Map<String, Object> findGoodsPicUrlById(Long goodsId);

	/**
	 * 根据goodsId批量查询商品详情
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> findBatchGoodsInfoByIdStr(String goodsIdStr);
	
	/**
	 *  商品销售量更新
	 * @param goodId
	 * @param salesCountStr
	 */
	public void updateSalesCount(Long goodId, String salesCountStr);
	
	
	/**
	 * 根据主键查询秒杀表信息
	 * @param id
	 * @return
	 */
	public Map<String,Object> findMiaoSha(Long id);
	

	/**
	 * 根据主键查询秒杀表实际销量(带悲观锁)
	 * @param id
	 * @return
	 */
	public int findMiaoShaSaleCountForUpdate(Long id);
	
	/**
	 * 根据主键更新秒杀表实际销量
	 * @param saleCount
	 * @param id
	 * @return
	 */
	public int updateMiaoShaSaleCountById(int saleCount,Long id);
	
	/**
	 * 我的订单展示
	 * @param trxgoods_id
	 * @return
	 */
	public Map<String,Object> findBytrxgoodsId(Long trxgoods_id);
	
	/**
	 * 根据goodsId获得商品所属catlog信息
	 * @param goodsId
	 * @return
	 */
	public List<Map<String,Object>> getCatalogGoods(Long goodsId);
	
	/**
	 * 查找所有父种类（商品类别）
	 * @return java.util.List<Tag>
	 * @throws Exception
	 */
	public Map<String,String> findParentTags();
	/**
	 * 查询分店名称
	 * 
	 * @param subGuestId
	 * @return
	 */
	public Map<String, Object> getMerchantById(Long subGuestId) ;
	
	/**
	 * @desc 根据 id查询 filmshow 表的信息
	 * @param id
	 * @return
	 * @author ljp
	 * @date 20121205
	 */
	public Map<String, Object> getFilmShowByShowIndex(Long id);
	
	/**
	 * @desc 根据影院Cinemaid查询影院信息
	 * @param Cinemaid
	 * @return
	 * @author ljp
	 * @date 20121205
	 */
	public Map<String, Object> getCinemaByCinemaId(Long id);
	
	/**
	 * 根据商品订单id查询影院id
	 * @param trxGoodsId
	 * @return
	 * @author ljp
	 */
	public Map<String, Object> getCinemaIdByTrxGoodsId(Long trxGoodsId);
	
	/**
	 * 根据大桥网票网影院ID查询千品平台影院ID
	 * @param cinemaId
	 * @return
	 */
	public Long queryQianpinCinemaByWpId(Long cinemaId) ;
	
	/**
	 * 查询品类
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<String, Object> findTagByIdName(Long goodsId);
}
