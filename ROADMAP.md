# SezR Focus Yol Haritası

Bu dosya, projede yapılacak işleri ve teknik öncelikleri takip etmek için hazırlanmıştır.

## Tamamlananlar

- Yerel Android WebView kabuğu kuruldu.
- Uygulama `file:///android_asset/focus.html` üzerinden internetsiz çalışacak şekilde yapılandırıldı.
- Pomodoro, görev, ödev, soru-deneme ve gelişim ekranları eklendi.
- JSON yedekleme / geri yükleme ve CSV dışa aktarma eklendi.
- GitHub Actions ile debug APK üretimi yapılandırıldı.
- Release dosyasına sürüm içeren APK adı verildi.

## Kısa Vadeli Öncelikler

1. Native Android bildirim ve hatırlatma sistemi
   - Issue: #2
   - Hatırlatma saati seçilince cihaz bildirimi planlanmalı.
   - Android 13+ için bildirim izni desteklenmeli.

2. Release imzalı APK / AAB üretimi
   - Issue: #3
   - Keystore bilgileri GitHub Secrets ile saklanmalı.
   - Debug ve release çıktıları ayrı adlandırılmalı.

3. Sürüm yönetimi
   - README, Gradle ve GitHub Actions sürüm değerleri aynı tutulmalı.
   - Yeni sürüm çıkarırken `versionName`, `APP_VERSION` ve APK adı birlikte güncellenmeli.

## Orta Vadeli Geliştirmeler

- Çoklu öğrenci profili desteği
- Daha ayrıntılı haftalık ve aylık raporlar
- Ders bazlı hedef belirleme
- Ödev teslim tarihi yaklaşınca uyarı
- Verileri dışarı aktarma ekranında daha anlaşılır seçenekler

## Test Notları

Her sürümden önce şu kontroller yapılmalı:

- Uygulama internetsiz açılıyor mu?
- Pomodoro tamamlanınca ders istatistiğine dakika ve seans ekleniyor mu?
- Görev ve ödev ekleme/silme/tamamlama çalışıyor mu?
- Soru hedefi ilerleme yüzdesi doğru hesaplanıyor mu?
- JSON yedekleme ve geri yükleme çalışıyor mu?
- CSV dışa aktarma dosya üretiyor mu?
- GitHub Actions APK üretip Release içine ekliyor mu?
