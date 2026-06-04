# SezR Focus Pro

SezR Focus Pro, YKS ve benzeri sınavlara hazırlanan öğrenciler için native Android odak, görev ve yerel AI koçluk uygulamasıdır.

## Özellikler

- Odak ve mola sayacı
- Günlük hedef, seans ve haftalık ilerleme takibi
- Görev listesi ve hazır çalışma şablonları
- Sınav sayacı ve koçluk notları
- İnternet gerektirmeyen yerel AI çalışma önerileri
- Gerçek cihazlarda Android 15 sistem çubuklarıyla uyumlu native arayüz

## Proje Yapısı

- `android/app/src/main/java/com/sezr/focuspro/MainActivity.java`: ana native Android UI
- `android/app/src/main/java/com/sezr/focuspro/AiStudyCoach.java`: yerel çalışma planı ve öneri motoru
- `android/app/src/main/java/com/sezr/focuspro/AmbientPlayer.java`: odak sesleri
- `android/app/src/main/java/com/sezr/focuspro/FocusNotificationHelper.java`: bildirim kanalı kurulumu
- `android/patch-mainactivity-ai-ui.gradle`: mevcut büyük `MainActivity` dosyasını build sırasında güvenli biçimde yamalayan geçiş scripti

## Build

GitHub Actions her PR ve `main` push için debug APK üretir.

Yerelde build almak için:

```bash
cd android
gradle :app:assembleDebug --no-daemon --stacktrace
```

Çıktı APK yolu:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

## Cihazda Test

```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.sezr.focuspro.nativeapp/com.sezr.focuspro.MainActivity
```

Mevcut kurulu uygulama farklı imzayla yüklendiyse Android üstüne kurmayı reddeder. Bu durumda uygulamayı kaldırmadan önce veri kaybı ihtimalini değerlendirin.

## Profesyonelleştirme Yol Haritası

- `MainActivity` içindeki büyük UI bloklarını küçük Java view builder sınıflarına ayırmak
- Kalıcı veri katmanını `SharedPreferences` string formatından daha güvenli bir modele taşımak
- Release imzalama ve Play Store dağıtım akışını dokümante etmek
- Cihaz QA ekran görüntülerini PR kontrollerine bağlamak
