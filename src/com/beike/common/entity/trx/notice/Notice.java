package com.beike.common.entity.trx.notice;

import java.util.Date;

import com.beike.common.enums.trx.NoticeStatus;
import com.beike.util.DateUtils;

/**   
 * 接口补单（回调）表
 * @title: Notice.java
 * @package com.beike.common.entity.trx.notice
 * @description: 
 * @author wangweijie  
 * @date 2012-6-13 上午10:16:56
 * @version v1.0   
 */
public class Notice {
	
	private Long id;			//主键
	private String hostNo;		//宿主编号。如：分销商接口编号；集群内部业务编号
	private String noticeType;	//通知类型。如：PARTNER为分销商；CLUSTER为内部服务器集群
	private String requestId;	//请求号（唯一标示）
	private String content="";		//通知内容
	private Integer count;			//已通知次数
	private Integer randomCount;	//随机发送通知数
	private String methodType;	//接口类型
	private NoticeStatus status;		//INIT:新建;FAIL:失败;SUCCESS:成功;FINAL_FAIL:最终失败
	private String rspMsg="";		//响应信息
	private Date createDate=new Date();	//创建日期
	private Date modifyDate=new Date();	//修改日期
	private Long version = 0L;		//乐观锁版本号
	private String token = "";
	
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHostNo() {
		return hostNo;
	}
	public void setHostNo(String hostNo) {
		this.hostNo = hostNo;
	}
	public String getNoticeType() {
		return noticeType;
	}
	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public NoticeStatus getStatus() {
		return status;
	}
	public void setStatus(NoticeStatus status) {
		this.status = status;
	}
	public String getRspMsg() {
		return rspMsg;
	}
	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Integer getRandomCount() {
		return randomCount;
	}
	public void setRandomCount(Integer randomCount) {
		this.randomCount = randomCount;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Notice=[");
		sb.append("id="+id);
		sb.append(",hostNo="+hostNo);
		sb.append(",requestId="+requestId);
		sb.append(",content="+content);
		sb.append(",count="+count);
		sb.append(",random_count="+randomCount);
		sb.append(",methodType="+methodType);
		sb.append(",status="+status);
		sb.append(",rspMsg="+rspMsg);
		sb.append(",createDate="+DateUtils.formatDate(createDate,"yyyy-MM-dd HH:mm:ss"));
		sb.append(",modifyDate="+DateUtils.formatDate(modifyDate,"yyyy-MM-dd HH:mm:ss"));
		sb.append(",version="+version);
		sb.append(",token="+token);
		return sb.toString();
	}
	
}
