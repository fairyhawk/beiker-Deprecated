package com.beike.entity.lottery;

import java.sql.Timestamp;

/**
 * @author zx.liu
 */

public class PrizeGoods {
	
	// 抽奖活动Id,对应一次抽奖活动
	private Long	prizeId=0L;	
	//推荐商品名称 ,可以参考商品名称
	private String	prizeName="";	
	
	// 商品Id,对应一种商品的ID
	private Long	goodsId;	

	/**
	 *  商品图片,用于临时保存商品图片的名称路径
	 */
	private String 	goodsLogo="";	
	// 商品名称
	private String	goodsName="";
		
	// 可以获奖的总人数
	private Long		winners=0L;	

	//抽奖开始时间
	private Timestamp	beginTime;	
	//抽奖结束时间
	private Timestamp	endTime;	

	//种子描述
	private String		seedDescription="";	
	// 种子出现时间 // 对应页面上的开奖时间
	private Timestamp	seedEmergenceTime;	
	//种子随机数串
	private String		prizeSeed="";
	
	//推荐商品1Id
	private Long	featuredId1=0L;	
	//推荐上平2Id
	private Long	featuredId2=0L;
	//推荐商品3Id
	private Long	featuredId3=0L;	
	//推荐商品4Id
	private Long	featuredId4=0L;
	
	//状态（抽奖中，抽奖结束）
	private Long	status;
	
	private Long  	participantsCount=0L;

	public Long getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsLogo() {
		return goodsLogo;
	}

	public void setGoodsLogo(String goodsLogo) {
		this.goodsLogo = goodsLogo;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Long getWinners() {
		return winners;
	}

	public void setWinners(Long winners) {
		this.winners = winners;
	}

	public Timestamp getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getSeedDescription() {
		return seedDescription;
	}

	public void setSeedDescription(String seedDescription) {
		this.seedDescription = seedDescription;
	}

	public Timestamp getSeedEmergenceTime() {
		return seedEmergenceTime;
	}

	public void setSeedEmergenceTime(Timestamp seedEmergenceTime) {
		this.seedEmergenceTime = seedEmergenceTime;
	}

	public String getPrizeSeed() {
		return prizeSeed;
	}

	public void setPrizeSeed(String prizeSeed) {
		this.prizeSeed = prizeSeed;
	}

	public Long getFeaturedId1() {
		return featuredId1;
	}

	public void setFeaturedId1(Long featuredId1) {
		this.featuredId1 = featuredId1;
	}

	public Long getFeaturedId2() {
		return featuredId2;
	}

	public void setFeaturedId2(Long featuredId2) {
		this.featuredId2 = featuredId2;
	}

	public Long getFeaturedId3() {
		return featuredId3;
	}

	public void setFeaturedId3(Long featuredId3) {
		this.featuredId3 = featuredId3;
	}

	public Long getFeaturedId4() {
		return featuredId4;
	}

	public void setFeaturedId4(Long featuredId4) {
		this.featuredId4 = featuredId4;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getParticipantsCount() {
		return participantsCount;
	}

	public void setParticipantsCount(Long participantsCount) {
		this.participantsCount = participantsCount;
	}
	
	
}

