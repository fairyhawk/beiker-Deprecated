package com.beike.common.bean.trx.partner;

import java.util.HashMap;
import java.util.Map;

import com.beike.common.enums.trx.PartnerApiType;

/**
 * @Title: ParErrorMsgUtil.java
 * @Package com.beike.action.trx.partner
 * @Description: 合作分销商相关错误信息
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public class ParErrorMsgUtil {

	private static Map<String, String> par58ErrorMsgMap = new HashMap<String, String>();
	private static Map<String, String> par800ErrorMsgMap = new HashMap<String, String>();
	private static Map<String,String> par1mallErrorMsgMap = new HashMap<String,String>();

	public static final String T800_SUCCESS = "0";
	public static final String T800_SIGN_MISMATCH = "-1";
	public static final String T800_SALE_OUT = "1";
	public static final String T800_OFF_LINE = "5";
	public static final String T800_AMOUNT_MISMATCH = "6";
	public static final String T800_OTHER_ERROR = "7";

	static {
		/**58TC 58同城错误信息**/
		par58ErrorMsgMap.put("10000","成功");
		par58ErrorMsgMap.put("10100","系统错误");
		par58ErrorMsgMap.put("10201","参数格式错误");
		par58ErrorMsgMap.put("10202","券ID不存在");
		par58ErrorMsgMap.put("10203","已消费退款");
		
		/**
		 * add tuan800 error message by wz.gu 2012-11-08
		 */
		par800ErrorMsgMap.put(T800_SUCCESS, "成功");
		par800ErrorMsgMap.put(T800_SIGN_MISMATCH, "sign值不匹配");
		par800ErrorMsgMap.put(T800_AMOUNT_MISMATCH, "总金额不匹配");
		par800ErrorMsgMap.put(T800_OTHER_ERROR, "其它原因");
		par800ErrorMsgMap.put(T800_OFF_LINE, "产品不存在");
		par800ErrorMsgMap.put(T800_SALE_OUT, "产品已售光");
		
		/**
		 * 1mall 一号店错误信息
		 **/
		//系统级错误
		par1mallErrorMsgMap.put("system.error", "系统处理异常");
		par1mallErrorMsgMap.put("invalid.request", "非法请求报文");
		
		/*
		 * 团购订单信息通知
		 */
		//该参数输入有误,检查对应参数是否正确
		par1mallErrorMsgMap.put("yhd.group.buy.order.inform.param_invalid", "参数{0}有误");
		//订单通知成功后，orderCode已经存入合作方数据库，所以，当1mall再一次发送订单通知请求时，合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.order.inform_orderCode_exist", "订单orderCode已存在，重复的订单请求");
		//合作方应保证接收的productPrice与合作方数据库产品单价保持一致,不一致则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.order.inform_productPrice_invalid", "商品价格不合法，请检查productPrice");
		//合作方应保证接收的产品单价productPrice与购买数量productNum之积和接收的订单金额orderAmount保持一致，不一致则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.order.inform_orderAmount_invalid", "订单金额不一致，请检查orderAmount");
		//合作方应保证接收的合作方团购编号outerGroupId是己方的，否则合作方应提示相应的错误信息
		par1mallErrorMsgMap.put("yhd.group.buy.order.inform_outerGroupId_invalid", "找不到商品，请检查outerGroupId");
		
		/*
		 * 查询消费券信息
		 */
		//合作方接收订单编码orderCode，如果不存在于己方数据库，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.vouchers.get_orderCode_invalid", "订单不存在，请检查 orderCode");
		//合作方接收编码partnerOrderCode和订单编码orderCode，如果在己方数据库里不匹配，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.vouchers.get_partnerOrderCode_invalid", "订单关联错误，请检查 partnerOrderCode");

		
		/*
		 * 消费券短信重新发送
		 */
		//合作方接收订单编码orderCode等参数，如果不存在于己方数据库，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_orderCode_invalid", "订单不存在，请检查 orderCode");
		//合作方接收编码partnerOrderCode和订单编码orderCode等参数，如果在己方数据库里不匹配，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_partnerOrderCode_invalid", "订单关联错误，请检查 partnerOrderCode");
		//短信重发请求时间requestTime和当前系统时间相比，不能超过合作方指定的时间段内，否则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_requestTime_invalid", "请求时间误差大于N分钟，请检查requestTime");
		//合作方接收的消费券voucherCode参数，如果不在己方的相应的订单及数据库里，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_voucherCode_invalid", "找不到相应的消费券，请检查voucherCode");
		//如果1mall达到的合作方短信次数的限制，合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_requestNumber_invalid", "短信重发次数已经达到" + Par1mallOrderGenerator.YHD_VOUCHERRESEND_LIMIT + "次上限");
		//合作方接收的消费券voucherCode参数，如果存在己方的相应的订单及数据库里但未使用已经过期，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_voucherCode_overTime", "消费券未使用已过期");
		//合作方接收的消费券voucherCode参数,如果消费券已经使用，则合作方应提示相应的错误信息描述
		par1mallErrorMsgMap.put("yhd.group.buy.voucher.resend_voucherCode_used", "消费券已使用不再发送短信");

	}

	public static String getParErrorMsgByCode(String errorCode,String  partnerApiType){
		String errorMsg="";
		if(PartnerApiType.TC58.name().equals(partnerApiType)){
			errorMsg = par58ErrorMsgMap.get(errorCode);
		} else if (PartnerApiType.TUAN800.name().equals(partnerApiType)) {
			errorMsg = par800ErrorMsgMap.get(errorCode);
		}else if(PartnerApiType.YHD.name().equals(partnerApiType)){
			errorMsg=par1mallErrorMsgMap.get(errorCode);
		}
		return errorMsg == null ? "" : errorMsg;
	}
}