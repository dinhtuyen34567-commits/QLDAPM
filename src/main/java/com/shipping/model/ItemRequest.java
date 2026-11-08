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

    public ItemRequest() {
        // Dung cho Gson
    }

    public ItemRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
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
}
