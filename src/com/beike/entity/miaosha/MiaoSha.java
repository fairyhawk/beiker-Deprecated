package com.beike.entity.miaosha;

import java.io.Serializable;
import java.sql.Timestamp;

/**      
 * project:beiker  
 * Title:秒杀
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:16:39 PM     
 * @version 1.0
 */
public class MiaoSha implements Serializable {
	private static final long serialVersionUID = 102183800755795752L;
	
	//秒杀ID
	private Long msId;
	//商品ID
	private Long goodsId;
	//秒杀商品标题
	private String msTitle;
	//短标题
	private String msShortTitle;
	//秒杀价
	private double msPayPrice;
	//秒杀库存
	private int msMaxCount;
	//秒杀个人限购数
	private int msSingleCount;
	//秒杀开始时间
	private Timestamp msStartTime;
	//秒杀结束时间
	private Timestamp msEndTime;
	//秒杀显示开始时间
	private Timestamp msShowStartTime;
	//秒杀显示结束时间
	private Timestamp msShowEndTime;
	//秒杀banner图片路径
	private String msBanner;
	//秒杀状态 0结束 1进行中 2即将开始
	private int msStatus;
	//秒杀虚拟销量
	private int msVirtualCount;
	//秒杀结算价
	private double msSettlePrice;
	//商品价格
	private double goodsCurrentPrice;
	//商品图片
	private String goodsLogo;
	//购买人数
	private int msSaleCount;
	//距离开始秒数
	private Long startSeconds;
	//距离结束秒数
	private Long endSeconds;
	//是否需要虚拟销量(1:是 0:否)
	private int needVirtual;
	
	public Long getMsId() {
		return msId;
	}
	public void setMsId(Long msId) {
		this.msId = msId;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public String getMsTitle() {
		return msTitle;
	}
	public void setMsTitle(String msTitle) {
		this.msTitle = msTitle;
	}
	public String getMsShortTitle() {
		return msShortTitle;
	}
	public void setMsShortTitle(String msShortTitle) {
		this.msShortTitle = msShortTitle;
	}
	public double getMsPayPrice() {
		return msPayPrice;
	}
	public void setMsPayPrice(double msPayPrice) {
		this.msPayPrice = msPayPrice;
	}
	public int getMsMaxCount() {
		return msMaxCount;
	}
	public void setMsMaxCount(int msMaxCount) {
		this.msMaxCount = msMaxCount;
	}
	public int getMsSingleCount() {
		return msSingleCount;
	}
	public void setMsSingleCount(int msSingleCount) {
		this.msSingleCount = msSingleCount;
	}
	public Timestamp getMsStartTime() {
		return msStartTime;
	}
	public void setMsStartTime(Timestamp msStartTime) {
		this.msStartTime = msStartTime;
	}
	public Timestamp getMsEndTime() {
		return msEndTime;
	}
	public void setMsEndTime(Timestamp msEndTime) {
		this.msEndTime = msEndTime;
	}
	public Timestamp getMsShowStartTime() {
		return msShowStartTime;
	}
	public void setMsShowStartTime(Timestamp msShowStartTime) {
		this.msShowStartTime = msShowStartTime;
	}
	public Timestamp getMsShowEndTime() {
		return msShowEndTime;
	}
	public void setMsShowEndTime(Timestamp msShowEndTime) {
		this.msShowEndTime = msShowEndTime;
	}
	public String getMsBanner() {
		return msBanner;
	}
	public void setMsBanner(String msBanner) {
		this.msBanner = msBanner;
	}
	public int getMsStatus() {
		return msStatus;
	}
	public void setMsStatus(int msStatus) {
		this.msStatus = msStatus;
	}
	public int getMsVirtualCount() {
		return msVirtualCount;
	}
	public void setMsVirtualCount(int msVirtualCount) {
		this.msVirtualCount = msVirtualCount;
	}
	public double getMsSettlePrice() {
		return msSettlePrice;
	}
	public void setMsSettlePrice(double msSettlePrice) {
		this.msSettlePrice = msSettlePrice;
	}
	public double getGoodsCurrentPrice() {
		return goodsCurrentPrice;
	}
	public void setGoodsCurrentPrice(double goodsCurrentPrice) {
		this.goodsCurrentPrice = goodsCurrentPrice;
	}
	public String getGoodsLogo() {
		return goodsLogo;
	}
	public void setGoodsLogo(String goodsLogo) {
		this.goodsLogo = goodsLogo;
	}
	public int getMsSaleCount() {
		return msSaleCount;
	}
	public void setMsSaleCount(int msSaleCount) {
		this.msSaleCount = msSaleCount;
	}
	public Long getStartSeconds() {
		return startSeconds;
	}
	public void setStartSeconds(Long startSeconds) {
		this.startSeconds = startSeconds;
	}
	public Long getEndSeconds() {
		return endSeconds;
	}
	public void setEndSeconds(Long endSeconds) {
		this.endSeconds = endSeconds;
	}
	public int getNeedVirtual() {
		return needVirtual;
	}
	public void setNeedVirtual(int needVirtual) {
		this.needVirtual = needVirtual;
	}
}
