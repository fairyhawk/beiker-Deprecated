package com.beike.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**  
* @Title:  千品网影院Form
* @Package com.beike.form
* @Description: TODOa
* @author wenjie.mai  
* @date 2012-12-3 下午3:55:24
* @version V1.0  
*/
public class CinemaInfoForm implements Serializable, Comparable<Object> {

	
	private static final long serialVersionUID = 1283386791505486025L;
	
	private Long cinemaId;

    private Long type;

    private String name;

    private Long cityId;

    private Long distId;

    private Integer hallCount;

    private String address;

    private String busLine;

    private String des;

    private String photo;

    private String url;

    private String tel;

    private Integer isPhonePay;

    private String specialDes;

    private String coord;

    private Long createucid;

    private Date createtime;

    private Long updateucid;

    private Date updatetime;
    
    private Integer salecount;
    
    /**
     * 是否支持在线选座
     */
    private Boolean onlinesit;
    
    /**
     * 是否支持团购
     */
    private Boolean onlinetuangou; 
    
    /**
     * 网票网价格
     */
    private BigDecimal wpwPrice;
    
    /**
     * 团购价格
     */
    private BigDecimal tgPrice;
    
	public BigDecimal getWpwPrice() {
		return wpwPrice;
	}

	public void setWpwPrice(BigDecimal wpwPrice) {
		this.wpwPrice = wpwPrice;
	}

	public BigDecimal getTgPrice() {
		return tgPrice;
	}

	public void setTgPrice(BigDecimal tgPrice) {
		this.tgPrice = tgPrice;
	}

	public Boolean getOnlinesit() {
		return onlinesit;
	}

	public void setOnlinesit(Boolean onlinesit) {
		this.onlinesit = onlinesit;
	}

	public Boolean getOnlinetuangou() {
		return onlinetuangou;
	}

	public void setOnlinetuangou(Boolean onlinetuangou) {
		this.onlinetuangou = onlinetuangou;
	}

	public Integer getSalecount() {
		return salecount;
	}

	public void setSalecount(Integer salecount) {
		this.salecount = salecount;
	}

	public Long getCinemaId() {
		return cinemaId;
	}

	public void setCinemaId(Long cinemaId) {
		this.cinemaId = cinemaId;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getDistId() {
		return distId;
	}

	public void setDistId(Long distId) {
		this.distId = distId;
	}

	public Integer getHallCount() {
		return hallCount;
	}

	public void setHallCount(Integer hallCount) {
		this.hallCount = hallCount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBusLine() {
		return busLine;
	}

	public void setBusLine(String busLine) {
		this.busLine = busLine;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getIsPhonePay() {
		return isPhonePay;
	}

	public void setIsPhonePay(Integer isPhonePay) {
		this.isPhonePay = isPhonePay;
	}

	public String getSpecialDes() {
		return specialDes;
	}

	public void setSpecialDes(String specialDes) {
		this.specialDes = specialDes;
	}

	public String getCoord() {
		return coord;
	}

	public void setCoord(String coord) {
		this.coord = coord;
	}

	public Long getCreateucid() {
		return createucid;
	}

	public void setCreateucid(Long createucid) {
		this.createucid = createucid;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Long getUpdateucid() {
		return updateucid;
	}

	public void setUpdateucid(Long updateucid) {
		this.updateucid = updateucid;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	@Override
	public int compareTo(Object o) {
		
		if(o == null)
			return -1;
		
		CinemaInfoForm form = null;
		
		if(o instanceof CinemaInfoForm)
			form = (CinemaInfoForm) o;
		
		if(form == null)
			return -1;
		
		if(this.salecount < form.getSalecount()){
			return 1;
		}else{
			return -1;
		}
	}
}
