package com.beike.core.service.trx.notice.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.notice.Notice;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.core.service.trx.notice.NoticeService;
import com.beike.dao.trx.notice.NoticeDao;
import com.beike.service.common.EmailService;
import com.beike.util.Configuration;
import com.beike.util.StringUtils;

/**   
 * @title: NoticeServiceImpl.java
 * @package com.beike.core.service.trx.notice.impl
 * @description: 
 * @author wangweijie  
 * @date 2012-6-27 下午04:04:55
 * @version v1.0   
 */
@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {
	private static final Log logger = LogFactory.getLog(NoticeServiceImpl.class);

	private static ResourceBundle rb = ResourceBundle.getBundle("project");
	private static final String sender = rb.getString("partner_notice_sender");
	private static final String toEmail = rb.getString("partner_notice_toer");
	
	@Autowired
	private NoticeDao noticeDao;
	
	@Autowired
	private EmailService emailService;
	
	@Override
	public Long addNotice(Notice notice) {
		return noticeDao.addNotice(notice);
	}

	@Override
	public Notice findById(Long id) {
		return noticeDao.findById(id);
	}

	@Override
	public void updateNotice(Notice notice) throws StaleObjectStateException {
		noticeDao.updateNotice(notice);
	}

	@Override
	public void updateNoticeStatusById(NoticeStatus status, Long id,
			Long version) throws StaleObjectStateException {
		noticeDao.updateNoticeStatusById(status, id, version);
	}

	@Override
	public List<Notice> findNoticeListByStatus(NoticeStatus status) {
		return noticeDao.findNoticeListByStatus(status);
	}

	public Notice findTokenByHostNo(String hostNo,String methodType,String requestId){
		return noticeDao.findTokenByHostNoNoticeTypeRequestId(hostNo, methodType, requestId);
	}

	@Override
	public Notice preQryInWtDBFindById(Long id) {
		return noticeDao.findById(id);
	}

	/**
	 * 向beiker_notice表添加一个通知记录，
	 * 系统后台会调用定时任务来发送该请求。（如果要求立即执行，调用sendNotice())
	 * @param hostNo 宿主编号 如：分销商接口编号；集群内部业务编号
	 * @param hostName 通知类型（PARTNER为分销商；CLUSTER为内部服务器集群）
	 * @param requestId 请求号
	 * @param contentMap 通知内容
	 * @param methodType 接口类型
	 * @return Notice
	 * @throws
	 */
	@Override
	public Notice createNotice(String hostNo,String hostName, String requestId,Map<String,String> contentMap, String methodType) {
		
		if(StringUtils.isEmpty(hostNo) || StringUtils.isEmpty(hostName) || StringUtils.isEmpty(requestId) || 
				(null==contentMap || contentMap.size()==0) || StringUtils.isEmpty(methodType)){
			throw new IllegalArgumentException("argument_null_or_empty");
		}
		logger.info("+++++++++++++++++hostNo="+hostNo+";hostName="+hostName+";requestId="+requestId+"++++++++++++");
		
		
		String token = contentMap.get("token");
		if(token==null||"".equals(token)){
			 token = StringUtils.createUUID();
		}
		
		
		Notice notice = new Notice();
		Date date = new Date();
		
		notice.setHostNo(hostNo);	//宿主编号
		notice.setNoticeType("PARTNER"); //通知类型（PARTNER为分销商；CLUSTER为内部服务器集群）
		notice.setRequestId(requestId);	//请求编号
		
		/*
		 * content前面需要加入url地址,保证content可以放入浏览器地址(get方式)直接发送目的地址
		 */
		String url = this.getUrlByHostNo(hostNo, hostName);
		String content = getContent(contentMap);
		if (url.indexOf("?") == -1) {
			content = url + "?method=" + methodType + "&" + content;
		} else {
			content = url + "&method=" + methodType + "&" + content;
		}
		logger.info("+++++++++++++++++content="+content);
		notice.setContent(content);		//请求内容
		
		notice.setCount(0);			//重发计数为0
		notice.setRandomCount(0);	//随机重发计数为0
		notice.setMethodType(methodType);  //接口类型
		notice.setStatus(NoticeStatus.INIT);		//设置状态为INIT
		notice.setCreateDate(date);	//创建时间
		notice.setModifyDate(date);//更新时间
		notice.setVersion(0L);	//version
		notice.setToken(token);
		
		//插入数据库
		Long id = addNotice(notice);
		
		notice.setId(id);
		return notice;
	}
	
	
	/**
	 * 向beiker_notice表添加一个通知记录，
	 * 系统后台会调用定时任务来发送该请求。（如果要求立即执行，调用sendNotice())
	 * @param hostNo 宿主编号 如：分销商接口编号；集群内部业务编号
	 * @param hostName 通知类型（PARTNER为分销商；CLUSTER为内部服务器集群）
	 * @param requestId 请求号
	 * @param content 通知内容
	 * @param methodType 接口类型
	 * @return Notice
	 * @throws
	 */
	@Override
	public Notice createNotice(String hostNo, String hostName, String requestId, String content, String methodType,NoticeStatus noticeStatus) {

		
		if(StringUtils.isEmpty(hostNo) || StringUtils.isEmpty(hostName) || StringUtils.isEmpty(requestId) || 
				StringUtils.isEmpty(content) || StringUtils.isEmpty(methodType)){
			throw new IllegalArgumentException("argument_null_or_empty");
		}
		logger.info("+++++++++++++++++hostNo="+hostNo+";hostName="+hostName+";requestId="+requestId+"++++++++++++");
		
		
		String token = StringUtils.createUUID();
		
		Notice notice = new Notice();
		Date date = new Date();
		
		notice.setHostNo(hostNo);	//宿主编号
		notice.setNoticeType("PARTNER"); //通知类型（PARTNER为分销商；CLUSTER为内部服务器集群）
		notice.setRequestId(requestId);	//请求编号
		
		/*
		 * content前面需要加入url地址,保证content可以放入浏览器地址(get方式)直接发送目的地址
		 */
		String url = this.getUrlByHostNo(hostNo, hostName);
		if (url.indexOf("?") == -1) {
			content = url + "?"+ content;
		} else {
			content = url + content;
		}
		logger.info("+++++++++++++++++content="+content);
		notice.setContent(content);		//请求内容
		
		notice.setCount(0);			//重发计数为0
		notice.setRandomCount(0);	//随机重发计数为0
		notice.setMethodType(methodType);  //接口类型
		notice.setStatus(noticeStatus);		//设置状态为INIT
		notice.setCreateDate(date);	//创建时间
		notice.setModifyDate(date);//更新时间
		notice.setVersion(0L);	//version
		notice.setToken(token);
		
		//插入数据库
		Long id = addNotice(notice);
		
		notice.setId(id);
		return notice;
	
	}
	
	/**
	 * 根据宿主编号、通知类型获得通知url地址
	 * @param hostNo 经销商编号
	 * @param parentName 名称
	 * @return    
	 * @return String
	 * @throws
	 */
	private String getUrlByHostNo(String hostNo,String name){
		String url = Configuration.getInstance().getValue(hostNo+"_"+name);
		
		return url;
	}
	

	private String getContent(Map<String,String> sourceMap) {
		if (null==sourceMap || sourceMap.size() == 0) {
			return ("");
		}
		StringBuilder content = new StringBuilder("");
		for(Entry<String,String> entry : sourceMap.entrySet()){
			if(null==entry.getValue() || "".equals(entry.getValue().trim())){
				continue;
			}
			content.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		content.deleteCharAt(content.length()-1);
		return content.toString();
	}
	
	public void sendWarningEmail(String content) {
		if (toEmail != null) {
			String[] emails = toEmail.split(",");
			if (emails != null && emails.length > 0) {
				for (String string : emails) {
					try {
						emailService.sendMail(string, sender, content, "Partner Notice Warning Email");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
}
