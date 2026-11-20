/**
 * Người code: Nguyễn Hữu Hiếu (UC-01)
 * Tên file: Customer.java
 * Lần cuối cập nhật: 30/09/2026 15:00
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể (Entity) đại diện cho thông tin khách hàng (người gửi), phục vụ lưu trữ và truyền tải dữ liệu khách hàng trong hệ thống.
 */
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

    /**
     * Phương thức Customer: dùng để khởi tạo đối tượng Customer với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Customer() {
        // Dung cho Jackson/Gson va test
    }

    /**
     * Phương thức Customer: dùng để khởi tạo đối tượng Customer với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public Customer(int id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    /**
     * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public int getId() {
        return id;
    }

    /**
     * Phương thức setId: dùng để gán hoặc cập nhật giá trị cho thuộc tính Id, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Phương thức getFullName: dùng để lấy giá trị của thuộc tính FullName, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return họ tên hiển thị ở ô [Tên người gửi].
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Phương thức setFullName: dùng để gán hoặc cập nhật giá trị cho thuộc tính FullName, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Phương thức getPhone: dùng để lấy giá trị của thuộc tính Phone, phục vụ việc truy xuất dữ liệu của đối tượng.
     * @return số điện thoại hiển thị ở ô [SĐT người gửi].
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Phương thức setPhone: dùng để gán hoặc cập nhật giá trị cho thuộc tính Phone, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
