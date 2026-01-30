# Taskaroo

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)](https://kotlinlang.org/docs/multiplatform.html)

A modern, cross-platform task management application built with Kotlin Multiplatform and Compose Multiplatform. Taskaroo helps you organize your tasks with priority-based management, calendar views, and a clean, intuitive interface that works seamlessly on both Android and iOS devices.

## Features

- **Task Management**: Create, edit, and delete tasks with ease
- **Priority Levels**: Organize tasks by priority (Urgent, High, Medium, Low) with color-coded indicators
- **Calendar View**: View and manage tasks through an intuitive calendar interface
- **Cross-Platform**: Native apps for both Android and iOS built from a shared codebase
- **Local Database**: SQLDelight-powered local persistence for fast, offline-first task management
- **Modern UI**: Clean Material Design 3 interface with custom theming

## Screenshots (Both Android & iOS)
### HomeScreen & Calendar.
----
<img width="23%" height="1500" alt="Simulator Screenshot - 17 I phone - 2026-01-31 at 00 17 37" src="https://github.com/user-attachments/assets/a1d25ed2-b340-43da-9f2f-75c08f38ac2a" />
<img width="22%" height="1500" alt="main_screen_dark" src="https://github.com/user-attachments/assets/6ffebf0e-c28c-4958-80b4-2ebdfbe5bf27" />

<img width="23%" height="1500" alt="Simulator Screenshot - 17 I phone - 2026-01-31 at 00 18 03" src="https://github.com/user-attachments/assets/d82043f3-bf53-4985-aa26-228270e1de97" />
<img width="22%" height="1500" alt="calendar_screen _lite" src="https://github.com/user-attachments/assets/cbf44860-5d20-4b13-934e-083f015746ed" />

### Notes Screen.
----
<img width="22%" height="1500" alt="Screenshot_20260131_000952" src="https://github.com/user-attachments/assets/d38c67a1-7b6c-4061-9f48-d83fe5998bfb" />

## Technology Stack

Taskaroo leverages modern mobile development technologies:

- **[Kotlin Multiplatform Mobile (KMM)](https://kotlinlang.org/docs/multiplatform.html)**: Share business logic across platforms
- **[Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)**: Declarative UI framework for Android and iOS
- **[SQLDelight](https://cashapp.github.io/sqldelight/)**: Type-safe SQL database with Kotlin extensions
- **[Voyager](https://voyager.adriel.cafe/)**: Multiplatform navigation library
- **[kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)**: Multiplatform date and time library

### Architecture Highlights

- **Shared Code (commonMain)**: Business logic, UI components, and database operations are shared across platforms
- **Platform-Specific (androidMain/iosMain)**: Platform-specific implementations using Kotlin's expect/actual pattern
- **Database Layer**: SQLDelight provides type-safe database access with platform-specific drivers
- **Navigation**: Voyager handles screen navigation in a multiplatform-friendly way
- **State Management**: Compose state hoisting pattern for reactive UI updates

## Getting Started

### Setup

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/Taskaroo.git
cd Taskaroo
```

2. **iOS Configuration** (required for iOS builds)

```bash
cp iosApp/Configuration/Config.xcconfig.template iosApp/Configuration/Config.xcconfig
# Edit Config.xcconfig and add your Apple Developer Team ID
```

### Building

#### Android

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Run on an Android device or emulator

#### iOS

1. Install CocoaPods dependencies:

```bash
cd iosApp
pod install
```

2. Open the workspace:

```bash
open iosApp.xcworkspace
```

3. Build and run in Xcode on an iOS device or simulator

### Supported Platforms

- **Android**: Minimum SDK 24 (Android 7.0), Target SDK 36
- **iOS**: iOS 14.1+, supports x64, ARM64, and Simulator ARM64

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on:

- Reporting bugs
- Suggesting features
- Submitting pull requests
- Code style guidelines

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Copyright (c) 2025 Muhammad Ali

## Author

**Made with ❤️ by Muhammad Ali using Kotlin Multiplatform**
- Portfolio: [https://muhammadali0092.netlify.app/](https://muhammadali0092.netlify.app/)
- Date: 2025-12-30

---

