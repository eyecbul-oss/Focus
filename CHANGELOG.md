# Değişiklik Günlüğü

## 2.3.0-dev

### Eklenenler

- Android tarafına native hatırlatma altyapısı eklendi.
- `ReminderReceiver` ile cihaz bildirimi gösterme desteği eklendi.
- WebView içinden Android'e hatırlatma ayarlarını aktaran `FocusAndroid` köprüsü eklendi.
- Hatırlatma açıkken günlük alarm planlama, kapalıyken alarm iptal etme mantığı eklendi.

### Test Edilecekler

- Android 13+ cihazda bildirim izni isteniyor mu?
- Ayarlar sekmesinde hatırlatma Açık yapılınca seçilen saatte bildirim geliyor mu?
- Hatırlatma Kapalı yapılınca bildirim iptal oluyor mu?
- Uygulama internetsiz açılmaya devam ediyor mu?
- Pomodoro, görev, ödev ve yedekleme özellikleri bozulmadan çalışıyor mu?

## 2.2.0

### Eklenenler

- Ana Sayfa, Pomodoro, Soru, Gelişim ve Ayarlar sekmeleri netleştirildi.
- Konu ve ödev takibi ekranı eklendi.
- Öğrenci profili alanı eklendi.
- Çalışma hatırlatması ayar alanı eklendi.
- Haftalık özet, aylık özet ve 30 günlük çalışma haritası geliştirildi.
- GitHub Actions debug APK çıktısı sürüm adıyla yayınlanacak şekilde düzenlendi.
- Proje yol haritası için `ROADMAP.md` eklendi.

### Düzeltilenler

- README içindeki sürüm bilgisi `2.2.0` ile uyumlu hale getirildi.
- README içinde eksik kalan asset dosyaları tamamlandı.
- APK release dosya adı `SezR-Focus-2.2.0-debug.apk` olarak güncellendi.
- GitHub Actions içinde sürüm ve APK adı `env` değişkenleriyle merkezi hale getirildi.

### Bilinen Durumlar

- Hatırlatma ayarı uygulama içinde saklanır; gerçek Android bildirimi henüz native olarak bağlanmamıştır.
- Release imzalı APK/AAB üretimi için keystore tabanlı ayrı bir workflow geliştirmesi gereklidir.

### Sonraki Hedefler

- Native Android bildirimleri eklemek. Bkz. Issue #2.
- Release imzalı APK/AAB üretimini eklemek. Bkz. Issue #3.
- Çoklu öğrenci profili ve daha ayrıntılı raporlama geliştirmek.
