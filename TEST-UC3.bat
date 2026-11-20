@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"
title QLDAPM - Test UC-03 Quan ly khieu nai & Dang nhap

set "APP=QLDAPM-ShippingOrder"
set "BASE=http://localhost:8080/%APP%"
set "DB_USER=root"
set "DB_PASS="

where mysql >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set "PATH=%PATH%;C:\Program Files\MySQL\MySQL Server 8.0\bin"
)

if "%DB_PASS%"=="" (
    for /f "tokens=2 delims==" %%p in ('findstr /i "^db.password=" src\main\resources\application.properties 2^>nul') do set "DB_PASS=%%p"
)

:menu
cls
echo ============================================================
echo  QLDAPM-ShippingOrder - Test UC-03 & Module Dang Nhap
echo ============================================================
echo.
echo  Ung dung phai dang chay tai:
echo    %BASE%/
echo.
echo  Tai khoan thu nghiem:
echo    - Nhan vien CSKH : cskh1 / Dev@12345 (hoac cskh2)
echo    - Khach hang     : khachhang1 / Dev@12345
echo.
echo  ============================================================
echo  1. Mo man hinh Dang nhap (/dang-nhap)
echo  2. Mo man hinh Gui khieu nai (/ho-tro/tao)
echo  3. Mo form khieu nai gan ma van don VN12345678
echo  4. Mo Danh sach yeu cau CSKH (/cskh/yeu-cau)
echo  5. Mo Chi tiet xu ly yeu cau mau YC00000001 (id=1)
echo  6. Xem du lieu bang tblYeuCauHoTro trong CSDL MySQL
echo  7. Xem tai khoan CSKH trong CSDL MySQL
echo  8. Mo Trang chu ung dung
echo  9. Thoat
echo ============================================================
echo.
set "CHON="
set /p "CHON=Chon [1-9]: "

if "%CHON%"=="1" goto openLogin
if "%CHON%"=="2" goto openComplaint
if "%CHON%"=="3" goto openComplaintWithCode
if "%CHON%"=="4" goto openSupportList
if "%CHON%"=="5" goto openSupportDetail
if "%CHON%"=="6" goto checkDbComplaints
if "%CHON%"=="7" goto checkDbStaff
if "%CHON%"=="8" goto openHome
if "%CHON%"=="9" exit /b 0

echo.
echo [LOI] Lua chon khong hop le.
timeout /t 2 >nul
goto menu

:openLogin
cls
call :header "1. Mo man hinh Dang nhap"
echo URL: %BASE%/dang-nhap
echo Goi y dang nhap:
echo   - CSKH: cskh1 / Dev@12345 (Chuyen huong toi /cskh/yeu-cau)
echo   - Khach hang: khachhang1 / Dev@12345 (Chuyen huong ve /)
call :openUrl "%BASE%/dang-nhap"
echo.
pause
goto menu

:openComplaint
cls
call :header "2. Mo man hinh Gui khieu nai (GDTaoKhieuNai)"
echo URL: %BASE%/ho-tro/tao
echo Kiem tra:
echo   - Khach hang co the gui an danh hoac da dang nhap
echo   - Bo dem ky tu noi dung (toi da 2000 ky tu)
echo   - Validate SĐT 10 so, email dung dinh dang
call :openUrl "%BASE%/ho-tro/tao"
echo.
pause
goto menu

:openComplaintWithCode
cls
call :header "3. Form khieu nai tu dong dien ma van don VN12345678"
echo URL: %BASE%/ho-tro/tao?trackingCode=VN12345678
echo Kiem tra o 'Ma van don lien quan' da duoc dien san 'VN12345678'
call :openUrl "%BASE%/ho-tro/tao?trackingCode=VN12345678"
echo.
pause
goto menu

:openSupportList
cls
call :header "4. Danh sach yeu cau ho tro CSKH (GDDanhSachYeuCau)"
echo URL: %BASE%/cskh/yeu-cau
echo Luu y: Phai dang nhap tai khoan CSKH (cskh1 hoac cskh2) de truy cap.
echo Neu chua dang nhap, AuthFilter se tu dong chuyen huong ve /dang-nhap.
call :openUrl "%BASE%/cskh/yeu-cau"
echo.
pause
goto menu

:openSupportDetail
cls
call :header "5. Chi tiet va phan hoi yeu cau YC00000001 (GDXuLyYeuCau)"
echo URL: %BASE%/cskh/yeu-cau/chi-tiet?id=1
echo Kiem tra:
echo   - Thong tin nguoi gui va noi dung khieu nai
echo   - Form phan hoi va chuyen trang thai DANG_XU_LY / DA_DONG
echo   - Quy tac khoa tuong tranh BR-25 khi 2 CSKH cung thao tac
call :openUrl "%BASE%/cskh/yeu-cau/chi-tiet?id=1"
echo.
pause
goto menu

:checkDbComplaints
cls
call :header "6. Kiem tra du lieu bang tblYeuCauHoTro"
where mysql >nul 2>nul
if errorlevel 1 (
    echo [CANH BAO] Khong tim thay mysql trong PATH.
    echo             Mo MySQL Workbench de kiem tra bang tblYeuCauHoTro.
    echo.
    pause
    goto menu
)
if "%DB_PASS%"=="" set /p "DB_PASS=Nhap mat khau MySQL root (Enter neu rong): "
set "MYSQL_PWD=%DB_PASS%"
mysql -u %DB_USER% -D vanchuyen -e "SELECT y.id, y.maYeuCau, u.hoTen, u.sdt, y.trangThai, y.nhanVienId, y.noiDung FROM tblYeuCauHoTro y LEFT JOIN tblKhachHang kh ON kh.nguoiDungId = y.khachHangId LEFT JOIN tblNguoiDung u ON u.id = kh.nguoiDungId ORDER BY y.id DESC LIMIT 10;"
if errorlevel 1 (
    echo [LOI] Khong truy van duoc CSDL. Kiem tra mat khau MySQL.
)
set "MYSQL_PWD="
echo.
pause
goto menu

:checkDbStaff
cls
call :header "7. Kiem tra tai khoan CSKH trong CSDL"
where mysql >nul 2>nul
if errorlevel 1 (
    echo [CANH BAO] Khong tim thay mysql trong PATH.
    pause
    goto menu
)
if "%DB_PASS%"=="" set /p "DB_PASS=Nhap mat khau MySQL root (Enter neu rong): "
set "MYSQL_PWD=%DB_PASS%"
mysql -u %DB_USER% -D vanchuyen -e "SELECT u.id, u.username, n.viTri, u.hoTen, u.sdt FROM tblNguoiDung u JOIN tblNhanVien n ON n.nguoiDungId = u.id;"
if errorlevel 1 (
    echo [LOI] Khong truy van duoc CSDL. Kiem tra mat khau MySQL.
)
set "MYSQL_PWD="
echo.
pause
goto menu

:openHome
call :openUrl "%BASE%/"
pause
goto menu

:openUrl
if defined NO_OPEN exit /b 0
start "" "%~1"
exit /b 0

:header
echo ============================================================
echo  %~1
echo ============================================================
echo.
exit /b 0
