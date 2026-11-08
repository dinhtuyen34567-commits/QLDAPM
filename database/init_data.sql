-- =====================================================================
-- DU LIEU MAU CHO UC-01 (Tao don hang van chuyen)
-- Chay sau database/schema.sql:
--     mysql -u root -p vanchuyen < database/init_data.sql
--
-- Script dung INSERT IGNORE va kiem tra ton tai nen CHAY LAI DUOC nhieu lan
-- ma khong sinh ban ghi trung hay loi khoa chinh.
--
-- Mat khau mau: Dev@12345
-- Hash ben duoi la BCrypt ($2a$10$, 60 ky tu) sinh bang thu vien bcrypt,
-- KHONG phai chuoi bam tu ban tay.
-- =====================================================================

USE vanchuyen;

-- ---------------------------------------------------------------------
-- 1. NGUOI DUNG VA KHACH HANG
-- ---------------------------------------------------------------------
-- tblNguoiDung: chung cho ca khach hang, nhan vien, tai xe.
-- tblKhachHang: danh dau nguoi dung nay la khach hang (BR-01).
INSERT IGNORE INTO tblNguoiDung (id, maNguoiDung, username, password, hoTen, email, sdt)
VALUES (1, 'KH001', 'khachhang1', '$2a$10$Xtybkrdhfr8feW7xGAC.3eRpBdlMG4HBG0j1Tw.S3OHAzmgq4/WJW',
        'Nguyễn Văn A', 'khachhang1@example.com', '0912345678');

INSERT IGNORE INTO tblKhachHang (nguoiDungId) VALUES (1);

-- ---------------------------------------------------------------------
-- 2. HANG HOA
-- ---------------------------------------------------------------------
-- khoiLuong: kg tren 1 don vi hang (dung cho tinhTongKhoiLuong())
-- donGiaCuoc: dong tren 1 kg (VND) (dung cong thuc muc 5.4)
INSERT IGNORE INTO tblHangHoa (id, maHang, tenHang, loaiHang, khoiLuong, donGiaCuoc) VALUES
  (1, 'H001', 'Điện thoại di động', 'Điện tử',    0.50, 12000),
  (2, 'H002', 'Laptop',             'Điện tử',    2.50, 15000),
  (3, 'H003', 'Quần áo',            'Thời trang',  0.30, 10000),
  (4, 'H004', 'Sách',               'Văn phòng phẩm', 0.40,  9000),
  (5, 'H005', 'Nồi cơm điện',      'Gia dụng',    3.00,  8000),
  (6, 'H006', 'Mỹ phẩm',           'Mỹ phẩm',    0.35, 12345);

-- ---------------------------------------------------------------------
-- 3. DON HANG MAU (tuy chon - dung de kiem thu tich hop)
-- ---------------------------------------------------------------------
-- Chi them khi bang tblDonHang chua co du lieu, de khong lam sai
-- so lieu don hang dang co trong CSDL cua nhom.
INSERT IGNORE INTO tblDonHang
  (id, maVanDon, ngayTao, trangThai, tongKhoiLuong, khachHangId,
   tenNguoiNhan, sdtNguoiNhan, diaChiGiao, chuyenXeId, idempotencyKey)
VALUES
  (1, 'VN00000001', '2026-01-15 09:30:00', 'CHO_LAY', 1.00, 1,
   'Trần Văn B', '0987654321', '123 Cầu Giấy, Hà Nội', NULL, NULL);

INSERT IGNORE INTO tblChiTietDonHang (donHangId, hangHoaId, soLuong, donGia) VALUES
  (1, 1, 2, 6000.00);   -- 0,50 kg x 12.000 đ/kg = 6.000 đ/don vi; x2 = 12.000 đ; VAT 1.200 đ; tong 13.200 đ

INSERT IGNORE INTO tblHoaDon (id, maHoaDon, donHangId, tongTien, vat, phuongThucThanhToan)
VALUES (1, 'HD00000001', 1, 13200.00, 10.00, 'CHUA_THANH_TOAN');
