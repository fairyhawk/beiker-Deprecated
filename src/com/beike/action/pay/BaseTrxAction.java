package com.beike.action.pay;

import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.common.exception.UserException;
import com.beike.entity.user.User;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * @Title: BaseTrxAction.java
 * @Package com.beike.action.pay
 * @Description: 交易基础action
 * @date Jun 13, 2011 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public class BaseTrxAction {

	/* 缺省的视图名称 */
	protected static final String VIEW_SHOW_TRX_ORDER = "/ucenter/showTrxOrder";

	protected static final String VIEW_PRINT_VOUCHER = "/ucenter/printVoucher";

	protected static final String VIEW_SEND_VOUCHER_BY_PHONE = "/ucenter/sendVoucherBySms";

	protected static final String VIEW_SEND_VOUCHER_BY_EMAIL = "/ucenter/sendVoucherByEmail";

	protected static final String VIEW_COMMENT_GOODS = "/ucenter/commentGoods";

	protected static final String VIEW_REFUND_APPLY = "/ucenter/refundApply";

	protected static final String VIEW_SHOW_PURSE = "/ucenter/showPurse";

	protected static final String VIEW_SHOW_REBATE = "/ucenter/showRebate";

	protected static final String VIEW_ERROR = "error";
	
	private final Log logger = LogFactory.getLog(BaseTrxAction.class);
	protected Long getLoginUserId(HttpServletRequest request)
			throws UserException {
		User user = SingletonLoginUtils.getMemcacheUser(request);

		if (user == null) {
				throw new UserException(UserException.USER_NOT_EXIST);
		}
		return user.getId();
	}
	
	/**
	 * 获取商家编号
	 * @param request
	 */
	protected String getPartnerNo(HttpServletRequest request) {
		String partnerNo = request.getParameter("appid");// app_key 即商家编号
		logger.info("+++++++++++++++partnerNo="+ partnerNo);
		return partnerNo;

	}
	
	
	/**
	 * 获取密文
	 * @param request
	 */
	protected String getPartnerdDes(HttpServletRequest request) {
		String desStr = request.getParameter("param");// 密文
		logger.info("++++++++++++++desStr=" + desStr + "+++++++++");
		return desStr;
	}
	
	
	
	
	/**
	 * 接收发码和重发参数组装
	 * @param request
	 * @return
	 */
	public TreeMap<String,String> getRequestMap(HttpServletRequest request){
		TreeMap<String, String> requestMap = new TreeMap<String, String>();
		requestMap.put("timestamp",request.getParameter("timestamp"));//接口调用时的时间
		requestMap.put("order_id",request.getParameter("order_id"));//淘宝订单交易号
		requestMap.put("mobile",request.getParameter("mobile"));//买家的手机号码
		requestMap.put("num",request.getParameter("num"));// 购买的商品数量
		requestMap.put("taobao_sid",request.getParameter("taobao_sid"));//商家编号
		requestMap.put("item_title",request.getParameter("item_title"));//商品标题
		requestMap.put("send_type",request.getParameter("send_type"));//发送类型
		requestMap.put("sms_template",request.getParameter("sms_template"));//短信、彩信文字模板
		requestMap.put("valid_start",request.getParameter("valid_start"));//有效期开始时间
		requestMap.put("valid_ends",request.getParameter("valid_ends"));//有效期截止时间
		requestMap.put("num_iid",request.getParameter("num_iid"));//淘宝商品编号
		requestMap.put("outer_iid",request.getParameter("outer_iid"));//外部商家自定义商品ID
		requestMap.put("serve_print_text",request.getParameter("serve_print_text"));//
		requestMap.put("token",request.getParameter("token"));//验证串，商家回调时须回传，否则将验证不通过
		requestMap.put("method",request.getParameter("method"));
		return requestMap;
	}

	/**
	 * 退款和维权参数组装
	 * @param request
	 * @return
	 */
	public TreeMap<String,String> getCancerMap(HttpServletRequest request){
		TreeMap<String, String> requestMap = new TreeMap<String, String>();
		requestMap.put("timestamp",request.getParameter("timestamp"));//接口调用时的时间
		requestMap.put("order_id",request.getParameter("order_id"));//淘宝订单交易号
		requestMap.put("mobile",request.getParameter("mobile"));//买家的手机号码
		requestMap.put("num",request.getParameter("num"));// 购买的商品数量
		requestMap.put("taobao_sid", request.getParameter("taobao_sid"));//商家编号
		requestMap.put("item_title",request.getParameter("item_title"));//商品标题
		requestMap.put("method",request.getParameter("method"));
		requestMap.put("send_type",request.getParameter("send_type"));//发送类型
		requestMap.put("valid_start",request.getParameter("valid_start"));//有效期开始时间
		requestMap.put("valid_ends",request.getParameter("valid_ends"));//有效期截止时间
		requestMap.put("num_iid",request.getParameter("num_iid"));//淘宝商品编号
		requestMap.put("outer_iid",request.getParameter("outer_iid"));//外部商家自定义商品ID
		requestMap.put("token",request.getParameter("token"));//验证串，商家回调时须回传，否则将验证不通过
		return requestMap;
	}
}
