package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * Khách hàng đang đăng nhập ({@code tblNguoiDung} + {@code tblKhachHang}).
 *
 * <p>Khách hàng là lớp con của người dùng trong CSDL: cùng một {@code id}
 * nhưng chỉ tồn tại trong {@code tblKhachHang} thì mới là khách hàng.</p>
 */
public class Customer {

    @SerializedName("id")
    private int id;

    @SerializedName("hoTen")
    private String fullName;

    @SerializedName("sdt")
    private String phone;

    public Customer() {
        // Dung cho Jackson/Gson va test
    }

    public Customer(int id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** @return họ tên hiển thị ở ô [Tên người gửi]. */
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** @return số điện thoại hiển thị ở ô [SĐT người gửi]. */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
