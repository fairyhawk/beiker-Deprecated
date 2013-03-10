package com.beike.entity.onlineorder;

public class OrderGuestMap {
    private Long id;

    private Long orderId;

    private Long guestId;

    private String guestSettle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getGuestSettle() {
        return guestSettle;
    }

    public void setGuestSettle(String guestSettle) {
        this.guestSettle = guestSettle == null ? null : guestSettle.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderId=").append(orderId);
        sb.append(", guestId=").append(guestId);
        sb.append(", guestSettle=").append(guestSettle);
        sb.append("]");
        return sb.toString();
    }
}