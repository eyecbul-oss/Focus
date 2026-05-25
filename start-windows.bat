@echo off
title SezR Focus Baslat
cd /d "%~dp0"
echo Klasor: %cd%
echo.
echo Node kontrol ediliyor...
node -v
echo NPM kontrol ediliyor...
npm -v
echo.
echo Paketler kuruluyor. Bu islem birkac dakika surebilir...
call npm install
if errorlevel 1 goto error
echo.
echo Expo baslatiliyor...
call npx expo start -c
goto end
:error
echo.
echo HATA OLUSTU. Bu ekranin fotografini ChatGPT'ye gonder.
:end
echo.
pause
