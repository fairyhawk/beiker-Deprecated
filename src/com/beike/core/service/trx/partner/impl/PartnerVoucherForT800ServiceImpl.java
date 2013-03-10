package com.beike.core.service.trx.partner.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.ParT800OrderGenerator;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.core.service.trx.partner.PartnerVoucherService;
import com.beike.util.Amount;
import com.beike.util.Configuration;
import com.beike.util.DateUtils;
import com.beike.util.PartnerUtil;
import com.beike.util.StringUtils;
import com.beike.util.img.JsonUtil;

/**
 * Title : PartnerVoucherForT800ServiceImpl.java <br/>
 * Description : 合作分销商API 验券 service for Tuan800<br/>
 * Company : Sinobo <br/>
 * Copyright : Copyright (c) 2010-2012 All rights reserved.<br/>
 * Created : 2012-11-9 下午4:05:31 <br/>
 * 
 * @author Wenzhong Gu
 * @version 1.0
 */
@Service("partnerVoucherForT800Service")
public class PartnerVoucherForT800ServiceImpl implements PartnerVoucherService {

	private final Log logger = LogFactory.getLog(PartnerVoucherForT800ServiceImpl.class);
	private static final String publicKey =Configuration.getInstance().getValue("tuan800_publicKey");// "859de8d7112c4bc7b6c768760102ca62";

	/**
	 * 远程查询分销商voucher info
	 * 
	 * @param voucherInfo
	 * @param userId
	 * @return
	 */
	@Override
	public Map<String, String> qryVoucherInfoToPar(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		logger.info("++++++++++qryVoucherInfoToPar++start++++++++++++++++++++");

		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();

		dataMap.put("tuan_order_no", voucherInfo.getTrxorder().getOutRequestId());
		dataMap.put("site_order_no", voucherInfo.getTrxorderGoods().getTrxGoodsSn());
		dataMap.put("deal_id", voucherInfo.getTrxorderGoods().getGoodsId());
		dataMap.put("price", Amount.mulByInt(voucherInfo.getTrxorderGoods().getPayPrice(), 100));
		dataMap.put("total_price", Amount.mulByInt(voucherInfo.getTrxorderGoods().getPayPrice(), 100));
		dataMap.put("count", "1");
		dataMap.put("coupon_code", voucherInfo.getVoucher().getVoucherCode());
		dataMap.put("send_flag", "0");
		dataMap.put("last_update_time", DateUtils.toString(voucherInfo.getTrxorderGoods().getLastUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
		dataMap.put("status", "4");
		dataMap.put("cancel_flag", "");

		// 核销标识
		Map<String, String> resultMap = new HashMap<String, String>();

		try {
			String params = JsonUtil.getJsonStringFromMap(dataMap);
			params = params.replace("\\", "");
			params = params.replace("\"[", "[\"");
			params = params.replace("]\"", "\"]");
			String sign = checkHmacData(params, publicKey, partnerInfo.getKeyValue());
			if (sign != null) {
				jsonMap.put("appKey", publicKey);
				jsonMap.put("params", params);
				jsonMap.put("sign", sign);

				Map<String, Object> respMap = ParT800OrderGenerator.getResponseParamsForT800(jsonMap, partnerInfo);

				if (respMap != null && !respMap.isEmpty()) {
					// 解析出字段，然后根据字段返回值确定是否可以核销，具体字段未确定
					// 团800返回券状态为已验证才处理
					if (!"4".equals(respMap.get("status"))) {
						resultMap.put("status", "ANTI_VALIDATE");
					} else {
						// 可以核销
						resultMap.put("status", "ALLOW_VALIDATE");
					}
				} else {
					resultMap.put("status", "ANTI_VALIDATE");
				}
			} else {
				resultMap.put("status", "ANTI_VALIDATE");
			}
		} catch (Exception e) {
			// 不可以核销
			resultMap.put("status", "ANTI_VALIDATE");
			e.printStackTrace();
		}

		return resultMap;
	}

	/**
	 * 推送分销商voucher info
	 * 
	 * @param voucherInfo
	 * @param partnerinfo
	 * @return
	 */
	@Override
	public String pushVoucherInfo(VoucherInfo voucherInfo, PartnerInfo partnerInfo) {
		return null;
	}

	private String checkHmacData(String params, String publicKey, String privateKey) {

		if (StringUtils.isEmpty(params) || StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {
			logger.info("+++++++++++++++++++数据不完整++++++++++");
			return null;
		}
		String desryptStr = PartnerUtil.md5SignatureSecret(params, publicKey, privateKey);
		logger.info("+++++++++tuan800++++++++++++md5 sign +++++++++++++" + desryptStr + "++++++++++++");
		if (!StringUtils.isEmpty(desryptStr)) {
			return desryptStr;
		} else {
			logger.info("+++++++++++++++++++sign++{ERROR}++++++++");
			return null;
		}
	}
}