package com.beike.entity.film;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FilmShow implements Serializable{
    
	private static final long serialVersionUID = 1733159045129626050L;

	private Long id;

    private Long cinemaId;

    private Long hallId;

    private String hallName;

    private Long showIndex;

    private Date showTime;

    private Date saleEndTime;

    private Long filmId;

    private String filmName;

    private String language;

    private Integer status;

    private BigDecimal cPrice;

    private BigDecimal uPrice;

    private BigDecimal vPrice;

    private Long cityId;

    private Long uwPrice;

    private String spType;

    private String spPrice;

    private Integer isImax;

    private String dimensional;

    private Integer seatCount;

    private Integer isAvailable;

    private Date updTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Long cinemaId) {
        this.cinemaId = cinemaId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName == null ? null : hallName.trim();
    }

    public Long getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(Long showIndex) {
        this.showIndex = showIndex;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public Date getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(Date saleEndTime) {
        this.saleEndTime = saleEndTime;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName == null ? null : filmName.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language == null ? null : language.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getcPrice() {
        return cPrice;
    }

    public void setcPrice(BigDecimal cPrice) {
        this.cPrice = cPrice;
    }

    public BigDecimal getuPrice() {
        return uPrice;
    }

    public void setuPrice(BigDecimal uPrice) {
        this.uPrice = uPrice;
    }

    public BigDecimal getvPrice() {
        return vPrice;
    }

    public void setvPrice(BigDecimal vPrice) {
        this.vPrice = vPrice;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getUwPrice() {
        return uwPrice;
    }

    public void setUwPrice(Long uwPrice) {
        this.uwPrice = uwPrice;
    }

    public String getSpType() {
        return spType;
    }

    public void setSpType(String spType) {
        this.spType = spType == null ? null : spType.trim();
    }

    public String getSpPrice() {
        return spPrice;
    }

    public void setSpPrice(String spPrice) {
        this.spPrice = spPrice == null ? null : spPrice.trim();
    }

    public Integer getIsImax() {
        return isImax;
    }

    public void setIsImax(Integer isImax) {
        this.isImax = isImax;
    }

    public String getDimensional() {
        return dimensional;
    }

    public void setDimensional(String dimensional) {
        this.dimensional = dimensional == null ? null : dimensional.trim();
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", cinemaId=").append(cinemaId);
        sb.append(", hallId=").append(hallId);
        sb.append(", hallName=").append(hallName);
        sb.append(", showIndex=").append(showIndex);
        sb.append(", showTime=").append(showTime);
        sb.append(", saleEndTime=").append(saleEndTime);
        sb.append(", filmId=").append(filmId);
        sb.append(", filmName=").append(filmName);
        sb.append(", language=").append(language);
        sb.append(", status=").append(status);
        sb.append(", cPrice=").append(cPrice);
        sb.append(", uPrice=").append(uPrice);
        sb.append(", vPrice=").append(vPrice);
        sb.append(", cityId=").append(cityId);
        sb.append(", uwPrice=").append(uwPrice);
        sb.append(", spType=").append(spType);
        sb.append(", spPrice=").append(spPrice);
        sb.append(", isImax=").append(isImax);
        sb.append(", dimensional=").append(dimensional);
        sb.append(", seatCount=").append(seatCount);
        sb.append(", isAvailable=").append(isAvailable);
        sb.append(", updTime=").append(updTime);
        sb.append("]");
        return sb.toString();
    }
}