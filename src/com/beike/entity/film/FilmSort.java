package com.beike.entity.film;

import java.util.Date;

public class FilmSort {
    private Long filmId;

    private String filmSort;

    private String filmPy;

    private Integer isAvailable;

    private Date updTime;

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public String getFilmSort() {
        return filmSort;
    }

    public void setFilmSort(String filmSort) {
        this.filmSort = filmSort == null ? null : filmSort.trim();
    }

    public String getFilmPy() {
        return filmPy;
    }

    public void setFilmPy(String filmPy) {
        this.filmPy = filmPy == null ? null : filmPy.trim();
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
        sb.append(", filmId=").append(filmId);
        sb.append(", filmSort=").append(filmSort);
        sb.append(", filmPy=").append(filmPy);
        sb.append(", isAvailable=").append(isAvailable);
        sb.append(", updTime=").append(updTime);
        sb.append("]");
        return sb.toString();
    }
}