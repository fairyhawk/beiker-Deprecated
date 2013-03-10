package com.beike.util;

import java.util.HashMap;
import java.util.Map;

/** 银行信息工具类 */
public class BankInfoUtil {

	/** 银行id对应银行名称map */
	private static Map<String, String> bankInfoMap;

	private static Map<String, String> payInfoMap;

	/**
	 * 获取银行信息Map对象
	 * 
	 * @return Map<String, String> key:pd_FrpId参数值， value:银行名称
	 */
	public static Map<String, String> getInstanceForBankMap() {
		if (bankInfoMap == null) {
			bankInfoMap = new HashMap<String, String>();
			initBankMapInfo();

		}
		return bankInfoMap;
	}

	/**
	 * 获取支付服务商信息 Map
	 * 
	 * @return Map<String,String> key:支付服务商Id，value:服务商名称
	 */
	public static Map<String, String> getInstanceForPayMap() {
		if (payInfoMap == null) {
			payInfoMap = new HashMap<String, String>();
			initPayMapInfo();
		}
		return payInfoMap;
	}

	/**
	 * 支付机构及通道转换
	 * 
	 * @param providerType
	 * @param providerChannel
	 * @param content
	 * @return
	 */
	public static String convProviderAndChannel(String providerType,
			String providerChannel) {
		String providerInfo = getInstanceForPayMap().get(providerType);
		StringBuilder resultSb = new StringBuilder();
		String typeProviderChannel = "";
		if ("ALIPAY".equals(providerType)) {
			typeProviderChannel = "ALIPAY-"+providerChannel;
		} else if ("YEEPAY".equals(providerType)) {
			typeProviderChannel = "YEEPAY-"+providerChannel;
		}else if("UPOP".equals(providerType)){
			typeProviderChannel = "UPOP-"+providerChannel;
		}
		String channelInfo = getInstanceForBankMap().get(typeProviderChannel);
		resultSb.append(providerInfo);
		if(!StringUtils.isEmpty(providerType) &&  ! providerType.equals(providerChannel)){//如果主子通道不一致，则为直连。则需要写入子通道日志
		resultSb.append("(");
		resultSb.append(channelInfo);
		resultSb.append(")");
		}
		return resultSb.toString();

	}

	/**
	 * 加载银行Map信息
	 */
	private static void initBankMapInfo() {
		bankInfoMap.put("YEEPAY-ICBC-NET", "工商银行");
		bankInfoMap.put("YEEPAY-CMBCHINA-NET", "招商银行");
		bankInfoMap.put("YEEPAY-ABC-NET", "中国农业银行");
		bankInfoMap.put("YEEPAY-CCB-NET", "建设银行");
		bankInfoMap.put("YEEPAY-BCCB-NET", "北京银行");
		bankInfoMap.put("YEEPAY-BOCO-NET", "交通银行");
		bankInfoMap.put("YEEPAY-CIB-NET", "兴业银行");
		bankInfoMap.put("YEEPAY-NJCB-NET", "南京银行");
		bankInfoMap.put("YEEPAY-CMBC-NET", "中国民生银行");
		bankInfoMap.put("YEEPAY-CEB-NET", "光大银行");
		bankInfoMap.put("YEEPAY-BOC-NET", "中国银行");
		bankInfoMap.put("YEEPAY-PINGANBANK-NET", "平安银行");
		bankInfoMap.put("YEEPAY-CBHB-NET", "渤海银行");
		bankInfoMap.put("YEEPAY-HKBEA-NET", "东亚银行");
		bankInfoMap.put("YEEPAY-NBCB-NET", "宁波银行");
		bankInfoMap.put("YEEPAY-ECITIC-NET", "中信银行");
		bankInfoMap.put("YEEPAY-SDB-NET", "深圳发展银行");
		bankInfoMap.put("YEEPAY-GDB-NET", "广发银行");
		bankInfoMap.put("YEEPAY-SHB-NET", "上海银行");
		bankInfoMap.put("YEEPAY-SPDB-NET", "上海浦东发展银行");
		bankInfoMap.put("YEEPAY-POST-NET", "中国邮政");
		bankInfoMap.put("YEEPAY-BJRCB-NET", "北京农村商业银行");
		bankInfoMap.put("YEEPAY-HXB-NET", "华夏银行");
		bankInfoMap.put("YEEPAY-CZ-NET", "浙商银行");
		
		bankInfoMap.put("ALIPAY-ALIPAY", "支付宝余额支付");
		
		bankInfoMap.put("ALIPAY-BOCB2C","中国银行");
		bankInfoMap.put("ALIPAY-ICBCB2C","中国工商银行");
		bankInfoMap.put("ALIPAY-CMB","招商银行");
		bankInfoMap.put("ALIPAY-CCB","中国建设银行");
		bankInfoMap.put("ALIPAY-ABC","中国农业银行");
		bankInfoMap.put("ALIPAY-SPDB","上海浦东发展银行");
		bankInfoMap.put("ALIPAY-CIB","兴业银行");
		bankInfoMap.put("ALIPAY-GDB","广东发展银行");
		bankInfoMap.put("ALIPAY-SDB","深圳发展银行");
		bankInfoMap.put("ALIPAY-CMBC","中国民生银行");
		bankInfoMap.put("ALIPAY-COMM","交通银行");
		bankInfoMap.put("ALIPAY-CITIC","中信银行");
		bankInfoMap.put("ALIPAY-HZCBB2C","杭州银行");
		bankInfoMap.put("ALIPAY-CEBBANK","中国光大银行");
		bankInfoMap.put("ALIPAY-SHBANK","上海银行");
		bankInfoMap.put("ALIPAY-NBBANK","宁波银行");
		bankInfoMap.put("ALIPAY-SPABANK","平安银行");
		bankInfoMap.put("ALIPAY-BJRCB","北京农村商业银行");
		bankInfoMap.put("ALIPAY-FDB","富滇银行");
		
		//银联接入
		bankInfoMap.put("UPOP-UPOP", "银联支付");
		
		bankInfoMap.put("UPOP-ICBC", "中国工商银行");//工行
		bankInfoMap.put("UPOP-ABC", "中国农业银行");//农行
		bankInfoMap.put("UPOP-BOC", "中国银行");//中行
		bankInfoMap.put("UPOP-CCB", "中国建设银行");//建行
		bankInfoMap.put("UPOP-CMB", "招商银行");//招行
		bankInfoMap.put("UPOP-SPDB", "浦东发展银行");//浦发
		bankInfoMap.put("UPOP-GDB", "广东发展银行");//广发
		bankInfoMap.put("UPOP-BOCOM", "交通银行");//交行
		bankInfoMap.put("UPOP-PSBC", "邮政储蓄银行");//邮储
		bankInfoMap.put("UPOP-CNCB", "中信银行");//中信
		bankInfoMap.put("UPOP-CMBC", "民生银行");//民生
		bankInfoMap.put("UPOP-CEB", "光大银行");//光大
		bankInfoMap.put("UPOP-HXB", "华夏银行");//华夏
		bankInfoMap.put("UPOP-CIB", "兴业银行");//兴业
		bankInfoMap.put("UPOP-BOS", "上海银行");//上海银行
		bankInfoMap.put("UPOP-SRCB", "上海农商行");//上海农商
		
		
		bankInfoMap.put("ALIPAY_SECURE-ALIPAY_SECURE", "支付宝安全支付"); //支付宝安全支付
		bankInfoMap.put("ALIPAY_WAP-ALIPAY_WAP", "支付宝WAP支付"); //支付宝WAP支付
		
	}

	/**
	 * 加载支付服务商Map信息
	 */
	private static void initPayMapInfo() {
		payInfoMap.put("YEEPAY", "易宝支付");
		payInfoMap.put("ALIPAY", "支付宝");
		payInfoMap.put("UPOP", "银联支付");
		payInfoMap.put("ALIPAY_SECURE", "支付宝安全支付");
		payInfoMap.put("ALIPAY_WAP", "支付宝WAP支付");

	}

}
