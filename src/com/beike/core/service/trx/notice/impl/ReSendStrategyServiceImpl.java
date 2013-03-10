package com.beike.core.service.trx.notice.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.partner.Par1mallOrderGenerator;
import com.beike.core.service.trx.notice.ReSendStrategyService;
import com.beike.util.StringUtils;

/**   
 * @title: ReSendStrategyServiceImpl.java
 * @package com.beike.core.service.trx.notice.impl
 * @description: 重发间隔策略
 * @author wangweijie  
 * @date 2012-6-13 下午06:23:02
 * @version v1.0   
 */
@Service("reSendStrategyService")
public class ReSendStrategyServiceImpl implements ReSendStrategyService {
	//时间间隔为0、2s、10s、15s、30s、1m、2m、10m、30m、60m、2h
	//private final static long[] INTERVAL = {0,2*1000,10*1000,15*1000,30*1000,60*1000,2*60*1000,10*60*1000,30*60*1000,60*60*1000,2*60*60*1000};
	private  static Map<String,Long[]> INTERVAL_MAP = new HashMap<String,Long[]>();
	static{
		INTERVAL_MAP.put("DEFAULT", new Long[]{2*1000L});		//默认
		INTERVAL_MAP.put(Par1mallOrderGenerator.PARTERNO_1MALL, new Long[]{2*1000L,4*1000L,8*1000L});
	}
	@Override
	public long get(String hostNo,int index) {
		hostNo = StringUtils.toTrim(hostNo);
		Long[] interval = INTERVAL_MAP.get(hostNo);
		if(null == interval){
			interval = INTERVAL_MAP.get("DEFAULT");
		}
		if(index < 0 || index > interval.length) index = 1;
		return interval[index-1];
	}


	@Override
	public boolean isEnd(String hostNo,int index) {
		hostNo = StringUtils.toTrim(hostNo);
		Long[] interval = INTERVAL_MAP.get(hostNo);
		if(null == interval){
			interval = INTERVAL_MAP.get("DEFAULT");
		}
		return index > interval.length;
	}

}
