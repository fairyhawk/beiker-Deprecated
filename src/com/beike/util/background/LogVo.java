package com.beike.util.background;

import java.sql.Timestamp;


public class LogVo {
	

	//log的id
	public long log_id;
	
	//log的记录时间，日志记录源的时间
	public Timestamp log_time = new Timestamp(System.currentTimeMillis());
	
	//log的级别:一般信息 0，重要信息 1，警告 2，错误3
	public String loglevel;
	
	//log的系统来源
	public String log_system;
	
	//log的产生来源：如操作者，类模块等
	public String log_source;
	
	//日志的内容
	public String log_content;
	
}
