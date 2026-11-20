/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: UpdateSupportRequest.java
 * Lần cuối cập nhật: 05/11/2026 09:00
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO tiếp nhận dữ liệu cập nhật trạng thái và phản hồi xử lý yêu cầu khiếu nại từ nhân viên CSKH.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * DTO nhận cập nhật yêu cầu khiếu nại từ nhân viên (API 10).
 */
public class UpdateSupportRequest {

    @SerializedName("trangThai")
    private String trangThai;

    @SerializedName("phanHoi")
    private String phanHoi;

    /**
     * Phương thức UpdateSupportRequest: dùng để khởi tạo đối tượng UpdateSupportRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public UpdateSupportRequest() {
    }

    /**
     * Phương thức UpdateSupportRequest: dùng để khởi tạo đối tượng UpdateSupportRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public UpdateSupportRequest(String trangThai, String phanHoi) {
        this.trangThai = trangThai;
        this.phanHoi = phanHoi;
    }

    /**
     * Phương thức getTrangThai: dùng để lấy giá trị của thuộc tính TrangThai, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getTrangThai() {
        return trangThai;
    }

    /**
     * Phương thức setTrangThai: dùng để gán hoặc cập nhật giá trị cho thuộc tính TrangThai, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Phương thức getPhanHoi: dùng để lấy giá trị của thuộc tính PhanHoi, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getPhanHoi() {
        return phanHoi;
    }

    /**
     * Phương thức setPhanHoi: dùng để gán hoặc cập nhật giá trị cho thuộc tính PhanHoi, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }
}
