package com.beike.common.entity.trx.lottery.reg;

import java.util.Date;

/**
 * 千品注册指定概率抽奖
 * 
 * @author jianjun.huo
 * 
 */
public class LotteryReg
{
	private Long id;
	//用户登录名
	private String userEmail ;
	// 用户iD
	private Long userId;
	// 创建时间
	private Date createDate = new Date();
	// 是否中奖: 1中奖， 0不中奖
	private boolean isLottery = false;
	// 奖品
	private String lotteryContent = "";
	// 描述
	private String description = "";

	public LotteryReg()
	{
		super();
	}

	public LotteryReg(Long userId, Date createDate, boolean isLottery, String lotteryContent)
	{
		this.userId = userId;
		this.createDate = createDate;
		this.isLottery = isLottery;
		this.lotteryContent = lotteryContent;
	}

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

	public boolean getIsLottery()
	{
		return isLottery;
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

	public void setIsLottery(boolean isLottery)
	{
		this.isLottery = isLottery;
	}

	public void setLotteryContent(String lotteryContent)
	{
		this.lotteryContent = lotteryContent;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

}
