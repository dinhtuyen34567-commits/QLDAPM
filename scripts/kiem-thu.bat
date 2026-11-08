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
    endlocal
    exit /b 1
)

if "%JAVA_HOME%"=="" (
    echo [LOI] Bien moi truong JAVA_HOME chua duoc dat.
    echo       Hay dat JAVA_HOME tro toi JDK 17 tro len.
    endlocal
    exit /b 1
)

REM Kiem tra phien ban JDK thuc su >= 17, vi du bi tro JAVA_HOME sang JDK 11.
REM Cach don gian va it loi: dung 'java -XshowSettings:properties' de lay
REM 'java.specification.version' (chi con so, khong co dau nhay hay dau cham).
REM Output dang ky: java.specification.version = 17
set "JAVA_MAJOR="
for /f "tokens=2 delims==" %%v in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /i "java.specification.version"') do (
    set "JAVA_MAJOR=%%v"
)
REM Cat khoang trang thua o dau/cuoi (findstr tra ve phan sau dau =)
for /f "tokens=* delims= " %%v in ("%JAVA_MAJOR%") do set "JAVA_MAJOR=%%v"
if not defined JAVA_MAJOR set "JAVA_MAJOR=0"
if %JAVA_MAJOR% LSS 17 (
    echo [LOI] Canh bao: phien ban Java hien tai khong phai 17 hoac cao hon.
    echo       Dung lenh 'java -version' de xem phien ban dang dung.
    endlocal
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

endlocal & exit /b %BUILD_RESULT%
