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

    public CreateOrderRequest() {
        // Dung cho Gson
    }

    public CreateOrderRequest(String receiverName, String receiverPhone,
                              String deliveryAddress, List<ItemRequest> items) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /** @return danh sách mặt hàng; có thể rỗng để kích hoạt lỗi 9.1. */
    public List<ItemRequest> getItems() {
        return items;
    }

    public void setItems(List<ItemRequest> items) {
        this.items = items;
    }
}
