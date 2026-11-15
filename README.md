# SchoolTime

Starter Android app using Kotlin + Jetpack Compose.

## Cloud build
The GitHub Actions workflow in `.github/workflows/build.yml` builds a debug APK and uploads it as an artifact.
It uses Java 17 and Gradle 8.11.1 on GitHub's hosted runner, so Android Studio is not needed for the build itself.

After pushing this project to a GitHub repository:
1. Open the repository's **Actions** tab.
2. Select **Build SchoolTime APK**.
3. Press **Run workflow** (or wait for the first push build).
4. Open the successful run and download the `SchoolTime-debug-apk` artifact.
5. Extract the ZIP; it contains `app-debug.apk`.

The current screen is a static starter mockup. Timetable logic and the home-screen widget are next steps.
