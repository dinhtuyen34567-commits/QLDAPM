/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: Order.java
 * Lần cuối cập nhật: 02/10/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể chính đại diện cho đơn hàng vận chuyển, chứa thông tin người nhận, địa chỉ giao hàng, tổng khối lượng, trạng thái và mã vận đơn.
 */
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

    /**
     * Phương thức Order: dùng để khởi tạo đối tượng Order với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Order() {
        // Dung cho Jackson/Gson va test
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
     * Phương thức getCreatedAt: dùng để lấy giá trị của thuộc tính CreatedAt, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Phương thức setCreatedAt: dùng để gán hoặc cập nhật giá trị cho thuộc tính CreatedAt, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Phương thức getStatus: dùng để lấy giá trị của thuộc tính Status, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Phương thức setStatus: dùng để gán hoặc cập nhật giá trị cho thuộc tính Status, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Phương thức getTotalWeight: dùng để lấy giá trị của thuộc tính TotalWeight, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return tổng khối lượng (kg), do server tự tính (BR-06).
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
     * Phương thức getCustomerId: dùng để lấy giá trị của thuộc tính CustomerId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Phương thức setCustomerId: dùng để gán hoặc cập nhật giá trị cho thuộc tính CustomerId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Phương thức getReceiverName: dùng để lấy giá trị của thuộc tính ReceiverName, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * Phương thức setReceiverName: dùng để gán hoặc cập nhật giá trị cho thuộc tính ReceiverName, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * Phương thức getReceiverPhone: dùng để lấy giá trị của thuộc tính ReceiverPhone, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * Phương thức setReceiverPhone: dùng để gán hoặc cập nhật giá trị cho thuộc tính ReceiverPhone, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    /**
     * Phương thức getDeliveryAddress: dùng để lấy giá trị của thuộc tính DeliveryAddress, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Phương thức setDeliveryAddress: dùng để gán hoặc cập nhật giá trị cho thuộc tính DeliveryAddress, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Phương thức getTripId: dùng để lấy giá trị của thuộc tính TripId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Integer getTripId() {
        return tripId;
    }

    /**
     * Phương thức setTripId: dùng để gán hoặc cập nhật giá trị cho thuộc tính TripId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    /**
     * Phương thức getIdempotencyKey: dùng để lấy giá trị của thuộc tính IdempotencyKey, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    /**
     * Phương thức setIdempotencyKey: dùng để gán hoặc cập nhật giá trị cho thuộc tính IdempotencyKey, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
