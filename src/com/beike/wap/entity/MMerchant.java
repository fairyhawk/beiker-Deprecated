package com.beike.wap.entity;

/** 商家和品牌 */
public class MMerchant {
	/** 主键id */
	private long merchantid;
	/** 商家地址 */
	private String addr;
	/** 商家名称 */
	private String merchantname;
	/** 商家电话 */
	private String tel;
	/** 父级别id */
	private long parentId;
	/** latitude */
	private String latitude;
	/** 7天退款 */
	private long sevenrefound;
	/** 过期退款 */
	private long overrefound;
	/** 质量保证 */
	private long quality;
	/** 商家介绍 */
	private String merchantintroduction;
	/** 营业时间 */
	private String buinesstime;
	/** 显示名称 */
	private String displayname;
	/** 商家描述 */
	private String merchantdesc;
	/** 区域id */
	private long areaid;
	/** 城市 */
	private String city;
	/** virtualcount */
	private long virtualcount;
	
	// 以下为品牌单独所有信息
	/** 品牌logo1 */
	private String logo1;
	/** 品牌logo1压缩文件路径 */
	private String zipLogo1;
	/** 品牌logotitle */
	private String logoTitle;
	/** 品牌logotitile压缩文件路径 */
	private String zipLogoTitle;
	/** 品牌logotitle图片名称 */
	private String logoTitleName;
	/** 平均分数 */
	private String avgscores;
	/** 评价数量 */
	private String Evaluation_count;
	/** 卖出数量 */
	private String salescount;
	
	public long getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(long merchantid) {
		this.merchantid = merchantid;
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
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public long getSevenrefound() {
		return sevenrefound;
	}
	public void setSevenrefound(long sevenrefound) {
		this.sevenrefound = sevenrefound;
	}
	public long getOverrefound() {
		return overrefound;
	}
	public void setOverrefound(long overrefound) {
		this.overrefound = overrefound;
	}
	public long getQuality() {
		return quality;
	}
	public void setQuality(long quality) {
		this.quality = quality;
	}
	public String getMerchantintroduction() {
		return merchantintroduction;
	}
	public void setMerchantintroduction(String merchantintroduction) {
		this.merchantintroduction = merchantintroduction;
	}
	public String getBuinesstime() {
		return buinesstime;
	}
	public void setBuinesstime(String buinesstime) {
		this.buinesstime = buinesstime;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public String getMerchantdesc() {
		return merchantdesc;
	}
	public void setMerchantdesc(String merchantdesc) {
		this.merchantdesc = merchantdesc;
	}
	public long getAreaid() {
		return areaid;
	}
	public void setAreaid(long areaid) {
		this.areaid = areaid;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public long getVirtualcount() {
		return virtualcount;
	}
	public void setVirtualcount(long virtualcount) {
		this.virtualcount = virtualcount;
	}
	public String getLogo1() {
		return logo1;
	}
	public void setLogo1(String logo1) {
		this.logo1 = logo1;
	}
	public String getZipLogo1() {
		return zipLogo1;
	}
	public void setZipLogo1(String zipLogo1) {
		this.zipLogo1 = zipLogo1;
	}
	public String getLogoTitle() {
		return logoTitle;
	}
	public void setLogoTitle(String logoTitle) {
		this.logoTitle = logoTitle;
	}
	public String getZipLogoTitle() {
		return zipLogoTitle;
	}
	public void setZipLogoTitle(String zipLogoTitle) {
		this.zipLogoTitle = zipLogoTitle;
	}
	public String getLogoTitleName() {
		return logoTitleName;
	}
	public void setLogoTitleName(String logoTitleName) {
		this.logoTitleName = logoTitleName;
	}
	public String getAvgscores() {
		return avgscores;
	}
	public void setAvgscores(String avgscores) {
		this.avgscores = avgscores;
	}
	public String getEvaluation_count() {
		return Evaluation_count;
	}
	public void setEvaluation_count(String evaluation_count) {
		Evaluation_count = evaluation_count;
	}
	public String getSalescount() {
		return salescount;
	}
	public void setSalescount(String salescount) {
		this.salescount = salescount;
	}
}
