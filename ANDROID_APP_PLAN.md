# SezR Focus Pro - Android Uygulama Planı

Bu repo artık ana siteden bağımsız Focus Pro Android uygulaması olarak ilerleyecek.

## Ana karar

- Ana site içindeki Focus bölümü sadece Focus Lite kalacak.
- Focus Pro, bu repoda ayrı Android uygulaması olarak geliştirilecek.
- Uygulama içinde ana site menüsü olmayacak.
- Android ekranları uygulama mantığına göre yeniden tasarlanacak.
- Eski web/PWA Focus Pro özellikleri kaybolmayacak; Android'e aşama aşama taşınacak.

## Eski Focus Pro'dan Android'e taşınacak özellikler

- Giriş ekranı
- Misafir devam etme
- Görev listesi
- Pomodoro / odak sayacı
- Müzik paneli
- Haftalık özet
- Sınav sayacı
- Günlük hedef
- Notlar
- Tam ekran odak modu
- Firebase bağlantısı
- Bulut senkronizasyonu
- PWA tarafındaki kurulum mantığının Android karşılığı

## Android ilk sürüm hedefi

- Pomodoro / odak sayacı
- Süre modları: 25 / 45 / 60 dk
- Görev ekleme
- Günlük çalışma süresi
- Tamamlanan seans sayısı
- Sade uygulama ana ekranı
- APK üretimi

## Android geliştirme sırası

1. Native Android temel ekran ve çalışan sayaç
2. Görev tamamlama ve günlük hedef
3. Yerel kayıt
4. Haftalık özet
5. Sınav sayacı
6. Notlar
7. Tam ekran odak modu
8. Müzik paneli
9. Misafir / giriş ekranı
10. Firebase hesap ve bulut senkronizasyonu
11. Rozet ve seviye sistemi
12. Bildirimler
13. APK / AAB final üretimi

## Proje yaklaşımı

Önce Android proje iskeleti kurulacak. Mevcut web/PWA dosyaları geçici olarak korunacak ama Android uygulamanın ana yapısı ayrı `android/` klasöründe geliştirilecek. Web/PWA tarafındaki özellikler referans alınacak, fakat Android uygulama bağımsız native tasarım ile yeniden kurulacak.
