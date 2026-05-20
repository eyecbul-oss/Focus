# SezR Focus Native App

Bu paket, SezR Focus'u Android APK/AAB ve iOS kaynak yapısına dönüştürmek için hazırlandı.

## Android APK üretme

1. GitHub'da yeni repo aç.
2. Bu ZIP içindeki tüm dosyaları repo köküne yükle.
3. GitHub > Actions > `Build SezR Focus Android`
4. `Run workflow` de.
5. Build bitince Artifacts bölümünden:
   - `SezR-Focus-debug-apk`
   - `SezR-Focus-release-aab-unsigned`
   dosyalarını indir.

## Google Play

Google Play için AAB kullanılır. Test için debug APK telefona kurulabilir. Play Console'a yükleme için release AAB imzalanmalıdır.

## iOS / App Store

iOS için Mac + Xcode gerekir:

```bash
npm install
npx cap add ios
npx cap sync ios
```

Sonra Xcode ile `ios/App/App.xcworkspace` açılır.

## Paket bilgileri

Android package / iOS bundle id:
`com.sezrmatematik.focus`

Uygulama adı:
`SezR Focus`
