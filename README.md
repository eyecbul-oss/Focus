# SezR Focus

Premium dark temalı öğrenci çalışma koçu uygulaması.

## GitHub üzerinden APK üretme

1. GitHub'da yeni repo aç.
2. Bu ZIP içindeki tüm dosyaları repoya yükle.
3. Repo içinde `Actions` sekmesine gir.
4. `Build Android APK` workflow'unu aç.
5. `Run workflow` butonuna bas.
6. Build bitince alttaki `Artifacts` bölümünden APK dosyasını indir.

## Ana klasörler

- `www/index.html`
- `www/css/style.css`
- `www/js/app.js`
- `.github/workflows/build-apk.yml`
- `package.json`
- `capacitor.config.ts`


## Düzeltme
Bu sürümde GitHub Actions NodeJS 22 kullanır. Capacitor CLI artık NodeJS >=22 istediği için build hatası giderildi.
