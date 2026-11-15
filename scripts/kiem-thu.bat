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

set "MVN_CMD=mvn"
where mvn >nul 2>nul
if errorlevel 1 (
    if exist "mvnw.cmd" (
        set "MVN_CMD=mvnw.cmd"
        echo [INFO] Khong tim thay 'mvn', dung Maven Wrapper: mvnw.cmd
    ) else (
        echo [LOI] Khong tim thay lenh 'mvn' va cung khong co 'mvnw.cmd'.
        echo       Hay cai Maven 3.9.x va them vao PATH, hoac dung Maven Wrapper.
        echo       Tai: https://maven.apache.org/download.cgi
        endlocal
        exit /b 1
    )
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

echo [INFO] Dang chay '%MVN_CMD% clean verify' ...
echo.

call %MVN_CMD% clean verify
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
