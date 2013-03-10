package com.beike.biz.service.hessian;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.beike.common.exception.BaseException;

/**
 * @Title:TrxHessianServiceGateWay.java
 * @Package com.beike.biz.service.hessian
 * @Description: 交易对其它模块提供的交易hessian网关接口
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 7:58:06 PM
 * @version V1.0
 */
public interface TrxHessianServiceGateWay {

	/**
	 * 创建交易订单
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> createTrxOrder(Map<String, String> sourceMap);

	/**
	 * 账户查询
	 * 
	 * @param userId
	 * @param actType
	 * @return
	 */
	public Map<String, String> getActByUserId(Map<String, String> sourceMap);

	/**
	 * 千品卡充值
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> topupCard(Map<String, String> sourceMap);
	
	
	/**
	 * 线上活动自动绑定优惠券
	 * @param sourceMap
	 * @return
	 */
	public Map<String,String> autoBindCoupon(Map<String, String> sourceMap);
	
	/**
	 * 优惠券激活
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> activateCoupon(Map<String, String> sourceMap);

	/**
	 * 千品卡查询
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> queryCardInfo(Map<String, String> sourceMap);

	/**
	 * 重发凭证码
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> reSendVoucher(Map<String, String> sourceMap);

	/**
	 * 账户创建
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, String> createAccount(Map<String, String> sourceMap);

	/**
	 * 返现--弃用
	 * 
	 * @param userId
	 * @param rebateCount
	 * @return
	 * @throws RebateException
	 */
	// public Map<String, String> rebate(Map<String, String> sourceMap);

	/**
	 * 支付机构查询接口
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryPayStauts(Map<String, String> sourceMap);

	/**
	 * 支付机构查询接口并补单
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> complatePayStauts(Map<String, String> sourceMap);

	/**
	 * 完成交易
	 * 
	 * @param sourceMap
	 * @return
	 * @throws BaseException
	 * @throws Exception
	 */
	public Map<String, String> complateTrx(Map<String, String> sourceMap)throws Exception;

	/**
	 * 根据订单商品明细ID返现--弃用
	 * 
	 * @param sourceMap
	 * @return
	 */
	// public Map<String, String> rebatebyTrxGoodsId(Map<String, String>
	// sourceMap);

	/**
	 * 根据用户帐号进行返现--弃用
	 * 
	 * @param sourceMap
	 * @return
	 */
	// public Map<String, String> rebatebyUserId(Map<String, String> sourceMap);

	/**
	 * 账户退款申请
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundApplyToAct(Map<String, String> sourceMap);

	/**
	 * 账户退款拒绝
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundRefuseToAct(Map<String, String> sourceMap);

	/**
	 * 退款到账户
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundToAct(Map<String, String> sourceMap);

	/**
	 * 银行卡退款申请
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundApplyToBank(Map<String, String> sourceMap);

	/**
	 * 银行卡退款拒绝
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundRefuseToBank(Map<String, String> sourceMap);

	/**
	 * 退款到银行卡
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> refundToBank(Map<String, String> sourceMap);

	/**
	 * 人工创建凭证--弃用
	 * 
	 * @return
	 */
	// public Map<String, String> createVoucher();

	/**
	 * 销毁凭证
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> destoryVoucherByID(Map<String, String> sourceMap);

	/**
	 * 根据客户号和凭证内容校验凭证及回收
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> validateVoucher(Map<String, String> sourceMap);

	/**
	 * 个人限购支付前校验--弃用（已整合到下单接口）
	 * 
	 * @param toPayCountList
	 * @param uId
	 * @param gIdList
	 * @return
	 */
	//public Map<String, String> validatePayLimit(Map<String, String> sourceMap);

	/**
	 * 创建虚拟款项账户
	 * 
	 * @return
	 */
	public Map<String, String> createVmAccount(Map<String, String> sourceMap);

	/**
	 * 往虚拟款项账户追加余额
	 * 
	 * @return
	 */
	public Map<String, String> pursueVmAccount(Map<String, String> sourceMap);

	/**
	 * 下发虚拟款项
	 * 
	 * @return
	 */
	public Map<String, String> dispatchVm(Map<String, String> sourceMap);
	
/**
 *  查询可购买数量  
 *
 * @param sourceMap
 * @return
 */
	public Map<String, String> qryAllowBuyCountByUIdAndGId (Map<String, String> sourceMap);

	/**
	 * 查看购物车列表
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryShoppingCart(Map<String, String> sourceMap);

	/**
	 * 添加购物车
	 * @author  jianjun.huo
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> addShoppingCart(Map<String, String> sourceMap);

	/**
	 * 删除购物车
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> delShoppingCartById(Map<String, String> sourceMap);

	/**
	 * 查看商品订单列表
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryTrxorderGoodsByUserId(Map<String, String> sourceMap);

	/**
	 * 查看凭证密码
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryVoucherByTgId(Map<String, String> sourceMap);
	
	/**
	 * 根据凭证码和商家编号查询订单信息 
	 * @param sourceMap
	 * @return
	 */
	public Map<String,String> qryOrderByVouCodeAndGuestId(Map<String,String> sourceMap);
	
	/**
	 * 查询商家交易订单
	 * @return
	 */
	public Map<String,Object> qryTrxOrderGoodsForGuest(Map<String,String> sourceMap);
	
	/**
	 * 根据商品订单id查询商品订单结算信息
	 * @param sourceMap
	 * @return
	 */
	public Map<String,Object> qryTrxOrderGoodsDetailForSettle(Map<String,String> sourceMap);
	
	/**
	 * 查询订单详情
	 * @param sourceMap
	 * @return
	 */
	public Map<String,Object> qryTgDetailForGuest(Map<String,String> sourceMap);
	
	/**
	 * 我的钱包
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> queryPurse(Map<String, String> sourceMap);
	/**
	 * 子账户金额查询
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> getSubAccountByUserId(Map<String, String> sourceMap);

	/***********************************************************************************
	 * 商家后台会员管理、统计分析数据服务接口 begin
	 ***********************************************************************************/
	/**
	 * 商家会员总览
	 */
	public Map<String,Object> queryVipStatistics(Long guestId);
	
	/**
	 * 商家月度会员统计
	 * @param guestId 商家id
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return
	 */
	public List<Map<String, Object>> queryMemberCountMonth(Long guestId,String startDate,String endDate);
	
	/**
	 * 分店商品维度收入统计分析
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String,Object>> queryIncomeStatistics(Map<String, Object> paramsMap);
	
	/**
	 * 线上会员查询
	 * @param paramsMap
	 * @return
	 */
	public List<Map<String,Object>> queryOnlineMember(Map<String, Object> paramsMap);
	
	/**
	 * 线上会员总数
	 * @param paramsMap
	 * @return
	 */
	public int queryOnlineMemberCount(Map<String, Object> paramsMap);
	
	/**
	 * 线上会员详情
	 * @param paramsMap
	 * @return
	 */
	public Map<String,Object> queryOnlineMemberDetail(Map<String, Object> paramsMap);
	
	/**
	 * 会员商品明细
	 * @param paramsMap 其中isConsume为true表示查询已消费，为false为未消费
	 * @return
	 */
	public List<Map<String,Object>> queryVipProduct(Map<String, Object> paramsMap);
	
	/**
	 * 会员商品数量
	 * @param paramsMap 其中isConsume为true表示查询已消费，为false为未消费
	 * @return
	 */
	public int queryVipProductCount(Map<String, Object> paramsMap);
	
	/**
	 *  查询商品菜单，订单总的原价以及总的支付价
	 * @param trxorderId,guestId
	 * @return
	 */
	Map<String, Object> queryMenuByOrderId(Long trxorderId,Long guestId);
	
	/**
	 * 会员评价查询
	 *@param paramsMap
	 *@return
	 */
	public List<Map<String, Object>> queryMemberEvaluation(Map<String, Object> paramsMap);

	/**
	 * 会员评价数量
	 *@param paramsMap
	 *@return
	 */
	public int queryMemberEvaluationCount(Map<String, Object> paramsMap);
	
	/**
	 * 每日计算商家会员
	 */
	public void countMemberDaily(Date date);
	
	/**
	 * 营销活动统计
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryOnlineOrder(Map<String,Object> params);
	/***********************************************************************************
	 * 商家后台会员管理、统计分析数据服务接口 end
	 ***********************************************************************************/
	/**
     * 商品订单查询,根据ID。不查询行数
     * @param  sourceMap
     * @return Map<String, Object>
     */
    public Map<String, Object> queryTrxGoodsByIds(Map<String, String> sourceMap);
    
    /**
     * 查询商家订单团购、网上分店的购买数量、消费数量
     * @param  sourceMap
     * @return Map<String, String>
     */
    public Map<String, String> queryTrxGoodsCountForGuest(Map<String, String> sourceMap);
}
