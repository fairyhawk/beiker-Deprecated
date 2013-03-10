package com.beike.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**  
* @Title:  上映影片Form
* @Package com.beike.form
* @Description: TODOa
* @author wenjie.mai  
* @date 2012-12-3 下午6:23:58
* @version V1.0  
*/
public class FilmReleaseForm implements Serializable {

	private static final long serialVersionUID = -3409623111829069262L;
	
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
    
    /**
     * 影片放映语言,同beiker_film_show表的language字段
     */
    private String syLanguage;
    
	public String getSyLanguage() {
		return syLanguage;
	}

	public void setSyLanguage(String syLanguage) {
		this.syLanguage = syLanguage;
	}

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
		this.filmName = filmName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getStarring() {
		return starring;
	}

	public void setStarring(String starring) {
		this.starring = starring;
	}

	public String getSmallPhoto() {
		return smallPhoto;
	}

	public void setSmallPhoto(String smallPhoto) {
		this.smallPhoto = smallPhoto;
	}

	public String getLargePhoto() {
		return largePhoto;
	}

	public void setLargePhoto(String largePhoto) {
		this.largePhoto = largePhoto;
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
		this.sort = sort;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		this.msg = msg;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTrailerDes() {
		return trailerDes;
	}

	public void setTrailerDes(String trailerDes) {
		this.trailerDes = trailerDes;
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
}
