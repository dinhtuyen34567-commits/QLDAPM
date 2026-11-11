@echo off
REM =====================================================================
REM Build va deploy len Tomcat 10.1 cho QLDAPM-ShippingOrder (UC-01)
REM
REM Yeu cau:
REM   - da cai Maven 3.9.x + JDK 17 (JAVA_HOME)
REM   - da cai Tomcat 10.1 va dat CATALINA_HOME
REM   - da co file %CATALINA_HOME%\bin\setenv.bat (xem HUONG-DAN-MO-LAN-DAU.md)
REM
REM Cach dung:
REM     scripts\deploy-tomcat.bat            (build + copy WAR + restart Tomcat)
REM     scripts\deploy-tomcat.bat --no-build (chi copy WAR + restart)
REM =====================================================================
setlocal
cd /d "%~dp0.."

set APP_NAME=QLDAPM-ShippingOrder
set DO_BUILD=1
if /i "%~1"=="--no-build" set DO_BUILD=0

echo.
echo ============================================================
echo  QLDAPM-ShippingOrder - Deploy len Tomcat
echo ============================================================
echo.

REM --- Kiem tra CATALINA_HOME ---
if "%CATALINA_HOME%"=="" (
    echo [LOI] Bien moi truong CATALINA_HOME chua duoc dat.
    echo       Hay dat CATALINA_HOME tro toi thu muc cai Tomcat 10.1.
    endlocal
    exit /b 1
)
if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo [LOI] Khong tim thay %CATALINA_HOME%\bin\catalina.bat
    echo       Hay kiem tra lai gia tri CATALINA_HOME.
    endlocal
    exit /b 1
)

REM --- Kiem tra setenv.bat (khong co se khong ket noi duoc CSDL) ---
if not exist "%CATALINA_HOME%\bin\setenv.bat" (
    echo [CANH BAO] Khong tim thay %CATALINA_HOME%\bin\setenv.bat
    echo       App se khong ket noi duoc CSDL va bao loi.
    echo.
    echo       Hay copy file mau co san:
    echo         copy scripts\setenv-mau.bat "%CATALINA_HOME%\bin\setenv.bat"
    echo       ROI sua DB_PASSWORD cho khop mat khau MySQL, va restart Tomcat.
    echo.
)

REM --- Build ---
if "%DO_BUILD%"=="1" (
    where mvn >nul 2>nul
    if errorlevel 1 (
        echo [LOI] Khong tim thay lenh 'mvn'.
        endlocal
        exit /b 1
    )
    echo [INFO] Dang build 'mvn clean package' ...
    echo.
    call mvn clean package
    if errorlevel 1 (
        echo.
        echo [LOI] BUILD THAT BAI - xem log loi o tren.
        endlocal
        exit /b 1
    )
    echo.
) else (
    echo [INFO] Bo qua build (tuy chon --no-build).
    echo.
)

REM --- Kiem tra file WAR ---
if not exist "target\%APP_NAME%.war" (
    echo [LOI] Khong tim thay target\%APP_NAME%.war
    echo       Hay chay 'mvn clean package' truoc.
    endlocal
    exit /b 1
)

REM --- Dung Tomcat truoc khi thay the file WAR ---
echo [INFO] Dung Tomcat ...
"%CATALINA_HOME%\bin\shutdown.bat" >nul 2>nul
REM Cho Tomcat co thoi gian dong het
timeout /t 3 /nobreak >nul 2>nul

REM --- Copy WAR ---
echo [INFO] Copy file WAR vao %CATALINA_HOME%\webapps ...
copy /Y "target\%APP_NAME%.war" "%CATALINA_HOME%\webapps\%APP_NAME%.war" >nul
if errorlevel 1 (
    echo [LOI] Khong copy duoc file WAR. Kiem tra quyen ghi vao thu muc webapps.
    endlocal
    exit /b 1
)

REM --- Xoa thu muc ung dung cu de tranh tomcat dung ban deploy cu ---
if exist "%CATALINA_HOME%\webapps\%APP_NAME%" (
    echo [INFO] Xoa thu muc ung dung cu ...
    rd /s /q "%CATALINA_HOME%\webapps\%APP_NAME%"
)

echo.
echo [INFO] Khoi dong Tomcat ...
"%CATALINA_HOME%\bin\startup.bat"

echo.
echo ============================================================
echo  DA DEPLOY XONG.
echo.
echo  Mo trinh duyet:
echo    http://localhost:8080/%APP_NAME%/
echo.
echo  Neu thay trang loi, xem log:
echo    %CATALINA_HOME%\logs\catalina.%DATE:~-4%%DATE:~4,2%%DATE:~7,2%.log
echo ============================================================
endlocal
exit /b 0
