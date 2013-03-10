package com.beike.entity.film;

public class WpwCinemaMap {
    private Long id;

    private Long cinemaWpwId;

    private Long cinemaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCinemaWpwId() {
        return cinemaWpwId;
    }

    public void setCinemaWpwId(Long cinemaWpwId) {
        this.cinemaWpwId = cinemaWpwId;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(Long cinemaId) {
        this.cinemaId = cinemaId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", cinemaWpwId=").append(cinemaWpwId);
        sb.append(", cinemaId=").append(cinemaId);
        sb.append("]");
        return sb.toString();
    }
}