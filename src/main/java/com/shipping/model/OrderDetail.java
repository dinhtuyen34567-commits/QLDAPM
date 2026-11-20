/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: OrderDetail.java
 * Lần cuối cập nhật: 01/10/2026 11:30
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể đại diện cho chi tiết mặt hàng trong đơn hàng vận chuyển (mã mặt hàng, tên mặt hàng, khối lượng, số lượng, đơn giá).
 */
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

    /**
     * Phương thức OrderDetail: dùng để khởi tạo đối tượng OrderDetail với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public OrderDetail() {
        // Dung cho Jackson/Gson va test
    }

    /**
     * Phương thức OrderDetail: dùng để khởi tạo đối tượng OrderDetail với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public OrderDetail(long orderId, int productId, int quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
     * @return đơn giá cước của một đơn vị hàng (đ).
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
}
