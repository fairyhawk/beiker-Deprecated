package com.beike.common.entity.trx;    

import java.util.Date;

import com.beike.common.enums.trx.TrxLogType;
import com.beike.common.enums.trx.TrxlogSubType;

/**   
 * @Title: TrxLog.java
 * @Package com.beike.common.entity.trx
 * @Description: TODO
 * @date Jun 30, 2011 5:01:06 PM
 * @author wh.cheng
 * @version v1.0   
 */
public class TrxLog {
	
	
	private Long id;
	private String trxGoodsSn;
	
	private Date createDate;
	private TrxLogType trxLogType;
	
	private String  logTitle;
	private String  logContent="";
	private TrxlogSubType trxlogSubType;
	
	public TrxlogSubType getTrxlogSubType() {
		return trxlogSubType;
	}
	public void setTrxlogSubType(TrxlogSubType trxlogSubType) {
		this.trxlogSubType = trxlogSubType;
	}
	public TrxLog(){
		
	}
	public TrxLog(String trxGoodsSn, Date createDate,TrxLogType trxLogType,String logTitle,String  logContent){
		this.trxGoodsSn=trxGoodsSn;
		this.createDate=createDate;
		this.trxLogType=trxLogType;
		this.logTitle=logTitle;
		this.logContent=logContent;
	}
	
	
	public TrxLog(String trxGoodsSn, Date createDate,TrxLogType trxLogType,String logTitle){
		this.trxGoodsSn=trxGoodsSn;
		this.createDate=createDate;
		this.trxLogType=trxLogType;
		this.logTitle=logTitle;
		
	}
	
	public TrxLog(String trxGoodsSn, Date createDate,TrxLogType trxLogType){
		this.trxGoodsSn=trxGoodsSn;
		this.createDate=createDate;
		this.trxLogType=trxLogType;
		
	}


	public String getTrxGoodsSn() {
		return trxGoodsSn;
	}
	public void setTrxGoodsSn(String trxGoodsSn) {
		this.trxGoodsSn = trxGoodsSn;
	}
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public TrxLogType getTrxLogType() {
		return trxLogType;
	}

	public void setTrxLogType(TrxLogType trxLogType) {
		this.trxLogType = trxLogType;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public String getLogTitle() {
		return logTitle;
	}
	public void setLogTitle(String logTitle) {
		this.logTitle = logTitle;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	

}
 