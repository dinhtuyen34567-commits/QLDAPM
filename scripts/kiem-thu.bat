@echo off
REM =====================================================================
REM Chay kiem thu don viet cho QLDAPM-ShippingOrder (UC-01)
REM
REM Yeu cau: da cai Maven 3.9.x va JDK 17 tro len (JAVA_HOME).
REM Cach dung: kiem-thu.bat
REM =====================================================================
setlocal
cd /d "%~dp0.."

echo.
echo ============================================================
echo  QLDAPM-ShippingOrder - Chay kiem thu
echo ============================================================
echo.

where mvn >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay lenh 'mvn'.
    echo       Hay cai Maven 3.9.x va them vao PATH.
    echo       Tai: https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip
    exit /b 1
)

if "%JAVA_HOME%"=="" (
    echo [LOI] Bien moi truong JAVA_HOME chua duoc dat.
    echo       Hay dat JAVA_HOME tro toi JDK 17 tro len.
    exit /b 1
)

echo [INFO] Java dang dung:
java -version
echo.

echo [INFO] Dang chay 'mvn clean verify' ...
echo.

call mvn clean verify
set BUILD_RESULT=%ERRORLEVEL%

echo.
if "%BUILD_RESULT%"=="0" (
    echo ============================================================
    echo  BUILD THANH CONG - tat ca kiem thu deu xanh.
    echo  Bao cao bao phu: target\site\jacoco\index.html
    echo  File WAR:        target\QLDAPM-ShippingOrder.war
    echo ============================================================
) else (
    echo ============================================================
    echo  BUILD THAT BAI - vui long xem log loi o tren.
    echo ============================================================
)

exit /b %BUILD_RESULT%
