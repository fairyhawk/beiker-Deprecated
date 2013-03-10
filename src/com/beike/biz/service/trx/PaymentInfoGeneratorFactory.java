package com.beike.biz.service.trx;

import java.util.Map;

import com.beike.common.bean.trx.OrderInfo;
import com.beike.core.service.trx.PaymentInfoGeneratorService;

/**
 * @Title: PaymentInfoGeneratorFactory.java
 * @Package com.beike.biz.service.trx
 * @Description: PaymentInfoGenerator支付机构处理工厂
 * @date May 17, 2011 4:22:07 PM
 * @author wh.cheng
 * @version v1.0
 */
public class PaymentInfoGeneratorFactory {

	private Map<String, PaymentInfoGeneratorService> serviceMap;

	public PaymentInfoGeneratorService getPaymentInfoGeneratorService(
			OrderInfo orderInfo) {

		return serviceMap.get(orderInfo.getProviderType());
	}

	public Map<String, PaymentInfoGeneratorService> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(
			Map<String, PaymentInfoGeneratorService> serviceMap) {
		this.serviceMap = serviceMap;
	}

}
