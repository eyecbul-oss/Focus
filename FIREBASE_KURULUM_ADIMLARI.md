# Firebase kurulum adimlari

Bu dosya Firebase Console ekraninda nerelere tiklayacagini adim adim anlatir.

## 1. Firebase Console'a gir

Adres:

https://console.firebase.google.com

Sonra `sezrfocus` projesini ac.

## 2. Authentication ac

Sol menude:

Build > Authentication

Sonra:

Get started

Sonra yukaridan:

Sign-in method

Burada iki secenegi ac:

### Anonymous

Anonymous satirina tikla.

Enable anahtarini ac.

Save butonuna bas.

### Email/Password

Email/Password satirina tikla.

Enable anahtarini ac.

Save butonuna bas.

## 3. Firestore Database ac

Sol menude:

Build > Firestore Database

Sonra:

Create database

Secenek olarak:

Start in test mode

Next

Region sec:

europe-west1 veya sana en yakin Europe secenegi

Enable / Create butonuna bas.

## 4. Firestore Rules yapistir

Firestore Database ekraninda ustten:

Rules

Mevcut kodu sil ve bunu yapistir:

```js
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /focusData/{userId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

Sonra:

Publish

## 5. Test

Bilgisayarda proje klasorunde terminal ac:

```bash
npm install
npx expo start -c
```

Android icin:

```bash
npx expo run:android
```

## Not

`.env` dosyasi repoya eklendi. Firebase bilgileri Expo tarafindan otomatik okunur.
