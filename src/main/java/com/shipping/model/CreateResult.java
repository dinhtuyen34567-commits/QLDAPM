package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Kết quả tạo đơn, trả về cho {@code POST /api/v1/don-hang}.
 *
 * <p>HTTP 201 khi tạo mới, HTTP 200 khi gửi lại cùng {@code Idempotency-Key}
 * và hệ thống trả về đơn đã có (BR-13).</p>
 */
public class CreateResult {

    @SerializedName("donHangId")
    private long orderId;

    @SerializedName("maVanDon")
    private String trackingCode;

    /** Tổng tiền đã gồm VAT (đ). */
    @SerializedName("tongTien")
    private BigDecimal totalAmount;

    public CreateResult() {
        // Dung cho Gson
    }

    public CreateResult(long orderId, String trackingCode, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.trackingCode = trackingCode;
        this.totalAmount = totalAmount;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
