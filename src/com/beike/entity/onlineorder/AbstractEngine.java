package com.beike.entity.onlineorder;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractEngine {

	
	public static final String json_key_price = "price";
	
	public static final String json_key_discount = "discount";
	
	public static final String json_key_discount_type = "discounttype";
	
	static final Log logger = LogFactory.getLog(AbstractEngine.class);
	private Timestamp starttime;
	
	
	private Timestamp endtime;
	public Timestamp getStarttime() {
		return starttime;
	}

	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}

	public Timestamp getEndtime() {
		return endtime;
	}

	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}

	private Long engineId;

    public Long getEngineId() {
		return engineId;
	}

	public void setEngineId(Long engineId) {
		this.engineId = engineId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	private Long orderId;
	
	//全端计算价格
	public abstract String formatJson();
    
    //计算支付价格
    public abstract double caculatePay(double subAmount);
    
    
    
    //活动信息
    public abstract String getPromotionInfo();
    
    //活动说明
    private String tip;
    
    private boolean isOnline;


    public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}
	
	
}
