/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: StaffDTO.java
 * Lần cuối cập nhật: 06/11/2026 11:00
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO đại diện cho thông tin cơ bản của nhân viên CSKH phục vụ hiển thị trên giao diện quản trị.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * DTO trả thông tin nhân viên đang trực (API 7).
 */
public class StaffDTO {

    @SerializedName("id")
    private long id;

    @SerializedName("hoTen")
    private String hoTen;

    @SerializedName("viTri")
    private String viTri;

    /**
     * Phương thức StaffDTO: dùng để khởi tạo đối tượng StaffDTO với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public StaffDTO() {
    }

    /**
     * Phương thức StaffDTO: dùng để khởi tạo đối tượng StaffDTO với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public StaffDTO(long id, String hoTen, String viTri) {
        this.id = id;
        this.hoTen = hoTen;
        this.viTri = viTri;
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
     * Phương thức getHoTen: dùng để lấy giá trị của thuộc tính HoTen, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getHoTen() {
        return hoTen;
    }

    /**
     * Phương thức setHoTen: dùng để gán hoặc cập nhật giá trị cho thuộc tính HoTen, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    /**
     * Phương thức getViTri: dùng để lấy giá trị của thuộc tính ViTri, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getViTri() {
        return viTri;
    }

    /**
     * Phương thức setViTri: dùng để gán hoặc cập nhật giá trị cho thuộc tính ViTri, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setViTri(String viTri) {
        this.viTri = viTri;
    }
}
