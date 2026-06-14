# Katkı Rehberi

Bu rehber, SezR Focus projesinde değişiklik yaparken izlenecek temel adımları açıklar.

## Proje Yapısı

Ana Android proje klasörü:

```text
android/
```

WebView içinde çalışan ana uygulama dosyaları:

```text
android/app/src/main/assets/focus.html
android/app/src/main/assets/focus-harmony.css
android/app/src/main/assets/focus-task.css
android/app/src/main/assets/focus-motion.css
android/app/src/main/assets/focus-friendly.css
android/app/src/main/assets/focus-splash.css
android/app/src/main/assets/focus-clean.js
android/app/src/main/assets/focus-home.js
android/app/src/main/assets/focus-friendly.js
```

Android kabuk dosyası:

```text
android/app/src/main/java/com/sezr/focuspro/MainActivity.java
```

## Yerelde Build Alma

```bash
cd android
gradle clean :app:assembleDebug --no-daemon --stacktrace
```

Debug APK çıktısı:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

## Değişiklik Yaparken

- Önce ilgili dosyanın mevcut halini kontrol edin.
- Küçük ve anlaşılır commit'ler oluşturun.
- README, ROADMAP veya CHANGELOG gerektiren değişikliklerde belgeleri de güncelleyin.
- Keystore, şifre, token veya gizli bilgi commit etmeyin.

## Test Kontrolü

Her değişiklikten sonra en az şu kontroller yapılmalı:

- Uygulama açılıyor mu?
- Sekmeler arası geçiş çalışıyor mu?
- Pomodoro başlat/sıfırla çalışıyor mu?
- Görev ve ödev ekleme/silme/tamamlama çalışıyor mu?
- Soru hedefi ve deneme alanları kayıt oluyor mu?
- JSON yedekleme ve geri yükleme çalışıyor mu?
- CSV dışa aktarma çalışıyor mu?

## Sürüm Güncelleme

Yeni sürüm çıkarırken şu yerler birlikte güncellenmeli:

- `android/app/build.gradle` içindeki `versionName`
- `.github/workflows/android-ci.yml` içindeki `APP_VERSION` ve `APK_NAME`
- `README.md` içindeki güncel sürüm bilgisi
- `CHANGELOG.md` içindeki yeni sürüm notları
