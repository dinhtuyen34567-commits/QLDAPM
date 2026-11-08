# Hướng dẫn mở dự án lần đầu

Dành cho người vừa nhận bàn giao mô-đun **UC-01 – Tạo đơn hàng vận chuyển**.
Làm theo đúng thứ tự từ trên xuống.

---

## Bước 0 — Kiểm tra máy đã có gì

Mở **Command Prompt (cmd)** và gõ từng lệnh:

```bat
java -version
mvn -version
mysql --version
```

| Lệnh | Kết quả mong đợi | Nếu báo không tìm thấy |
|---|---|---|
| `java -version` | `17` trở lên | Cài JDK 17 → Bước 1A |
| `mvn -version` | `Apache Maven 3.9.x` | Cài Maven → Bước 1B |
| `mysql --version` | `8.0.16` trở lên | Cài MySQL → Bước 1C |

> **Lưu ý:** `java -version` phải ra **17 trở lên**. Máy đang có Java 11 thì phải
> cài JDK 17 (không phải sửa `JAVA_HOME` cũng được — cài song song, rồi trỏ
> `JAVA_HOME` sang bản 17).

---

## Bước 1 — Cài phần mềm còn thiếu

### 1A. Cài JDK 17

1. Tải **Temurin JDK 17 (x64)** hoặc **Oracle JDK 17**:
   <https://adoptium.net/temurin/releases/?version=17>
2. Cài xong, mở **cmd mới** và kiểm tra:
   ```bat
   java -version
   ```
   Phải hiện `17.0.x`.
3. Đặt biến môi trường:
   - `JAVA_HOME` = thư mục cài JDK (ví dụ `C:\Program Files\Java\jdk-17`)
   - Thêm `%JAVA_HOME%\bin` vào `Path`

### 1B. Cài Maven

1. Tải `apache-maven-3.9.9-bin.zip`:
   <https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip>
2. Giải nén, ví dụ vào `C:\apache-maven-3.9.9`
3. Đặt biến môi trường:
   - `MAVEN_HOME` = `C:\apache-maven-3.9.9`
   - Thêm `%MAVEN_HOME%\bin` vào `Path`
4. Mở **cmd mới**:
   ```bat
   mvn -version
   ```
   Phải hiện `Apache Maven 3.9.9` và `Java version: 17.x`

> **Không dùng Maven Wrapper** (`mvnw`) vì dự án chưa kèm file đó.

### 1C. Cài MySQL 8

1. Tải **MySQL Installer for Windows**:
   <https://dev.mysql.com/downloads/installer/>
2. Cài, chọn **Developer Default** hoặc tối thiểu **MySQL Server 8.0**.
3. **Bắt buộc nhớ mật khẩu root** bạn đặt ở bước cài — sẽ cần dùng ở Bước 2.
4. Thêm `C:\Program Files\MySQL\MySQL Server 8.0\bin` vào `Path`
   (để gõ được lệnh `mysql` trong cmd).
5. Kiểm tra:
   ```bat
   mysql --version
   ```

### 1D. Cài Tomcat 10.1

1. Tải **Tomcat 10.1** (bản 64-bit Windows zip):
   <https://tomcat.apache.org/download-10.cgi>
   - ⚠️ **Phải là bản 10.1**, không dùng Tomcat 9 hay 11.
     Dự án dùng `jakarta.*` nên Tomcat 9 sẽ không chạy được.
2. Giải nén, ví dụ vào `C:\apache-tomcat-10.1.x`
3. Đặt biến môi trường:
   - `CATALINA_HOME` = `C:\apache-tomcat-10.1.x`
   - Thêm `%CATALINA_HOME%\bin` vào `Path`

### 1E. (Tùy chọn) Cài IDE

IntelliJ IDEA Community hoặc Eclipse. Với Eclipse cần thêm **Tomcat 9+** vào
`Preferences > Server > Runtime Environments` và chỉnh **Tomcat 10** trong
`Preferences > Server > Runtime Environments > Tomcat`.

> Dự án là WAR (không phải project Maven của IDE), nên cách chạy chắc chắn nhất
> vẫn là **build bằng Maven rồi thả file WAR vào Tomcat** như Bước 4.

---

## Bước 2 — Tạo cơ sở dữ liệu

Mở **cmd tại thư mục dự án** (`...\QLDAPM-ShippingOrder`):

```bat
scripts\tao-csdl.bat root <mat_khau_mysql>
```

Ví dụ mật khẩu là `123456`:

```bat
scripts\tao-csdl.bat root 123456
```

Script sẽ chạy `schema.sql` (tạo database `vanchuyen` + 18 bảng) rồi
`init_data.sql` (khách hàng mẫu + 6 hàng hóa).

**Chạy tay** (nếu script báo lỗi):

```bat
mysql -u root -p < database\schema.sql
mysql -u root -p < database\init_data.sql
```

> **Mật khẩu có ký tự đặc biệt** (`@`, `#`, `$`, `!`…) vẫn chạy được:
> ```bat
> scripts\tao-csdl.bat root "P@ss#w0rd"
> ```
> Script dùng biến `MYSQL_PWD` thay cho tham số `-p`, nên mật khẩu không bị lộ
> trên màn hình và không vỡ khi chứa ký tự đặc biệt. Bỏ trống mật khẩu
> (`scripts\tao-csdl.bat root`) thì MySQL sẽ hỏi trực tiếp.

Kiểm tra đã tạo đủ:

```bat
mysql -u root -p -e "USE vanchuyen; SELECT maHang, tenHang, khoiLuong, donGiaCuoc FROM tblHangHoa;"
```

Phải ra 6 dòng `H001` … `H006`.

> `init_data.sql` dùng `INSERT IGNORE` nên **chạy lại nhiều lần cũng được**,
> không sinh bản ghi trùng.

---

## Bước 3 — Build và chạy kiểm thử

```bat
scripts\kiem-thu.bat
```

Hoặc chạy tay:

```bat
mvn clean verify
```

**Kết quả mong đợi:**

```
[INFO] BUILD SUCCESS
[INFO] Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
```

Nếu build đỏ, đọc phần log lỗi và gửi lại — lỗi thường là:
- `release version 17 not supported` → `JAVA_HOME` đang trỏ sai JDK
- `Could not resolve dependencies` → mạng bị chặn hoặc thiếu kho Maven

Báo cáo độ bao phủ: mở `target\site\jacoco\index.html`

---

## Bước 4 — Deploy lên Tomcat

### 4.1. Cài thông tin kết nối CSDL cho Tomcat

Tomcat không tự đọc biến môi trường của Windows. Cách đơn giản nhất: tạo file
`setenv.bat` trong `%CATALINA_HOME%\bin\`:

```bat
set "DB_URL=jdbc:mysql://localhost:3306/vanchuyen?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh"
set "DB_USER=root"
set "DB_PASSWORD=mat_khau_mysql_cua_ban"
set "APP_DEV_LOGIN=true"
```

> `APP_DEV_LOGIN=true` chỉ dùng khi **chạy thử cục bộ**. Xem Bước 5.

### 4.2. Copy file WAR

```bat
copy target\QLDAPM-ShippingOrder.war %CATALINA_HOME%\webapps\
```

### 4.3. Khởi động Tomcat

```bat
%CATALINA_HOME%\bin\startup.bat
```

Mở trình duyệt:

```
http://localhost:8080/QLDAPM-ShippingOrder/
```

Phải thấy trang **"Hệ thống đơn hàng vận chuyển"** với nút **[Tạo đơn hàng mới]**.

### 4.4. Dừng Tomcat

```bat
%CATALINA_HOME%\bin\shutdown.bat
```

> Nếu đổi cấu hình, **phải build lại** (`mvn clean package`) rồi copy lại WAR —
> Tomcat không tự biên dịch lại.

---

## Bước 5 — Đăng nhập để dùng được chức năng tạo đơn

Module đăng nhập thật **chưa** có trong phạm vi Sprint này, nên dự án có một
servlet tạm thời `/dev-login` (mặc định **tắt**).

Bật bằng cách thêm vào `setenv.bat` ở Bước 4.1:

```bat
set "APP_DEV_LOGIN=true"
```

Rồi **restart Tomcat** và mở:

```
http://localhost:8080/QLDAPM-ShippingOrder/dev-login?userId=1
```

Sau đó bấm **[Tạo đơn hàng mới]**.

| Tài khoản mẫu | Mật khẩu |
|---|---|
| `khachhang1` | `Dev@12345` |

> ⚠️ **Tắt dev-login khi nộp bài hoặc chạy trên máy chung.** Xoá dòng
> `APP_DEV_LOGIN` trong `setenv.bat` rồi restart Tomcat. Servlet này được ghi
> chú rõ là "tạm thời, bỏ khi có module đăng nhập thật".

---

## Bước 6 — Chạy thử luồng chính

1. Vào `http://localhost:8080/QLDAPM-ShippingOrder/don-hang/tao`
2. Kiểm tra **[Tên người gửi]** và **[SĐT người gửi]** đã điền sẵn, bị khoá (xám)
3. Nhập người nhận: tên `Trần Văn B`, SĐT `0987654321`, địa chỉ `123 Cầu Giấy, Hà Nội`
   → mỗi ô hiện **dấu tick xanh**
4. Chọn hàng hóa **H001**, số lượng `2`, bấm **[Thêm mặt hàng]**
   → bảng chi tiết có 1 dòng, **tổng khối lượng = 1,00 kg**
5. Nút **[Lưu đơn hàng]** chuyển từ xám sang **xanh dương**
6. Bấm **[Lưu đơn hàng]** → pop-up **[Xác nhận tạo đơn]** hiện ra
7. Bấm **[Xác nhận]** → sang màn hình thành công, hiện mã vận đơn `VN########`

Kiểm tra CSDL:

```bat
mysql -u root -p -e "USE vanchuyen; SELECT o.maVanDon, o.trangThai, o.chuyenXeId, d.soLuong, d.donGia, h.tongTien, h.phuongThucThanhToan FROM tblDonHang o JOIN tblChiTietDonHang d ON d.donHangId=o.id JOIN tblHoaDon h ON h.donHangId=o.id ORDER BY o.id DESC LIMIT 1;"
```

Kết quả đúng:

| maVanDon | trangThai | chuyenXeId | soLuong | donGia | tongTien | phuongThucThanhToan |
|---|---|---|---|---|---|---|
| VN######## | CHO_LAY | NULL | 2 | 6000.00 | 13200.00 | CHUA_THANH_TOAN |

Checklist kiểm thử thủ công đầy đủ (TC-01 … TC-06) nằm ở **README.md mục 7**.

---

## Xử lý sự cố thường gặp

| Triệu chứng | Nguyên nhân | Cách sửa |
|---|---|---|
| `mvn` không chạy | Chưa cài Maven hoặc chưa mở cmd mới | Cài Maven → Bổ sung `%MAVEN_HOME%\bin` vào `Path` → **mở cmd mới** |
| `release version 17 not supported` | `JAVA_HOME` trỏ JDK 11 | Đặt `JAVA_HOME` sang JDK 17, mở cmd mới |
| `kiem-thu.bat` báo *"phien ban Java hien tai khong phai 17"* | `JAVA_HOME` trỏ sai JDK | Script đã tự kiểm tra phiên bản; đổi `JAVA_HOME` sang JDK 17 rồi chạy lại |
| Tomcat 404 mọi đường dẫn | Deploy WAR sai chỗ | Copy WAR vào `%CATALINA_HOME%\webapps\` rồi restart |
| `ClassNotFoundException: jakarta.servlet...` | Dùng Tomcat 9 | Đổi sang Tomcat **10.1** |
| `Access denied for user 'root'` | Sai mật khẩu CSDL | Sửa `DB_PASSWORD` trong `setenv.bat`, restart Tomcat |
| `Unknown database 'vanchuyen'` | Chưa chạy `schema.sql` | Chạy lại Bước 2 |
| Trang trắng, không có dữ liệu | Thiếu `setenv.bat` trong `%CATALINA_HOME%\bin` | Làm Bước 4.1, restart Tomcat |
| API trả `401 KHONG_CO_QUYEN` | Chưa đăng nhập | Mở `/dev-login?userId=1` (Bước 5) |
| Tiếng Việt hiển thị sai dấu | Tomcat không đọc UTF-8 | Kiểm tra `web.xml` có `page-encoding UTF-8`; xoá `work`/`temp` trong thư mục Tomcat rồi restart |

---

## Nếu không dùng Tomcat, chạy bằng plugin Maven

Cài `cargo-maven2-plugin` rồi chạy:

```bat
mvn clean package
mvn org.apache.cargo:maven-cargo-plugin:run -Dcargo.servlet.port=8080
```

Tuy nhiên cách này **chưa được kiểm chứng** trong môi trường sinh mã (sandbox
không có Maven). Cách chắc chắn nhất vẫn là Bước 4 (WAR + Tomcat).
