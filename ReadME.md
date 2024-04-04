# FCI Attendance

FCI Attendance is an Android application designed to simplify the process of student attendance tracking using QR codes. The application is built on the Google Cloud Project and uses the Google Sheets API for real-time tracking without the need for a server.

## Features

- Track attendance records in a user-friendly interface.
- Export attendance records to a CSV file.
- Delete specific attendance records.
- Persist data across app launches.
- Asynchronous data handling with Kotlin Coroutines.
- Native-level performance with Android NDK.
  
## Setup

1. **Google Cloud Project**: The application is built on a Google Cloud Project. Make sure to create a credential for the Android application with the SHA1 and the package name to ensure the application works without any issues.

2. **QR Generation**: The QR codes for the students are generated using Python, [pandas](https://pandas.pydata.org/), and [qrcode](https://pypi.org/project/qrcode/). The data in the QR code is encrypted using the AES algorithm. The same key and IV used for encryption are used for decryption in the Android application.

3. **Android Application**: The Android application reads the QR code using the Google code scanner and decrypts the data using Bouncy Castle cryptography. The decrypted data is then sent to the Google Sheets using the Google Sheets API.

## Built With

- [Kotlin](https://kotlinlang.org/) - The main programming language used.
- [Java](https://www.java.com/) - Used in some parts of the project.
- [C](https://en.wikipedia.org/wiki/C_(programming_language)) - Used in some parts of the project.
- [Python](https://www.python.org/) - Used to generate the QR codes for each student and create the Excel file with the QRs.
- [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript) - Used on the [Apps Script](https://www.google.com/script/start/) of the registration form to add the verified responses to the specific sheets they want.

### Gradle Libraries

- [AndroidX Core KTX](https://developer.android.com/kotlin/ktx) - Provides Kotlin extensions for Android framework APIs.
- [AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat) - Provides backward-compatible versions of Android framework APIs.
- [Material Components](https://material.io/develop/android/docs/getting-started/) - Material design components for Android.
- [ConstraintLayout](https://developer.android.com/training/constraint-layout) - Allows you to create large and complex layouts with a flat view hierarchy.
- [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Library support for Kotlin coroutines.
- [Hilt](https://dagger.dev/hilt/) - Dependency injection library built on top of Dagger.
- [Room](https://developer.android.com/training/data-storage/room) - Provides an abstraction layer over SQLite to allow for more robust database access.
- [Lifecycle ViewModel KTX](https://developer.android.com/kotlin/ktx#lifecycle-viewmodel) - Provides Kotlin extensions for ViewModel component.
- [Lifecycle LiveData KTX](https://developer.android.com/kotlin/ktx#lifecycle-livedata) - Provides Kotlin extensions for LiveData component.
- [Fragment KTX](https://developer.android.com/kotlin/ktx#fragment) - Provides Kotlin extensions for Fragment component.
- [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started) - Android Jetpack's Navigation component helps you implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer.
- [Google Play Services Code Scanner](https://developers.google.com/android/guides/setup) - Provides APIs to help you implement Google Play services code scanning features in your app.
- [EasyPermissions](https://github.com/googlesamples/easypermissions) - Simplifies Android permissions.
- [Google Sheets API](https://developers.google.com/sheets/api) - Read, write, and format data in Sheets.
- [Google Play Services Auth](https://developers.google.com/android/guides/setup) - Provides APIs to help you implement Google Sign-In and other features.
- [Google HTTP Client Android](https://developers.google.com/api-client-library/java/google-http-java-client/android) - Simplifies the process of connecting to and making requests to Google APIs on Android.
- [Google API Client Android](https://developers.google.com/api-client-library/java/google-api-java-client/android) - Simplifies the process of connecting to and making requests to Google APIs on Android.
- [AndroidX Work Runtime KTX](https://developer.android.com/kotlin/ktx#work) - Provides Kotlin extensions for WorkManager.
- [AndroidX Security Crypto KTX](https://developer.android.com/kotlin/ktx#security-crypto) - Provides Kotlin extensions for AndroidX Security Crypto.
- [Bouncy Castle](https://www.bouncycastle.org/) - A collection of APIs used in cryptography, it allows for encryption and decryption of data.
- [Gson](https://github.com/google/gson) - A Java serialization/deserialization library to convert Java Objects into JSON and back.
- [Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning) - Allows your app to read barcodes of all kinds.
