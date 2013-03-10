package com.beike.core.service.trx.partner;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.common.enums.trx.PartnerApiType;
import com.beike.util.EnumUtil;

/**
 * @Title: PartnerVoucherServiceFactory.java
 * @Package  com.beike.core.service.trx.parter
 * @Description: 合作分销商API 凭证远程查询及推送 service(spring  bean相互依赖问题，故增加此工厂）
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerVoucherServiceFactory")
public class PartnerVoucherServiceFactory {
	
	private static Map<String,PartnerVoucherService>  serviceMap;
	
	@Resource(name="partnerVoucherFor58Service")
	private PartnerVoucherService  partnerVoucherFor58Service;
	
	@Resource(name="partnerVoucherForT800Service")
	private PartnerVoucherService partnerVoucherForT800Service;
	
	@Resource(name="partnerVoucherForNomalService")
	private PartnerVoucherService  partnerVoucherForNomalService;
	@Resource(name="partnerVoucherForTBService")
	private PartnerVoucherService  partnerVoucherForTBService;
	
	@Resource(name="partnerVoucherFor360buyService")
	private PartnerVoucherService  partnerVoucherFor360buyService;
	@Resource(name="partnerVoucherFor1mallbuyService")
	private PartnerVoucherService partnerVoucherFor1mallbuyService;
	
	public PartnerVoucherService getPartnerVoucherService(String partnerApiType) {
		if (serviceMap == null || serviceMap.isEmpty()) {
			serviceMap = new HashMap<String, PartnerVoucherService>();
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.TC58),partnerVoucherFor58Service);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.NOMAL),partnerVoucherForNomalService);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.TAOBAO),partnerVoucherForTBService);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.BUY360),partnerVoucherFor360buyService);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.TUAN800), partnerVoucherForT800Service);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.YHD), partnerVoucherFor1mallbuyService);
		}
		return serviceMap.get(partnerApiType);

	}

}
