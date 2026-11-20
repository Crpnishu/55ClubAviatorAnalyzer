# 55 Club Aviator Analyzer

A starter Android app written in Kotlin + Jetpack Compose.

## Included
- English interface
- Manual multiplier entry
- Recent round history (in-memory for this starter)
- Frequency and average statistics for `<2x`, `2x–5x`, and `5x+`
- Transparent history-only insight; no next-round prediction claims

## Build the APK
1. Install the latest stable Android Studio.
2. Open this folder as an existing project.
3. Allow Gradle sync and install the Android SDK platform requested by the project (API 35).
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Android Studio will show the APK location, usually `app/build/outputs/apk/debug/app-debug.apk`.

## Notes
- This starter stores rounds only while the app process is running. Persistent local storage can be added next.
- It does not connect to 55 Club, access accounts, bypass app protections, or predict future outcomes.
- Never share passwords, OTPs, payment details, or account access with a third-party prediction app.
