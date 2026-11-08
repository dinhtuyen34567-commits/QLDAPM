package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Chi tiết đơn hàng ({@code tblChiTietDonHang}).
 *
 * <p>Khóa chính kép {@code (donHangId, hangHoaId)} nên mỗi hàng hóa chỉ có
 * một dòng trong một đơn; thêm lại cùng hàng hóa sẽ cộng dồn số lượng (BR-09).</p>
 */
public class OrderDetail {

    @SerializedName("donHangId")
    private long orderId;

    @SerializedName("hangHoaId")
    private int productId;

    @SerializedName("soLuong")
    private int quantity;

    /**
     * Đơn giá cước của một đơn vị hàng, đã làm tròn về đồng.
     * Được chốt tại thời điểm tạo đơn, không lấy từ client.
     */
    @SerializedName("donGia")
    private BigDecimal unitPrice;

    public OrderDetail() {
        // Dung cho Jackson/Gson va test
    }

    public OrderDetail(long orderId, int productId, int quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /** @return đơn giá cước của một đơn vị hàng (đ). */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
