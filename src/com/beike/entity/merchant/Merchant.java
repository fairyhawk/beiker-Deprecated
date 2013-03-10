package com.beike.entity.merchant;

import java.io.Serializable;

/**
 * <p>Title:商家信息</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Merchant implements Serializable{
	
	private Long id;
	private String addr;
	private String merchantname;
	private String tel;
	private Long parentId;
	private String latitude;
	private int sevenrefound;
	private int overrefound;
	private int quality;
	private String merchantintroduction;
	
	private double avgscores;
	private Long evaluation_count;
		
	/**
	 * 补充属性说明：
	 * 
	 * 对应品牌下商品的虚拟购买次数
	 */
	private int virtualCount=0;	
	
	//add by qiaowb 2011-10-31
	//消费者说
	private String salescountent;
	//店长说
	private String ownercontent;
	
	public double getAvgscores() {
		return avgscores;
	}
	public void setAvgscores(double avgscores) {
		this.avgscores = avgscores;
	}
	public Long getEvaluation_count() {
		return evaluation_count;
	}
	public void setEvaluation_count(Long evaluation_count) {
		this.evaluation_count = evaluation_count;
	}
	public Merchant() {
		
	}
	public Merchant(Long id, String addr, String merchantname, String tel,
			Long parentId, String latitude, int sevenrefound, int overrefound,
			int quality, String merchantintroduction) {
		this.id = id;
		this.addr = addr;
		this.merchantname = merchantname;
		this.tel = tel;
		this.parentId = parentId;
		this.latitude = latitude;
		this.sevenrefound = sevenrefound;
		this.overrefound = overrefound;
		this.quality = quality;
		this.merchantintroduction = merchantintroduction;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getMerchantname() {
		return merchantname;
	}
	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public int getSevenrefound() {
		return sevenrefound;
	}
	public void setSevenrefound(int sevenrefound) {
		this.sevenrefound = sevenrefound;
	}
	public int getOverrefound() {
		return overrefound;
	}
	public void setOverrefound(int overrefound) {
		this.overrefound = overrefound;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public String getMerchantintroduction() {
		return merchantintroduction;
	}
	public void setMerchantintroduction(String merchantintroduction) {
		this.merchantintroduction = merchantintroduction;
	}

	
	/**
	 * 补充属性说明：
	 * 
	 * 对应品牌下商品的虚拟购买次数
	 * 
	 * 以下为该属性对应的 setter 和getter 方法
	 */		
	public int getVirtualcCount() {
		return virtualCount;
	}
	public void setVirtualCount(int virtualCount) {
		this.virtualCount = virtualCount;
	}
	public String getSalescountent() {
		return salescountent;
	}
	public void setSalescountent(String salescountent) {
		this.salescountent = salescountent;
	}
	public String getOwnercontent() {
		return ownercontent;
	}
	public void setOwnercontent(String ownercontent) {
		this.ownercontent = ownercontent;
	}
}
