package com.beike.action.pay.hessianclient;

import java.util.Map;

import com.beike.common.exception.BaseException;

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
	public Map<String, String> complateTrx(Map<String, String> sourceMap)
			throws Exception;

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
	 * 查看购物车列表
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryShoppingCart(Map<String, String> sourceMap);

	/**
	 * 添加购物车
	 * 
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
	public Map<String, String> qryTrxorderGoodsByUserId(
			Map<String, String> sourceMap);

	/**
	 * 查看凭证密码
	 * 
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryVoucherByTgId(Map<String, String> sourceMap);
	
	/**
	 * 我的钱包
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> queryPurse(Map<String, String> sourceMap);
	
	/**
	 *  查询可购买数量  
	 *
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> qryAllowBuyCountByUIdAndGId (Map<String, String> sourceMap);
	
	/**
	 * 子账户金额查询
	 * @param sourceMap
	 * @return
	 */
	public Map<String, String> getSubAccountByUserId(Map<String, String> sourceMap);
	
	/**
	 * 根据商品订单id查询商品订单结算信息
	 * @param sourceMap
	 * @return
	 */
	public Map<String,Object> qryTrxOrderGoodsDetailForSettle(Map<String,String> sourceMap) ;

}
