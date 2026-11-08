package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Hóa đơn ({@code tblHoaDon}).
 *
 * <p>Mỗi đơn hàng đúng một hóa đơn (ràng buộc UNIQUE {@code donHangId}).
 * {@code totalAmount} là tổng tiền <b>đã gồm VAT</b> (BR-12).</p>
 */
public class Invoice {

    @SerializedName("id")
    private long id;

    @SerializedName("maHoaDon")
    private String code;

    @SerializedName("donHangId")
    private long orderId;

    /** Tổng phải trả đã gồm VAT (đ). */
    @SerializedName("tongTien")
    private BigDecimal totalAmount;

    /** Thuế suất VAT (%). */
    @SerializedName("vat")
    private BigDecimal vatPercent;

    @SerializedName("phuongThucThanhToan")
    private String paymentMethod;

    public Invoice() {
        // Dung cho Jackson/Gson va test
    }

    public Invoice(long id, String code, long orderId, BigDecimal totalAmount,
                   BigDecimal vatPercent, String paymentMethod) {
        this.id = id;
        this.code = code;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.vatPercent = vatPercent;
        this.paymentMethod = paymentMethod;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(BigDecimal vatPercent) {
        this.vatPercent = vatPercent;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
