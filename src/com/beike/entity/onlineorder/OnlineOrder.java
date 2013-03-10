package com.beike.entity.onlineorder;

import java.util.Date;

public class OnlineOrder {
    private Long orderId;

    private Long guestId;

    private String orderSn;

    private Date orderStartTime;

    private Date orderEndTime;

    private String discountEngine;

    private String orderExplain;

    private String auditStatus;

    private Integer createucid;

    private Date createtime;

    private Integer onlineucid;

    private Date onlinetime;

    private Integer updateucid;

    private Date updatetime;

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

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn == null ? null : orderSn.trim();
    }

    public Date getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(Date orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public Date getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(Date orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public String getDiscountEngine() {
        return discountEngine;
    }

    public void setDiscountEngine(String discountEngine) {
        this.discountEngine = discountEngine == null ? null : discountEngine.trim();
    }

    public String getOrderExplain() {
        return orderExplain;
    }

    public void setOrderExplain(String orderExplain) {
        this.orderExplain = orderExplain == null ? null : orderExplain.trim();
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus == null ? null : auditStatus.trim();
    }

    public Integer getCreateucid() {
        return createucid;
    }

    public void setCreateucid(Integer createucid) {
        this.createucid = createucid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getOnlineucid() {
        return onlineucid;
    }

    public void setOnlineucid(Integer onlineucid) {
        this.onlineucid = onlineucid;
    }

    public Date getOnlinetime() {
        return onlinetime;
    }

    public void setOnlinetime(Date onlinetime) {
        this.onlinetime = onlinetime;
    }

    public Integer getUpdateucid() {
        return updateucid;
    }

    public void setUpdateucid(Integer updateucid) {
        this.updateucid = updateucid;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", orderId=").append(orderId);
        sb.append(", guestId=").append(guestId);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", orderStartTime=").append(orderStartTime);
        sb.append(", orderEndTime=").append(orderEndTime);
        sb.append(", discountEngine=").append(discountEngine);
        sb.append(", orderExplain=").append(orderExplain);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", createucid=").append(createucid);
        sb.append(", createtime=").append(createtime);
        sb.append(", onlineucid=").append(onlineucid);
        sb.append(", onlinetime=").append(onlinetime);
        sb.append(", updateucid=").append(updateucid);
        sb.append(", updatetime=").append(updatetime);
        sb.append("]");
        return sb.toString();
    }
}