package com.beike.common.entity.trx.lottery.full;

import java.util.Date;

public class FullLottery
{
	private Long id; // 主键
	private Long userId; // 用户ID
	private String cityName ; //城市名称
	private Date createDate = new Date(); // 创建时间
	private Long lotteryType; // 奖抽类型(0:线上商品;1:线下商品;2:虚拟币充值)
	private Long isLottery; // 是否中奖
	private String lotteryContent; // 奖品
	private String description; // 描述
	private String userEmail ;  // 用户邮箱
	
	private String prizeName ;  // 奖品名字

	public Long getId()
	{
		return id;
	}

	public Long getUserId()
	{
		return userId;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	public Long getLotteryType()
	{
		return lotteryType;
	}


	public String getLotteryContent()
	{
		return lotteryContent;
	}

	public String getDescription()
	{
		return description;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public void setLotteryType(Long lotteryType)
	{
		this.lotteryType = lotteryType;
	}

	public void setLotteryContent(String lotteryContent)
	{
		this.lotteryContent = lotteryContent;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCityName()
	{
		return cityName;
	}

	public Long getIsLottery()
	{
		return isLottery;
	}

	public void setCityName(String cityName)
	{
		this.cityName = cityName;
	}

	public void setIsLottery(Long isLottery)
	{
		this.isLottery = isLottery;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

}
