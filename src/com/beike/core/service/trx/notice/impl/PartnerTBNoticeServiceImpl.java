package com.beike.core.service.trx.notice.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.enums.trx.NoticeStatus;
import com.beike.core.service.trx.notice.PartnerNoticeService;
import com.beike.core.service.trx.notice.ResSuccessCodeMap;
import com.beike.util.HttpUtils;

/**   
 * @title: PartnerNoticeServiceTBImpl.java
 * @package com.beike.core.service.trx.notice.impl
 * @description: 通知接口  淘宝实现
 * @author wangweijie  
 * @date 2012-12-4 下午05:20:18
 * @version v1.0   
 */
@Service("partnerTBNoticeService")
public class PartnerTBNoticeServiceImpl implements PartnerNoticeService {
	private static final Log logger = LogFactory.getLog(PartnerTBNoticeServiceImpl.class);
	private static final int INTERVAL[] = {2,4,8,10};	// 通知策略2,4,8,10秒
	@Override
	public long getDelaySecond(int times) {
		if(times < 0 || times >= INTERVAL.length) times = 1;
		return INTERVAL[times];
	}

	@Override
	public boolean isEnd(NoticeStatus noticeStatus,int times) {
		//淘宝失败，则停止重发
		if(noticeStatus.compareTo(NoticeStatus.FAIL)==0){
			return true;
		}
		return times>INTERVAL.length-1;
	}

	@Override
	public int isSuccess(String resMsg,String methodType) {
		//订单正在处理中，请稍后再试
		if(resMsg.indexOf("\"sub_code\":\"isv.eticket-service-unavailable:order-is-processing\"")!=-1 ) return 1;  //
		// 该订单已经发码，请不要重复调用
		if(resMsg.indexOf("\"sub_code\":\"isv.eticket-send-error:code-alreay-send\"")!=-1 ) return 0; //
		return resMsg.indexOf(ResSuccessCodeMap.getSuccessCode(methodType)) != -1?0:-1;
	}

	@Override
	public String send(String content) throws Exception {
		/*
		 * 获得发送地址和内容
		 * 数据库content存放格式为url?param=xxx&content=xxx
		 * 以?分割，下标0为url；下标1为content内容
		 */
		String [] urlContent = content.split("\\?",2);
		String url = urlContent[0];  //
		String requestMsg = urlContent[1];//
		logger.info("+++++++++=send req to taobao++++url="+url);
		return HttpUtils.URLPost(url,requestMsg);
	}


	@Override
	public int getMaxReSendNum() {
		return INTERVAL.length;
	}
	
	//淘宝不需要随机
	@Override
	public boolean needRandomSend() {
		return true;
	}
}
