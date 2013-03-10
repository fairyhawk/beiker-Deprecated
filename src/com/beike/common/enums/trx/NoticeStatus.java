package com.beike.common.enums.trx;

/**
 * 
 * @title: NoticeStatus.java
 * @package com.beike.common.enums.trx
 * @description: beiker_notice表status枚举字段
 * @author wangweijie  
 * @date 2012-6-13 下午02:04:46
 * @version v1.0   
 */
public enum NoticeStatus {
	INIT,		//新建
	PROCESSING, //处理中
	RANDOMINIT,	//随机发送初始化
	FAIL,		//失败
	SUCCESS,	//成功
}
