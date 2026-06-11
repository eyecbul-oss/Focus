# SezR Focus

SezR Focus, siteye bağlanmadan çalışan yerel Android Focus uygulamasıdır. Uygulama, Focus arayüzünü APK içine gömülü HTML/CSS/JS dosyalarından açar.

## Güncel Sürüm

- Sürüm: `2.1.1-stable`
- Paket: `com.sezr.focuspro.nativeapp`
- Uygulama adı: `SezR Focus`
- Debug APK çıktısı: `android/app/build/outputs/apk/debug/app-debug.apk`
- Release çıktısı: GitHub Releases içinde `app-debug.apk`

## Mantık

Bu repo artık eski native patch sistemini kullanmaz. Ana uygulama sade bir Android WebView kabuğudur ve şu yerel dosyayı açar:

```text
file:///android_asset/focus.html
```

Geliştirme bundan sonra şu dosyalar üzerinden yapılır:

```text
android/app/src/main/assets/focus.html
android/app/src/main/assets/focus-harmony.css
android/app/src/main/assets/focus-task.css
android/app/src/main/assets/focus-clean.js
```

## Özellikler

- Siteye bağımlı olmayan APK içi çalışma
- Pomodoro / odak sayacı
- Ders bazlı seans ve dakika takibi
- Görev ekleme ve tamamlama
- Günlük hedef takibi
- Günlük soru hedefi ve deneme takibi
- Sınav geri sayımı: YKS, TYT, AYT, YDT, AGS, DGS, ALES, KPSS, YDS, LGS
- Sınav saatine göre canlı geri sayım
- XP, seviye ve rozet sistemi
- Haftalık özet kartı
- 30 günlük çalışma haritası
- CSV dışa aktarma
- JSON yedekleme ve geri yükleme
- Verileri sıfırlama
- Özel SezR Focus ikon ve koyu tema
- Yerel kayıt: `localStorage`

## APK Üretimi

GitHub Actions, `main` branch'e push edildiğinde APK üretir ve Release içine APK dosyası olarak koyar.

Yerelde build almak için:

```bash
cd android
gradle clean :app:assembleDebug --no-daemon --stacktrace
```

## Test Listesi

- Uygulama internetsiz açılıyor mu?
- Sınav sayacı seçili sınav tarih ve saatine göre azalıyor mu?
- Ders seçip pomodoro bitirince ders istatistiğine işleniyor mu?
- Günlük soru hedefi ilerleme çubuğunu güncelliyor mu?
- JSON yedekleme dosya indiriyor mu?
- JSON içe aktarma eski veriyi geri getiriyor mu?
- CSV seans geçmişini çıkarıyor mu?
- Verileri sıfırla tüm local veriyi temizliyor mu?
- Yeni ikon cihazda görünüyor mu?

## Bundan Sonraki Geliştirme

- Ödev ve konu takip modülü
- Öğrenci profili desteği
- Daha ayrıntılı haftalık/aylık rapor
- Bildirim ve hatırlatma sistemi
