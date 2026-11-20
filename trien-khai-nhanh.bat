@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"
title QLDAPM - Trien Khai Nhanh

REM =====================================================================
REM SCRIPT TRIEN KHAI NHANH QLDAPM-ShippingOrder (UC-01, UC-02, UC-03)
REM
REM Cach dung:
REM     trien-khai-nhanh.bat
REM     trien-khai-nhanh.bat [mat_khau_mysql]
REM     trien-khai-nhanh.bat [mat_khau_mysql] [duong_dan_tomcat]
REM =====================================================================

set "APP_NAME=QLDAPM-ShippingOrder"
set "DB_USER=root"
set "DB_PASS=%~1"
set "TOMCAT_DIR=%~2"

if "%DB_PASS%"=="" (
    for /f "tokens=2 delims==" %%p in ('findstr /i "^db.password=" src\main\resources\application.properties 2^>nul') do set "DB_PASS=%%p"
)

echo.
echo ============================================================
echo   QLDAPM-ShippingOrder - TRIEN KHAI NHANH
echo ============================================================
echo.

REM --- 1. Kiem tra Java ---
where java >nul 2>nul
if errorlevel 1 (
    echo [LOI] Khong tim thay Java. Vui long cai JDK 17 tro len va them vao PATH.
    pause
    exit /b 1
)
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-24\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-24"
    if exist "C:\Program Files\Java\jdk-17\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    if exist "C:\Program Files\Java\jdk-21\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-21"
)

REM --- 2. Xac dinh cong cu Maven ---
set "MVN_CMD="
if exist "mvnw.cmd" (
    set "MVN_CMD=mvnw.cmd"
) else (
    where mvn >nul 2>nul
    if not errorlevel 1 set "MVN_CMD=mvn"
)

if not defined MVN_CMD (
    echo [LOI] Khong tim thay 'mvnw.cmd' hoac 'mvn'.
    pause
    exit /b 1
)

REM --- 3. Kiem tra / Cap nhat CSDL MySQL ---
echo [1/4] Kiem tra Co so du lieu MySQL...
where mysql >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
        set "PATH=%PATH%;C:\Program Files\MySQL\MySQL Server 8.0\bin"
    )
)
where mysql >nul 2>nul
if errorlevel 1 (
    echo [CANH BAO] Khong tim thay lenh 'mysql' trong PATH.
    echo           Neu da co CSDL 'vanchuyen' thi co the bo qua buoc nay.
) else (
    if "%DB_PASS%"=="" (
        set /p "DB_PASS=Nhap mat khau MySQL root (an Enter neu khong co): "
    )
    if not "!DB_PASS!"=="" set "MYSQL_PWD=!DB_PASS!"

    echo [INFO] Cap nhat / khoi tao CSDL vanchuyen tu schema.sql va init_data.sql...
    mysql -u %DB_USER% --default-character-set=utf8mb4 -e "source %CD:\=/%/database/schema.sql" 2>nul
    mysql -u %DB_USER% --default-character-set=utf8mb4 -e "source %CD:\=/%/database/init_data.sql" 2>nul
    if errorlevel 1 (
        echo [CANH BAO] Khong the nap CSDL tu dong. Hay dam bao MySQL dang chay va dung mat khau.
    ) else (
        echo [OK] CSDL 'vanchuyen' da san sang voi day du du lieu mau UC-01, UC-02 va UC-03.
    )
    set "MYSQL_PWD="
)
echo.

REM --- 4. Dong goi WAR ---
echo [2/4] Dang dong goi file WAR (%MVN_CMD% package -DskipTests)...
call %MVN_CMD% clean package -DskipTests -Dfile.encoding=UTF-8
if errorlevel 1 (
    echo.
    echo [LOI] Dong goi WAR that bai. Xem log loi o tren.
    pause
    exit /b 1
)

if not exist "target\%APP_NAME%.war" (
    echo [LOI] Khong tim thay target\%APP_NAME%.war sau khi build.
    pause
    exit /b 1
)
echo [OK] Da build thanh cong target\%APP_NAME%.war
echo.

REM --- 5. Xac dinh thu muc Tomcat ---
echo [3/4] Xac dinh thu muc cai dat Apache Tomcat 10.1...

if not "%TOMCAT_DIR%"=="" (
    set "CATALINA_HOME=%TOMCAT_DIR%"
)

if "%CATALINA_HOME%"=="" (
    for /d %%D in ("%~dp0tomcat" "%~dp0..\tomcat" "E:\Programs\apache-tomcat-10.1.52-windows-x64\apache-tomcat-10.1.52" "C:\apache-tomcat-10*" "D:\apache-tomcat-10*" "C:\Program Files\Apache Software Foundation\Tomcat 10*" "%USERPROFILE%\apache-tomcat-10*" "%USERPROFILE%\Downloads\apache-tomcat-10*") do (
        if exist "%%~fD\bin\catalina.bat" (
            set "CATALINA_HOME=%%~fD"
            goto :found_tomcat
        )
    )
)

:found_tomcat
if "%CATALINA_HOME%"=="" (
    echo [CANH BAO] Chua dat bien moi truong CATALINA_HOME va khong tim thay Tomcat 10 tu dong.
    set /p "CATALINA_HOME=Vui long nhap duong dan thu muc Tomcat (vi du C:\apache-tomcat-10.1.x): "
)

if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo.
    echo [LOI] Khong tim thay %CATALINA_HOME%\bin\catalina.bat
    echo       File WAR da san sang tai: %CD%\target\%APP_NAME%.war
    echo       Ban co the copy file WAR nay vao webapps cua Tomcat.
    pause
    exit /b 1
)

echo [OK] Tomcat duoc tim thay tai: %CATALINA_HOME%

REM --- 6. Cau hinh setenv.bat neu chua co ---
if not exist "%CATALINA_HOME%\bin\setenv.bat" (
    echo [INFO] Dang tao %CATALINA_HOME%\bin\setenv.bat...
    > "%CATALINA_HOME%\bin\setenv.bat" echo @echo off
    >> "%CATALINA_HOME%\bin\setenv.bat" echo if "%%JAVA_HOME%%"=="" (
    >> "%CATALINA_HOME%\bin\setenv.bat" echo     if exist "C:\Program Files\Java\jdk-24\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-24"
    >> "%CATALINA_HOME%\bin\setenv.bat" echo     if exist "C:\Program Files\Java\jdk-17\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    >> "%CATALINA_HOME%\bin\setenv.bat" echo     if exist "C:\Program Files\Java\jdk-21\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    >> "%CATALINA_HOME%\bin\setenv.bat" echo )
    >> "%CATALINA_HOME%\bin\setenv.bat" echo set "DB_URL=jdbc:mysql://localhost:3306/vanchuyen?useUnicode=true^&characterEncoding=UTF-8^&useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=Asia/Ho_Chi_Minh"
    >> "%CATALINA_HOME%\bin\setenv.bat" echo set "DB_USER=%DB_USER%"
    >> "%CATALINA_HOME%\bin\setenv.bat" echo set "DB_PASSWORD=!DB_PASS!"
    >> "%CATALINA_HOME%\bin\setenv.bat" echo set "APP_DEV_LOGIN=true"
    echo [OK] Da tao setenv.bat thanh cong.
) else (
    if not "!DB_PASS!"=="" (
        > "%CATALINA_HOME%\bin\setenv.bat" echo @echo off
        >> "%CATALINA_HOME%\bin\setenv.bat" echo if "%%JAVA_HOME%%"=="" (
        >> "%CATALINA_HOME%\bin\setenv.bat" echo     if exist "C:\Program Files\Java\jdk-24\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-24"
        >> "%CATALINA_HOME%\bin\setenv.bat" echo     if exist "C:\Program Files\Java\jdk-17\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-17"
        >> "%CATALINA_HOME%\bin\setenv.bat" echo     if exist "C:\Program Files\Java\jdk-21\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-21"
        >> "%CATALINA_HOME%\bin\setenv.bat" echo )
        >> "%CATALINA_HOME%\bin\setenv.bat" echo set "DB_URL=jdbc:mysql://localhost:3306/vanchuyen?useUnicode=true^&characterEncoding=UTF-8^&useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=Asia/Ho_Chi_Minh"
        >> "%CATALINA_HOME%\bin\setenv.bat" echo set "DB_USER=%DB_USER%"
        >> "%CATALINA_HOME%\bin\setenv.bat" echo set "DB_PASSWORD=!DB_PASS!"
        >> "%CATALINA_HOME%\bin\setenv.bat" echo set "APP_DEV_LOGIN=true"
        echo [OK] Da cap nhat mat khau MySQL vao setenv.bat.
    )
)

REM --- 7. Deploy len Tomcat ---
echo [4/4] Deploy WAR va khoi dong Tomcat...
echo [INFO] Dung Tomcat neu dang chay...
call "%CATALINA_HOME%\bin\shutdown.bat" >nul 2>nul
timeout /t 2 /nobreak >nul 2>nul

if exist "%CATALINA_HOME%\webapps\%APP_NAME%" (
    echo [INFO] Xoa ban deploy cu...
    rd /s /q "%CATALINA_HOME%\webapps\%APP_NAME%" 2>nul
)

echo [INFO] Copy file WAR vao %CATALINA_HOME%\webapps\...
copy /Y "target\%APP_NAME%.war" "%CATALINA_HOME%\webapps\%APP_NAME%.war" >nul
if errorlevel 1 (
    echo [LOI] Khong the copy vao webapps. Hay kiem tra quyen ghi hoac tat han tien trinh Tomcat.
    pause
    exit /b 1
)

echo [INFO] Khoi dong lai Tomcat...
start "" "%CATALINA_HOME%\bin\startup.bat"

timeout /t 3 /nobreak >nul 2>nul

echo.
echo ============================================================
echo   TRIEN KHAI THANH CONG!
echo ============================================================
echo.
echo  Ung dung dang chay tai:
echo    http://localhost:8080/%APP_NAME%/
echo.
echo  Cac duong dan truc tiep:
echo    - Trang chu:          http://localhost:8080/%APP_NAME%/
echo    - Dang nhap:          http://localhost:8080/%APP_NAME%/dang-nhap
echo    - Tao khieu nai:      http://localhost:8080/%APP_NAME%/ho-tro/tao
echo    - Quan ly CSKH:       http://localhost:8080/%APP_NAME%/cskh/yeu-cau
echo    - Tra cuu van don:    http://localhost:8080/%APP_NAME%/tra-cuu
echo.
echo  Tai khoan thu nghiem:
echo    - CSKH:               cskh1 / Dev@12345 (hoac cskh2)
echo    - Khach hang:         khachhang1 / Dev@12345
echo.
echo ============================================================

start "" "http://localhost:8080/%APP_NAME%/"

pause
exit /b 0
