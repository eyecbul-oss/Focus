# SezR Focus Pro

SezR Focus Pro, YKS ve benzeri sınavlara hazırlanan öğrenciler için native Android odak, görev ve yerel AI koçluk uygulamasıdır.

## Güncel Android Sürümü

- Sürüm: `1.4.0-focus-v2`
- Paket: `com.sezr.focuspro.nativeapp`
- Debug APK çıktısı: `android/app/build/outputs/apk/debug/app-debug.apk`

## Özellikler

- Odak ve mola sayacı
- Günlük hedef, seans ve haftalık ilerleme takibi
- Görev listesi ve hazır çalışma şablonları
- Sınav sayacı ve koçluk notları
- Ders bazlı çalışma takibi
- XP, seviye, rozet ve çalışma motivasyon sistemi
- YKS öğrenci paneli fikrine uygun soru/deneme takibi yol haritası
- İnternet gerektirmeyen yerel AI çalışma önerileri
- Gerçek cihazlarda Android 15 sistem çubuklarıyla uyumlu native arayüz

## APK Üretimi

GitHub Actions, `main` branch'e Android dosyaları push edildiğinde otomatik debug APK üretir.

Actions ekranında şu workflow çalışır:

```text
Build Native Android APK
```

Artifact adı:

```text
SezR-Focus-Pro-1.3.1-roadmap-stable-debug-apk
```

> Not: Uygulama sürümü `1.4.0-focus-v2` olarak güncellendi. Artifact adı bir sonraki workflow düzenlemesinde sadeleştirilebilir.

Yerelde build almak için:

```bash
cd android
gradle clean :app:assembleDebug --no-daemon --stacktrace
```

## Proje Yapısı

- `android/app/src/main/java/com/sezr/focuspro/MainActivity.java`: ana native Android UI
- `android/app/src/main/java/com/sezr/focuspro/AiStudyCoach.java`: yerel çalışma planı ve öneri motoru
- `android/app/src/main/java/com/sezr/focuspro/AmbientPlayer.java`: odak sesleri
- `android/app/src/main/java/com/sezr/focuspro/FocusNotificationHelper.java`: bildirim kanalı kurulumu
- `android/patch-mainactivity-ai-ui.gradle`: mevcut büyük `MainActivity` dosyasını build sırasında güvenli biçimde yamalayan geçiş scripti

## Cihazda Test

Debug build, mevcut kurulu uygulamanın üstüne yazmaz. `applicationIdSuffix '.debug'` kullandığı için telefona ayrı paket olarak kurulur ve kullanıcı verisini silmeden QA yapılabilir.

```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.sezr.focuspro.nativeapp.debug/com.sezr.focuspro.MainActivity
```

## Profesyonelleştirme Yol Haritası

- Focus web sayfasındaki v2 fikirlerini native Android ekranlarına taşımak
- Ders bazlı grafikler ve 30 günlük çalışma haritasını native kartlara dönüştürmek
- Kalıcı veri katmanını `SharedPreferences` string formatından daha güvenli bir modele taşımak
- Release imzalama ve Play Store dağıtım akışını dokümante etmek
- Cihaz QA ekran görüntülerini PR kontrollerine bağlamak
