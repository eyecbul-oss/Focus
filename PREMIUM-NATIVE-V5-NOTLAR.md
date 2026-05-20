# Premium Native V5

Bu sürüm eski özellikleri yeni native yapıya daha sıkı bağlar.

## Eklenenler
- Otomatik yerel kayıt
- Uygulama açılırken local veriyi geri yükleme
- Maille girişte bulut senkron köprüsü
- Profil ekranında Bulutu Kontrol Et butonu
- Müzik servisi focus start/pause ile bağlantılı
- assets/sounds klasörü ve ses dosyası yerleşim açıklaması
- Expo/Android build workflow
- EAS build config
- typecheck script

## Kullanım
npm install
npm start

Android test APK için GitHub Actions:
Build SezR Focus Expo Android

## Ses ekleme
assets/sounds içine rain.mp3, lofi.mp3, piano.mp3, fire.mp3 ekle.
Sonra app/services/audioService.ts içindeki require satırlarını aktif et.
