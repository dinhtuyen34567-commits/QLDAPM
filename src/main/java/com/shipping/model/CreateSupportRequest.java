/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: CreateSupportRequest.java
 * Lần cuối cập nhật: 04/11/2026 14:30
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO tiếp nhận dữ liệu tạo mới yêu cầu khiếu nại từ khách hàng (mã vận đơn, nội dung khiếu nại).
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * DTO nhận yêu cầu tạo khiếu nại từ khách hàng (API 6).
 */
public class CreateSupportRequest {

    @SerializedName("maVanDon")
    private String maVanDon;

    @SerializedName("noiDung")
    private String noiDung;

    /**
     * Phương thức CreateSupportRequest: dùng để khởi tạo đối tượng CreateSupportRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public CreateSupportRequest() {
    }

    /**
     * Phương thức CreateSupportRequest: dùng để khởi tạo đối tượng CreateSupportRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public CreateSupportRequest(String maVanDon, String noiDung) {
        this.maVanDon = maVanDon;
        this.noiDung = noiDung;
    }

    /**
     * Phương thức getMaVanDon: dùng để lấy giá trị của thuộc tính MaVanDon, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getMaVanDon() {
        return maVanDon;
    }

    /**
     * Phương thức setMaVanDon: dùng để gán hoặc cập nhật giá trị cho thuộc tính MaVanDon, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setMaVanDon(String maVanDon) {
        this.maVanDon = maVanDon;
    }

    /**
     * Phương thức getNoiDung: dùng để lấy giá trị của thuộc tính NoiDung, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getNoiDung() {
        return noiDung;
    }

    /**
     * Phương thức setNoiDung: dùng để gán hoặc cập nhật giá trị cho thuộc tính NoiDung, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
