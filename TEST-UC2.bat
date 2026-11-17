@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"
title QLDAPM - Test UC-02 Tra cuu van don

set "APP=QLDAPM-ShippingOrder"
set "BASE=http://localhost:8080/%APP%"
set "DB_USER=root"
set "DB_PASS="

:menu
cls
echo ============================================================
echo  QLDAPM-ShippingOrder - Test UC-02 Tra cuu van don
echo ============================================================
echo.
echo  Web phai dang chay tai:
echo    %BASE%/
echo.
echo  Du lieu mau sau khi chay init_data.sql:
echo    Ma van don : VN12345678
echo    Trang thai : DANG_VAN_CHUYEN
echo.
echo  1. Mo man hinh tra cuu
echo  2. TC-02.1  Ma hop le VN12345678
echo  3. TC-02.2  Ma sai dinh dang
echo  4. TC-02.3  Ma khong ton tai VN99999999
echo  5. TC-02.4  Mat mang / timeout
echo  6. Kiem tra du lieu mau trong CSDL
echo  7. Mo trang chinh
echo  8. Thoat
echo.
set "CHON="
set /p "CHON=Chon [1-8]: "

if "%CHON%"=="1" goto openPage
if "%CHON%"=="2" goto tc01
if "%CHON%"=="3" goto tc02
if "%CHON%"=="4" goto tc03
if "%CHON%"=="5" goto tc04
if "%CHON%"=="6" goto checkDb
if "%CHON%"=="7" goto openHome
if "%CHON%"=="8" exit /b 0

echo.
echo [LOI] Lua chon khong hop le.
timeout /t 2 >nul
goto menu

:openPage
cls
call :header "Mo man hinh tra cuu"
echo Mo: %BASE%/tra-cuu
call :openUrl "%BASE%/tra-cuu"
echo.
pause
goto menu

:tc01
cls
call :header "TC-02.1 - Ma hop le VN12345678"
echo 1. He thong mo trang tra cuu va tu dien ma VN12345678.
echo 2. Bam [Tim kiem].
echo.
echo Ky vong:
echo   - Hien trang thai: Dang van chuyen
echo   - Thanh tien trinh 65%%
echo   - Hien chuyen xe CX0001, bien so 51C-12345, tai xe Tran Van Tai
echo   - Hien lo trinh LT0001, khoang cach 12,50 km
echo   - Hien 5 diem toa do va bieu do lo trinh
echo.
call :openUrl "%BASE%/tra-cuu?maVanDon=VN12345678"
echo.
pause
goto menu

:tc02
cls
call :header "TC-02.2 - Ma sai dinh dang"
echo 1. Mo trang tra cuu.
echo 2. Nhap ma: VN123
echo.
echo Ky vong:
echo   - Vien do quanh o nhap
echo   - Hien: "Ma van don khong hop le, vui long kiem tra lai"
echo   - Nut [Tim kiem] bi khoa
echo.
call :openUrl "%BASE%/tra-cuu"
echo.
pause
goto menu

:tc03
cls
call :header "TC-02.3 - Ma khong ton tai"
echo 1. He thong mo trang tra cuu voi ma VN99999999.
echo 2. Bam [Tim kiem].
echo.
echo Ky vong:
echo   - Hien thung hang rong
echo   - Thong bao: "Khong tim thay thong tin cho ma van don nay"
echo   - Khong hien thong tin don hang
echo.
call :openUrl "%BASE%/tra-cuu?maVanDon=VN99999999"
echo.
pause
goto menu

:tc04
cls
call :header "TC-02.4 - Mat mang / timeout"
echo 1. Mo trang tra cuu, nhap VN12345678.
echo 2. Mo DevTools -^> Network -^> Offline.
echo 3. Bam [Tim kiem].
echo.
echo Ky vong:
echo   - Spinner hien khoang 5 giay
echo   - Pop-up loi: "Loi ket noi may chu, vui long thu lai sau"
echo   - Bam [Dong], ma van don da nhap van con
echo.
call :openUrl "%BASE%/tra-cuu?maVanDon=VN12345678"
echo.
pause
goto menu

:checkDb
cls
call :header "Kiem tra du lieu mau UC-02"
where mysql >nul 2>nul
if errorlevel 1 (
    echo [CANH BAO] Khong tim thay mysql trong PATH.
    echo             Mo MySQL Workbench va chay cau SQL trong README.
    echo.
    pause
    goto menu
)
if "%DB_PASS%"=="" set /p "DB_PASS=Nhap mat khau MySQL root (Enter neu rong): "
set "MYSQL_PWD=%DB_PASS%"
mysql -u %DB_USER% -D vanchuyen -e "SELECT o.maVanDon, o.trangThai, o.chuyenXeId, c.maChuyen, x.bienSo, lt.maLoTrinh, lt.khoangCach, COUNT(n.id) AS soDiemToaDo FROM tblDonHang o LEFT JOIN tblChuyenXe c ON c.id = o.chuyenXeId LEFT JOIN tblXeTai x ON x.id = c.xeTaiId LEFT JOIN tblLoTrinh lt ON lt.chuyenXeId = c.id LEFT JOIN tblNhatKyToaDo n ON n.xeTaiId = c.xeTaiId WHERE o.maVanDon = 'VN12345678' GROUP BY o.maVanDon, o.trangThai, o.chuyenXeId, c.maChuyen, x.bienSo, lt.maLoTrinh, lt.khoangCach;"
if errorlevel 1 (
    echo.
    echo [LOI] Khong truy van duoc CSDL.
    echo       Kiem tra DB_USER/DB_PASSWORD va da chay init_data.sql chua.
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
