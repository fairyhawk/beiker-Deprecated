package com.beike.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Title:商户参数
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */

public class MerchantForm implements Serializable
{
	private static final long serialVersionUID = 1L;
    public static final String IS_SUPPORT_TAKEAWAY = "1";//支持外卖
    public static final String IS_NOT_SUPPORT_TAKEAWAY = "0";//不支持外卖
    public static final String IS_SUPPORT_ORDER = "1";//支持点菜
    public static final String IS_NOT_SUPPORT_ORDER = "0";//不支持点菜
    
	private String id;
	
	private String addr;
	private String merchantname;
	private String tel;
	private Long parentId;
	private String latitude;
	
	private String buinesstime;
	
	private String logo1;//商品详情 logo
	
	private String avgscores;
	private String evaluation_count;
	
	private String city;

	private String logo2;//品牌列表logo
	
	private String logoDetail;//品牌列表详情logo
	
	private String logoTitle;//品牌详情 title图片

	private String merchantdesc;
	
	private String salescount; //销售数量

	//add by qiaowb 2011-10-31
	private String salescountent;//消费者说
	private String ownercontent;//店长说
	private String csstemplatename;//css模板文件名
	
	//店面环境图片
	private String MerchantbaoLogo1;
	private String MerchantbaoLogo2;
	private String MerchantbaoLogo3;
	private String MerchantbaoLogo4;
	private String MerchantbaoLogo5;
	private String MerchantbaoLogo6;
	private String MerchantbaoLogo7;
	private String MerchantbaoLogo8;
	
	//店面环境图片集合
	private List<String[]> listMerchantbaoLogo;
	//商铺宝banner
	private String baoTitleLogo;
	
	private int avgscoreswidth;
	
	//add by qiaowb 2012-03-15
	private Long mcScore = 0l;	//商家得分
	private Long wellCount = 0l;	//好评价数
	private Long satisfyCount = 0l;//满意评价数
	private Long poorCount = 0l;	//差评价数
	private float satisfyRate;	//总满意率 = (好评价数+满意评价数)*100/(好评价数+满意评价数+差评价数)
	private float partWellRate; //分好评率 = 好评价数*100/(好评价数+满意评价数+差评价数)
	private float partSatisfyRate; //分满意率 = 满意评价数*100/(好评价数+满意评价数+差评价数)
	private float partPoorRate; //分满意率 = 差评价数*100/(好评价数+满意评价数+差评价数)
	private int isVip;	//是否VIP商家
	/*
	 * 1：支持外卖 0：不支持外卖
	 */
	private String is_Support_Takeaway;
	/*
	 * 1：支持点菜 0：不点菜外卖
	 */
	private String is_Support_Online_Meal;
	
	private double lng;  //分店经度
	
	private double lat;   //分店纬度
	/**
	 * 商家主营业务
	 */
	private Set<String> mainBusiness;
	
	/**
	 * 补充属性说明：
	 * 
	 * 对应品牌下商品的虚拟购买次数
	 */
	private int virtualCount=0;
	
	/**
	 * 店铺环境
	 */
	private String environment;
	
	/**
	 * 接待能力
	 */
	private String capacity;
	
	/**
	 * 其他服务
	 */
	private String otherservice;
	
	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getOtherservice() {
		return otherservice;
	}

	public void setOtherservice(String otherservice) {
		this.otherservice = otherservice;
	}

	public Set<String> getMainBusiness() {
		return mainBusiness;
	}

	public void setMainBusiness(Set<String> mainBusiness) {
		this.mainBusiness = mainBusiness;
	}

	public String getSalescount() {
		return salescount;
	}

	public void setSalescount(String salescount) {
		this.salescount = salescount;
	}

	public String getMerchantdesc() {
		return merchantdesc;
	}

	public void setMerchantdesc(String merchantdesc) {
		this.merchantdesc = merchantdesc;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		MerchantForm mf=null;
		if(obj instanceof MerchantForm){
			mf=(MerchantForm) obj;
		}
		if(mf==null)return false;
		
		return this.getId().equals(mf.getId());
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
		
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLogo1() {
		return logo1;
	}

	public void setLogo1(String logo1) {
		this.logo1 = logo1;
	}

	public String getBuinesstime() {
		return buinesstime;
	}

	public void setBuinesstime(String buinesstime) {
		this.buinesstime = buinesstime;
	}

	public MerchantForm() {
		
	}

	private Long sevenrefound;
	private Long overrefound;
	private Long quality;
	private String merchantintroduction;

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


	public String getMerchantintroduction() {
		return merchantintroduction;
	}

	public void setMerchantintroduction(String merchantintroduction) {
		this.merchantintroduction = merchantintroduction;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvgscores() {
		return avgscores;
	}

	public void setAvgscores(String avgscores) {
		this.avgscores = avgscores;
	}

	public String getEvaluation_count() {
		return evaluation_count;
	}

	public void setEvaluation_count(String evaluation_count) {
		this.evaluation_count = evaluation_count;
	}

	public String getLogo2() {
		return logo2;
	}

	public void setLogo2(String logo2) {
		this.logo2 = logo2;
	}

	public String getLogoDetail() {
		return logoDetail;
	}

	public void setLogoDetail(String logoDetail) {
		this.logoDetail = logoDetail;
	}

	public String getLogoTitle() {
		return logoTitle;
	}

	public void setLogoTitle(String logoTitle) {
		this.logoTitle = logoTitle;
	}

	public Long getSevenrefound() {
		return sevenrefound;
	}

	public void setSevenrefound(Long sevenrefound) {
		this.sevenrefound = sevenrefound;
	}

	public Long getOverrefound() {
		return overrefound;
	}

	public void setOverrefound(Long overrefound) {
		this.overrefound = overrefound;
	}

	public Long getQuality() {
		return quality;
	}

	public void setQuality(Long quality) {
		this.quality = quality;
	}

	
	/**
	 * 补充属性说明：
	 * 
	 * 对应品牌下商品的虚拟购买次数
	 * 
	 * 以下为该属性对应的 setter 和getter 方法
	 */	
	public int getVirtualCount() {
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

	public String getCsstemplatename() {
		return csstemplatename;
	}

	public void setCsstemplatename(String csstemplatename) {
		this.csstemplatename = csstemplatename;
	}

	public String getMerchantbaoLogo1() {
		return MerchantbaoLogo1;
	}

	public void setMerchantbaoLogo1(String merchantbaoLogo1) {
		MerchantbaoLogo1 = merchantbaoLogo1;
	}

	public String getMerchantbaoLogo2() {
		return MerchantbaoLogo2;
	}

	public void setMerchantbaoLogo2(String merchantbaoLogo2) {
		MerchantbaoLogo2 = merchantbaoLogo2;
	}

	public String getMerchantbaoLogo3() {
		return MerchantbaoLogo3;
	}

	public void setMerchantbaoLogo3(String merchantbaoLogo3) {
		MerchantbaoLogo3 = merchantbaoLogo3;
	}

	public String getMerchantbaoLogo4() {
		return MerchantbaoLogo4;
	}

	public void setMerchantbaoLogo4(String merchantbaoLogo4) {
		MerchantbaoLogo4 = merchantbaoLogo4;
	}

	public String getMerchantbaoLogo5() {
		return MerchantbaoLogo5;
	}

	public void setMerchantbaoLogo5(String merchantbaoLogo5) {
		MerchantbaoLogo5 = merchantbaoLogo5;
	}

	public String getMerchantbaoLogo6() {
		return MerchantbaoLogo6;
	}

	public void setMerchantbaoLogo6(String merchantbaoLogo6) {
		MerchantbaoLogo6 = merchantbaoLogo6;
	}

	public String getMerchantbaoLogo7() {
		return MerchantbaoLogo7;
	}

	public void setMerchantbaoLogo7(String merchantbaoLogo7) {
		MerchantbaoLogo7 = merchantbaoLogo7;
	}

	public String getMerchantbaoLogo8() {
		return MerchantbaoLogo8;
	}

	public void setMerchantbaoLogo8(String merchantbaoLogo8) {
		MerchantbaoLogo8 = merchantbaoLogo8;
	}

	public List<String[]> getListMerchantbaoLogo() {
		return listMerchantbaoLogo;
	}

	public void setListMerchantbaoLogo(List<String[]> listMerchantbaoLogo) {
		this.listMerchantbaoLogo = listMerchantbaoLogo;
	}

	public String getBaoTitleLogo() {
		return baoTitleLogo;
	}

	public void setBaoTitleLogo(String baoTitleLogo) {
		this.baoTitleLogo = baoTitleLogo;
	}

	public int getAvgscoreswidth() {
		return avgscoreswidth;
	}

	public void setAvgscoreswidth(int avgscoreswidth) {
		this.avgscoreswidth = avgscoreswidth;
	}

	public Long getMcScore() {
		return mcScore;
	}

	public void setMcScore(Long mcScore) {
		this.mcScore = mcScore;
	}

	public Long getWellCount() {
		return wellCount;
	}

	public void setWellCount(Long wellCount) {
		this.wellCount = wellCount;
	}

	public Long getSatisfyCount() {
		return satisfyCount;
	}

	public void setSatisfyCount(Long satisfyCount) {
		this.satisfyCount = satisfyCount;
	}

	public Long getPoorCount() {
		return poorCount;
	}

	public void setPoorCount(Long poorCount) {
		this.poorCount = poorCount;
	}
	
	public void setPartPoorRate(int partPoorRate) {
		this.partPoorRate = partPoorRate;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}
	
	public float getSatisfyRate() {
		return satisfyRate;
	}

	public void setSatisfyRate(float satisfyRate) {
		this.satisfyRate = satisfyRate;
	}

	public float getPartWellRate() {
		return partWellRate;
	}

	public void setPartWellRate(float partWellRate) {
		this.partWellRate = partWellRate;
	}

	public float getPartSatisfyRate() {
		return partSatisfyRate;
	}

	public void setPartSatisfyRate(float partSatisfyRate) {
		this.partSatisfyRate = partSatisfyRate;
	}

	public float getPartPoorRate() {
		return partPoorRate;
	}

	public void setPartPoorRate(float partPoorRate) {
		this.partPoorRate = partPoorRate;
	}

	/**
	 * 计算满意率
	 * -1表示不需要显示好评率,星级
	 */
	public MerchantForm calculateScore(){
		//满意率计算
		Long evalCount = getWellCount() + getSatisfyCount() + getPoorCount();
		if(evalCount>10){
			setSatisfyRate(new BigDecimal((float)(getWellCount() + getSatisfyCount())* 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
			
			setPartWellRate(new BigDecimal((float)getWellCount() * 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
			
			setPartSatisfyRate(new BigDecimal((float)getSatisfyCount() * 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
					
			setPartPoorRate(new BigDecimal((float)getPoorCount() * 100 / evalCount)
				.setScale(2,RoundingMode.HALF_UP).floatValue());
		}else{
			setSatisfyRate(-1);
			setPartWellRate(-1);
			setPartSatisfyRate(-1);
			setPartPoorRate(-1);
		}
		return this;
	}

	public String getIs_Support_Takeaway() {
		return is_Support_Takeaway;
	}

	public void setIs_Support_Takeaway(String is_Support_Takeaway) {
		this.is_Support_Takeaway = is_Support_Takeaway;
	}

	public String getIs_Support_Online_Meal() {
		return is_Support_Online_Meal;
	}

	public void setIs_Support_Online_Meal(String is_Support_Online_Meal) {
		this.is_Support_Online_Meal = is_Support_Online_Meal;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
}
