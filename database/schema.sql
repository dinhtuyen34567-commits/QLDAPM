-- =====================================================================
-- CSDL HỆ THỐNG VẬN CHUYỂN 
-- =====================================================================
CREATE DATABASE IF NOT EXISTS vanchuyen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vanchuyen;

-- ---------------------------------------------------------------------
-- 1. NGƯỜI DÙNG và 3 lớp con (Nhân viên, Khách hàng, Tài xế)
-- ---------------------------------------------------------------------
CREATE TABLE tblNguoiDung (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    maNguoiDung   VARCHAR(20)  NOT NULL UNIQUE,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,           -- lưu mã băm
    hoTen         VARCHAR(100) NOT NULL,
    email         VARCHAR(100) UNIQUE,
    sdt           VARCHAR(11)  NOT NULL,
    CONSTRAINT chk_nd_sdt CHECK (sdt REGEXP '^[0-9]{10,11}$')
);

CREATE TABLE tblNhanVien (
    nguoiDungId   INT PRIMARY KEY,                 --  kế thừa NguoiDung
    viTri         VARCHAR(50) NOT NULL,
    FOREIGN KEY (nguoiDungId) REFERENCES tblNguoiDung(id) ON DELETE CASCADE
);

CREATE TABLE tblKhachHang (
    nguoiDungId   INT PRIMARY KEY,                 --  kế thừa NguoiDung
    FOREIGN KEY (nguoiDungId) REFERENCES tblNguoiDung(id) ON DELETE CASCADE
);

CREATE TABLE tblTaiXe (
    nguoiDungId    INT PRIMARY KEY,                --  kế thừa NguoiDung
    bangLai        VARCHAR(20) NOT NULL UNIQUE,
    trangThaiTaiXe VARCHAR(20) NOT NULL DEFAULT 'SAN_SANG',
    FOREIGN KEY (nguoiDungId) REFERENCES tblNguoiDung(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 2. XE TẢI, KHO BÃI và các thành phần
-- ---------------------------------------------------------------------
CREATE TABLE tblXeTai (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    bienSo       VARCHAR(15)   NOT NULL UNIQUE,
    taiTrong     DECIMAL(10,2) NOT NULL,           -- kg
    trangThaiXe  VARCHAR(20)   NOT NULL DEFAULT 'SAN_SANG',
    CONSTRAINT chk_xe CHECK (taiTrong > 0)
);

CREATE TABLE tblNhatKyToaDo (                      -- thành phần của Xe tải 
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    xeTaiId     INT NOT NULL,                      
    viDo        DECIMAL(9,6) NOT NULL,
    kinhDo      DECIMAL(9,6) NOT NULL,
    thoiGian    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  --  để lấy getViTriHienTai()
    FOREIGN KEY (xeTaiId) REFERENCES tblXeTai(id) ON DELETE CASCADE,
    INDEX idx_nktd_xe_tg (xeTaiId, thoiGian)
);

CREATE TABLE tblKhoBai (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    maKho   VARCHAR(20)  NOT NULL UNIQUE,
    tenKho  VARCHAR(100) NOT NULL,
    diaChi  VARCHAR(255) NOT NULL
);

CREATE TABLE tblViTriLuuKho (                      -- thành phần của Kho bãi 
    id        INT AUTO_INCREMENT PRIMARY KEY,
    khoId     INT NOT NULL,                        
    maViTri   VARCHAR(20) NOT NULL,               
    sucChua   DECIMAL(10,2) NOT NULL,
    UNIQUE (khoId, maViTri),
    FOREIGN KEY (khoId) REFERENCES tblKhoBai(id) ON DELETE CASCADE,
    CONSTRAINT chk_vt CHECK (sucChua > 0)
);

CREATE TABLE tblNhatKyCanhBao (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    maCanhBao   VARCHAR(20) NOT NULL UNIQUE,
    khoId       INT NOT NULL,                      --  cảnh báo của kho
    nhanVienId  INT NULL,                          --  nhân viên phụ trách cảnh báo (NULL khi hệ thống mới ghi)
    mucDo       VARCHAR(20) NOT NULL,
    thoiGian    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (khoId)      REFERENCES tblKhoBai(id),
    FOREIGN KEY (nhanVienId) REFERENCES tblNhanVien(nguoiDungId)
);

-- ---------------------------------------------------------------------
-- 3. CHUYẾN XE và LỘ TRÌNH
-- ---------------------------------------------------------------------
CREATE TABLE tblChuyenXe (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    maChuyen     VARCHAR(20) NOT NULL UNIQUE,
    thoiGianDi   DATETIME    NOT NULL,
    xeTaiId      INT NOT NULL,                     --  Chuyến xe - Xe tải
    taiXeId      INT NOT NULL,                     --  Tài xế - Chuyến xe 
    FOREIGN KEY (xeTaiId) REFERENCES tblXeTai(id),
    FOREIGN KEY (taiXeId) REFERENCES tblTaiXe(nguoiDungId)
);

CREATE TABLE tblLoTrinh (                          -- thành phần của Chuyến xe 
    id           INT AUTO_INCREMENT PRIMARY KEY,
    maLoTrinh    VARCHAR(20)   NOT NULL UNIQUE,
    chuyenXeId   INT NOT NULL UNIQUE,              --  1 chuyến - 1 lộ trình
    khoangCach   DECIMAL(10,2) NOT NULL,           -- km; thời gian dự kiến tính từ đây
    FOREIGN KEY (chuyenXeId) REFERENCES tblChuyenXe(id) ON DELETE CASCADE,
    CONSTRAINT chk_lt CHECK (khoangCach > 0)
);

-- ---------------------------------------------------------------------
-- 4. HÀNG HÓA, ĐƠN HÀNG, CHI TIẾT ĐƠN HÀNG, HÓA ĐƠN
-- ---------------------------------------------------------------------
CREATE TABLE tblHangHoa (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    maHang      VARCHAR(20)  NOT NULL UNIQUE,
    tenHang     VARCHAR(100) NOT NULL,
    loaiHang    VARCHAR(50)  NOT NULL,
    khoiLuong   DECIMAL(10,2) NOT NULL,            --  kg/đơn vị, để tinhTongKhoiLuong()
    donGiaCuoc  DECIMAL(15,2) NOT NULL,            --  đơn giá cước trên 1 kg (VND)
    CONSTRAINT chk_hh CHECK (khoiLuong > 0 AND donGiaCuoc >= 0)
);

CREATE TABLE tblDonHang (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    maVanDon       VARCHAR(20)  NOT NULL UNIQUE,   -- VD: VN99887766
    ngayTao        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    trangThai      VARCHAR(20)  NOT NULL DEFAULT 'CHO_LAY',
    tongKhoiLuong  DECIMAL(12,2) NOT NULL DEFAULT 0,
    khachHangId    INT NOT NULL,                   --  người gửi (tự điền, bị khóa)
    tenNguoiNhan   VARCHAR(100) NOT NULL,          --  bước 3
    sdtNguoiNhan   VARCHAR(11)  NOT NULL,          --  bước 3
    diaChiGiao     VARCHAR(255) NOT NULL,          -- bước 3
    chuyenXeId     INT NULL,                       --  Chuyến xe gồm Đơn hàng; NULL tới khi xếp chuyến
    idempotencyKey VARCHAR(64) NULL,               --  khóa chống tạo trùng đơn khi khách bấm lại sau lỗi mạng
    FOREIGN KEY (khachHangId) REFERENCES tblKhachHang(nguoiDungId),
    FOREIGN KEY (chuyenXeId)  REFERENCES tblChuyenXe(id),
    UNIQUE (khachHangId, idempotencyKey),          
    CONSTRAINT chk_dh_sdt CHECK (sdtNguoiNhan REGEXP '^[0-9]{10,11}$'),   -- ngoại lệ 3.1
    CONSTRAINT chk_dh_kl  CHECK (tongKhoiLuong >= 0)
);

CREATE TABLE tblChiTietDonHang (                   -- lớp liên kết Đơn hàng - Hàng hóa
    donHangId   INT NOT NULL,
    hangHoaId   INT NOT NULL,
    soLuong     INT NOT NULL,
    donGia      DECIMAL(15,2) NOT NULL,
    PRIMARY KEY (donHangId, hangHoaId),
    FOREIGN KEY (donHangId) REFERENCES tblDonHang(id) ON DELETE CASCADE,
    FOREIGN KEY (hangHoaId) REFERENCES tblHangHoa(id),
    CONSTRAINT chk_ctdh CHECK (soLuong > 0 AND donGia >= 0)               -- ngoại lệ 7.1
);

CREATE TABLE tblHoaDon (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    maHoaDon            VARCHAR(20)   NOT NULL UNIQUE,
    donHangId           INT NOT NULL UNIQUE,       --  1 đơn hàng - 1 hóa đơn
    tongTien            DECIMAL(15,2) NOT NULL,
    vat                 DECIMAL(5,2)  NOT NULL DEFAULT 10.00,   -- thuế suất (%)
    phuongThucThanhToan VARCHAR(30)   NOT NULL DEFAULT 'CHUA_THANH_TOAN',  -- màn hình tạo đơn không chọn; cập nhật khi thanh toán
    FOREIGN KEY (donHangId) REFERENCES tblDonHang(id),
    CONSTRAINT chk_hd CHECK (tongTien >= 0 AND vat >= 0)
);

CREATE TABLE tblChungTuGiao (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    maChungTu   VARCHAR(20) NOT NULL UNIQUE,
    donHangId   INT NOT NULL UNIQUE,               --  chứng từ giao của đơn hàng
    taiXeId     INT NOT NULL,                      --  tài xế giao hàng và lập chứng từ
    chuKySo     TEXT NOT NULL,
    FOREIGN KEY (donHangId) REFERENCES tblDonHang(id),
    FOREIGN KEY (taiXeId)   REFERENCES tblTaiXe(nguoiDungId)
);

-- ---------------------------------------------------------------------
-- 5. PHIẾU NHẬP/XUẤT KHO
-- ---------------------------------------------------------------------
CREATE TABLE tblPhieuNhapXuat (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    maPhieu     VARCHAR(20) NOT NULL UNIQUE,
    loaiPhieu   VARCHAR(10) NOT NULL,              -- 'NHAP' | 'XUAT'
    ngayLap     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    khoId       INT NOT NULL,                      
    nhanVienId  INT NOT NULL,                      --  nhân viên lập phiếu
    donHangId   INT NOT NULL,                      --  đơn hàng được nhập/xuất
    FOREIGN KEY (khoId)      REFERENCES tblKhoBai(id),
    FOREIGN KEY (nhanVienId) REFERENCES tblNhanVien(nguoiDungId),
    FOREIGN KEY (donHangId)  REFERENCES tblDonHang(id),
    CONSTRAINT chk_pnx CHECK (loaiPhieu IN ('NHAP','XUAT'))
);

-- ---------------------------------------------------------------------
-- 6. YÊU CẦU HỖ TRỢ / KHIẾU NẠI
-- ---------------------------------------------------------------------
CREATE TABLE tblYeuCauHoTro (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    maYeuCau        VARCHAR(20) NOT NULL UNIQUE,
    noiDung         TEXT        NOT NULL,
    trangThai       VARCHAR(20) NOT NULL DEFAULT 'MOI',
    phanHoi         TEXT NULL,                     --  GDXuLyYeuCau.inoutPhanHoi
    khachHangId     INT NOT NULL,                  -- khách hàng gửi yêu cầu
    donHangId       INT NOT NULL,                  --  yêu cầu về đơn hàng
    nhanVienId      INT NULL,                      --  NULL tới khi nhân viên nhận xử lý
    FOREIGN KEY (khachHangId) REFERENCES tblKhachHang(nguoiDungId),
    FOREIGN KEY (donHangId)   REFERENCES tblDonHang(id),
    FOREIGN KEY (nhanVienId)  REFERENCES tblNhanVien(nguoiDungId)
);
