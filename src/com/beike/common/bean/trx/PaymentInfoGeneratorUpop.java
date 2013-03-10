package com.beike.common.bean.trx;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.util.Amount;
import com.beike.util.Configuration;
import com.beike.util.HttpUtils;


/** 
* @ClassName: PaymentInfoGeneratorUpop 
* @Description: 银联支付接口相关
* @author yurenli
* @date 2012-5-8 下午04:04:58 
* @version V1.0 
*/ 
public class PaymentInfoGeneratorUpop {

	private static final Log logger = LogFactory.getLog(PaymentInfoGeneratorUpop.class);
	

	private static String upopCallbackURL = Configuration.getInstance()
			.getValue("upopCallbackURL");
	
	private static String upopCallbackNotifyURL = Configuration.getInstance()
	.getValue("upopCallbackNotifyURL");

	private static String alipayrefundURL = Configuration.getInstance()
			.getValue("upoprefundURL");
	private static String upopMerCode = Configuration.getInstance()
	.getValue("upopMerCode");
	
	public static String getReqMd5HmacForOnlinePayment(String payRequestId,String needAmount,String extendInfo,String providerChannel,String goodsName) {
		//商户需要组装如下对象的数据
		String channel = "UPOP".equals(providerChannel)?"":providerChannel;
		String strExtendInfo = "?extendInfo="+extendInfo;
		String[] valueVo;
			valueVo = new String[]{
					UpopPayConfig.version,//协议版本
					UpopPayConfig.charset,//字符编码
			        "01",//交易类型
			        "",//原始交易流水号
			        upopMerCode,//商户代码
			        UpopPayConfig.merName,//商户简称
			        "",//收单机构代码（仅收单机构接入需要填写）
			        "",//商户类别（收单机构接入需要填写）
			        "",//商品URL
			        "",//商品名称
			       "",//商品单价 单位：分
			        "",//商品数量
			        "",//折扣 单位：分
			        "",//运费 单位：分
			        payRequestId,//订单号  
			        String.valueOf(Amount.mulByInt(Double.valueOf(needAmount),100)),//交易金额 单位：分.极其重要
			        "156",//交易币种
			        new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),//交易时间
			        "127.0.0.1",//用户IP
			        "",//用户真实姓名
			        "",//默认支付方式   
			        channel,//银行编号     
			        "1800000",//交易超时时间30分钟
			        upopCallbackURL+strExtendInfo,// 前台回调商户URL
			        upopCallbackNotifyURL+strExtendInfo,// 后台回调商户URL
			        ""//商户保留域
			};
		
		
			String	signType = UpopPayConfig.signType;
			
			//StringBuffer sbalipay = new StringBuffer();// 请求参数
		
		/*
		 * 说明：以下代码直接返回跳转到银联在线支付页面字符串
		 *       new QuickPayUtils().createPayHtml(valueVo, signType)
		 */
		String html = new QuickPayUtils().createPayHtml(valueVo, signType);//跳转到银联页面支付
		
		/*
		 * 说明：以下代码直接返回跳转到银行支付页面字符串 目前:支持工行(ICBC)，农行(ABC)，中行(BOC)，建行(CCB)，招行(CMB)，广发(GDB)，浦发(SPDB)
		 *       new QuickPayUtils().createPayHtml(valueVo, "CCB", signType)
		 */
		//String html = new QuickPayUtils().createPayHtml(valueVo, "CCB", signType);//直接跳转到网银页面支付
		return html;
	}

	/**
	 * 退款接口
	 * 
	 * @param p2_Order
	 * @param pb_TrxId
	 * @param p3_Amt
	 * @param p4_Cur
	 * @param p5_Desc
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static boolean refundByTrxId(String payRequestId,String needAmount,String proExternalId,String extendInfo,String goodsName) throws Exception {
		//商户需要组装如下对象的数据
		boolean boo = false;
		String strExtendInfo = "?extendInfo="+extendInfo;
		String[] valueVo;
		try {
			valueVo = new String[]{
					UpopPayConfig.version,//协议版本
					UpopPayConfig.charset,//字符编码
			        "04",//交易类型
			        proExternalId,//原始交易流水号
			        upopMerCode,//商户代码
			        UpopPayConfig.merName_refund,//商户简称
			        "",//收单机构代码（仅收单机构接入需要填写）
			        "",//商户类别（收单机构接入需要填写）
			        "",//商品URL
			        "",//商品名称
			       "",//商品单价 单位：分
			        "",//商品数量
			        "",//折扣 单位：分
			        "",//运费 单位：分
			        payRequestId,//订单号  
			        String.valueOf(Amount.mulByInt(Double.valueOf(needAmount),100)),//交易金额 单位：分.极其重要
			        "156",//交易币种
			        new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),//交易时间
			        "127.0.0.1",//用户IP
			        "",//用户真实姓名
			        "",//默认支付方式   
			        "",//银行编号     
			        "1800000",//交易超时时间30分钟
			        upopCallbackURL+strExtendInfo,// 前台回调商户URL
			        upopCallbackNotifyURL+strExtendInfo,// 后台回调商户URL
			        ""//商户保留域
			};
		
		
			String	signType = UpopPayConfig.signType;
			
		/*
		 * 说明：以下代码直接返回跳转到银联在线支付页面字符串
		 *     
		 */
		Map<String,String> map = new QuickPayUtils().refundPayHtml(valueVo,null, signType);//跳转到银联页面支付
		
		List<String> responseStr = HttpUtils.URLPost(alipayrefundURL, map);
		
	
		if (responseStr.size() == 0 || responseStr == null) {
			throw new Exception("No response.");
		}

		String respCode = "";
		String respMsg = "";
		for (int t = 0; t < responseStr.size(); t++) {
			String currentResult = responseStr.get(t);
			logger.info(currentResult);
			if (currentResult == null || currentResult.equals("")) {
				continue;
			}else{
				String currentArray[] = currentResult.split("&");
				for(int j=0;j<currentArray.length;j++){
					String array[] = currentArray[j].split("=");
					if("respCode".equals(array[0])){
						respCode = array[1];
					}
					if("respMsg".equals(array[0])){
						respMsg = array[1];
					}
				}
			}
		}
		if("00".equals(respCode)){
			boo = true;
		}else{
			logger.info("++++++++++++++++++++"+payRequestId+"++++++++"+respMsg);
		}
		return boo;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return boo;
		}
	}
	
	
}
