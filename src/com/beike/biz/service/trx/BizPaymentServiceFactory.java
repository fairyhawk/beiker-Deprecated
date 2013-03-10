package com.beike.biz.service.trx;

import java.util.Map;

import com.beike.common.enums.trx.PaymentType;
import com.beike.core.service.trx.PaymentService;

/**
 * @Title: BizPaymentServiceFactory.java
 * @Package com.beike.biz.service.trx
 * @Description: PaymentServic工厂
 * @date May 17, 2011 4:22:07 PM
 * @author wh.cheng
 * @version v1.0
 */
public class BizPaymentServiceFactory {

	private Map<String, PaymentService> serviceMap;

	public PaymentService getPaymentService(PaymentType paymentType) {

		return serviceMap.get(paymentType.name());
	}

	public Map<String, PaymentService> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, PaymentService> serviceMap) {
		this.serviceMap = serviceMap;
	}

}
