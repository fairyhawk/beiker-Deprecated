package com.beike.core.service.trx.notice.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.partner.Par1mallOrderGenerator;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.core.service.trx.notice.PartnerNoticeService;
import com.beike.core.service.trx.notice.ResSuccessCodeMap;
import com.beike.util.HttpUtils;
import com.beike.util.json.JSONObject;

/**   
 * @title: Partner1mallNoticeServiceImpl.java
 * @package com.beike.core.service.trx.notice.impl
 * @description: 通知接口 1号店实现
 * @author wangweijie  
 * @date 2012-12-4 下午05:20:18
 * @version v1.0   
 */
@Service("partner1mallNoticeService")
public class Partner1mallNoticeServiceImpl implements PartnerNoticeService {
	private static final Log logger = LogFactory.getLog(Partner1mallNoticeServiceImpl.class);
	private static final long INTERVAL[] = {0,1,2,4,8};	// 通知策略1,2,4,8,10秒
	@Override
	public long getDelaySecond(int times) {
		if(times < 0 || times >= INTERVAL.length) times = 0;
		return INTERVAL[times];
	}

	@Override
	public boolean isEnd(NoticeStatus noticeStatus,int times) {
		return times>INTERVAL.length-1;
	}

	@Override
	public int isSuccess(String resMsg,String methodType) {
		try {
			JSONObject jsonRes = new JSONObject(resMsg).getJSONObject("response");
			if(null != jsonRes){
				Integer totalCount = (Integer) jsonRes.get("totalCount");
				Integer updateCount = (Integer) jsonRes.get("updateCount");
				Integer errorCount = (Integer) jsonRes.get("errorCount");
				totalCount = null==totalCount?-1:totalCount;
				updateCount = null==updateCount?-1:updateCount;
				errorCount = null==errorCount?-1:errorCount;
				if(updateCount+totalCount>0){
					jsonRes.put("totalCount", 1);
					resMsg = jsonRes.toString();
				}
			}
		} catch (Exception e) {
			logger.error("++++++++1mall response message format error+++",e);
			return -1;
		}
		
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
		logger.info("+++++++++=send req to 1mall++++url="+url);
		return HttpUtils.URLPost(url,requestMsg,Par1mallOrderGenerator.CHAR_ENCODING);
	}

	
	@Override
	public int getMaxReSendNum() {
		return INTERVAL.length;
	}

	@Override
	public boolean needRandomSend() {
		return true;
	}
}
