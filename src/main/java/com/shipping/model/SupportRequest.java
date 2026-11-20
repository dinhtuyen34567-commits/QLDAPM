/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportRequest.java
 * Lần cuối cập nhật: 04/11/2026 11:15
 * 
 * Tổng quát chức năng của lớp:
 * Thực thể đại diện cho yêu cầu khiếu nại hoặc hỗ trợ khách hàng gắn liền với một đơn hàng cụ thể.
 */
package com.shipping.model;

/**
 * Entity ánh xạ bản ghi tblYeuCauHoTro.
 */
public class SupportRequest {

    private long id;
    private String maYeuCau;
    private String noiDung;
    private String trangThai;
    private String phanHoi;
    private long khachHangId;
    private long donHangId;
    private Long nhanVienId;

    /**
     * Phương thức SupportRequest: dùng để khởi tạo đối tượng SupportRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportRequest() {
    }

    /**
     * Phương thức SupportRequest: dùng để khởi tạo đối tượng SupportRequest với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportRequest(long id, String maYeuCau, String noiDung, String trangThai,
                          String phanHoi, long khachHangId, long donHangId, Long nhanVienId) {
        this.id = id;
        this.maYeuCau = maYeuCau;
        this.noiDung = noiDung;
        this.trangThai = trangThai;
        this.phanHoi = phanHoi;
        this.khachHangId = khachHangId;
        this.donHangId = donHangId;
        this.nhanVienId = nhanVienId;
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

    /**
     * Phương thức getKhachHangId: dùng để lấy giá trị của thuộc tính KhachHangId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public long getKhachHangId() {
        return khachHangId;
    }

    /**
     * Phương thức setKhachHangId: dùng để gán hoặc cập nhật giá trị cho thuộc tính KhachHangId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setKhachHangId(long khachHangId) {
        this.khachHangId = khachHangId;
    }

    /**
     * Phương thức getDonHangId: dùng để lấy giá trị của thuộc tính DonHangId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public long getDonHangId() {
        return donHangId;
    }

    /**
     * Phương thức setDonHangId: dùng để gán hoặc cập nhật giá trị cho thuộc tính DonHangId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setDonHangId(long donHangId) {
        this.donHangId = donHangId;
    }

    /**
     * Phương thức getNhanVienId: dùng để lấy giá trị của thuộc tính NhanVienId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Long getNhanVienId() {
        return nhanVienId;
    }

    /**
     * Phương thức setNhanVienId: dùng để gán hoặc cập nhật giá trị cho thuộc tính NhanVienId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setNhanVienId(Long nhanVienId) {
        this.nhanVienId = nhanVienId;
    }
}
