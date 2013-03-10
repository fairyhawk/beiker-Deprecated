package com.beike.entity.film;

import java.math.BigDecimal;
import java.util.Date;

public class CinemaHall {
    private Long id;

    private Long hallId;

    private Long cinemaId;

    private String name;

    private Integer seatCount;

    private String type;

    private Boolean isValid;

    private BigDecimal sh;

    private BigDecimal sw;

    private BigDecimal sr;

    private Date updateTime;

    private Integer isAvailable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Long cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public BigDecimal getSh() {
        return sh;
    }

    public void setSh(BigDecimal sh) {
        this.sh = sh;
    }

    public BigDecimal getSw() {
        return sw;
    }

    public void setSw(BigDecimal sw) {
        this.sw = sw;
    }

    public BigDecimal getSr() {
        return sr;
    }

    public void setSr(BigDecimal sr) {
        this.sr = sr;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", hallId=").append(hallId);
        sb.append(", cinemaId=").append(cinemaId);
        sb.append(", name=").append(name);
        sb.append(", seatCount=").append(seatCount);
        sb.append(", type=").append(type);
        sb.append(", isValid=").append(isValid);
        sb.append(", sh=").append(sh);
        sb.append(", sw=").append(sw);
        sb.append(", sr=").append(sr);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isAvailable=").append(isAvailable);
        sb.append("]");
        return sb.toString();
    }
}