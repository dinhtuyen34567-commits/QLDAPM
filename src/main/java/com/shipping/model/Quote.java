/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: Quote.java
 * Lần cuối cập nhật: 02/10/2026 09:15
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng truyền tải dữ liệu báo giá cước vận chuyển (chi phí khối lượng, chi phí khoảng cách, tổng cước tạm tính).
 */
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

    /**
     * Phương thức Quote: dùng để khởi tạo đối tượng Quote với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Quote() {
        // Dung cho Gson
    }

    /**
     * Phương thức Quote: dùng để khởi tạo đối tượng Quote với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Quote(BigDecimal totalWeight, BigDecimal totalFee, BigDecimal vatPercent,
                 BigDecimal vatAmount, BigDecimal totalAmount) {
        this.totalWeight = totalWeight;
        this.totalFee = totalFee;
        this.vatPercent = vatPercent;
        this.vatAmount = vatAmount;
        this.totalAmount = totalAmount;
    }

    /**
     * Phương thức getTotalWeight: dùng để lấy giá trị của thuộc tính TotalWeight, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    /**
     * Phương thức setTotalWeight: dùng để gán hoặc cập nhật giá trị cho thuộc tính TotalWeight, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * Phương thức getTotalFee: dùng để lấy giá trị của thuộc tính TotalFee, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public BigDecimal getTotalFee() {
        return totalFee;
    }

    /**
     * Phương thức setTotalFee: dùng để gán hoặc cập nhật giá trị cho thuộc tính TotalFee, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * Phương thức getVatPercent: dùng để lấy giá trị của thuộc tính VatPercent, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public BigDecimal getVatPercent() {
        return vatPercent;
    }

    /**
     * Phương thức setVatPercent: dùng để gán hoặc cập nhật giá trị cho thuộc tính VatPercent, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setVatPercent(BigDecimal vatPercent) {
        this.vatPercent = vatPercent;
    }

    /**
     * Phương thức getVatAmount: dùng để lấy giá trị của thuộc tính VatAmount, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    /**
     * Phương thức setVatAmount: dùng để gán hoặc cập nhật giá trị cho thuộc tính VatAmount, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    /**
     * Phương thức getTotalAmount: dùng để lấy giá trị của thuộc tính TotalAmount, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Phương thức setTotalAmount: dùng để gán hoặc cập nhật giá trị cho thuộc tính TotalAmount, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Phương thức getLines: dùng để lấy giá trị của thuộc tính Lines, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return danh sách chi tiết cước từng dòng.
     */
    public List<QuoteLine> getLines() {
        return lines;
    }

    /**
     * Phương thức setLines: dùng để gán hoặc cập nhật giá trị cho thuộc tính Lines, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
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

        /**
         * Phương thức QuoteLine: dùng để khởi tạo đối tượng QuoteLine với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public QuoteLine() {
            // Dung cho Gson
        }

        /**
         * Phương thức QuoteLine: dùng để khởi tạo đối tượng QuoteLine với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public QuoteLine(int productId, int quantity, BigDecimal unitPrice,
                         BigDecimal weight) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.weight = weight;
        }

        /**
         * Phương thức getProductId: dùng để lấy giá trị của thuộc tính ProductId, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public int getProductId() {
            return productId;
        }

        /**
         * Phương thức setProductId: dùng để gán hoặc cập nhật giá trị cho thuộc tính ProductId, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setProductId(int productId) {
            this.productId = productId;
        }

        /**
         * Phương thức getQuantity: dùng để lấy giá trị của thuộc tính Quantity, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public int getQuantity() {
            return quantity;
        }

        /**
         * Phương thức setQuantity: dùng để gán hoặc cập nhật giá trị cho thuộc tính Quantity, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        /**
         * Phương thức getUnitPrice: dùng để lấy giá trị của thuộc tính UnitPrice, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        /**
         * Phương thức setUnitPrice: dùng để gán hoặc cập nhật giá trị cho thuộc tính UnitPrice, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        /**
         * Phương thức getWeight: dùng để lấy giá trị của thuộc tính Weight, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getWeight() {
            return weight;
        }

        /**
         * Phương thức setWeight: dùng để gán hoặc cập nhật giá trị cho thuộc tính Weight, phục vụ việc đồng bộ dữ liệu của đối tượng.
         */
        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }
    }
}
