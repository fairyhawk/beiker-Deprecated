package com.beike.entity.film;

import java.util.Date;

public class CinemaInfo {
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
    
    //扩展字段
    private Long wpwCinemaId;//对应的网票网影院ID

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
        this.name = name == null ? null : name.trim();
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
        this.address = address == null ? null : address.trim();
    }

    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine == null ? null : busLine.trim();
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des == null ? null : des.trim();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo == null ? null : photo.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
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
        this.specialDes = specialDes == null ? null : specialDes.trim();
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord == null ? null : coord.trim();
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

    public Long getWpwCinemaId() {
		return wpwCinemaId;
	}

	public void setWpwCinemaId(Long wpwCinemaId) {
		this.wpwCinemaId = wpwCinemaId;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", cinemaId=").append(cinemaId);
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", cityId=").append(cityId);
        sb.append(", distId=").append(distId);
        sb.append(", hallCount=").append(hallCount);
        sb.append(", address=").append(address);
        sb.append(", busLine=").append(busLine);
        sb.append(", des=").append(des);
        sb.append(", photo=").append(photo);
        sb.append(", url=").append(url);
        sb.append(", tel=").append(tel);
        sb.append(", isPhonePay=").append(isPhonePay);
        sb.append(", specialDes=").append(specialDes);
        sb.append(", coord=").append(coord);
        sb.append(", createucid=").append(createucid);
        sb.append(", createtime=").append(createtime);
        sb.append(", updateucid=").append(updateucid);
        sb.append(", updatetime=").append(updatetime);
        sb.append("]");
        return sb.toString();
    }
}