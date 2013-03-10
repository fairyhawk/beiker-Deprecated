package com.beike.common.bean.trx;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 支付宝Wap 和 安全支付 参数集 
 * @author  jianjun.huo
 *
 */
@XObject("notify")
public class AlipayWapParams {
  
    @XNode("payment_type")
    private String paymentType;

    @XNode("subject")
    private String subject;
    
    @XNode("trade_no")
    private String tradeNo;
    
    @XNode("buyer_email")
    private String buyerEmail;
    
    @XNode("gmt_create")
    private String gmtCreate;
    
    @XNode("notify_type")
    private String notifyType;
    
    @XNode("quantity")
    private String quantity;
    
    @XNode("out_trade_no")
    private String outTradeNo;
    
    @XNode("notify_time")
    private String notifyTime;
    
    @XNode("seller_id")
    private String sellerId;
    
    @XNode("trade_status")
    private String tradeStatus;
    
    @XNode("is_total_fee_adjust")
    private String isTotalFeeAdjust;
    
    @XNode("notify_id")
    private String notifyId;
    
    @XNode("total_fee")
    private String totalFee;
    
    

	public String getPaymentType()
	{
		return paymentType;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getTradeNo()
	{
		return tradeNo;
	}

	public String getBuyerEmail()
	{
		return buyerEmail;
	}

	public String getGmtCreate()
	{
		return gmtCreate;
	}

	public String getNotifyType()
	{
		return notifyType;
	}

	public String getQuantity()
	{
		return quantity;
	}

	public String getOutTradeNo()
	{
		return outTradeNo;
	}

	public String getNotifyTime()
	{
		return notifyTime;
	}

	public String getSellerId()
	{
		return sellerId;
	}

	public String getTradeStatus()
	{
		return tradeStatus;
	}

	public String getIsTotalFeeAdjust()
	{
		return isTotalFeeAdjust;
	}

	public String getNotifyId()
	{
		return notifyId;
	}

	public void setPaymentType(String paymentType)
	{
		this.paymentType = paymentType;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public void setTradeNo(String tradeNo)
	{
		this.tradeNo = tradeNo;
	}

	public void setBuyerEmail(String buyerEmail)
	{
		this.buyerEmail = buyerEmail;
	}

	public void setGmtCreate(String gmtCreate)
	{
		this.gmtCreate = gmtCreate;
	}

	public void setNotifyType(String notifyType)
	{
		this.notifyType = notifyType;
	}

	public void setQuantity(String quantity)
	{
		this.quantity = quantity;
	}

	public void setOutTradeNo(String outTradeNo)
	{
		this.outTradeNo = outTradeNo;
	}

	public void setNotifyTime(String notifyTime)
	{
		this.notifyTime = notifyTime;
	}

	public void setSellerId(String sellerId)
	{
		this.sellerId = sellerId;
	}

	public void setTradeStatus(String tradeStatus)
	{
		this.tradeStatus = tradeStatus;
	}

	public void setIsTotalFeeAdjust(String isTotalFeeAdjust)
	{
		this.isTotalFeeAdjust = isTotalFeeAdjust;
	}

	public void setNotifyId(String notifyId)
	{
		this.notifyId = notifyId;
	}

	public String getTotalFee()
	{
		return totalFee;
	}

	public void setTotalFee(String totalFee)
	{
		this.totalFee = totalFee;
	}
    
    

}
