package com.beike.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.beike.common.enums.trx.TrxStatusInMC;

/**
 * @Title: TrxConstant.java
 * @Package com.beike.util
 * @Description: 交易相关常量
 * @date Jun 13, 2011 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public class TrxConstant {
	/*
	 * 账户安全手机校验码在缓存服务器上的KEY
	 */
	public static final String TRX_RANDOMNUMBER_NEW="TRX_RANDOMNUMBER_NEW_";
	public static final int CHECK_PHONE_CODE_TIMEOUT=30*60;
	public static final String TRX_BEFORE_CHECK_PHONE_CODE = "TRX_BEFORE_CHECK_PHONE_CODE";
	/**
	 * 手机验证码开关默认为true
	 * 当手机短信服务器不能用时可以把值修改成false
	 */
	public static final boolean CHECK_CODE_FLAG = true;
	/**
	 * 支付时手机验证码金额阀值
	 */
	public static final double SMS_PWD_AMOUNT = 1.0;
	
	public static final String TRX_LOGIN_TYPE = "TRX_LOGIN_TYPE_";

	public static final String TRX_LOGIN_TYPE_NEW_REG = "TRX_LOGIN_TYPE_NEW_REG";
	public static final String TRX_LOGIN_TYPE_MOBILE_VALIDATE = "TRX_LOGIN_TYPE_MOBILE_VALIDATE";
	public static final String TRX_NORMAL = "NORMAL"; // 常规交易类型

	static PropertyUtil propertyUtil = PropertyUtil.getInstance("project");

	// 凭证预取报警手机号
	public static String alterVouPrefetchTel = PropertyUtil.getInstance("project").getProperty("alter_vou_prefetch_tel");

	// 凭证预取报警短信模板
	public static String VOUCHER_PREFETCH_ALTER_SMS_TEMPLATE = "VOUCHER_PREFETCH_ALTER_SMS_TEMPLATE";
	

	// 商品订单号预取报警短信模板
	public static String TGSN_PREFETCH_ALTER_SMS_TEMPLATE = "TGSN_PREFETCH_ALTER_SMS_TEMPLATE";
	
	
	
	
	/**
	 * 短信退款模板，退款到账户模板
	 */
	public static final String OVERRUN_AUTO_REFUD_SMS_TEMPLATE = "SMS_REFUND_LIMIT_TEMPLATE";

	/**
	 * 短信退款模板，出现异常，只发送超限信息模板
	 */
	public  static final String OVERRUN_AUTO_REFUD_ERROR_SMS_TEMPLATE = "SMS_COUNT_LIMIT_TEMPLATE";
	
	/**
	 * 网票网短信退款模板，退款到账户模板
	 */
	public static final String FILM_REFUD_SMS_TEMPLATE = "SMS_REFUND_FILM_TEMPLATE";

	/**
	 * 网票网短信退款模板，出现异常，只发送超限信息模板
	 */
	public  static final String FILM_REFUD_ERROR_SMS_TEMPLATE = "SMS_ERROR_FILM_TEMPLATE";
	
	/**
	 * 手机客户端 图片地址前缀
	 */
	public static final String UPLOAD_IMAGES_PATH = "/jsp/uploadimages/";
	/**
	 * 个人限购memcacheKey
	 */
	public static final String SINGLE_COUNT_KEY = "SINGLE_COUNT_GOODSID_";
	/**
	 * 个人限购memcache过期时间
	 */
	public static final int singleExpTimeout = 30 * 60;
	/**
	 * 总量限购memcacheKey
	 */
	public static final String GOODS_MAXCOUNT_KEY = "GOODS_MAXCOUNT_GOODSID_";
	/**
	 * 总量限购memcache过期时间
	 */
	public static final int MaxCountExpTimeout = 5 * 60;

	/**
	 * 支付前 payLimitDec memcacheKey
	 */
	public static final String PAY_LIMIT_DES_PRO_PAY_CACHE_KEY = "PAY_LIMIT_DES_PRO_PAY_";

	/**
	 * 支付前 payLimitDec过期时间
	 */
	public static final int PAY_LIMIT_DES_PRO_PAY_CACHE_TIMEOUT = 5 * 60;

	/**
	 * 支付后 payLimitDec memcacheKey
	 */
	public static final String PAY_LIMIT_DES_POST_PAY_CACHE_KEY = "PAY_LIMIT_DES_POST_PAY_";

	/**
	 * 支付后 payLimitDec过期时间
	 */
	public static final int PAY_LIMIT_DES_POST_PAY_CACHE_TIMEOUT = 60 * 60;

	/**
	 * goodsTitle memcacheKey
	 */
	public static final String GOODS_TITLE_CACHE_KEY = "GOODS_TITLE_GOODSID_";
	/**
	 * goodsTitle memcacheKey
	 */
	public static final String VM_ACCOUNT_ID_KEY = "VM_ACCOUNT_ID_";
	/**
	 * goodsTitleAndisScheduled memcacheKey
	 */
	public static final String GOODS_TITLE_CACHE_AND_SCHEDULED_KEY = "GOODS_TITLE_GOODSID_SCHEDULED";
	
	/**
	 * goodsTitleAndisScheduled memcacheKey
	 */
	public static final String GOODS_TAG_ID_NAME_KEY = "GOODS_TAG_ID_NAME_KEY";
	/**
	 * goodsLOGO memcacheKey
	 */
	public static final String GOODS_LOGO_CACHE_KEY = "GOODS_LOGO_GOODSID_";
	
	/**
	 * 有效partner的缓存key,通过分销商编号
	 */
	public static final String PARTNER_AVA_BY_PARTNERNO_CACHE_KEY = "PARTNER_AVA_PARTNERNO_";
	
	/**
	 * 有效partner的过期时间,通过分销商编号
	 */
	public static final int PARTNER_AVA_BY_PARTNERNO_CACHE_TIMEOUT= 10 * 60;
	
	
	/**
	 * 所有的partner的缓存key
	 */
	public static final String ALL_PARTNER_CACHE_KEY = "ALL_PARTNER_KEY";
	
	/**
	 * 所有的partner的过期时间
	 */
	public static final int ALL_PARTNER_CACHE_TIMEOUT= 10 * 60;
	
	
	
	/**
	 * 分销商编号下所有 partner的缓存key
	 */
	public static final String PARTNER_ALL_CACHE_KEY = "PARTNER_ALL_PARTNERNO_";
	
	/**
	 * 分销商编号下所有partner的过期时间
	 */
	public static final int PARTNER_ALL_CACHE_TIMEOUT= 10 * 60;
	
	/**
	 * 分销商淘宝的token
	 */
	public  final static String 	PARTNER_TOKEN_TAOBAO = "";

	/**
	 * goodsTitle过期时间
	 */
	public static final int GOODS_TITLE_CACHE_TIMEOUT = 60 * 60;
	
	/**
	 * vmAccount过期时间
	 */
	public static final int VM_ACCOUNT_TIMEOUT = 10 * 60;
	/**
	 * 我的钱包memcache过期时间
	 */
	public static final int myPurseTimeout = 30 * 60;

	/**
	 * 凭证生成总量阀值
	 */

	public static final int VOUCHER_AUTO_CREATE_TOTAL_THRES = 800000;
	

	/**
	 * 凭证生成单日量阀值
	 */

	public static final int VOUCHER_AUTO_CREATE_DAY_THRES = 100000;
	

	/**
	 * 购买超限报警邮件模板
	 */
	// public static final String VOUCHER_OVER_EMIAL_ALTER_TEM ="VOUCHER_OVER_EMIAL_ALTER_TEM";

	/**
	 * 购买超限报警邮件memcache过期时间
	 */
	// ublic static final int voucherOverAlterExpTimeout = 12 * 60 * 60;

	/**
	 * 全站返现暂时暂停，在此时间点购买成功的订单，还可继续返现。
	 */
	public static final Date rebateEndDate = DateUtils.toDate(
			"2012-02-08 12:00:00", "yyyy-MM-dd HH:mm:ss");
	/**
	 * 凭证短信商品名字简称字数
	 */
	public static final int smsVouGoodsNameCount = 11;

	public static List<?> voucherPrefetchVouList = null;
	/**
	 * 商品订单号预取List
	 */
	public static List<?> trxGoodsSnPrefetchTgSnList = null;

	public static final int voucherPrefetchCount = 500;
	
	/**
	 * 商品订单号预取数量
	 */
	public static final int trxGoodsSnPrefetchCount = 500;

	/**
	 * 千品注册指定概率抽奖: 奖品和概率
	 */
	public static Map<String, String> lotteryRegMap = new LinkedHashMap<String, String>();

	/**
	 * 千品注册指定概率抽奖: 奖品总数
	 */
	public static final int LOTTERYREG_TOTAL = 554;
	/**
	 * 千品注册指定概率抽奖: 指定中奖名单显示的个数
	 */
	public static final int LOTTERYREGLIST_NUM = 3;

	/**
	 * 千品注册指定概率抽奖:虚拟款项id
	 */
	public static final String LOTTERY_REG_VM_ID = "10";
	/**
     * 查询购买数量缓存超时时间
     */
    public static final int TRXCOUNTFORGUESTSALESTIMEOUT = 30 * 60;
	
	/**
	 * 交易定时相关
	 */
	public static final int DAENON_LENGTH = 50000;

	public static List<Map<String, String>> payWayChannelList = new ArrayList<Map<String, String>>();// 网关展示--初始化List
	/*
	 * 非网关展示（对ALIPAY_WAP-*和ALIPAY_SECURE-ALIPAY_SECURE）
	 */
	public static List<Map<String, String>> unPayWayChannelList = new ArrayList<Map<String, String>>();// 非网关展示--初始化List

	public static List<List<String>> payWayResultChannelList = new ArrayList<List<String>>();// 网站展示且开通的支付通道List
	public static List<List<String>> validResultChannelList = new ArrayList<List<String>>();// 开通的支付通道List


	public static Map<String, String> payWayAlipayMap = new LinkedHashMap<String, String>();// 支付网关支付宝独占行
	public static Map<String, String> payWayUpopMap = new LinkedHashMap<String, String>();// 银联银行和支付宝网关共占一行
	public static Map<String, String> payWayBankMap = new LinkedHashMap<String, String>();// 支付网关银行平铺
	
	public static Map<String, String> alipaySecureMap = new LinkedHashMap<String, String>();// 支付宝安全支付（不在支付通道List展现）
	public static Map<String, String> alipayWapMap = new LinkedHashMap<String, String>();  //支付宝WAP支付（不在支付通道List展现）
	static {
		
		alipaySecureMap.put("ALIPAY_SECURE-ALIPAY_SECURE", "1"); //支付宝安全支付
		alipayWapMap.put("ALIPAY_WAP-*", "1"); //支付宝WAP支付
		
		payWayAlipayMap.put("ALIPAY-ALIPAY", "1");// 支付宝余额支付

		payWayUpopMap.put("UPOP-UPOP", "1");// 银联网关
		
		
		
		// 易宝银行直连
		payWayBankMap.put("YEEPAY-ICBC-NET", "0");// 工商银行
		payWayBankMap.put("YEEPAY-CMBCHINA-NET", "0");// 招商银行
		payWayBankMap.put("YEEPAY-ABC-NET", "0");// 中国农业银行
		payWayBankMap.put("YEEPAY-CCB-NET", "0");// 建设银行
		payWayBankMap.put("YEEPAY-BCCB-NET", "0");// 北京银行
		payWayBankMap.put("YEEPAY-BOCO-NET", "0");// 交通银行
		payWayBankMap.put("YEEPAY-CIB-NET", "0");// 兴业银行
		payWayBankMap.put("YEEPAY-NJCB-NET", "0");// 南京银行
		payWayBankMap.put("YEEPAY-CMBC-NET", "0");// 中国民生银行
		payWayBankMap.put("YEEPAY-CEB-NET", "0");// 光大银行
		payWayBankMap.put("YEEPAY-BOC-NET", "0");// 中国银行
		payWayBankMap.put("YEEPAY-PINGANBANK-NET", "0");// 平安银行
		payWayBankMap.put("YEEPAY-CBHB-NET", "0");// 渤海银行
		payWayBankMap.put("YEEPAY-HKBEA-NET", "0");// 东亚银行
		payWayBankMap.put("YEEPAY-NBCB-NET", "0");// 宁波银行
		payWayBankMap.put("YEEPAY-ECITIC-NET", "0");// 中信银行
		payWayBankMap.put("YEEPAY-GDB-NET", "0");// 广发银行
		payWayBankMap.put("YEEPAY-SHB-NET", "0");// 上海银行
		payWayBankMap.put("YEEPAY-SPDB-NET", "0");// 上海浦东发展银行
		payWayBankMap.put("YEEPAY-POST-NET", "0");// 中国邮政
		payWayBankMap.put("YEEPAY-BJRCB-NET", "0");// 北京农村商业银行
		payWayBankMap.put("YEEPAY-HXB-NET", "0");// 华夏银行 易宝关闭了此银行,此银行不测
		payWayBankMap.put("YEEPAY-CZ-NET", "0");// 浙商银行

		// 支付宝银行直连
		payWayBankMap.put("ALIPAY-CMB", "1");// 招商银行
		payWayBankMap.put("ALIPAY-CEBBANK", "1");// 中国光大银行
		payWayBankMap.put("ALIPAY-CCB", "1");// 中国建设银行
		payWayBankMap.put("ALIPAY-SPDB", "1");// 上海浦东发展银行
		
		payWayBankMap.put("ALIPAY-ICBCB2C", "1");// 中国工商银行
		payWayBankMap.put("ALIPAY-COMM", "1");// 交通银行
		payWayBankMap.put("ALIPAY-CIB", "1");// 兴业银行
		payWayBankMap.put("ALIPAY-BOCB2C", "1");// 中国银行
		
		payWayBankMap.put("ALIPAY-SPABANK", "1");// 平安银行
		payWayBankMap.put("ALIPAY-SHBANK", "1");// 上海银行
		payWayBankMap.put("ALIPAY-NBBANK", "1");// 宁波银行
		payWayBankMap.put("ALIPAY-GDB", "1");// 广东发展银行
		
		payWayBankMap.put("ALIPAY-ABC", "1");// 中国农业银行
		payWayBankMap.put("ALIPAY-BJRCB", "1");// 北京农村商业银行
		payWayBankMap.put("ALIPAY-CITIC", "1");// 中信银行
		
		payWayBankMap.put("ALIPAY-CMBC", "1");// 中国民生银行
		payWayBankMap.put("ALIPAY-HZCBB2C", "1");// 杭州银行
		payWayBankMap.put("ALIPAY-FDB", "1");// 富滇银行
		
		//银联银行直连
		payWayBankMap.put("UPOP-ICBC", "0");//工行
		payWayBankMap.put("UPOP-ABC", "0");//农行
		payWayBankMap.put("UPOP-BOC", "0");//中行
		payWayBankMap.put("UPOP-CCB", "0");//建行
		payWayBankMap.put("UPOP-CMB", "0");//招行
		payWayBankMap.put("UPOP-SPDB", "0");//浦发
		payWayBankMap.put("UPOP-GDB", "0");//广发
		payWayBankMap.put("UPOP-BOCOM", "0");//交行
		payWayBankMap.put("UPOP-PSBC", "0");//邮储
		payWayBankMap.put("UPOP-CNCB", "0");//中信
		payWayBankMap.put("UPOP-CMBC", "0");//民生
		payWayBankMap.put("UPOP-CEB", "0");//光大
		payWayBankMap.put("UPOP-HXB", "0");//华夏
		payWayBankMap.put("UPOP-CIB", "0");//兴业
		payWayBankMap.put("UPOP-BOS", "0");//上海银行
		payWayBankMap.put("UPOP-SRCB", "0");//上海农商

		// payWayChannelList填充
		payWayChannelList.add(payWayAlipayMap);// --index:0
		payWayChannelList.add(payWayBankMap);// --index:1
		payWayChannelList.add(payWayUpopMap);// --index:2
		
		
		//支付宝wap和安全支付，支付网关列表里，不进行展示
		//此通道不需要在web的支付网关展示
		unPayWayChannelList.add(alipaySecureMap);//安全支付
		unPayWayChannelList.add(alipayWapMap);	//支付宝WAP支付
	

		// 千品注册指定概率抽奖
		lotteryRegMap.put("1", "1-90");
		// lotteryRegMap.put("100", "1-5");
		// lotteryRegMap.put("50", "6-15");
		// lotteryRegMap.put("20", "16-30");
		// lotteryRegMap.put("10", "31-60");
		// lotteryRegMap.put("3", "61-100");

	}
	
	
	
	/**
	 * 网关展示且有效的支付通道
	 *
	 * @return
	 */
	public synchronized static List<List<String>> getPayWayResultChannelList() {
		
		if (payWayResultChannelList.size() > 0) {
			return payWayResultChannelList; 
		}
		
		for(Map<String,String> payWayMap : payWayChannelList){
			
			if(null == payWayMap || payWayMap.size() == 0){
				continue;
			}
			
			List<String> list = new ArrayList<String>();
			for(Entry<String,String> entry : payWayMap.entrySet()){
				if ("1".equals(entry.getValue())) {
					list.add(entry.getKey()); 	// 小类
				}
			}
			
			if(list.size()>0){
				payWayResultChannelList.add(list);
			}
		}
		
		return payWayResultChannelList;
	}
	

	/**
	 * 获得所有有效的支付通道。
	 * @return
	 */
	private synchronized static List<List<String>> getValidResultChannelList() {

		if (validResultChannelList.size() > 0) {
			return validResultChannelList; 
		}
		
		validResultChannelList.addAll(getPayWayResultChannelList());
		
		for(Map<String,String> payWayMap : unPayWayChannelList){
			
			if(null == payWayMap || payWayMap.size() == 0){
				continue;
			}
			
			List<String> list = new ArrayList<String>();
			for(Entry<String,String> entry : payWayMap.entrySet()){
				if ("1".equals(entry.getValue())) {
					list.add(entry.getKey()); 	// 小类
				}
			}
			
			if(list.size()>0){
				validResultChannelList.add(list);
			}
		}
		return validResultChannelList;
	}

	/**
	 * 检查网关通道是否允许支付
	 * 
	 * @param channelInfo
	 * @return
	 */
	public static Map<String, String> checkChannelMap(String providerType,String providerChannel) {
		providerType = StringUtils.toTrim(providerType);
		providerChannel = StringUtils.toTrim(providerChannel);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		List<List<String>> channelList = getValidResultChannelList();
		
		StringBuilder channelSb = new StringBuilder();
		channelSb.append(providerType);
		channelSb.append("-");
		channelSb.append(providerChannel);
		
		if (channelSb.length() > 0) {// 防止初始化通道的空值

			for (List<String> itemList : channelList) {
				
				if (itemList.contains(channelSb.toString())) {
					resultMap.put("providerType", providerType);
					resultMap.put("providerChannel", providerChannel);
				}else if(itemList.contains(providerType+"-*")){
					resultMap.put("providerType", providerType);
					resultMap.put("providerChannel", providerChannel);
				}

			}
		}

		return resultMap;

	}


	/**
	 * 转化为线程安全的list
	 * 
	 * @param list
	 */
	public static void conVerSynchronizedList(List<?> list) {
		List<?> voucherPrefetchVouSouList = Collections.synchronizedList(list);

		voucherPrefetchVouList = voucherPrefetchVouSouList;

	}
	
	/**
	 * 转化为线程安全的list
	 * 
	 * @param list
	 */
	public static void conTrxGoodsSnSynchronizedList(List<?> list) {
		List<?> trxGoodsSnPrefetchSouList = Collections.synchronizedList(list);

		trxGoodsSnPrefetchTgSnList = trxGoodsSnPrefetchSouList;

	}

	/**
	 * 校验平台产品编号
	 * 
	 * @param goodsId
	 * @return
	 */
	public static boolean checkGoodsId(Long guestId, Long goodsId,
			String product) {
		boolean boo = false;
		String products = product == null ? "" : product;
		String productArray[] = products.split(";");
		for (int i = 0; i < productArray.length; i++) {
			String str = productArray[i];
			String strArray[] = str.split(":")[0].split(",");
			for (int j = 0; j < strArray.length; j++) {
				if (goodsId.toString().equals(strArray[j])) {
					boo = true;
					break;
				}
			}
		}
		return boo;

	}

	/**
	 * 手机客户端交易状态校验
	 */
	public static void checkTrxStatus(String trxStatus) {
		String arrayStr[] = null;
		if (trxStatus.contains("|")) {
			arrayStr = trxStatus.split("\\|");
		} else {
			arrayStr = new String[1];
			arrayStr[0] = trxStatus.trim();
		}
		for (int i = 0; i < arrayStr.length; i++) {
			Enum.valueOf(TrxStatusInMC.class, arrayStr[i].trim().toUpperCase());
		}
	}

	/**
	 * 手机客户端交易状态校验转移
	 * 
	 * @param status
	 * @return
	 */
	public static String getSqlStatus(String status) {

		String str1 = "tg.trx_status='INIT'";
		String str2 = "tg.trx_status='SUCCESS'";
		String str3 = "tg.trx_status='REFUNDACCEPT'";
		String str4 = "tg.trx_status='REFUNDTOACT'";
		String str5 = "tg.trx_status='RECHECK'";
		String str6 = "tg.trx_status='REFUNDTOBANK'";
		String str7 = "tg.trx_status='EXPIRED'";
		String str8 = "(tg.trx_status='USED' and tg.is_send_mer_vou=0)";
		String str9 = "(tg.trx_status='USED' and tg.is_send_mer_vou in (1,2))";
		String str10 = "(tg.trx_status='COMMENTED' and tg.is_send_mer_vou=0)";
		String str11 = "(tg.trx_status='COMMENTED' and tg.is_send_mer_vou in (1,2))";
		String sqlStatus = "";
		String strStatus = status;
		if (strStatus.contains(EnumUtil.transEnumToString(TrxStatusInMC.ALL))) {
			sqlStatus = "";
		} else {
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.INIT))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.INIT), str1);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.SUCCESS))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.SUCCESS), str2);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.REFUNDACCEPT))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.REFUNDACCEPT), str3);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.REFUNDTOACT))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.REFUNDTOACT), str4);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.RECHECK))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.RECHECK), str5);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.REFUNDTOBANK))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.REFUNDTOBANK), str6);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.EXPIRED))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.EXPIRED), str7);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.USEDINPFVOU))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.USEDINPFVOU), str8);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.USEDINMERVOU))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.USEDINMERVOU), str9);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.COMMENTEDINPFVOU))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.COMMENTEDINPFVOU),
						str10);
			}
			if (strStatus.contains(EnumUtil
					.transEnumToString(TrxStatusInMC.COMMENTEDINMERVOU))) {
				strStatus = strStatus.replace(EnumUtil
						.transEnumToString(TrxStatusInMC.COMMENTEDINMERVOU),
						str11);
			}
			if (strStatus.contains("|")) {
				strStatus = strStatus.replace("|", " or ");
			}

			sqlStatus = " and (" + strStatus + ")";
		}

		return sqlStatus;
	}
	
}
