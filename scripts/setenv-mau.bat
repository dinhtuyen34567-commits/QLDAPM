@echo off
REM =====================================================================
REM FILE MAU - setenv.bat cho Tomcat 10.1 (QLDAPM-ShippingOrder / UC-01)
REM
REM TOMCAT KHONG DOC BIEN MOI TRUONG CUA WINDOWS. Muon truyen bien vao
REM ung dung ban phai copy file nay vao:
REM
REM     %CATALINA_HOME%\bin\setenv.bat
REM
REM Cach copy (mo cmd, thay CATALINA_HOME bang duong dan that cua ban):
REM
REM     copy scripts\setenv-mau.bat "%CATALINA_HOME%\bin\setenv.bat"
REM
REM ROI sua lai DB_PASSWORD cho khop mat khau MySQL cua ban,
REM sau do RESTART TOMCAT (dung tomcat de doc lai bien moi truong).
REM
REM !!! KHONG COMMIT FILE DA SUA vao git !!!
REM     %CATALINA_HOME%\bin\setenv.bat nam ngoai thu muc du an nen
REM     khong bi commit, nhung van nen xoa khi may dung chung.
REM =====================================================================

REM --- Ket noi CSDL ---
REM Dong DB_URL phai dung MOT dong, & trong .bat can escape bang ^ &
REM (neu khong escape, cmd se cat dong o ky tu &).
set "DB_URL=jdbc:mysql://localhost:3306/vanchuyen?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh"
set "DB_USER=root"
REM !!! SUA MAT KHAU MYSQL CUA BAN O DONG DUOI !!!
set "DB_PASSWORD="

REM --- Dev login (TAM THOI) ---
REM BAY true de mo duong dan /dev-login de chay thu cuc bo.
REM !!! XOA DONG NAY KHI NOP BAI HOAC DUNG MAY CHUNG !!!
set "APP_DEV_LOGIN=true"
