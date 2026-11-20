/**
 * Người code: Nguyễn Đình Tuyển (UC-03)
 * Tên file: SupportDetailDTO.java
 * Lần cuối cập nhật: 05/11/2026 14:00
 * 
 * Tổng quát chức năng của lớp:
 * Đối tượng DTO tổng hợp toàn diện chi tiết khiếu nại, thông tin khách hàng và thông tin đơn hàng bị khiếu nại.
 */
package com.shipping.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO chi tiết yêu cầu hỗ trợ (API 9 & BR-27).
 * Gồm 3 khối: Thông tin yêu cầu, Thông tin khách hàng, Chi tiết đơn hàng bị khiếu nại.
 */
public class SupportDetailDTO {

    @SerializedName("id")
    private long id;

    @SerializedName("maYeuCau")
    private String maYeuCau;

    @SerializedName("noiDung")
    private String noiDung;

    @SerializedName("trangThai")
    private String trangThai;

    @SerializedName("phanHoi")
    private String phanHoi;

    @SerializedName("nhanVienId")
    private Long nhanVienId;

    @SerializedName("tenNhanVien")
    private String tenNhanVien;

    @SerializedName("khachHang")
    private CustomerInfo khachHang;

    @SerializedName("donHang")
    private OrderInfo donHang;

    public static class CustomerInfo {
        @SerializedName("id")
        private long id;
        @SerializedName("hoTen")
        private String hoTen;
        @SerializedName("sdt")
        private String sdt;
        @SerializedName("email")
        private String email;

        /**
         * Phương thức CustomerInfo: dùng để khởi tạo đối tượng CustomerInfo với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public CustomerInfo() {}

        /**
         * Phương thức CustomerInfo: dùng để khởi tạo đối tượng CustomerInfo với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public CustomerInfo(long id, String hoTen, String sdt, String email) {
            this.id = id;
            this.hoTen = hoTen;
            this.sdt = sdt;
            this.email = email;
        }

        /**
         * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public long getId() { return id; }
        /**
         * Phương thức getHoTen: dùng để lấy giá trị của thuộc tính HoTen, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getHoTen() { return hoTen; }
        /**
         * Phương thức getSdt: dùng để lấy giá trị của thuộc tính Sdt, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getSdt() { return sdt; }
        /**
         * Phương thức getEmail: dùng để lấy giá trị của thuộc tính Email, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getEmail() { return email; }
    }

    public static class OrderItemInfo {
        @SerializedName("tenHang")
        private String tenHang;
        @SerializedName("soLuong")
        private int soLuong;
        @SerializedName("donGia")
        private BigDecimal donGia;

        /**
         * Phương thức OrderItemInfo: dùng để khởi tạo đối tượng OrderItemInfo với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public OrderItemInfo() {}

        /**
         * Phương thức OrderItemInfo: dùng để khởi tạo đối tượng OrderItemInfo với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public OrderItemInfo(String tenHang, int soLuong, BigDecimal donGia) {
            this.tenHang = tenHang;
            this.soLuong = soLuong;
            this.donGia = donGia;
        }

        /**
         * Phương thức getTenHang: dùng để lấy giá trị của thuộc tính TenHang, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getTenHang() { return tenHang; }
        /**
         * Phương thức getSoLuong: dùng để lấy giá trị của thuộc tính SoLuong, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public int getSoLuong() { return soLuong; }
        /**
         * Phương thức getDonGia: dùng để lấy giá trị của thuộc tính DonGia, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getDonGia() { return donGia; }
    }

    public static class OrderInfo {
        @SerializedName("id")
        private long id;
        @SerializedName("maVanDon")
        private String maVanDon;
        @SerializedName("trangThai")
        private String trangThai;
        @SerializedName("tenNguoiNhan")
        private String tenNguoiNhan;
        @SerializedName("sdtNguoiNhan")
        private String sdtNguoiNhan;
        @SerializedName("diaChiGiao")
        private String diaChiGiao;
        @SerializedName("tongKhoiLuong")
        private BigDecimal tongKhoiLuong;
        @SerializedName("items")
        private List<OrderItemInfo> items;

        /**
         * Phương thức OrderInfo: dùng để khởi tạo đối tượng OrderInfo với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public OrderInfo() {}

        /**
         * Phương thức OrderInfo: dùng để khởi tạo đối tượng OrderInfo với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
         */
        public OrderInfo(long id, String maVanDon, String trangThai, String tenNguoiNhan,
                         String sdtNguoiNhan, String diaChiGiao, BigDecimal tongKhoiLuong,
                         List<OrderItemInfo> items) {
            this.id = id;
            this.maVanDon = maVanDon;
            this.trangThai = trangThai;
            this.tenNguoiNhan = tenNguoiNhan;
            this.sdtNguoiNhan = sdtNguoiNhan;
            this.diaChiGiao = diaChiGiao;
            this.tongKhoiLuong = tongKhoiLuong;
            this.items = items;
        }

        /**
         * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public long getId() { return id; }
        /**
         * Phương thức getMaVanDon: dùng để lấy giá trị của thuộc tính MaVanDon, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getMaVanDon() { return maVanDon; }
        /**
         * Phương thức getTrangThai: dùng để lấy giá trị của thuộc tính TrangThai, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getTrangThai() { return trangThai; }
        /**
         * Phương thức getTenNguoiNhan: dùng để lấy giá trị của thuộc tính TenNguoiNhan, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getTenNguoiNhan() { return tenNguoiNhan; }
        /**
         * Phương thức getSdtNguoiNhan: dùng để lấy giá trị của thuộc tính SdtNguoiNhan, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getSdtNguoiNhan() { return sdtNguoiNhan; }
        /**
         * Phương thức getDiaChiGiao: dùng để lấy giá trị của thuộc tính DiaChiGiao, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public String getDiaChiGiao() { return diaChiGiao; }
        /**
         * Phương thức getTongKhoiLuong: dùng để lấy giá trị của thuộc tính TongKhoiLuong, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public BigDecimal getTongKhoiLuong() { return tongKhoiLuong; }
        /**
         * Phương thức getItems: dùng để lấy giá trị của thuộc tính Items, phục vụ việc truy xuất dữ liệu của đối tượng.
         */
        public List<OrderItemInfo> getItems() { return items; }
    }

    /**
     * Phương thức SupportDetailDTO: dùng để khởi tạo đối tượng SupportDetailDTO với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportDetailDTO() {
    }

    /**
     * Phương thức SupportDetailDTO: dùng để khởi tạo đối tượng SupportDetailDTO với các thông số tương ứng, phục vụ việc đóng gói và chuyển giao dữ liệu trong phân hệ.
     */
    public SupportDetailDTO(long id, String maYeuCau, String noiDung, String trangThai,
                            String phanHoi, Long nhanVienId, String tenNhanVien,
                            CustomerInfo khachHang, OrderInfo donHang) {
        this.id = id;
        this.maYeuCau = maYeuCau;
        this.noiDung = noiDung;
        this.trangThai = trangThai;
        this.phanHoi = phanHoi;
        this.nhanVienId = nhanVienId;
        this.tenNhanVien = tenNhanVien;
        this.khachHang = khachHang;
        this.donHang = donHang;
    }

    /**
     * Phương thức getId: dùng để lấy giá trị của thuộc tính Id, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public long getId() { return id; }
    /**
     * Phương thức setId: dùng để gán hoặc cập nhật giá trị cho thuộc tính Id, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setId(long id) { this.id = id; }
    /**
     * Phương thức getMaYeuCau: dùng để lấy giá trị của thuộc tính MaYeuCau, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getMaYeuCau() { return maYeuCau; }
    /**
     * Phương thức setMaYeuCau: dùng để gán hoặc cập nhật giá trị cho thuộc tính MaYeuCau, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }
    /**
     * Phương thức getNoiDung: dùng để lấy giá trị của thuộc tính NoiDung, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getNoiDung() { return noiDung; }
    /**
     * Phương thức setNoiDung: dùng để gán hoặc cập nhật giá trị cho thuộc tính NoiDung, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    /**
     * Phương thức getTrangThai: dùng để lấy giá trị của thuộc tính TrangThai, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getTrangThai() { return trangThai; }
    /**
     * Phương thức setTrangThai: dùng để gán hoặc cập nhật giá trị cho thuộc tính TrangThai, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    /**
     * Phương thức getPhanHoi: dùng để lấy giá trị của thuộc tính PhanHoi, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getPhanHoi() { return phanHoi; }
    /**
     * Phương thức setPhanHoi: dùng để gán hoặc cập nhật giá trị cho thuộc tính PhanHoi, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setPhanHoi(String phanHoi) { this.phanHoi = phanHoi; }
    /**
     * Phương thức getNhanVienId: dùng để lấy giá trị của thuộc tính NhanVienId, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public Long getNhanVienId() { return nhanVienId; }
    /**
     * Phương thức setNhanVienId: dùng để gán hoặc cập nhật giá trị cho thuộc tính NhanVienId, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setNhanVienId(Long nhanVienId) { this.nhanVienId = nhanVienId; }
    /**
     * Phương thức getTenNhanVien: dùng để lấy giá trị của thuộc tính TenNhanVien, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public String getTenNhanVien() { return tenNhanVien; }
    /**
     * Phương thức setTenNhanVien: dùng để gán hoặc cập nhật giá trị cho thuộc tính TenNhanVien, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    /**
     * Phương thức getKhachHang: dùng để lấy giá trị của thuộc tính KhachHang, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public CustomerInfo getKhachHang() { return khachHang; }
    /**
     * Phương thức setKhachHang: dùng để gán hoặc cập nhật giá trị cho thuộc tính KhachHang, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setKhachHang(CustomerInfo khachHang) { this.khachHang = khachHang; }
    /**
     * Phương thức getDonHang: dùng để lấy giá trị của thuộc tính DonHang, phục vụ việc truy xuất dữ liệu của đối tượng.
     */
    public OrderInfo getDonHang() { return donHang; }
    /**
     * Phương thức setDonHang: dùng để gán hoặc cập nhật giá trị cho thuộc tính DonHang, phục vụ việc đồng bộ dữ liệu của đối tượng.
     */
    public void setDonHang(OrderInfo donHang) { this.donHang = donHang; }
}
