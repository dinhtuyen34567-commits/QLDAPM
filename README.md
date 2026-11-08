# QLDAPM-ShippingOrder — UC-01: Tạo đơn hàng vận chuyển

Mô-đun **Use case 01: Tạo đơn hàng vận chuyển** của hệ thống quản lý vận chuyển.

| Hạng mục | Nội dung |
|---|---|
| Use case | UC-01 — Tạo đơn hàng vận chuyển |
| Actor | Khách hàng |
| Công nghệ | Java 17, Maven (WAR), Jakarta Servlet 6 + JSP + JSTL, JDBC thuần, HikariCP, MySQL 8, Gson |
| Chạy trên | Tomcat 10.1 |
| Kiểm thử | JUnit 5 + Mockito, JaCoCo (ngưỡng ≥ 80% cho tầng Service và util) |

> **Phạm vi:** chỉ UC-01. UC-02 (Tra cứu vận đơn) và UC-03 (Khiếu nại) do thành viên khác phụ trách.

> ### 👉 Mở dự án lần đầu?
> Đọc **[docs/HUONG-DAN-MO-LAN-DAU.md](docs/HUONG-DAN-MO-LAN-DAU.md)** — hướng dẫn đầy đủ
> từ bước cài JDK 17 / Maven / MySQL / Tomcat 10.1, tạo CSDL, build, deploy lên Tomcat,
> đăng nhập bằng dev-login và chạy thử luồng tạo đơn.

---

## 1. Yêu cầu môi trường

| Phần mềm | Phiên bản | Ghi chú |
|---|---|---|
| JDK | 17 trở lên | Build với `maven.compiler.release=17` nên JDK 17/21/25 đều chạy được |
| Maven | 3.9.x | Bắt buộc cài và thêm `mvn` vào `PATH` |
| MySQL | 8.0.16 trở lên | Bản thấp hơn bỏ qua ràng buộc `CHECK` |
| Tomcat | 10.1.x | Bản Tomcat 9 trở về không hỗ trợ `jakarta.*` |

### Cài Maven (Windows)

1. Tải `apache-maven-3.9.9-bin.zip` từ <https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/>
2. Giải nén, ví dụ vào `C:\apache-maven-3.9.9`
3. Thêm `%JAVA_HOME%\bin` và `C:\apache-maven-3.9.9\bin` vào biến môi trường `PATH`
4. Kiểm tra: `mvn -version` và `java -version`

### Cài Maven (Linux/macOS)

```bash
curl -fsSL https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz \
  | tar xz -C /opt
export PATH=/opt/apache-maven-3.9.9/bin:$PATH
```

---

## 2. Tạo cơ sở dữ liệu

Script `database/schema.sql` là **bản sao nguyên vẹn** của `csdl_van_chuyen.sql` (18 bảng, không rút gọn, không đổi tên bảng/cột).

### Cách 1: dùng script có sẵn

```bat
scripts\tao-csdl.bat root mat_khau_mysql
```

### Cách 2: chạy tay

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p vanchuyen < database/init_data.sql
```

`init_data.sql` dùng `INSERT IGNORE` nên **chạy lại được** nhiều lần mà không sinh bản ghi trùng.

### Dữ liệu mẫu

**Khách hàng:**

| Mã | Tên đăng nhập | Mật khẩu | Họ tên | SĐT |
|---|---|---|---|---|
| KH001 | `khachhang1` | `Dev@12345` | Nguyễn Văn A | 0912345678 |

Mật khẩu được lưu bằng **BCrypt** (`$2a$10$…`), hash sinh bằng thư viện `bcrypt`, không phải chuỗi bịa.

**Hàng hóa:**

| Mã | Tên hàng | Loại hàng | Khối lượng (kg) | Đơn giá cước (đ/kg) |
|---|---|---|---|---|
| H001 | Điện thoại di động | Điện tử | 0.50 | 12000 |
| H002 | Laptop | Điện tử | 2.50 | 15000 |
| H003 | Quần áo | Thời trang | 0.30 | 10000 |
| H004 | Sách | Văn phòng phẩm | 0.40 | 9000 |
| H005 | Nồi cơm điện | Gia dụng | 3.00 | 8000 |
| H006 | Mỹ phẩm | Mỹ phẩm | 0.35 | 12345 |

---

## 3. Biến môi trường

Cấu hình nằm trong `src/main/resources/application.properties`. **Mọi giá trị đều có thể ghi đè bằng biến môi trường cùng tên.** Mật khẩu CSDL **không** được hardcode trong mã nguồn.

| Biến môi trường | Mặc định | Ý nghĩa |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/vanchuyen?useUnicode=true&characterEncoding=UTF-8&…` | JDBC URL |
| `DB_USER` | `root` | Tài khoản MySQL |
| `DB_PASSWORD` | *(rỗng)* | Mật khẩu MySQL |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | Driver JDBC |
| `BILLING_VAT_PERCENT` | `10` | Thuế suất VAT (%) |
| `ORDER_MAX_QUANTITY_PER_LINE` | `1000` | Giới hạn số lượng mỗi dòng (BR-14) |
| `ORDER_MAX_LINES` | `50` | Giới hạn số dòng mỗi đơn (BR-14) |
| `ORDER_MAX_TOTAL_WEIGHT_KG` | `5000` | Giới hạn tổng khối lượng mỗi đơn (BR-14) |
| `APP_DEV_LOGIN` | *(không đặt = tắt)* | Bật `/dev-login` để đăng nhập nhanh khi chạy thử |

> Tên biến môi trường viết HOA theo quy ước (xem bảng `ENV_NAMES` trong `AppConfig`).
> Ngoài ra, đặt biến trùng **tên thuộc tính** trong file cũng có hiệu lực
> (ví dụ `db.password`), nhưng nên dùng tên viết HOA đã liệt kê ở trên.

Ví dụ chạy thử trên PowerShell:

```powershell
$env:DB_USER     = "root"
$env:DB_PASSWORD = "123456"
$env:APP_DEV_LOGIN = "true"
```

### Bật dev-login (tạm thời)

Module đăng nhập thật **chưa** nằm trong phạm vi Sprint này, nên dự án có một servlet
`/dev-login` phục vụ chạy thử cục bộ.

- Mặc định **tắt**. Chỉ hoạt động khi `APP_DEV_LOGIN=true` **và** `app.dev-login-enabled=true`.
- `DevLoginServlet` được ghi chú rõ là tạm thời, sẽ xoá khi có module đăng nhập thật.
- Sau khi bật: mở `http://localhost:8080/QLDAPM-ShippingOrder/dev-login?userId=1`.

> ⚠️ Không được bật dev-login trên môi trường thật.

---

## 4. Build và chạy

### Build + kiểm thử (một lệnh)

```bat
scripts\kiem-thu.bat
```

hoặc chạy tay:

```bash
mvn clean verify
```

Lệnh này chạy biên dịch → unit test → kiểm tra ngưỡng bao phủ JaCoCo → đóng gói WAR.

- Báo cáo bao phủ: `target/site/jacoco/index.html`
- File WAR: `target/QLDAPM-ShippingOrder.war`

### Chạy trên Tomcat 10.1

**Bước 1 — Cấu hình biến môi trường cho Tomcat**

Tomcat không tự đọc biến môi trường của Windows. Tạo file
`%CATALINA_HOME%\bin\setenv.bat`:

```bat
set "DB_URL=jdbc:mysql://localhost:3306/vanchuyen?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh"
set "DB_USER=root"
set "DB_PASSWORD=mat_khau_mysql_cua_ban"
set "APP_DEV_LOGIN=true"
```

> `APP_DEV_LOGIN=true` chỉ dùng khi **chạy thử cục bộ** (mục 3).

**Bước 2 — Copy file WAR và khởi động Tomcat**

```bat
copy target\QLDAPM-ShippingOrder.war %CATALINA_HOME%\webapps\
%CATALINA_HOME%\bin\startup.bat
```

**Bước 3 — Mở trình duyệt**

```
http://localhost:8080/QLDAPM-ShippingOrder/
```

Phải thấy trang **"Hệ thống đơn hàng vận chuyển"** với nút **[Tạo đơn hàng mới]**.

Dừng Tomcat: `%CATALINA_HOME%\bin\shutdown.bat`

> Sau khi đổi cấu hình phải **build lại** (`mvn clean package`) rồi copy lại WAR —
> Tomcat không tự biên dịch lại.

Chi tiết từng bước (kèm xử lý sự cố) nằm ở
[docs/HUONG-DAN-MO-LAN-DAU.md](docs/HUONG-DAN-MO-LAN-DAU.md).

---

## 5. Danh mục API

Tiền tố `/api/v1`. Mọi API yêu cầu phiên đăng nhập hợp lệ và vai trò khách hàng
(nếu không sẽ trả `401 KHONG_CO_QUYEN`).

| # | Phương thức, đường dẫn | Mô tả | Thành công |
|---|---|---|---|
| 1 | `GET /api/v1/khach-hang/me` | Thông tin người gửi | `200 { "hoTen", "sdt" }` |
| 2 | `GET /api/v1/hang-hoa` | Danh sách hàng hóa | `200 [ { "id", "maHang", "tenHang", "loaiHang", "khoiLuong" } ]` |
| 3 | `POST /api/v1/don-hang/tinh-cuoc` | Tính cước | `200 { "tongKhoiLuong", "tongCuoc", "vat", "tienVat", "tongThanhToan", "lines" }` |
| 4 | `POST /api/v1/don-hang` | Tạo đơn hàng (header `Idempotency-Key`) | `201` (mới) / `200` (gửi lại cùng khoá) → `{ "donHangId", "maVanDon", "tongTien" }` |

**Trường `lines` của API 3** là phần bổ sung ngoài đặc tả (xem giả định #8), phục vụ
hiển thị đơn giá từng dòng trong bảng mặt hàng:

```json
"lines": [ { "hangHoaId": 1, "soLuong": 2, "donGia": 6000, "khoiLuong": 0.50 } ]
```

Giá trị `donGia` do **server** tính, không phải giá trị client gửi lên.

**Trang giao diện:**

| Đường dẫn | Màn hình |
|---|---|
| `GET /` | Trang chính khách hàng (`views/home.jsp`) |
| `GET /don-hang/tao` | Màn hình tạo đơn (`views/create-order.jsp`) |
| `GET /don-hang/thanh-cong?maVanDon=…` | Màn hình tạo đơn thành công (`views/order-success.jsp`) |

### Body của API 4

```json
{
  "tenNguoiNhan": "Trần Văn B",
  "sdtNguoiNhan": "0987654321",
  "diaChiGiao": "123 Cầu Giấy, Hà Nội",
  "items": [ { "hangHoaId": 1, "soLuong": 2 } ]
}
```

Thông tin người gửi **không** nằm trong body — server lấy `khachHangId` từ phiên (BR-01).
Đơn giá **không** bao giờ do client gửi — server tự tính theo công thức mục 5.4.

### Mã lỗi

| HTTP | Mã lỗi | Thông báo |
|---|---|---|
| 400 | `SDT_KHONG_HOP_LE` | Số điện thoại không hợp lệ, chỉ được chứa từ 10-11 chữ số |
| 400 | `SO_LUONG_KHONG_HOP_LE` | Số lượng phải lớn hơn 0 |
| 422 | `DON_HANG_RONG` | Đơn hàng phải có ít nhất 1 mặt hàng |
| 400 | `THIEU_THONG_TIN` | Thiếu tên người nhận hoặc địa chỉ giao hàng |
| 400 | `DU_LIEU_QUA_DAI` | Dữ liệu vượt độ dài cho phép |
| 400 | `VUOT_GIOI_HAN` | Vượt giới hạn cho phép của một đơn hàng |
| 404 | `HANG_HOA_KHONG_TON_TAI` | Hàng hóa không tồn tại |
| 401/403 | `KHONG_CO_QUYEN` | Phiên hết hạn hoặc không phải khách hàng |
| 500 | `LOI_HE_THONG` | Đã xảy ra lỗi hệ thống. Vui lòng thử lại |

Cấu trúc lỗi: `{ "code": "...", "message": "..." }`. Không bao giờ lộ stack trace ra ngoài.

---

## 6. Công thức tính cước

Tất cả dùng `BigDecimal`, làm tròn về **đồng** bằng `RoundingMode.HALF_UP`. Cấm `double`/`float` cho tiền.

```
unitPrice   = round(khoiLuong × donGiaCuoc)          // đơn giá 1 đơn vị hàng, lưu vào tblChiTietDonHang.donGia
lineFee     = soLuong × unitPrice
totalFee    = Σ lineFee                              // tongCuoc
vatAmount   = round(totalFee × vat / 100)            // vat mặc định 10%
totalAmount = totalFee + vatAmount                   // tongTien, đã gồm VAT
```

`unitPrice` được **làm tròn trước** khi nhân số lượng, để chi tiết đơn khớp hóa đơn.

| Hàng | Số lượng | unitPrice | totalFee | VAT | totalAmount | totalWeight |
|---|---|---|---|---|---|---|
| 0,50 kg × 12.000 đ/kg | 2 | 6.000 | 12.000 | 1.200 | **13.200** | 1,00 kg |
| 0,35 kg × 12.345 đ/kg | 3 | 4.321 | 12.963 | 1.296 | **14.259** | 1,05 kg |

---

## 7. Checklist kiểm thử thủ công

Các kiểm thử dưới đây cần thao tác trên trình duyệt, không tự động hoá được.

### Tiền điểu kiện

1. Đã tạo CSDL và nạp `init_data.sql`.
2. Đã build và deploy lên Tomcat 10.1.
3. Đã bật dev-login (`APP_DEV_LOGIN=true`) **hoặc** đăng nhập bằng cách khác.

### TC-01 — Luồng chính (bước 1–12)

1. Mở `http://localhost:8080/QLDAPM-ShippingOrder/` → bấm **[Tạo đơn hàng mới]**.
2. Kiểm tra **[Tên người gửi]** và **[SĐT người gửi]** đã điền sẵn và bị khoá (xám).
3. Nhập người nhận: tên `Trần Văn B`, SĐT `0987654321`, địa chỉ `123 Cầu Giấy, Hà Nội`.
4. Kiểm tra dấu tick xanh xuất hiện bên cạnh mỗi ô.
5. Chọn hàng hóa **H001**, nhập số lượng `2`, bấm **[Thêm mặt hàng]**.
6. Kiểm tra bảng chi tiết có 1 dòng và **tổng khối lượng = 1,00 kg**.
7. Kiểm tra nút **[Lưu đơn hàng]** chuyển từ xám sang **xanh dương**.
8. Bấm **[Lưu đơn hàng]** → pop-up **[Xác nhận tạo đơn]** hiện ra, làm mờ nền, tóm tắt cước phí.
9. Bấm **[Xác nhận]** → chuyển sang màn hình **thành công**, hiển thị mã vận đơn dạng `VN########`.
10. Kiểm tra CSDL: `tblDonHang` có 1 bản ghi `trangThai = 'CHO_LAY'`, `chuyenXeId IS NULL`;
    `tblChiTietDonHang` có 1 dòng `donGia = 6000.00`; `tblHoaDon` có `tongTien = 13200.00`,
    `phuongThucThanhToan = 'CHUA_THANH_TOAN'`.

```sql
SELECT o.maVanDon, o.trangThai, o.chuyenXeId, o.tongKhoiLuong,
       d.soLuong, d.donGia, h.tongTien, h.phuongThucThanhToan
FROM tblDonHang o
JOIN tblChiTietDonHang d ON d.donHangId = o.id
JOIN tblHoaDon h ON h.donHangId = o.id
ORDER BY o.id DESC LIMIT 1;
```

### TC-02 — Ngoại lệ 3.1: SĐT sai định dạng

1. Nhập `0987abc@` vào ô **[SĐT người nhận]**.
2. **Kết quả mong đợi:** viền đỏ quanh ô, dòng chữ đỏ ngay bên dưới ô ghi
   *"Số điện thoại không hợp lệ, chỉ được chứa từ 10-11 chữ số"*; nút **[Lưu đơn hàng]** vẫn xám.

### TC-03 — Ngoại lệ 7.1: số lượng bất thường

1. Chọn hàng hóa, nhập số lượng `0` rồi bấm **[Thêm mặt hàng]**.
2. Nhập số lượng `-5` rồi bấm **[Thêm mặt hàng]**.
3. **Kết quả mong đợi:** bảng chi tiết **không** thêm dòng nào; hiện cảnh báo nền đỏ
   *"Số lượng phải lớn hơn 0"* ngay tại ô số lượng.

### TC-04 — Ngoại lệ 9.1: đơn hàng rỗng

1. Mở DevTools (F12), sửa thuộc tính `disabled` của nút **[Lưu đơn hàng]** để bỏ khoá.
2. Bấm nút đó khi chưa chọn mặt hàng nào.
3. **Kết quả mong đợi:** thông báo **nền vàng ở góc phải màn hình** ghi
   *"Đơn hàng phải có ít nhất 1 mặt hàng"*; **không** mở pop-up xác nhận.

### TC-05 — Ngoại lệ 11.1: mất mạng / timeout

1. Chọn hàng hóa, nhập đủ người nhận, bấm **[Lưu đơn hàng]** → pop-up xác nhận hiện ra.
2. Bấm **[Xác nhận]**.
3. **Ngay sau đó**, trong DevTools → Network → chuyển mạng sang **Offline** (hoặc Throttling → Offline).
4. **Kết quả mong đợi:** pop-up hiện **biểu tượng xoay** trong khoảng 10 giây, sau đó đổi thành
   pop-up lỗi ghi *"Kết nối máy chủ thất bại. Vui lòng kiểm tra đường truyền!"* kèm nút **[Đóng]**.
5. Bấm **[Đóng]**.
6. **Kết quả mong đợi:** pop-up đóng; **toàn bộ dữ liệu đã nhập vẫn còn nguyên** (tên, SĐT, địa chỉ, mặt hàng).
7. Bật lại mạng và bấm **[Lưu đơn hàng]** → **[Xác nhận]** lần nữa.
8. **Kết quả mong đợi:** chỉ có **một** đơn hàng trong CSDL, cùng mã vận đơn (kiểm tra BR-13).

### TC-06 — Ngoại lệ 11.2: bấm [Hủy bỏ]

1. Điền đủ thông tin, thêm ít nhất 1 mặt hàng, bấm **[Lưu đơn hàng]**.
2. Bấm **[Hủy bỏ]** trên pop-up.
3. **Kết quả mong đợi:** pop-up đóng, lớp phủ mờ biến mất, quay lại màn hình tạo đơn với
   **mọi dữ liệu đã nhập được giữ nguyên**; **không** ghi gì vào CSDL.

```sql
-- Chạy trước và sau TC-06, số dòng phải không đổi
SELECT COUNT(*) FROM tblDonHang;
```

---

## 8. Giả định và điểm cần xác nhận

| # | Điểm | Mặc định đang dùng | Cần xác nhận |
|---|---|---|---|
| 1 | Công thức cước, nguồn đơn giá | Theo khối lượng: `donGiaCuoc` (đ/kg) trong `tblHangHoa` | Khớp mục 3.4 tài liệu thiết kế |
| 2 | Phương thức thanh toán | `CHUA_THANH_TOAN`, cập nhật khi có chức năng thanh toán | Thống nhất với nhóm |
| 3 | Định dạng mã vận đơn | `VN` + 8 chữ số ngẫu nhiên (`SecureRandom`) | Khớp BR-08 |
| 4 | Định dạng mã hóa đơn | `HD` + 8 chữ số ngẫu nhiên | **Tài liệu chưa quy định** — cần nhóm chốt |
| 5 | Giới hạn một đơn | 1.000 / dòng, 50 dòng, 5.000 kg | Khớp BR-14 |
| 6 | Hàm `tinhTongKhoiLuong()` | Sơ đồ lớp phân tích hiện chỉ có `tinhTongTien()`; đã bổ sung `tinhTongKhoiLuong()` vào lớp Đơn hàng | Cần bổ sung vào tài liệu phân tích |
| 7 | Màn hình chính và đăng nhập | `home.jsp` placeholder + `DevLoginServlet` tạm thời (mặc định tắt) | Nhóm khác sẽ thay thế |
| 8 | Trường `lines` trong API 3 | Bổ sung ngoài đặc tả để hiển thị đơn giá từng dòng trong bảng chi tiết | Cần thống nhất với nhóm |
| 9 | `Idempotency-Key` khi không có header | Tạo đơn bình thường với `idempotencyKey = NULL` | Khớp mục 5.5 |
| 10 | Hệ số làm tròn khối lượng | `tongKhoiLuong` giữ 2 chữ số thập phân (khớp `DECIMAL(12,2)`) | Khớp kiểu cột CSDL |
| 11 | Phân biệt trùng mã vận đơn với trùng khóa idempotency | DAO trích tên ràng buộc từ thông báo lỗi MySQL (`for key '…'`). Với ràng buộc nhiều cột MySQL chỉ nêu cột đầu tiên, nên: vi phạm `maVanDon` ⇒ sinh mã mới (BR-08); còn lại ⇒ trùng khoá idempotency, trả lại đơn cũ (BR-13) | Nhóm xác nhận cách đặt tên ràng buộc |
| 12 | Mã lỗi khi tài khoản không phải khách hàng | `KHONG_CO_QUYEN` trả HTTP **401** | Cần nhóm chốt 401 hay 403 |

---

## 9. Cấu trúc thư mục

```
QLDAPM-ShippingOrder/
├── .gitignore
├── README.md
├── pom.xml
├── docs/
│   ├── Scenario_TaoDonHang.docx
│   └── Tai_lieu_thiet_ke_kien_truc_module.docx
├── database/
│   ├── schema.sql              # bản sao nguyên vẹn csdl_van_chuyen.sql (18 bảng)
│   └── init_data.sql
├── scripts/
│   ├── kiem-thu.bat            # chạy mvn clean verify
│   └── tao-csdl.bat            # tạo CSDL + nạp dữ liệu mẫu
└── src/
    ├── main/
    │   ├── java/com/shipping/
    │   │   ├── config/          # AppConfig, DBConfig, ConnectionProvider, SessionUtils,
    │   │   │                   # AuthFilter, EncodingFilter, DevLoginServlet, AppInitializer
    │   │   ├── controller/      # BaseController, OrderController, ProductController
    │   │   ├── service/         # OrderService
    │   │   │   └── impl/        # OrderServiceImpl
    │   │   ├── dao/             # OrderDAO, ProductDAO, CustomerDAO, DuplicateKeyException
    │   │   │   └── impl/        # OrderDAOImpl, ProductDAOImpl, CustomerDAOImpl
    │   │   ├── model/           # Order, OrderDetail, Product, Invoice, Customer,
    │   │   │                   # OrderStatus, PaymentMethod + DTO
    │   │   ├── exception/       # AppException, ErrorCode
    │   │   └── util/            # OrderUtils
    │   ├── resources/
    │   │   └── application.properties
    │   └── webapp/
    │       ├── WEB-INF/web.xml
    │       ├── assets/css/style.css
    │       ├── assets/js/order-create.js
    │       └── views/
    │           ├── home.jsp
    │           ├── create-order.jsp
    │           └── order-success.jsp
    └── test/java/com/shipping/
        ├── service/             # OrderServiceTest, OrderServiceTestSupport
        ├── util/                # OrderUtilsTest
        └── dao/                 # (để trống — kiểm thử tích hợp tùy chọn)
```

---

## 10. Quy tắc nghiệp vụ đã hiện thực

| Mã | Quy tắc | Nơi kiểm tra |
|---|---|---|
| BR-01 | Người gửi lấy từ phiên, bỏ qua giá trị client gửi | `OrderController` + `OrderServiceImpl.createOrder` |
| BR-02 | SĐT người nhận `^[0-9]{10,11}$` | `OrderUtils.isValidPhone` + `order-create.js` |
| BR-03 | Tên người nhận (≤100) và địa chỉ (≤255) bắt buộc | `OrderServiceImpl.createOrder` |
| BR-04 | Số lượng là số nguyên > 0 | `OrderServiceImpl.resolveItems` + CHECK CSDL |
| BR-05 | Nút Lưu chỉ bật khi 3 ô hợp lệ và có ≥1 mặt hàng | `order-create.js` + server kiểm tra lại |
| BR-06 | `tongKhoiLuong = Σ(soLuong × khoiLuong)`, server tự tính | `OrderServiceImpl.resolveItems` |
| BR-07 | Ba lần ghi nguyên tử, rollback khi lỗi | `OrderServiceImpl.persistOrder` |
| BR-08 | Mã vận đơn `VN` + 8 số, thử lại tối đa 5 lần | `OrderServiceImpl.insertOrderWithRetry` |
| BR-09 | Thêm lại cùng hàng hóa thì cộng dồn | `OrderServiceImpl.resolveItems` + `order-create.js` |
| BR-10 | Hủy bỏ không ghi CSDL; lỗi mạng giữ nguyên dữ liệu | `order-create.js` |
| BR-11 | Đơn mới: `status = CHO_LAY`, `tripId = NULL` | `OrderServiceImpl.insertOrderWithRetry` |
| BR-12 | Cước, VAT, tổng tiền theo mục 5.4; hóa đơn `CHUA_THANH_TOAN` | `OrderServiceImpl.buildQuote` |
| BR-13 | Chống tạo trùng bằng `Idempotency-Key` | `OrderServiceImpl.createOrder` + UNIQUE CSDL |
| BR-14 | Giới hạn 1.000/dòng, 50 dòng, 5.000 kg | `OrderServiceImpl.resolveItems` |

**Thứ tự kiểm tra ở server** (cố định để kiểm thử xác định):
(1) vai trò khách hàng → (2) tên/địa chỉ → (3) SĐT → (4) danh sách rỗng →
(5) từng số lượng → (6) giới hạn → (7) hàng hóa tồn tại.

---

## 11. Tuân thủ Coding Conventions

Đối chiếu với tài liệu `Coding Conventions.docx` của nhóm:

| Quy tắc | Yêu cầu | Trạng thái UC-01 |
|---|---|---|
| Class / Entity | PascalCase (danh từ) | Tuân thủ — 38 lớp đều PascalCase |
| Biến / thuộc tính | camelCase | Tuân thủ |
| Hằng số | UPPER_SNAKE_CASE | Tuân thủ (`MAX_TRACKING_CODE_ATTEMPTS`, `VAT_PERCENT`…) |
| Hàm / phương thức | camelCase (động từ + danh từ) | Tuân thủ (`createOrder`, `resolveItems`, `buildQuote`…) |
| Kiến trúc | Controller → Service → DAO → Database | Tuân thủ — `service/` **không còn SQL nào**, chỉ quản lý giao dịch |
| Cấu trúc thư mục | MVC hoặc Layered | Tuân thủ — có thêm gói `config/` để dựng bean, filter và `DevLoginServlet` (được phép theo CLAUDE.md mục 4) |
| Indentation | 4 spaces, thống nhất | Tuân thủ — không dùng TAB trong toàn bộ mã nguồn |
| Độ dài dòng | tối đa 100–120 ký tự | Tuân thủ — dòng dài nhất **101** ký tự |
| Comment | tiếng Việt hoặc tiếng Anh, thống nhất | Tuân thủ — nhất quán tiếng Việt (Javadoc tiếng Việt có dấu, comment trong hàm viết không dấu cho nhất quán với mã nguồn cũ) |
| Javadoc cho hàm nghiệp vụ phức tạp | bắt buộc | Tuân thủ — Javadoc đặt trên **interface** `OrderService` cho các hàm `@Override` ở tầng impl |
| Exception: lỗi nghiệp vụ | thông báo thân thiện cho UI | Tuân thủ — `AppException` + `ErrorCode` trả `{code, message}` |
| Exception: lỗi kỹ thuật | ghi log để debug | Tuân thủ — `LOG.log(Level.SEVERE, …)`; **không** lộ stack trace ra ngoài |
| Transaction | BEGIN / COMMIT / ROLLBACK | Tuân thủ — `setAutoCommit(false)` → `commit()` / `rollback()` quanh 3 bảng |
| Tổng tiền tính ở tầng Service | không nhận giá trị từ client | Tuân thủ — `BigDecimal`, `unitPrice` server tự tính, client không gửi giá lên |
| Mật khẩu | mã hóa, không lưu plain text | Tuân thủ — BCrypt (`$2a$10$…`) trong `init_data.sql` |
| Tên nhánh | `feature/<tên>`, `bugfix/<lỗi>` | Tuân thủ — nhánh hiện tại `feature/order-checkout` |
| Commit message | `[Add]` / `[Update]` / `[Fix]` | **Lệch** — dùng Conventional Commits (`feat`, `test`, `docs`, `chore`) theo CLAUDE.md mục 2 và mục 12. Quy ước trong tài liệu nhóm ghi "nên", còn CLAUDE.md yêu cầu rõ ràng; nhóm đã thống nhất giữ Conventional Commits làm chuẩn chung cho các module |

**Hai quy tắc không áp dụng cho UC-01:**

- *"Không cho phép trùng lặp email hoặc phone trước khi INSERT"* — UC-01 **không có**
  chức năng tạo/cập nhật khách hàng (chỉ đọc khách hàng đang đăng nhập). Ràng buộc
  `UNIQUE` cho `email` và `username` đã có sẵn trong `tblNguoiDung`.
- *"Trừ số lượng tồn kho của sản phẩm"* — schema hiện tại (18 bảng) **không có bảng
  tồn kho**, nên không thể trừ. Đơn hàng UC-01 không quản lý tồn kho.

---

## 12. Bảo mật

- Mọi truy vấn dùng `PreparedStatement`; **không** ghép chuỗi SQL.
- Không tin client: `customerId` lấy từ session; đơn giá, tổng khối lượng, tổng tiền do server tính.
- Cookie phiên `HttpOnly`, `SameSite=Lax` (khai báo trong `web.xml`).
- API JSON chỉ nhận `Content-Type: application/json`.
- Không ghi mật khẩu hay `Idempotency-Key` đầy đủ vào log; không lộ stack trace ra người dùng (trả `LOI_HE_THONG`).
- `.gitignore` chặn `application-local.properties`, `.env`, `target/`.
- Mật khẩu CSDL và mật khẩu mẫu không hardcode trong mã Java.
- Chống XSS: JSP dùng `<c:out>`; JavaScript dùng `textContent`, không `innerHTML` với dữ liệu người dùng.

---

## 13. Ghi chú về môi trường phát triển

Bản dự án này được sinh ra trong môi trường làm việc **không có Maven và không có
trình biên dịch Java** (sandbox chỉ có JRE 11, không có `javac`; mạng bị chặn nên không
tải được dependency). Vì vậy:

**Đã kiểm chứng trong môi trường sinh mã:**

| Hạng mục | Cách kiểm chứng |
|---|---|
| `schema.sql` | So khớp **từng byte** với `csdl_van_chuyen.sql`; đủ 18 bảng |
| Mọi cột trong truy vấn DAO | Đối chiếu tự động với tên cột thật trong `schema.sql` |
| Hash BCrypt của mật khẩu mẫu | Sinh và kiểm tra lại bằng thư viện `bcrypt`: `Dev@12345` → đúng, sai → từ chối |
| Công thức tiền | Tính lại bằng `Decimal` (giống hệt `BigDecimal`): ra đúng **13.200 đ** và **14.259 đ**; xác nhận việc làm tròn `unitPrice` **trước** khi nhân số lượng là bắt buộc |
| Thông báo lỗi | Đối chiếu **từng ký tự** với CLAUDE.md mục 5.2/6.2 cho cả `ErrorCode`, JS và JSP |
| Khóa JSON ↔ JavaScript | Đối chiếu tự động: mọi khoá JS đọc đều tồn tại trong `@SerializedName` |
| DOM ↔ JavaScript | Đối chiếu tự động: cả 34 `id` mà JS truy cập đều có trong JSP |
| CSS | Mọi class dùng trong JSP/JS đều được định nghĩa trong `style.css` |
| Cấu trúc Java | Kiểm tra cân bằng ngoặc (bộ quét nhận biết chuỗi/chú thích), đối chiếu chữ ký interface ↔ implementation, phát hiện import thiếu/thừa |
| XML | `pom.xml` và `web.xml` parse thành công |

**Chưa kiểm chứng được trong môi trường sinh mã:**

- `mvn clean verify` (biên dịch + chạy test + kiểm tra ngưỡng bao phủ JaCoCo).
- Chạy thật trên Tomcat 10.1 với MySQL 8.

Vì vậy **bước đầu tiên khi nhận bàn giao** là chạy:

```bat
scripts\kiem-thu.bat
```

Nếu build báo lỗi biên dịch hoặc test đỏ, gửi lại phần log lỗi để sửa tiếp.

### Danh sách kiểm thử

`OrderServiceTest` có **31** test, `OrderUtilsTest` có **9** test — tổng cộng **40** test,
bao phủ TC-01 đến TC-16 của CLAUDE.md mục 10:

| Nhóm | Test |
|---|---|
| Luồng chính | `tc01`, `tc01b` — ghi 3 bảng, `CHO_LAY`, `tripId` null, mã vận đơn, hóa đơn `CHUA_THANH_TOAN` |
| Ngoại lệ 3.1 | `tc02`, `tc02b` — SĐT sai; SĐT 10/11 số hợp lệ |
| Ngoại lệ 7.1 | `tc03` — số lượng `0`, `-5` |
| Ngoại lệ 9.1 | `tc04`, `tc04b` — danh sách rỗng |
| Giao dịch | `tc07` — `insertInvoice` lỗi ⇒ `rollback` được gọi, `commit` không |
| BR-01 | `tc08`, `tc08b` — client gửi `khachHangId` giả bị bỏ qua; không phải khách hàng ⇒ `KHONG_CO_QUYEN` |
| BR-03 | `tc10`, `tc10b`, `tc10c` — thiếu tên, thiếu địa chỉ, tên quá dài |
| BR-09 | `tc11` — cùng hàng hai lần ⇒ gộp, cộng dồn |
| BR-06 | `tc12`, `tc12b` — tổng khối lượng do server tính |
| BR-08 | `tc13`, `tc13b` — trùng mã 2 lần rồi thành công; trùng 6 lần ⇒ `LOI_HE_THONG` |
| BR-13 | `tc14`, `tc14b`, `tc14c` — gửi lại cùng khoá; khoá quá dài; hai request cùng khoá chạy song song |
| Công thức | `tc15`, `tc15b`, `tc15c` — hai ví dụ 13.200 đ / 14.259 đ; làm tròn `unitPrice` trước |
| BR-14 | `tc16`, `tc16b`, `tc16c`, `tc16d` — 1.001/dòng; 51 dòng; > 5.000 kg; hàng hoá không tồn tại |
| Tiện ích | `tc17`, `tc18`, `tc19` + 9 test `OrderUtilsTest` |
