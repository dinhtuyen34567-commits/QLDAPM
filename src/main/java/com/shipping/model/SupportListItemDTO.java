/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportListItemDTO.java
 * Lần cuối cập nhật: 05/11/2026 11:30
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO chứa dữ liệu tóm tắt của yêu cầu khiếu nại phục vụ hiển thị danh sách trong màn hình quản lý CSKH.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;

/**
 * DTO đại diện cho một dòng trong danh sách yêu cầu hỗ trợ (API 8).
 */
public class SupportListItemDTO {

    @SerializedName("id")
    private long id;

    @SerializedName("maYeuCau")
    private String maYeuCau;

    @SerializedName("trangThai")
    private String trangThai;

    @SerializedName("maVanDon")
    private String maVanDon;

    @SerializedName("hoTenKhachHang")
    private String hoTenKhachHang;

    @SerializedName("thoiGianTao")
    private String thoiGianTao;

    /**
     * Phương thức SupportListItemDTO: dùng để khởi tạo đối tượng SupportListItemDTO với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportListItemDTO() {
    }

    /**
     * Phương thức SupportListItemDTO: dùng để khởi tạo đối tượng SupportListItemDTO với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportListItemDTO(long id, String maYeuCau, String trangThai,
                              String maVanDon, String hoTenKhachHang, String thoiGianTao) {
        this.id = id;
        this.maYeuCau = maYeuCau;
        this.trangThai = trangThai;
        this.maVanDon = maVanDon;
        this.hoTenKhachHang = hoTenKhachHang;
        this.thoiGianTao = thoiGianTao;
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
     * Phương thức getMaYeuCau: dùng để lấy giá trị của thuộc tính MaYeuCau, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getMaYeuCau() {
        return maYeuCau;
    }

    /**
     * Phương thức setMaYeuCau: dùng để gán hoặc cập nhật giá trị cho thuộc tính MaYeuCau, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau;
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
     * Phương thức getHoTenKhachHang: dùng để lấy giá trị của thuộc tính HoTenKhachHang, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getHoTenKhachHang() {
        return hoTenKhachHang;
    }

    /**
     * Phương thức setHoTenKhachHang: dùng để gán hoặc cập nhật giá trị cho thuộc tính HoTenKhachHang, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setHoTenKhachHang(String hoTenKhachHang) {
        this.hoTenKhachHang = hoTenKhachHang;
    }

    /**
     * Phương thức getThoiGianTao: dùng để lấy giá trị của thuộc tính ThoiGianTao, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getThoiGianTao() {
        return thoiGianTao;
    }

    /**
     * Phương thức setThoiGianTao: dùng để gán hoặc cập nhật giá trị cho thuộc tính ThoiGianTao, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setThoiGianTao(String thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }
}
