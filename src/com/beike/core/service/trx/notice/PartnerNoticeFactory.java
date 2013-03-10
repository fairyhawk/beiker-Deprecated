package com.beike.core.service.trx.notice;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.util.Configuration;

/**   
 * @title: PartnerNoticeFactory.java
 * @package com.beike.core.service.trx.notice
 * @description: 
 * @author wangweijie  
 * @date 2012-12-4 下午05:13:35
 * @version v1.0   
 */
@Service("partnerNoticeFactory")
public class PartnerNoticeFactory {
	private static Map<String,PartnerNoticeService>  serviceMap;
	
	private static String TAOBAO_HOSTNO = Configuration.getInstance().getValue("TAOBAO_PARTNERNO"); //淘宝
	private static String BUY360_HOSTNO = Configuration.getInstance().getValue("BUY360_PARTNERNO"); //京东
	private static String YHD_HOSTNO = Configuration.getInstance().getValue("1MALL_PARTNERNO"); //一号店
	
	@Autowired
	private NoticeService noticeService;
	//淘宝
	@Resource(name="partnerTBNoticeService")
	private PartnerNoticeService  partnerTBNoticeService;
	//京东
	@Resource(name="partner360buyNoticeService")
	private PartnerNoticeService partner360buyNoticeService;
	//1号店
	@Resource(name="partner1mallNoticeService")
	private PartnerNoticeService partner1mallNoticeService;
	
	public PartnerNoticeService getPartnerNoticeService(String hostNo) {
		if (serviceMap == null || serviceMap.isEmpty()) {
			serviceMap = new HashMap<String, PartnerNoticeService>();
			
			serviceMap.put(TAOBAO_HOSTNO,partnerTBNoticeService);  //淘宝
			serviceMap.put(BUY360_HOSTNO, partner360buyNoticeService);  //京东
			serviceMap.put(YHD_HOSTNO, partner1mallNoticeService);   //16218 1号店
		}
		return serviceMap.get(hostNo);
	}
	
	/**
	 * 获得noticeService
	 * @return
	 */
	public NoticeService getNoticeService(){
		return noticeService;
	}
}
