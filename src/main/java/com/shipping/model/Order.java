package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Đơn hàng ({@code tblDonHang}).
 *
 * <p>Đơn mới luôn có {@code status = CHO_LAY} và {@code tripId = null} (BR-11).
 * Trường {@code customerId} là người gửi, lấy từ phiên đăng nhập, không bao
 * giờ nhận từ client (BR-01).</p>
 */
public class Order {

    @SerializedName("id")
    private long id;

    @SerializedName("maVanDon")
    private String trackingCode;

    @SerializedName("ngayTao")
    private LocalDateTime createdAt;

    @SerializedName("trangThai")
    private String status;

    @SerializedName("tongKhoiLuong")
    private BigDecimal totalWeight;

    @SerializedName("khachHangId")
    private int customerId;

    @SerializedName("tenNguoiNhan")
    private String receiverName;

    @SerializedName("sdtNguoiNhan")
    private String receiverPhone;

    @SerializedName("diaChiGiao")
    private String deliveryAddress;

    /** Chuyến xe gán cho đơn; luôn {@code null} trong UC-01. */
    @SerializedName("chuyenXeId")
    private Integer tripId;

    /** Khóa chống tạo trùng đơn; {@code null} nếu client không gửi header. */
    @SerializedName("idempotencyKey")
    private String idempotencyKey;

    public Order() {
        // Dung cho Jackson/Gson va test
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** @return tổng khối lượng (kg), do server tự tính (BR-06). */
    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
