/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: CreateResult.java
 * Lần cuối cập nhật: 04/10/2026 09:30
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO trả về kết quả sau khi tạo đơn hàng thành công, bao gồm mã vận đơn, mã hóa đơn và tổng chi phí.
 */
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

    /**
     * Phương thức CreateResult: dùng để khởi tạo đối tượng CreateResult với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public CreateResult() {
        // Dung cho Gson
    }

    /**
     * Phương thức CreateResult: dùng để khởi tạo đối tượng CreateResult với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public CreateResult(long orderId, String trackingCode, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.trackingCode = trackingCode;
        this.totalAmount = totalAmount;
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
     * Phương thức getTrackingCode: dùng để lấy giá trị của thuộc tính TrackingCode, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getTrackingCode() {
        return trackingCode;
    }

    /**
     * Phương thức setTrackingCode: dùng để gán hoặc cập nhật giá trị cho thuộc tính TrackingCode, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
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
}
