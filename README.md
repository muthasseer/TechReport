# TechReport
Android field technician service-management app.

## Build APK without Android Studio
1. Create a GitHub repository named `TechReport`.
2. Upload all files in this folder.
3. Open **Actions** → **Build TechReport APK**.
4. When it finishes, open the workflow run → **Artifacts** → download `TechReport-debug-apk`.
5. Extract the ZIP and install the APK on the Android phone.

The app uses a local SQLite database. PDF reports are generated into the app's external files directory. Device passwords are stored in the local database and are intentionally excluded from generated PDFs.
