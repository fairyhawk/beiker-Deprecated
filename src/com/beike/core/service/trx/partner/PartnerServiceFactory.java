package com.beike.core.service.trx.partner;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.common.enums.trx.PartnerApiType;
import com.beike.util.EnumUtil;

/**
 * @Title: PartnerServiceFactory.java
 * @Package  com.beike.core.service.trx.parter
 * @Description: 合作分销商API交易相关Service工厂
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerServiceFactory")
public class PartnerServiceFactory {
	
	private static Map<String,PartnerService>  serviceMap;
	
	@Resource(name="partnerFor58Service")
	private PartnerService  partnerFor58Service;
	
	@Resource(name="partnerForNomalService")
	private PartnerService  partnerForNomalService;
	@Resource(name="partnerForTBService")
	private PartnerService  partnerForTBService;
	@Resource(name="partnerFor1mallService")
	private PartnerService partnerFor1mallService;
	
	//京东
	@Resource(name="partnerFor360buyService")
	private PartnerService partnerFor360buyService;
	
	//add 团800 服务接口 by wz.gu 2012-11-07
	@Resource(name="partnerForT800Service")
	private PartnerService partnerForT800Service;
	
	public PartnerService getPartnerService(String partnerApiType) {
		if (serviceMap == null || serviceMap.isEmpty()) {
			serviceMap = new HashMap<String, PartnerService>();
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.TC58),partnerFor58Service);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.NOMAL),partnerForNomalService);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.TAOBAO),partnerForTBService);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.BUY360),partnerFor360buyService);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.TUAN800), partnerForT800Service);
			serviceMap.put(EnumUtil.transEnumToString(PartnerApiType.YHD),partnerFor1mallService);
		}
		return serviceMap.get(partnerApiType);

	}

}
