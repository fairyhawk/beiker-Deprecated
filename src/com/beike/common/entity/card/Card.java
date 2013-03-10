package com.beike.common.entity.card;

import java.util.Date;

import com.beike.common.enums.trx.CardStatus;
import com.beike.common.enums.trx.CardType;

/**
 * 卡密记录表对应实体
 * @author yurenli
 *
 */
public class Card {
	private Long id;
	private Date createDate; //创建时间
	private String cardNo;   //卡号
	private String cardPwd;  //卡密
	private int cardValue;  //面值(冗余)
	private CardStatus cardStatus;//卡状态：待印刷入库、已印刷入库、已发放未激活、已发放已激活、已使用、已过期、已废弃
	private CardType cardType;//卡类型(冗余)
	private Long bacthId;//所属批次
	private Long orderId;//所属购卡订单
	private String topupChannel;//充值渠道
	private Date updateDate;//更新时间
	private Date loseDate;//过期时间(冗余)
	private Long userId;//用户ID
	private Long vmAccountId;//所属虚拟款项ID(冗余)
	private Long bizId;//业务ID
	private Long version = 0L;//乐观锁版本号
	private String description;//备注信息
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCardPwd() {
		return cardPwd;
	}
	public void setCardPwd(String cardPwd) {
		this.cardPwd = cardPwd;
	}
	public int getCardValue() {
		return cardValue;
	}
	public void setCardValue(int cardValue) {
		this.cardValue = cardValue;
	}
	
	public CardStatus getCardStatus() {
		return cardStatus;
	}
	public void setCardStatus(CardStatus cardStatus) {
		this.cardStatus = cardStatus;
	}
	public CardType getCardType() {
		return cardType;
	}
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}
	public Long getBacthId() {
		return bacthId;
	}
	public void setBacthId(Long bacthId) {
		this.bacthId = bacthId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getTopupChannel() {
		return topupChannel;
	}
	public void setTopupChannel(String topupChannel) {
		this.topupChannel = topupChannel;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getLoseDate() {
		return loseDate;
	}
	public void setLoseDate(Date loseDate) {
		this.loseDate = loseDate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getVmAccountId() {
		return vmAccountId;
	}
	public void setVmAccountId(Long vmAccountId) {
		this.vmAccountId = vmAccountId;
	}
	public Long getBizId() {
		return bizId;
	}
	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
