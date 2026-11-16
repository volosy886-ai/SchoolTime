# SchoolTime 5.0

Android app for showing the current lesson, break, countdown and next lesson.

## Build

Open this folder in Android Studio (JDK 17), wait for Gradle sync, then run `:app:assembleDebug`.
The GitHub Actions workflow also builds a debug APK on push to `main` or manual dispatch.

## Compatibility notes

- Minimum Android version: Android 7.0 (API 24).
- Uses standard Android AppWidget/RemoteViews rather than a launcher-specific widget framework.
- Widget uses a rounded drawable background, refreshes on widget updates/boot/app replacement, and schedules inexact minute refreshes.
- Exact widget dimensions and corner masking are controlled partly by each launcher; Android launchers can resize or crop widgets differently.
- Android battery optimization may delay inexact background refreshes. Opening the app always recalculates the current status.

## Themes

Classic dark and Liquid Glass; selection is persisted and applied to the home-screen widget.
