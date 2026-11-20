/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: ItemRequest.java
 * Lần cuối cập nhật: 03/10/2026 10:00
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO tiếp nhận thông tin từng mặt hàng và số lượng do khách hàng lựa chọn từ giao diện tạo đơn hàng.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * Một mặt hàng trong request tạo đơn hoặc tính cước.
 *
 * <p>Đơn giá không nằm trong DTO này: server tự xác định đơn giá từ CSDL
 * (mục 3.4 tài liệu thiết kế), client không được gửi lên.</p>
 */
public class ItemRequest {

    @SerializedName("hangHoaId")
    private int productId;

    @SerializedName("soLuong")
    private int quantity;

    /**
     * Phương thức ItemRequest: dùng để khởi tạo đối tượng ItemRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public ItemRequest() {
        // Dung cho Gson
    }

    /**
     * Phương thức ItemRequest: dùng để khởi tạo đối tượng ItemRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public ItemRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
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
}
