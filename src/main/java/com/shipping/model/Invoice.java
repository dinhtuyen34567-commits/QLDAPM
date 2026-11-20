/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: Invoice.java
 * Lần cuối cập nhật: 01/10/2026 15:45
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể đại diện cho hóa đơn cước vận chuyển gắn liền với đơn hàng, phục vụ hạch toán tài chính và theo dõi thanh toán.
 */
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

    /**
     * Phương thức Invoice: dùng để khởi tạo đối tượng Invoice với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Invoice() {
        // Dung cho Jackson/Gson va test
    }

    /**
     * Phương thức Invoice: dùng để khởi tạo đối tượng Invoice với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Invoice(long id, String code, long orderId, BigDecimal totalAmount,
                   BigDecimal vatPercent, String paymentMethod) {
        this.id = id;
        this.code = code;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.vatPercent = vatPercent;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public long getId() {
        return id;
    }

    /**
     * Phương thức setId: dùng để gán hoặc cập nhật giá trị cho thuộc tính Id, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Phương thức getCode: dùng để lấy giá trị của thuộc tính Code, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getCode() {
        return code;
    }

    /**
     * Phương thức setCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính Code, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Phương thức getOrderId: dùng để lấy giá trị của thuộc tính OrderId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Phương thức setOrderId: dùng để gán hoặc cập nhật giá trị cho thuộc tính OrderId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
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
     * Phương thức getPaymentMethod: dùng để lấy giá trị của thuộc tính PaymentMethod, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Phương thức setPaymentMethod: dùng để gán hoặc cập nhật giá trị cho thuộc tính PaymentMethod, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
