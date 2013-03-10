package com.beike.core.service.trx.notify.impl;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beike.core.service.trx.notify.TrxorderNotifyProcessService;
import com.beike.dao.notify.TrxorderNotifyRecordDao;
import com.beike.dao.trx.soa.proxy.UserSoaDao;
import com.beike.entity.notify.TrxorderNotifyRecord;

/**
 * 订单余额过期提醒服务实现
 * 
 * @author jianjun.huo
 * 
 */
@Repository("trxorderNotifyProcessService")
public class TrxorderNotifyProcessServiceImpl implements TrxorderNotifyProcessService
{
	private final Log logger = LogFactory.getLog(TrxorderNotifyProcessServiceImpl.class);

	@Autowired
	private UserSoaDao userSoaDao;
	@Autowired
	private TrxorderNotifyRecordDao trxorderNotifyRecordDao;
	

	@Override
	public void processTrxorderNotify(TrxorderNotifyRecord trxorderNotifyRecord)
	{	
		try{
			Long userId = trxorderNotifyRecord.getUserId();
			Map<String, Object> userMap = userSoaDao.findMobileById(userId);
			String mobile = "";// 手机号
			if (userMap != null){
				mobile = (String) userMap.get("mobile");
			}

			if (mobile != null && mobile.length() > 0){
				trxorderNotifyRecordDao.addTrxorderNotifyRecord(trxorderNotifyRecord);
			}
			logger.info("++executeTrxOrderNotifyPrepare id:"+ trxorderNotifyRecord.getId()+ "+++notifyType:"+
				trxorderNotifyRecord.getNotifyType()+ "+++++userId:"+ userId+ "++ success!++");
		}catch(Exception e){
			logger.debug(e);
			e.printStackTrace();
			
		}
	}

}
