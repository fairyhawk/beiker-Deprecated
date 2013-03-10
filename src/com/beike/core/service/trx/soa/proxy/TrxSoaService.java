package com.beike.core.service.trx.soa.proxy;

import java.util.List;
import java.util.Map;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.common.bean.trx.TrxRequestData;
import com.beike.common.exception.TrxOrderException;

/**
 * @Title: TrxSoaService.java
 * @Package com.beike.dao.trx
 * @Description: 交易解耦。伪SOA,TrxSoaService代理类(所有交易涉及的解耦Dao都会走此Service)
 * @date May 16, 2011 10:47:50 AM
 * @author wh.cheng
 * @version v1.0
 */
public interface TrxSoaService {

	/*	*//**
	 * 根据手机号查找用户
	 * 
	 * @param mobile
	 *            手机号
	 * @return 用户对象
	 */
	/*
	 * public Map<String,Object> findUserByEmail(String email);
	 *//**
	 * 根据email查找用户
	 * 
	 * @param email
	 *            邮箱
	 * @return 用户对象
	 */
	/*
	 * public Map<String,Object> findUserByMobile(String mobile);
	 */

	/**
	 * 根据id查找用户
	 * 
	 * @param id
	 *            主键
	 * @return Long
	 */
	public Long findUserById(Long id);

	/**
	 * 根据id查找用户手机号相关信息
	 * 
	 * @param id
	 *            主键
	 * @return Long
	 */
	public Map<String, Object> findMobileUserById(Long id);
	
	/**
	 * 根据id查找用户手机号相关信息（主库查询）
	 * 
	 * @param id
	 *            主键
	 * @return Long
	 */
	public Map<String, Object> preQryInWtDBMobileUserById(Long id);
	
	
	/**
	 * 查用户
	 * 
	 * @param id
	 * @return
	 */

	public Map<String, Object> findUserInfoById(Long id);

	/**
	 * 查询商品个人限购信息。
	 * 
	 * @param goodsId
	 *            add by wenhua.cheng
	 * @return
	 */
	public Map<String, Object> getSingleCount(Long goodsId);

	/**
	 * 查询goodsTitle
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<Long, String> findGoodsTitle(List<Long> goodsIdList);

	/**
	 * 查询品牌
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<String, Object> findMerchantName(List<Long> goodsIdList);
	
	
	/**
	 * 总量限购和上下架获取
	 * @param goodsId
	 * @return
	 */
	public Map<String, Object> getMaxCountAndIsAvbByIdInMem(Long goodsId);

	/**
	 * 查询商品logo4
	 * 
	 * @param goodsIdList
	 * @return
	 */
	public Map<Long, String> findGoodsDTPicUrl(List<Long> goodsIdList);

	/**
	 * 交易请求数据转换
	 * 
	 * @param requestData
	 * @return
	 * @throws TrxOrderException
	 */
	public OrderInfo tansTrxReqData(TrxRequestData requestData)
			throws TrxOrderException;
	/**
	 * 销售总量更新
	 * @param map
	 */
	public void updateSalesCount(Map<Long, Integer> map);
	
	/**
	 * 查询goodsTitle和是否支持预定
	 * @param goodsIdList
	 * @return
	 */
	public Map<Long, String> findGoodsTitleAndIsscheduled(List<Long> goodsIdList) ;
	
	/**
	 * 我的订单展示
	 * @param trxgoods_id
	 * @return
	 */
	public  Map<String,Object> findBytrxgoodsId(Long trxgoods_id);
	
	/**
	 * 退款或过期调用取消预定
	 * @param trxGoodsId
	 * @param trxorderId
	 * @return
	 */
	public boolean processScheduled(Long trxGoodsId,Long trxorderId);
	/**
	 * 查询分店名称。
	 * 
	 * @param goodsId
	 *            add by renli.yu
	 * @return
	 */
	public Map<String,Object> getMerchantById(Long merchantId);
	
	/**
	 * 根据goodsId批量查询商品详情
	 * @param goodsId
	 * @return
	 */
	public List<Map<String, Object>> findGoodsList(String goodsIdStr);
	
	/**
	 * 查询品类
	 * 
	 * @param goodsId
	 * @return
	 */
	public Map<Long, String> findTagByIdName(Long goodsId);
}
