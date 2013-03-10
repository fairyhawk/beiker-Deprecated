package com.beike.core.service.trx.notice;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.common.entity.trx.notice.Notice;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.util.StringUtils;


/**   
 * @title: NoticeSend.java
 * @package com.beike.core.service.trx.notice
 * @description: 分销商通知发送
 * @author wangweijie  
 * @date 2012-12-4 下午05:53:29
 * @version v1.0   
 */
public class NoticeSend implements Runnable{
	private static final Log logger = LogFactory.getLog(NoticeSend.class);
	private Notice notice;	//通知ID
	private int times = 0;	//发送次数
	private ScheduledThreadPoolExecutor threadPool;		//java 定时线程池
	private PartnerNoticeFactory partnerNoticeFactory; //
	public NoticeSend(Notice notice,PartnerNoticeFactory partnerNoticeFactory,ScheduledThreadPoolExecutor threadPool){
		this.notice = notice;
		this.partnerNoticeFactory = partnerNoticeFactory;
		this.threadPool = threadPool;
		this.times = 0;	//首次发送
	}
	
	/**
	 * 内部构造方法
	 * @param noticeId
	 * @param times
	 * @param threadPool
	 */
	private NoticeSend(Notice notice,PartnerNoticeFactory partnerNoticeFactory,int times,ScheduledThreadPoolExecutor threadPool){
		if (times < 1) {
			this.times = 0;
		}else{
			this.times = times;
		}
		this.notice = notice;
		this.partnerNoticeFactory = partnerNoticeFactory;
		this.threadPool = threadPool;
	}
	
	
	/**
	 * 发送接口
	 */
	public void send(){
		long delaySecond = partnerNoticeFactory.getPartnerNoticeService(notice.getHostNo()).getDelaySecond(times);
		threadPool.schedule(new NoticeSend(notice,partnerNoticeFactory,times,threadPool),delaySecond,TimeUnit.SECONDS);
		logger.info("+++++++++++++noticeId="+notice.getId()+"+++add to threadPool......+++++++times="+times+"+++++++");
	}
	
	
	@Override
	public void run() {
		NoticeService noticeService = partnerNoticeFactory.getNoticeService();
		notice = noticeService.preQryInWtDBFindById(notice.getId());
		if(null==notice || null==notice.getId()) 
			return;
		//首次发送
		if(0 == times){
			//判断该通知是在进行时，则不进行任何操作
			if(!NoticeStatus.INIT.equals(notice.getStatus())){
				logger.info("++++++++++++++++++notice[id="+notice.getId()+"] is processing by another thread....++++++++++++++ ");
				return;
			}
			//更改notice 为PROCESSING 状态
			notice.setStatus(NoticeStatus.PROCESSING);
			try {
				noticeService.updateNotice(notice);
			} catch (StaleObjectStateException e) {
				logger.error("++++++++++++++++++notice[id="+notice.getId()+"] update error.",e);
				return;
			}
			notice.setVersion(notice.getVersion()+1L); //version+=1  避免再次查询数据库
		}
		
		//重复发送
		else{
			//判断该通知是在进行时，则不进行任何操作
			if(!NoticeStatus.PROCESSING.equals(notice.getStatus())){
				logger.info("++++++++++++++++++notice[id="+notice.getId()+"] is finished....++++++++++++++ ");
				return;
			}
		}
		
		logger.info("+++++++++++notice[id="+notice.getId()+",hostNo="+notice.getHostNo()+"],the " + times + " times send....+++");
		PartnerNoticeService partnerNoticeService = partnerNoticeFactory.getPartnerNoticeService(notice.getHostNo());
		
		times ++;  //发送次数+1
		
		String resMsg="";
		//发送报文
		try {
			resMsg = partnerNoticeService.send(notice.getContent());
			logger.info("+++++++++++++++notice[id="+notice.getId()+",hostNo="+notice.getHostNo()+"] return Messsage:"+resMsg);

		} catch (Exception e) {
			//发送报文 通讯异常处理
			logger.error("+++++++++{EXCEPTION_CONNECTION}++++++notice[id="+notice.getId()+",hostNo="+notice.getHostNo()+"].",e);
			String lastRspMsg = StringUtils.toTrim(notice.getRspMsg());
			resMsg = "EXCEPTION#"+lastRspMsg;
		}
		
		if (resMsg.length() > 1000) {
			resMsg = resMsg.substring(0, 1000);
		}
		
		int isSuccess = partnerNoticeService.isSuccess(resMsg,notice.getMethodType());
		//判断是否成功
		if(0 == isSuccess){
			
			notice.setStatus(NoticeStatus.SUCCESS);  
			notice.setRspMsg(resMsg);
			notice.setCount(times);
			notice.setModifyDate(new Date());
			try {
				noticeService.updateNotice(notice);
			} catch (StaleObjectStateException e) {
//				logger.error("++++++++++++++{ERROR_notice SUCCESS}++++notice[id="+notice.getId()+"] update error.",e);
				logger.error("++++++++++++++{ERROR_notice SUCCESS}++++notice[id="+notice.getId()+"] update error.");

				return;
			}	
			return ;
		}else{
			//判断是否已经发送结束 (1代表还需要继续重试,状态为PROCESSING)
			if(partnerNoticeService.isEnd(1==isSuccess?NoticeStatus.PROCESSING:NoticeStatus.FAIL,times)){
				//如果支持24小时 随机发送策略，则把状态置为RANDOMINIT 否则置为FAIL
				notice.setStatus(partnerNoticeService.needRandomSend()?NoticeStatus.RANDOMINIT:NoticeStatus.FAIL); 
				notice.setRspMsg(resMsg);
				notice.setCount(times);
				notice.setModifyDate(new Date());
				try {
					noticeService.updateNotice(notice);
				} catch (StaleObjectStateException e) {
					logger.error("++++++++++++++{ERROR_notice fail}++++notice[id="+notice.getId()+"] update error.",e);
				}
			}else{
				notice.setStatus(NoticeStatus.PROCESSING);
				notice.setRspMsg(resMsg);
				notice.setCount(times);
				notice.setModifyDate(new Date());
				try {
					noticeService.updateNotice(notice);
				} catch (StaleObjectStateException e) {
					logger.error("++++++++++++++{ERROR_notice fail}++++notice[id="+notice.getId()+"] update error.",e);
				}
				
				long delaySecond = partnerNoticeFactory.getPartnerNoticeService(notice.getHostNo()).getDelaySecond(times);
				threadPool.schedule(new NoticeSend(notice,partnerNoticeFactory,times,threadPool),delaySecond,TimeUnit.SECONDS);
				logger.info("+++++++++++++noticeId="+notice.getId()+"+++add to threadPool......+++++++times="+times+"+++++++");
			}

				
			
			//如果失败，则发送报警邮件
			if(notice.getStatus().compareTo(NoticeStatus.FAIL) == 0){
				String content = "<b>分销商回调失败</b><br/>";
				content += "分销商编号:"+notice.getHostNo()+"<br/>";
				content += "接口方法名:"+notice.getMethodType()+"<br/>";
				content += "已回调次数:"+notice.getCount()+"<br/>";
				content += "已随机回调次数:"+notice.getRandomCount()+"<br/>";
				content += "请求ID:" + notice.getRequestId() +"<br/>";
				content += "请求内容:" + notice.getContent() + "<br/>";
				content += "返回内容:" + resMsg;
				noticeService.sendWarningEmail(content);
			}
		}
	}
	
	/**
	 *	延迟发送通知
	 * @param partnerNoticeFactory
	 * @param notice
	 * @param maxTimes
	 */
	public static void delaySendNotice(PartnerNoticeFactory partnerNoticeFactory,Notice notice,int maxTimes){
		PartnerNoticeService partnerNoticeService = partnerNoticeFactory.getPartnerNoticeService(notice.getHostNo());
		NoticeStatus noticeStatus = NoticeStatus.RANDOMINIT;
		int randomCount = notice.getRandomCount()+1;
		String resMsg = "";
		//发送报文
		try {
			resMsg = partnerNoticeService.send(notice.getContent());
			logger.info("+++++++++++++++notice[id="+notice.getId()+",hostNo="+notice.getHostNo()+"] return Messsage:"+resMsg);
			//判断是否成功
			if(0 == partnerNoticeService.isSuccess(resMsg,notice.getMethodType())){
				noticeStatus = NoticeStatus.SUCCESS;
			}else if(randomCount>=maxTimes){
				noticeStatus = NoticeStatus.FAIL;
			}
		} catch (Exception e) {
			//发送报文 通讯异常处理
			logger.error("+++++++++{EXCEPTION_CONNECTION}++++++notice[id="+notice.getId()+",hostNo="+notice.getHostNo()+"].",e);
			String content = StringUtils.toTrim(notice.getRspMsg());
			resMsg = "EXCEPTION#"+content;
		}
		
		notice.setRspMsg(resMsg);
		notice.setRandomCount(randomCount);
		notice.setModifyDate(new Date());
		notice.setStatus(noticeStatus);
		try {
			partnerNoticeFactory.getNoticeService().updateNotice(notice);
		} catch (StaleObjectStateException e) {
			logger.error("++++++++++++++{ERROR_notice fail}++++notice[id="+notice.getId()+"] update error.",e);
		}
		
		if(notice.getStatus().compareTo(NoticeStatus.FAIL) == 0){
			String content = "<b>分销商回调[<font color='red'>延迟发送</font>]失败</b><br/>";
			content += "分销商编号:"+notice.getHostNo()+"<br/>";
			content += "接口方法名:"+notice.getMethodType()+"<br/>";
			content += "已回调次数:"+notice.getCount()+"<br/>";
			content += "已随机回调次数:"+notice.getRandomCount()+"<br/>";
			content += "请求ID:" + notice.getRequestId() +"<br/>";
			content += "请求内容:" + notice.getContent() + "<br/>";
			content += "返回内容:" + resMsg;
			partnerNoticeFactory.getNoticeService().sendWarningEmail(content);
		}
	}
}
