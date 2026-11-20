/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: CreateOrderRequest.java
 * Lần cuối cập nhật: 03/10/2026 14:30
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO đóng gói toàn bộ dữ liệu yêu cầu tạo đơn hàng gửi từ phía giao diện người dùng lên máy chủ.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Body của {@code POST /api/v1/don-hang}.
 *
 * <p>Body <b>không</b> chứa thông tin người gửi: server lấy {@code khachHangId}
 * từ phiên đăng nhập và bỏ qua mọi giá trị người gửi do client gửi (BR-01).</p>
 */
public class CreateOrderRequest {

    @SerializedName("tenNguoiNhan")
    private String receiverName;

    @SerializedName("sdtNguoiNhan")
    private String receiverPhone;

    @SerializedName("diaChiGiao")
    private String deliveryAddress;

    @SerializedName("items")
    private List<ItemRequest> items = new ArrayList<>();

    /**
     * Phương thức CreateOrderRequest: dùng để khởi tạo đối tượng CreateOrderRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public CreateOrderRequest() {
        // Dung cho Gson
    }

    /**
     * Phương thức CreateOrderRequest: dùng để khởi tạo đối tượng CreateOrderRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public CreateOrderRequest(String receiverName, String receiverPhone,
                              String deliveryAddress, List<ItemRequest> items) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
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
     * Phương thức getItems: dùng để lấy giá trị của thuộc tính Items, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return danh sách mặt hàng; có thể rỗng để kích hoạt lỗi 9.1.
     */
    public List<ItemRequest> getItems() {
        return items;
    }

    /**
     * Phương thức setItems: dùng để gán hoặc cập nhật giá trị cho thuộc tính Items, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setItems(List<ItemRequest> items) {
        this.items = items;
    }
}
