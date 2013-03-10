package com.beike.entity.film;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FilmRelease implements Serializable {
    
	private static final long serialVersionUID = -7464543600408106912L;

	private Long id;

    private Long filmId;

    private String filmName;

    private String duration;

    private String director;

    private String starring;

    private String smallPhoto;

    private String largePhoto;

    private Date showDate;

    private String sort;

    private String area;

    private String type;

    private String description;

    private BigDecimal lowestPrice;

    private BigDecimal grade;

    private String msg;

    private String url;

    private String trailerDes;

    private Integer isAvailable;

    private Date updTime;
    
    //扩展字段
    private BigDecimal lowestOLBookingPrice; //在线订座最低价

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration == null ? null : duration.trim();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director == null ? null : director.trim();
    }

    public String getStarring() {
        return starring;
    }

    public void setStarring(String starring) {
        this.starring = starring == null ? null : starring.trim();
    }

    public String getSmallPhoto() {
        return smallPhoto;
    }

    public void setSmallPhoto(String smallPhoto) {
        this.smallPhoto = smallPhoto == null ? null : smallPhoto.trim();
    }

    public String getLargePhoto() {
        return largePhoto;
    }

    public void setLargePhoto(String largePhoto) {
        this.largePhoto = largePhoto == null ? null : largePhoto.trim();
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort == null ? null : sort.trim();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg == null ? null : msg.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getTrailerDes() {
        return trailerDes;
    }

    public void setTrailerDes(String trailerDes) {
        this.trailerDes = trailerDes == null ? null : trailerDes.trim();
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

    public BigDecimal getLowestOLBookingPrice() {
		return lowestOLBookingPrice;
	}

	public void setLowestOLBookingPrice(BigDecimal lowestOLBookingPrice) {
		this.lowestOLBookingPrice = lowestOLBookingPrice;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", filmId=").append(filmId);
        sb.append(", filmName=").append(filmName);
        sb.append(", duration=").append(duration);
        sb.append(", director=").append(director);
        sb.append(", starring=").append(starring);
        sb.append(", smallPhoto=").append(smallPhoto);
        sb.append(", largePhoto=").append(largePhoto);
        sb.append(", showDate=").append(showDate);
        sb.append(", sort=").append(sort);
        sb.append(", area=").append(area);
        sb.append(", type=").append(type);
        sb.append(", description=").append(description);
        sb.append(", lowestPrice=").append(lowestPrice);
        sb.append(", grade=").append(grade);
        sb.append(", msg=").append(msg);
        sb.append(", url=").append(url);
        sb.append(", trailerDes=").append(trailerDes);
        sb.append(", isAvailable=").append(isAvailable);
        sb.append(", updTime=").append(updTime);
        sb.append("]");
        return sb.toString();
    }
}