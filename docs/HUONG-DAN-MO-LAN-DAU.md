# Hướng dẫn mở dự án lần đầu

Dành cho người vừa nhận bàn giao mô-đun **UC-01 – Tạo đơn hàng vận chuyển**.

Các bước 2, 3, 4 đã được tự động hoá bằng script trong `scripts/`, nên hướng dẫn
này chỉ còn lại phần **cài đặt** và **chạy thử**.

---

## Tóm tắt — 4 bước

| Bước | Việc cần làm | Thời gian ước tính |
|---|---|---|
| 1 | Cài JDK 17, Maven, MySQL 8, Tomcat 10.1 | 30–60 phút |
| 2 | Tạo CSDL + dữ liệu mẫu | 1 phút |
| 3 | Cấu hình kết nối cho Tomcat | 2 phút |
| 4 | Build, deploy và chạy thử | 5 phút |

---

## Bước 1 — Cài phần mềm

### 1.1. Kiểm tra máy đã có gì

Mở **Command Prompt (cmd)** và gõ:

```bat
java -version
mvn -version
mysql --version
```

| Lệnh | Kết quả mong đợi | Nếu báo không tìm thấy |
|---|---|---|
| `java -version` | `17` trở lên | Cài JDK 17 → mục 1.2 |
| `mvn -version` | `Apache Maven 3.9.x` | Cài Maven → mục 1.3 |
| `mysql --version` | `8.0.16` trở lên | Cài MySQL → mục 1.4 |

Sau khi cài xong, chạy script kiểm tra để biết còn thiếu gì:

```bat
scripts\kiem-tra-moi-truong.bat
```

Script này chỉ **báo cáo**, không thay đổi gì.

### 1.2. Cài JDK 17

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

> Máy đang có Java 11 thì cài JDK 17 **song song**, rồi trỏ `JAVA_HOME` sang bản 17.
> Không cần gỡ Java 11.

### 1.3. Cài Maven

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

### 1.4. Cài MySQL 8

1. Tải **MySQL Installer for Windows**:
   <https://dev.mysql.com/downloads/installer/>
2. Cài, chọn **Developer Default** hoặc tối thiểu **MySQL Server 8.0**.
3. **Bắt buộc nhớ mật khẩu root** bạn đặt ở bước cài — sẽ dùng ở Bước 2 và Bước 3.
4. Thêm `C:\Program Files\MySQL\MySQL Server 8.0\bin` vào `Path`
   (để gõ được lệnh `mysql` trong cmd).
5. Kiểm tra:
   ```bat
   mysql --version
   ```

### 1.5. Cài Tomcat 10.1

1. Tải **Tomcat 10.1** (bản 64-bit Windows zip):
   <https://tomcat.apache.org/download-10.cgi>
   - ⚠️ **Phải là bản 10.1**, không dùng Tomcat 9 hay 11.
     Dự án dùng `jakarta.*` nên Tomcat 9 sẽ không chạy được.
2. Giải nén, ví dụ vào `C:\apache-tomcat-10.1.x`
3. Đặt biến môi trường:
   - `CATALINA_HOME` = `C:\apache-tomcat-10.1.x`
   - Thêm `%CATALINA_HOME%\bin` vào `Path`
4. Mở **cmd mới** và kiểm tra:
   ```bat
   scripts\tomcat.bat status
   ```

### 1.6. (Tùy chọn) Cài IDE

IntelliJ IDEA Community hoặc Eclipse. Với Eclipse cần thêm **Tomcat 10** vào
`Preferences > Server > Runtime Environments`.

> Dự án là WAR (không phải project Maven của IDE), nên cách chạy chắc chắn nhất
> vẫn là **build bằng Maven rồi deploy vào Tomcat** như Bước 4.

---

## Bước 2 — Tạo cơ sở dữ liệu

Mở **cmd tại thư mục dự án** (`...\QLDAPM-ShippingOrder`) và chạy:

```bat
scripts\tao-csdl.bat root <mat_khau_mysql>
```

Ví dụ mật khẩu là `123456`:

```bat
scripts\tao-csdl.bat root 123456
```

Script sẽ chạy `database\schema.sql` (tạo database `vanchuyen` + 18 bảng) rồi
`database\init_data.sql` (khách hàng mẫu + 6 hàng hóa).

**Mật khẩu có ký tự đặc biệt** (`@`, `#`, `$`, `!`…) vẫn chạy được:

```bat
scripts\tao-csdl.bat root "P@ss#w0rd"
```

> Script dùng biến `MYSQL_PWD` thay cho tham số `-p`, nên mật khẩu không bị lộ
> trên màn hình và không vỡ khi chứa ký tự đặc biệt. Bỏ trống mật khẩu
> (`scripts\tao-csdl.bat root`) thì MySQL sẽ hỏi trực tiếp.

`init_data.sql` dùng `INSERT IGNORE` nên **chạy lại nhiều lần cũng được**,
không sinh bản ghi trùng.

---

## Bước 3 — Cấu hình kết nối cho Tomcat

Tomcat không tự đọc biến môi trường của Windows. Cần copy file mẫu có sẵn vào
thư mục `bin` của Tomcat:

```bat
copy scripts\setenv-mau.bat "%CATALINA_HOME%\bin\setenv.bat"
```

Sau đó mở `%CATALINA_HOME%\bin\setenv.bat` bằng Notepad và sửa **mật khẩu MySQL**:

```bat
set "DB_PASSWORD=mat_khau_mysql_cua_ban"
```

File mẫu đã có sẵn `DB_URL` đúng của dự án và `APP_DEV_LOGIN=true` để chạy thử.

> ⚠️ `APP_DEV_LOGIN=true` chỉ dùng khi **chạy thử cục bộ**. Xem Bước 4.4.

---

## Bước 4 — Build, deploy và chạy thử

### 4.1. Build và kiểm thử

```bat
scripts\kiem-thu.bat
```

Lệnh này chạy `mvn clean verify`: biên dịch → 40 unit test → kiểm tra ngưỡng
bao phủ JaCoCo → đóng gói file WAR.

**Kết quả mong đợi:**

```
[INFO] BUILD SUCCESS
[INFO] Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
```

Báo cáo bao phủ: `target\site\jacoco\index.html`

### 4.2. Deploy lên Tomcat

```bat
scripts\deploy-tomcat.bat
```

Script sẽ: build lại (`mvn clean package`) → dừng Tomcat → copy file WAR vào
`%CATALINA_HOME%\webapps\` → xoá bản deploy cũ → khởi động lại Tomcat.

Đã build rồi và chỉ muốn copy lại WAR (nhanh hơn):

```bat
scripts\deploy-tomcat.bat --no-build
```

### 4.3. Mở trình duyệt

```
http://localhost:8080/QLDAPM-ShippingOrder/
```

Phải thấy trang **"Hệ thống đơn hàng vận chuyển"** với nút **[Tạo đơn hàng mới]**.

### 4.4. Đăng nhập để dùng được chức năng tạo đơn

Module đăng nhập thật **chưa** có trong phạm vi Sprint này, nên dự án có một
servlet tạm thời `/dev-login` (mặc định **tắt**).

Nếu `setenv.bat` đã có `APP_DEV_LOGIN=true` thì mở thẳng:

```
http://localhost:8080/QLDAPM-ShippingOrder/dev-login?userId=1
```

Sau đó bấm **[Tạo đơn hàng mới]**.

| Tài khoản mẫu | Mật khẩu |
|---|---|
| `khachhang1` | `Dev@12345` |

> ⚠️ **Tắt dev-login khi nộp bài hoặc chạy trên máy chung.** Xoá dòng
> `APP_DEV_LOGIN` trong `%CATALINA_HOME%\bin\setenv.bat`, rồi
> `scripts\tomcat.bat restart`. Servlet này được ghi chú rõ là "tạm thời, bỏ khi
> có module đăng nhập thật".

### 4.5. Điều khiển Tomcat

```bat
scripts\tomcat.bat start      REM khởi động
scripts\tomcat.bat stop       REM dừng
scripts\tomcat.bat restart    REM restart (bắt buộc sau khi đổi setenv.bat)
scripts\tomcat.bat status     REM kiểm tra đang chay hay chưa
```

> Sau khi đổi `setenv.bat`, **phải restart** Tomcat thì app mới đọc được biến
> môi trường mới.

### 4.6. Chạy thử luồng chính

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

## Danh sách script

| Script | Việc |
|---|---|
| `scripts\kiem-tra-moi-truong.bat` | Kiểm tra JDK / Maven / MySQL / Tomcat / WAR / CSDL. Chỉ báo cáo |
| `scripts\tao-csdl.bat` | Tạo CSDL `vanchuyen` + 18 bảng + dữ liệu mẫu |
| `scripts\kiem-thu.bat` | `mvn clean verify` (build + 40 test + kiểm tra bao phủ) |
| `scripts\deploy-tomcat.bat` | Build → copy WAR → restart Tomcat |
| `scripts\tomcat.bat` | `start` / `stop` / `restart` / `status` |
| `scripts\setenv-mau.bat` | File mẫu cấu hình, copy vào `%CATALINA_HOME%\bin\setenv.bat` |

---

## Xử lý sự cố thường gặp

Trước tiên hãy chạy `scripts\kiem-tra-moi-truong.bat` — nó chỉ ra phần lớn lỗi.

| Triệu chứng | Nguyên nhân | Cách sửa |
|---|---|---|
| `mvn` không chạy | Chưa cài Maven hoặc chưa mở cmd mới | Cài Maven → thêm `%MAVEN_HOME%\bin` vào `Path` → **mở cmd mới** |
| `release version 17 not supported` | `JAVA_HOME` trỏ JDK 11 | Đặt `JAVA_HOME` sang JDK 17, mở cmd mới |
| `kiem-thu.bat` báo *"phien ban Java hien tai khong phai 17"* | `JAVA_HOME` trỏ sai JDK | Đổi `JAVA_HOME` sang JDK 17 rồi chạy lại |
| Tomcat 404 mọi đường dẫn | Deploy WAR sai chỗ | Chạy lại `scripts\deploy-tomcat.bat` |
| `ClassNotFoundException: jakarta.servlet...` | Dùng Tomcat 9 | Đổi sang Tomcat **10.1** |
| `Access denied for user 'root'` | Sai mật khẩu CSDL | Sửa `DB_PASSWORD` trong `setenv.bat`, rồi `scripts\tomcat.bat restart` |
| `Unknown database 'vanchuyen'` | Chưa chạy `schema.sql` | Chạy lại Bước 2 |
| Trang trắng, không có dữ liệu | Thiếu `setenv.bat` trong `%CATALINA_HOME%\bin` | Làm Bước 3, rồi `scripts\tomcat.bat restart` |
| Đổi `setenv.bat` mà app không đổi theo | Tomcat chưa restart | `scripts\tomcat.bat restart` |
| API trả `401 KHONG_CO_QUYEN` | Chưa đăng nhập | Mở `/dev-login?userId=1` (Bước 4.4) |
| Tiếng Việt hiển thị sai dấu | Tomcat không đọc UTF-8 | Xoá `work`/`temp` trong thư mục Tomcat rồi restart |
| Cổng 8080 đã bị chiếm | Ứng dụng khác đang chạy | Đổi `server.xml` (`Connector port="8080"` → `8081`) rồi restart |

---

## Ghi chú

> **Chưa kiểm chứng được trong môi trường sinh mã:** sandbox không có Maven,
> MySQL, Tomcat và bị chặn mạng, nên toàn bộ quy trình trên **chưa được chạy
> thật**. Các đường dẫn, tên script, biến môi trường và cây SQL trong hướng dẫn
> đã được đối chiếu với mã nguồn; cú pháp `.bat` đã được kiểm tra cân bằng ngoặc
> và mô phỏng logic trích phiên bản Java.
>
> Khi chạy lần đầu, nếu có lỗi gửi lại phần log để sửa tiếp.
