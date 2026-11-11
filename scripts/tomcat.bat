@echo off
REM =====================================================================
REM KhOI dong / Dung Tomcat 10.1 cho QLDAPM-ShippingOrder (UC-01)
REM
REM Cach dung:
REM     scripts\tomcat.bat start     - khoi dong Tomcat
REM     scripts\tomcat.bat stop      - dung Tomcat
REM     scripts\tomcat.bat restart  - restart (dung sau khi doi setenv.bat)
REM     scripts\tomcat.bat status    - kiem tra dang chay hay khong
REM
REM Yeu cau: da cai Tomcat 10.1 va dat CATALINA_HOME.
REM =====================================================================
setlocal
cd /d "%~dp0.."

set ACTION=%~1
if "%ACTION%"=="" set ACTION=start

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

if /i "%ACTION%"=="start" goto :do_start
if /i "%ACTION%"=="stop" goto :do_stop
if /i "%ACTION%"=="restart" goto :do_restart
if /i "%ACTION%"=="status" goto :do_status

echo [LOI] Tham so khong hop le: %ACTION%
echo       Cach dung: scripts\tomcat.bat [start^|stop^|restart^|status]
endlocal
exit /b 1

:do_start
echo [INFO] Khoi dong Tomcat ...
"%CATALINA_HOME%\bin\startup.bat"
echo.
echo  Mo trinh duyet: http://localhost:8080/QLDAPM-ShippingOrder/
endlocal
exit /b 0

:do_stop
echo [INFO] Dung Tomcat ...
"%CATALINA_HOME%\bin\shutdown.bat"
endlocal
exit /b 0

:do_restart
echo [INFO] Dung Tomcat ...
"%CATALINA_HOME%\bin\shutdown.bat" >nul 2>nul
timeout /t 3 /nobreak >nul 2>nul
echo [INFO] Khoi dong Tomcat ...
"%CATALINA_HOME%\bin\startup.bat"
echo.
echo  Mo trinh duyet: http://localhost:8080/QLDAPM-ShippingOrder/
echo  Luu y: neu vua doi setenv.bat, phai chay 'restart' de doc lai bien moi truong.
endlocal
exit /b 0

:do_status
REM Kiem tra cong 8080 co dang mo khong
netstat -an | find /i "LISTENING" | find /i ":8080" >nul
if errorlevel 1 (
    echo [INFO] Cong 8080 dang DONG - Tomcat chua chay.
    endlocal
    exit /b 1
)
echo [INFO] Cong 8080 dang MO - Tomcat dang chay.
echo  Thu mo: http://localhost:8080/QLDAPM-ShippingOrder/
endlocal
exit /b 0
