package com.beike.biz.service.hessian;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Title: VipHessianService.java
 * @Package com.beike.biz.service.hessian
 * @Description: 商家后台会员相关信息查询hessian
 * @date January 28, 2013 10:22:06 AM
 * @version V1.0
 */
public interface VipHessianService {
	/**
	 *  查询线上会员信息
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryVip(Map<String, Object> params);
	
	/**
	 *  通过userId查询线上会员信息
	 * @param userId,guestId
	 * @return
	 */
	Map<String, Object> queryVipById(Long userId,Long guestId);
	
	/**
	 *  查询线上会员数量
	 * @param params
	 * @return
	 */
	int queryVipCount(Map<String, Object> params);
	/**
	 *  会员未消费商品明细
	 * @param params,其中params中isConsume为true表示查询已消费，为false为未消费
	 * @return
	 */
	List<Map<String, Object>> queryVipProduct(Map<String, Object> params);
	/**
	 *  会员未消费商品数量
	 * @param params,其中params中isConsume为true表示查询已消费，为false为未消费
	 * @return
	 */
	int queryVipProductCount(Map<String, Object> params);
	
	/**
	 *  查询商品菜单
	 * @param trxorderId
	 * @return
	 */
	Map<String, Object> queryMenuByOrderId(Long trxorderId,Long guestId);
	
	/**
	 * 根据查询条件查询会员评价记录数
	 *@param queryMap
	 *@return
	 */
	public int queryEvaluateCount(Map<String, Object> queryMap);
	
	/**
	 * 分页获取评价数据
	 *@param queryMap 查询参数
	 *@param curPage  当前页
	 *@param pageSize 每页显示记录数
	 *@return
	 */
    public List<Map<String, Object>> queryEvaluation(Map<String, Object> queryMap,  int curPage, int pageSize);
    
    /**
	 * 查询当天交易信息
	 * @param date 日期
	 * @return
	 */
	public List<Map<String,Object>> queryTrxOrderInfo(Date date);
	
	/**
	 * 不同分店不同产品的收入统计
	 *@param queryMap 查询参数
	 *@return
	 */
	public List<Map<String, Object>> queryIncomeStatistics(Map<String, Object> queryMap);
	
	/**
	 * 各分店总的收入统计
	 *@param lstTrxOrderGoods
	 *@return
	 */
	public Map<Long, Map<String, Object>> queryIncomeTotalStatistics(List<Map<String, Object>> lstOrderGoods);
	
	/**
	 * 更新vip每月的新增的数量
	 * @return
	 */
	public void updateVipInfoForMonth(Date date);
	
	/**
	 * 新增新的会员信息
	 * @param trxOrderInfo 会员信息
	 * @return
	 */
	public void addNewVipStatistics(Map<String,Object> trxOrderInfo);
	
	/**
	 * 线上会员总数
	 * @return int
	 */
	public int queryOnlineVipCount(Long guestId);
	
	/**
	 * 查询新增会员数 本月 三月内 十二月内
	 * @param month 几个月内
	 * @param guestId 商家id
	 * @return 
	 */
	public int queryNewVipCount(int month,Long guestId);
	
	/**
	 * 查询老会员活跃度 30天 60天
	 * @param guestId 商家id
	 * @param days 天数
	 * @return 
	 */
	public int queryOldVipActive(Long guestId,int days);
	
	/**
	 * 根据时间和商家id查询截止到当月的会员总数
	 * @param guestId 商家id
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return 
	 */
	public List<Map<String,Object>> queryAllVipNumByDate(Long guestId,String startDate,String endDate);
	
	/**
	 * 根据商家id查询会员总览
	 * @param guestId 商家id
	 * @return 
	 */
	public Map<String,Object> queryVipStatistics(Long guestId);
	
	/**
	 * 营销活动统计
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryOnlineOrder(Map<String,Object> params);
}
