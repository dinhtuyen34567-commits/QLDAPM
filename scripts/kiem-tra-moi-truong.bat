@echo off
REM =====================================================================
REM Kiem tra toan bo moi truong de chay QLDAPM-ShippingOrder (UC-01)
REM
REM Cach dung:
REM     scripts\kiem-tra-moi-truong.bat
REM
REM Script nay KHONG thay doi gi ca - chi bao loi. Chay len sau khi
REM cai JDK/Maven/MySQL/Tomcat va tao CSDL, de biet con thieu gi.
REM
REM Luu y: dung 'enabledelayedexpansion' de cac bien set trong khoi
REM if/for duoc doc dung gia tri moi nhat (mac dinh cmd doc %VAR% tai
REM thoi diem parse, nen se doc gia tri cu).
REM =====================================================================
setlocal enabledelayedexpansion
cd /d "%~dp0.."

set FAIL=0
set WARN=0

echo.
echo ============================================================
echo  QLDAPM-ShippingOrder - Kiem tra moi truong
echo ============================================================
echo.

REM ---------------------------------------------------------------------
echo [1/6] JDK (can 17 tro len)
REM ---------------------------------------------------------------------
where java >nul 2>nul
if errorlevel 1 (
    echo   [LOI] Khong tim thay lenh 'java'. Hay cai JDK 17 va them vao PATH.
    set /a FAIL+=1
) else (
    set "JAVA_MAJOR="
    for /f "tokens=2 delims==" %%v in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /i "java.specification.version"') do (
        set "JAVA_MAJOR=%%v"
    )
    REM Cat khoang trang thua, dung !...! de lay gia tri moi nhat
    for /f "tokens=* delims= " %%v in ("!JAVA_MAJOR!") do set "JAVA_MAJOR=%%v"
    if not defined JAVA_MAJOR set "JAVA_MAJOR=0"
    if !JAVA_MAJOR! LSS 17 (
        echo   [LOI] Phien ban Java dang dung la !JAVA_MAJOR! - can 17 tro len.
        set /a FAIL+=1
    ) else (
        echo   [OK] Java !JAVA_MAJOR!
    )
)
if not "%JAVA_HOME%"=="" (
    echo        JAVA_HOME = %JAVA_HOME%
) else (
    echo   [CANH BAO] JAVA_HOME chua duoc dat.
    set /a WARN+=1
)

REM ---------------------------------------------------------------------
echo.
echo [2/6] Maven
REM ---------------------------------------------------------------------
where mvn >nul 2>nul
if errorlevel 1 (
    echo   [LOI] Khong tim thay lenh 'mvn'. Hay cai Maven 3.9.x va them vao PATH.
    set /a FAIL+=1
) else (
    echo   [OK] Maven tim thay.
)

REM ---------------------------------------------------------------------
echo.
echo [3/6] MySQL
REM ---------------------------------------------------------------------
where mysql >nul 2>nul
if errorlevel 1 (
    echo   [LOI] Khong tim thay lenh 'mysql'. Hay cai MySQL 8 va them vao PATH.
    set /a FAIL+=1
) else (
    echo   [OK] MySQL client tim thay.
)

REM ---------------------------------------------------------------------
echo.
echo [4/6] Tomcat 10.1
REM ---------------------------------------------------------------------
if "%CATALINA_HOME%"=="" (
    echo   [LOI] Bien moi truong CATALINA_HOME chua duoc dat.
    set /a FAIL+=1
) else if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo   [LOI] Khong tim thay %CATALINA_HOME%\bin\catalina.bat
    set /a FAIL+=1
) else (
    echo   [OK] CATALINA_HOME = %CATALINA_HOME%
    if exist "%CATALINA_HOME%\bin\setenv.bat" (
        echo        [OK] Da co setenv.bat
    ) else (
        echo        [CANH BAO] Chua co setenv.bat - app se khong ket noi duoc CSDL.
        echo                 Copy scripts\setenv-mau.bat vao %CATALINA_HOME%\bin\setenv.bat
        set /a WARN+=1
    )
)

REM ---------------------------------------------------------------------
echo.
echo [5/6] File WAR
REM ---------------------------------------------------------------------
if exist "target\QLDAPM-ShippingOrder.war" (
    echo   [OK] target\QLDAPM-ShippingOrder.war da ton tai.
) else (
    echo   [CANH BAO] Chua build. Chay: scripts\kiem-thu.bat
    set /a WARN+=1
)

REM ---------------------------------------------------------------------
echo.
echo [6/6] Ket noi CSDL
REM ---------------------------------------------------------------------
REM Doc thong tin ket noi tu setenv.bat cua Tomcat neu co.
REM Luu y: phai khop chinh xac "DB_USER=" va "DB_PASSWORD=",
REM neu khong "DB_URL=" se bi match nham (cung chua "DB_USER" trong "DB_URL"? khong,
REM nhung de chac, ta dung dau = ngay sau ten bien).
set "DB_USER_CHECK=root"
set "DB_PASS_CHECK="
if not "%CATALINA_HOME%"=="" if exist "%CATALINA_HOME%\bin\setenv.bat" (
    for /f "tokens=2 delims==" %%v in ('findstr /i /r /c:"^set \"DB_USER=" "%CATALINA_HOME%\bin\setenv.bat"') do set "DB_USER_CHECK=%%v"
    for /f "tokens=2 delims==" %%v in ('findstr /i /r /c:"^set \"DB_PASSWORD=" "%CATALINA_HOME%\bin\setenv.bat"') do set "DB_PASS_CHECK=%%v"
)
set "MYSQL_PWD=%DB_PASS_CHECK%"
mysql -u %DB_USER_CHECK% -e "USE vanchuyen; SELECT COUNT(*) FROM tblHangHoa;" >nul 2>&1
if errorlevel 1 (
    echo   [LOI] Khong ket noi duoc CSDL 'vanchuyen'.
    echo        Kiem tra: mat khau DB trong setenv.bat, va da chay scripts\tao-csdl.bat chua.
    set /a FAIL+=1
) else (
    echo   [OK] Ket noi CSDL 'vanchuyen' thanh cong.
)
set "MYSQL_PWD="

REM ---------------------------------------------------------------------
echo.
echo ============================================================
if !FAIL! GTR 0 (
    echo  KET QUA: !FAIL! loi, !WARN! canh bao.
    echo  Hay sua cac loi o tren truoc khi chay thu.
) else if !WARN! GTR 0 (
    echo  KET QUA: khong co loi, !WARN! canh bao.
    echo  Co the chay tiep, nhung nen xem cac canh bao o tren.
) else (
    echo  KET QUA: moi truong san sang. Chay scripts\deploy-tomcat.bat de deploy.
)
echo ============================================================
echo.
endlocal
exit /b 0
