@echo off
title SezR Focus Baslat
echo SezR Focus kuruluyor...
echo.
npm install
if errorlevel 1 (
  echo.
  echo HATA: npm install basarisiz oldu. Node.js kurulu mu kontrol et.
  pause
  exit /b 1
)
echo.
echo Expo baslatiliyor...
npx expo start -c
pause
