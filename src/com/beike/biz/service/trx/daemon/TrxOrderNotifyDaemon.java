package com.beike.biz.service.trx.daemon;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.core.service.trx.notify.NotifyRecordBizService;
import com.beike.core.service.trx.notify.TrxorderNotifyRecordService;
import com.beike.dao.notify.TrxorderNotifyRecordDao;
import com.beike.entity.notify.TrxorderNotifyRecord;
import com.beike.util.TrxConstant;

/**
 * @Title: TrxOrderNotifyDaemon.java
 * @Package com.beike.biz.service.trx
 * @Description: 订单过期提醒，数据准备及短信通知定时入口
 * @date 2012.03.15
 * @author jianjun.huo
 * @version v1.0
 */
@Service("trxOrderNotifyDaemon")
public class TrxOrderNotifyDaemon
{

	private final Log logger = LogFactory.getLog(TrxOrderNotifyDaemon.class);

	@Autowired
	private TrxorderNotifyRecordService trxorderNotifyRecordService;
	@Autowired
	private NotifyRecordBizService notifyRecordBizService;
	@Autowired
	private TrxorderNotifyRecordDao trxorderNotifyRecordDao;


	/**
	 * 执行订单提醒数据准备
	 */
	public void executeTrxOrderNotifyPrepare()
	{

		logger.info("++++++++executeTrxOrderNotifyPrepare start+++++++++++");
		trxorderNotifyRecordService.noTscProcessAllLoseTrxOrder();

		logger.info("++++++++executeTrxOrderNotifyPrepare end+++++++++++");

	}
	
	/**
	 * 执行短信数据发送
	 */
	public void  executeDataNotify(){
		logger.info("++++++++TrxOrderNotifyDaemon+++executeDataNotify start+++++++++++");
		int notifyCount = trxorderNotifyRecordDao.findByIsNotifyCount(false);
		
		int leng = 0;
		int daemonLength = TrxConstant.DAENON_LENGTH;
		if(notifyCount>0){
			leng = (notifyCount+daemonLength)/daemonLength;
		
		}else{
			return;
		}
		for(int i=0;i<leng;i++){
			int start = i*daemonLength;
		List<TrxorderNotifyRecord>  trxorderNotifyRecord = trxorderNotifyRecordDao.findByIsNotify(false,start,daemonLength);
		if(trxorderNotifyRecord!=null&&trxorderNotifyRecord.size()>0){
		for(TrxorderNotifyRecord tnr:trxorderNotifyRecord){
			notifyRecordBizService.processNotifySms(tnr);
			}
		}
		logger.info("++++++++executeDataNotify end+++++trxorderNotifyRecord.size()="+trxorderNotifyRecord.size()+"++++++");
		}
	
	}
}
