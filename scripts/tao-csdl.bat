@echo off
REM =====================================================================
REM Tao co so du lieu MySQL cho QLDAPM-ShippingOrder (UC-01)
REM
REM Yeu cau: MySQL 8.0.16 tro len da cai va dang chay.
REM Cach dung: tao-csdl.bat [user] [password]
REM           Vi du: tao-csdl.bat root 123456
REM =====================================================================
setlocal
cd /d "%~dp0.."

set DB_USER=%~1
if "%DB_USER%"=="" set DB_USER=root
set DB_PASS=%~2

echo.
echo ============================================================
echo  QLDAPM-ShippingOrder - Tao co so du lieu
echo ============================================================
echo.

where mysql >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay lenh 'mysql'.
    echo       Hay them thu muc bin cua MySQL vao PATH.
    exit /b 1
)

echo [INFO] Tao database va 18 bang tu database\schema.sql ...
mysql -u %DB_USER% -p%DB_PASS% < database\schema.sql
if errorlevel 1 (
    echo [LOI] That bai khi chay schema.sql
    exit /b 1
)

echo [INFO] Nap du lieu mau tu database\init_data.sql ...
mysql -u %DB_USER% -p%DB_PASS% < database\init_data.sql
if errorlevel 1 (
    echo [LOI] That bai khi chay init_data.sql
    exit /b 1
)

echo.
echo ============================================================
echo  DA TAO XONG CO SO DU LIEU.
echo.
echo  Khach hang mau:
echo    ten dang nhap : khachhang1
echo    mat khau      : Dev@12345
echo.
echo  Nhanh: goi API /api/v1/khach-hang/me de kiem tra.
echo ============================================================
