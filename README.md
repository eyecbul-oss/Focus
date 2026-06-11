# SezR Focus

SezR Focus, siteye bağlanmadan çalışan yerel Android Focus uygulamasıdır. Uygulama, Focus arayüzünü APK içine gömülü HTML/CSS/JS dosyalarından açar.

## Güncel Sürüm

- Sürüm: `2.0.0-local-focus`
- Paket: `com.sezr.focuspro.nativeapp`
- Debug APK çıktısı: `android/app/build/outputs/apk/debug/app-debug.apk`

## Mantık

Bu repo artık eski native patch sistemini kullanmaz. Ana uygulama sade bir Android kabuğudur ve şu yerel dosyayı açar:

```text
android/app/src/main/assets/focus.html
```

Geliştirme bundan sonra şu dosyalar üzerinden yapılır:

```text
android/app/src/main/assets/focus.html
android/app/src/main/assets/focus-harmony.css
android/app/src/main/assets/focus-task.css
android/app/src/main/assets/focus-clean.js
```

## Özellikler

- Pomodoro / odak sayacı
- Görev ekleme ve tamamlama
- Günlük hedef takibi
- XP ve seviye mantığı
- Haftalık özet kartı
- Rozet alanı
- Yerel kayıt: `localStorage`
- Siteye bağımlı olmayan APK içi çalışma

## APK Üretimi

GitHub Actions, `main` branch'e Android dosyaları push edildiğinde otomatik debug APK üretir.

Yerelde build almak için:

```bash
cd android
gradle clean :app:assembleDebug --no-daemon --stacktrace
```

## Bundan Sonraki Geliştirme

- Asset içindeki Focus arayüzünü büyütmek
- Ders bazlı istatistikleri güçlendirmek
- JSON içe/dışa aktarmayı APK içinde daha kullanışlı yapmak
- Daha iyi mobil kart düzeni oluşturmak
- Uygulama ikonunu ve splash ekranını yenilemek
