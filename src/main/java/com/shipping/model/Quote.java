package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Kết quả tính cước, trả về cho {@code POST /api/v1/don-hang/tinh-cuoc}.
 *
 * <p>Tất cả giá trị tiền đã được làm tròn về đồng theo công thức mục 5.4.</p>
 */
public class Quote {

    /** Tổng khối lượng (kg), do server tự tính (BR-06). */
    @SerializedName("tongKhoiLuong")
    private BigDecimal totalWeight;

    /** Tổng cước chưa VAT (đ). */
    @SerializedName("tongCuoc")
    private BigDecimal totalFee;

    /** Thuế suất VAT (%). */
    @SerializedName("vat")
    private BigDecimal vatPercent;

    /** Tiền VAT (đ). */
    @SerializedName("tienVat")
    private BigDecimal vatAmount;

    /** Tổng thanh toán đã gồm VAT (đ). */
    @SerializedName("tongThanhToan")
    private BigDecimal totalAmount;

    /**
     * Chi tiết cước từng dòng, phục vụ hiển thị bảng mặt hàng.
     *
     * <p>Do server tự tính và trả về, client không bao giờ gửi đơn giá lên
     * (mục 3.4).</p>
     */
    @SerializedName("lines")
    private List<QuoteLine> lines = new ArrayList<>();

    public Quote() {
        // Dung cho Gson
    }

    public Quote(BigDecimal totalWeight, BigDecimal totalFee, BigDecimal vatPercent,
                 BigDecimal vatAmount, BigDecimal totalAmount) {
        this.totalWeight = totalWeight;
        this.totalFee = totalFee;
        this.vatPercent = vatPercent;
        this.vatAmount = vatAmount;
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(BigDecimal vatPercent) {
        this.vatPercent = vatPercent;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /** @return danh sách chi tiết cước từng dòng. */
    public List<QuoteLine> getLines() {
        return lines;
    }

    public void setLines(List<QuoteLine> lines) {
        this.lines = lines;
    }

    /**
     * Chi tiết cước của một dòng mặt hàng.
     */
    public static class QuoteLine {

        @SerializedName("hangHoaId")
        private int productId;

        @SerializedName("soLuong")
        private int quantity;

        /** Đơn giá cước của một đơn vị hàng, đã làm tròn (đ). */
        @SerializedName("donGia")
        private BigDecimal unitPrice;

        /** Khối lượng của một đơn vị hàng (kg). */
        @SerializedName("khoiLuong")
        private BigDecimal weight;

        public QuoteLine() {
            // Dung cho Gson
        }

        public QuoteLine(int productId, int quantity, BigDecimal unitPrice,
                         BigDecimal weight) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.weight = weight;
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

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }
    }
}
