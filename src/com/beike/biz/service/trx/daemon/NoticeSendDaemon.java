package com.beike.biz.service.trx.daemon;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.notice.Notice;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.core.service.trx.notice.NoticeSend;
import com.beike.core.service.trx.notice.NoticeService;
import com.beike.core.service.trx.notice.PartnerNoticeFactory;
import com.beike.util.thread.ThreadPool;

/**   
 * 
 * @title: NoticeSendDaemon.java
 * @package com.beike.biz.service.trx.daemon
 * @description: 通知重发定时
 * @author wangweijie  
 * @date 2012-6-13 下午02:16:20
 * @version v1.0   
 */
@Service("noticeSendDaemon")
public class NoticeSendDaemon {
	private static final Log logger = LogFactory.getLog(NoticeSendDaemon.class);
	private static Random random = new Random();
	public final static int DELAY_RESEND_MAX_TIMES = 3;  			//随机最大发送次数
	private static final int TIMING_FREQUENCY = 50;				//定时频率10秒每次
	public static final int DELAY_SECOND_LIMIT = 1*24*60*60 ;	//延迟秒数限制
	
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private PartnerNoticeFactory partnerNoticeFactory;
	private static ScheduledThreadPoolExecutor threadPool;

	/**
	 * 自动执行通知发送
	 *    
	 * @return void
	 * @throws
	 */
	public void excuteNoticeSend(){
		if(null == threadPool){
			threadPool = new ScheduledThreadPoolExecutor(100);
		}
		logger.info("+++++++++++++++noticeSendDaemon start+++++++++++++++");
		noticeRealTimeSend();
		noticeDelaySend();
	}
	
	/**
	 * 通知 实时发送 
	 */
	public void noticeRealTimeSend(){

		/*
		 * 查找数据库中状态为新建INIT状态的所有数据
		 */
		List<Notice> noticeList = noticeService.findNoticeListByStatus(NoticeStatus.INIT);
		logger.info("++++++++++++++++noticeRealTimeSend++noticeList.size="+noticeList.size()+"++++++++++++");
		if(null == noticeList) return;
		for(Notice notice : noticeList){
			if(null != notice.getId()){
				new NoticeSend(notice,partnerNoticeFactory,threadPool).send();
			}
		}
	}
	
	
	/**
	 * 通知延迟发送 （24小时）
	 */
	public void noticeDelaySend(){
		//查找数据库中状态为新建RANDOMINIT状态的所有数据
		List<Notice> noticeList = noticeService.findNoticeListByStatus(NoticeStatus.RANDOMINIT);
		logger.info("++++++++++++++++noticeDelaySend++noticeList.size="+noticeList.size()+"++++++++++++");
		if(null == noticeList) return;
		for(Notice notice : noticeList){
			try{
				if(null != notice.getId()){
					long disSeconds = (System.currentTimeMillis()-notice.getCreateDate().getTime())/1000;
	
					//超过一天时间，则不发送
					if(disSeconds>DELAY_SECOND_LIMIT){
						notice.setStatus(NoticeStatus.FAIL);//超出一天，设置为失败
						notice.setModifyDate(new Date());
						noticeService.updateNotice(notice);
						
						String content = "<b>分销商回调[延迟发送<font color='red'>超时</font>]失败</b><br/>";
						content += "分销商编号:"+notice.getHostNo()+"<br/>";
						content += "接口方法名:"+notice.getMethodType()+"<br/>";
						content += "已回调次数:"+notice.getCount()+"<br/>";
						content += "已随机回调次数:"+notice.getRandomCount()+"<br/>";
						content += "请求ID:" + notice.getRequestId() +"<br/>";
						content += "请求内容:" + notice.getContent() + "<br/>";
						content += "返回内容:" + notice.getRspMsg();
						noticeService.sendWarningEmail(content);
					}else{
						//判断是否发送
						if(NoticeSendDaemon.randomTarget((int)disSeconds/TIMING_FREQUENCY,DELAY_SECOND_LIMIT/TIMING_FREQUENCY-DELAY_RESEND_MAX_TIMES)){
							logger.info("+++++++delay send noticeId="+notice.getId()+"+++++++");
							delaySend(notice.getId(),DELAY_RESEND_MAX_TIMES);
						}
					}
				}
			}catch (Exception e) {
				logger.error("+++++++++{EXCEPTION}noticeDelaySend++++",e);
			}
		}
	}
	
	
	
	/**
	 *延迟发送
	 * @param noticeId
	 * @param partnerNoticeFactory 延迟发送最大次数
	 * @param timeFrequency
	 */
	public void delaySend(Long noticeId,int timingFrequency){
		
		NoticeService noticeService = partnerNoticeFactory.getNoticeService();
		Notice notice = noticeService.preQryInWtDBFindById(noticeId);
		if(null==notice || null==notice.getId()){
			return;
		}
		
		//重复发送
		//判断该通知是在进行时，则不进行任何操作
		if(!(NoticeStatus.RANDOMINIT.compareTo(notice.getStatus())==0) || notice.getRandomCount()>=timingFrequency){
			logger.info("++++++++++++++++++notice[id="+notice.getId()+"] is finished....++++++++++++++ ");
			return;
		}
		
		//讲该通知加入到notice中
		RandomNoticeSend randomNoticeSend = new RandomNoticeSend(partnerNoticeFactory,notice,timingFrequency);
		ThreadPool.getInstance().execute(randomNoticeSend);
	}

	
	public class RandomNoticeSend implements Runnable{
		private PartnerNoticeFactory partnerNoticeFactory;
		private Notice notice;
		private int maxTimes;
		public RandomNoticeSend(PartnerNoticeFactory partnerNoticeFactory,Notice notice,int maxTimes){
			this.partnerNoticeFactory = partnerNoticeFactory;
			this.notice = notice;
			this.maxTimes = maxTimes;
		}
		
		@Override
		public void run() {
			NoticeSend.delaySendNotice(partnerNoticeFactory, notice,maxTimes);
		}
	}
	
	/**
	 * 随机命中
	 * @param index
	 * @param max
	 * @return
	 */
	public static boolean randomTarget (int currentInterval,int maxInterval){
		int interval = maxInterval - currentInterval;
		int _rand = random.nextInt(interval<1?1:interval);
		return 0 == _rand;
	}
}
