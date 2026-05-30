# Streak HabitTracker

A premium, privacy-first Android habit tracker built with a modern tech stack. Features a dark-mode glassmorphism design, locked vault for private habits, and a heatmap calendar.

## Features
- **Dark Mode & Glassmorphism**: Premium user interface with smooth animations and transitions.
- **Custom Wallpaper**: Set any image from your gallery as a blurred background for the ultimate aesthetic feel.
- **Locked Vault**: Keep your private habits hidden behind a secure PIN or Pattern lock (AES-256 encrypted).
- **Heatmap Calendar**: View your streak progress with GitHub-style contribution heatmaps.
- **Reliable Reminders**: WorkManager-based notifications to keep your streaks alive. Mark habits done directly from the notification shade.
- **100% Private**: Zero analytics, zero ads, zero internet connection required (except for optional features). All data stays on your device using Room Database.
- **Data Export/Import**: Easily backup and restore your habit data in JSON format.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Local DB**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Compose Navigation
- **Animations**: Compose Animations + Lottie
- **Notifications**: WorkManager + NotificationManager
- **Security**: AndroidX Crypto (EncryptedSharedPreferences)
- **Image Loading**: Coil

## Building the App
To build the app, open this project in **Android Studio Ladybug** or newer and run it on an emulator or physical device.

```bash
# To generate a debug APK:
./gradlew assembleDebug
```

The APK will be available in `app/build/outputs/apk/debug/app-debug.apk`.

## License
MIT License