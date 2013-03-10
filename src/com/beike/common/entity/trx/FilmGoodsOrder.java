package com.beike.common.entity.trx;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FilmGoodsOrder {
	private Long id;                 //id               
	private Long filmShowId;         //影片放映计划ID
	private Long userId;             //用户id
	private Long trxGoodsId = 0L ;         //商品订单id
	private Long trxOrderId = 0L;         //订单ID
	private Timestamp createDate;    //创建时间
	private Timestamp showTime;      //放映时间
	private Timestamp updateDate;    //更新时间
	private BigDecimal filmPrice;    //座位单价
	private String seatInfo;         //座位信息
	private String hallName;         //影厅名称
	private String filmName;         //影片名称
	private String language;         //影片语言版本
	private String dimensional;      //场次版本。如：2D 3D 普通等
	private String cinemaName;      //影院名称
	private String trxStatus;        //影票状态
	private String description;      //备注
	private Long version;            //版本
	private String filmTrxSn;        //网票网唯一订单号
	private String filmPayNo;    //网票网下单回调唯一标识
	private Long filmCount;      //购买票数量
	

	
	
	

	@Override
	public String toString() {
		return "FilmGoodsOrder [id=" + id + ", filmShowId=" + filmShowId
				+ ", userId=" + userId + ", trxGoodsId=" + trxGoodsId
				+ ", trxOrderId=" + trxOrderId + ", createDate=" + createDate
				+ ", showTime=" + showTime + ", updateDate=" + updateDate
				+ ", filmPrice=" + filmPrice + ", seatInfo=" + seatInfo
				+ ", hallName=" + hallName + ", filmName=" + filmName
				+ ", language=" + language + ", dimensional=" + dimensional
				+ ", cinemaName=" + cinemaName + ", trxStatus=" + trxStatus
				+ ", description=" + description + ", version=" + version
				+ ", filmTrxSn=" + filmTrxSn + ", filmPayNo=" + filmPayNo
				+ ", filmCount=" + filmCount + "]";
	}

	public Long getFilmCount() {
		return filmCount;
	}

	public void setFilmCount(Long filmCount) {
		this.filmCount = filmCount;
	}

	public String getFilmPayNo() {
		return filmPayNo;
	}

	public void setFilmPayNo(String filmPayNo) {
		this.filmPayNo = filmPayNo;
	}

	public FilmGoodsOrder() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFilmShowId() {
		return filmShowId;
	}

	public void setFilmShowId(Long filmShowId) {
		this.filmShowId = filmShowId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTrxGoodsId() {
		return trxGoodsId;
	}

	public void setTrxGoodsId(Long trxGoodsId) {
		this.trxGoodsId = trxGoodsId;
	}

	public Long getTrxOrderId() {
		return trxOrderId;
	}

	public void setTrxOrderId(Long trxOrderId) {
		this.trxOrderId = trxOrderId;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getShowTime() {
		return showTime;
	}

	public void setShowTime(Timestamp showTime) {
		this.showTime = showTime;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public BigDecimal getFilmPrice() {
		return filmPrice;
	}

	public void setFilmPrice(BigDecimal filmPrice) {
		this.filmPrice = filmPrice;
	}

	public String getSeatInfo() {
		return seatInfo;
	}

	public void setSeatInfo(String seatInfo) {
		this.seatInfo = seatInfo;
	}

	public String getHallName() {
		return hallName;
	}

	public void setHallName(String hallName) {
		this.hallName = hallName;
	}

	public String getFilmName() {
		return filmName;
	}

	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDimensional() {
		return dimensional;
	}

	public void setDimensional(String dimensional) {
		this.dimensional = dimensional;
	}

	

	public String getCinemaName() {
		return cinemaName;
	}

	public void setCinemaName(String cinemaName) {
		this.cinemaName = cinemaName;
	}

	public String getTrxStatus() {
		return trxStatus;
	}

	public void setTrxStatus(String trxStatus) {
		this.trxStatus = trxStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getFilmTrxSn() {
		return filmTrxSn;
	}

	public void setFilmTrxSn(String filmTrxSn) {
		this.filmTrxSn = filmTrxSn;
	}
	
	
	
}
