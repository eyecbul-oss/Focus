name: Build SezR Focus Expo Android

on:
  workflow_dispatch:
  push:
    branches: [ "main" ]

jobs:
  android:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up Node
        uses: actions/setup-node@v4
        with:
          node-version: 20

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3

      - name: Install dependencies
        run: npm install

      - name: Type check
        run: npm run typecheck
        continue-on-error: true

      - name: Expo prebuild Android
        run: npx expo prebuild --platform android --non-interactive

      - name: Build debug APK
        run: cd android && ./gradlew assembleDebug

      - name: Build release AAB unsigned
        run: cd android && ./gradlew bundleRelease

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: SezR-Focus-Premium-debug-apk
          path: android/app/build/outputs/apk/debug/*.apk

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: SezR-Focus-Premium-release-aab
          path: android/app/build/outputs/bundle/release/*.aab
