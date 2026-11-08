@echo off
REM =====================================================================
REM Tao co so du lieu MySQL cho QLDAPM-ShippingOrder (UC-01)
REM
REM Yeu cau: MySQL 8.0.16 tro len da cai va dang chay.
REM
REM Cach dung:
REM     tao-csdl.bat [user] [password]
REM     tao-csdl.bat root 123456
REM
REM Mat khau co ky tu dac biet (vi du @ # $ !) van dung:
REM     tao-csdl.bat root "P@ss#w0rd"
REM
REM Khong truyen mat khau -> MySQL se hoi truc tiep (an toan hon):
REM     tao-csdl.bat root
REM =====================================================================
setlocal
cd /d "%~dp0.."

set DB_USER=%~1
if "%DB_USER%"=="" set DB_USER=root

REM Lay mat khau nguyen ban (giu nguyen ky dac biet) thay vi %~2
REM de khong bi cmd bo dau nhay.
set "DB_PASS="
if not "%~2"=="" set "DB_PASS=%~2"

echo.
echo ============================================================
echo  QLDAPM-ShippingOrder - Tao co so du lieu
echo ============================================================
echo.

where mysql >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay lenh 'mysql'.
    echo       Hay them thu muc bin cua MySQL vao PATH.
    endlocal
    exit /b 1
)

REM Dung MYSQL_PWD thay cho -p... de mat khau khong lo ra tren man hinh
REM va khong bi loi khi chua ky tu dac biet.
if not "%DB_PASS%"=="" set "MYSQL_PWD=%DB_PASS%"

echo [INFO] Tao database va 18 bang tu database\schema.sql ...
mysql -u %DB_USER% < database\schema.sql
if errorlevel 1 (
    echo [LOI] That bai khi chay schema.sql
    endlocal
    exit /b 1
)

echo [INFO] Nap du lieu mau tu database\init_data.sql ...
mysql -u %DB_USER% < database\init_data.sql
if errorlevel 1 (
    echo [LOI] That bai khi chay init_data.sql
    endlocal
    exit /b 1
)

set "MYSQL_PWD="

echo.
echo ============================================================
echo  DA TAO XONG CO SO DU LIEU.
echo.
echo  Khach hang mau:
echo    ten dang nhap : khachhang1
echo    mat khau      : Dev@12345
echo.
echo  Buoc tiep theo: xem docs\HUONG-DAN-MO-LAN-DAU.md
echo ============================================================
endlocal
exit /b 0
