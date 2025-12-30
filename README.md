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

## Screenshots

> **Note**: Add your screenshots here to showcase the app's interface on both Android and iOS platforms.

## Technology Stack

Taskaroo leverages modern mobile development technologies:

- **[Kotlin Multiplatform Mobile (KMM)](https://kotlinlang.org/docs/multiplatform.html)**: Share business logic across platforms
- **[Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)**: Declarative UI framework for Android and iOS
- **[SQLDelight](https://cashapp.github.io/sqldelight/)**: Type-safe SQL database with Kotlin extensions
- **[Voyager](https://voyager.adriel.cafe/)**: Multiplatform navigation library
- **[kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)**: Multiplatform date and time library

## Architecture

Taskaroo follows a clean architecture pattern with clear separation of concerns:

### Project Structure

```
Taskaroo/
├── composeApp/              # Main application module
│   ├── commonMain/          # Shared code for all platforms
│   │   ├── kotlin/
│   │   │   ├── common/      # Reusable UI components
│   │   │   ├── database/    # SQLDelight database layer
│   │   │   ├── modal/       # Data models
│   │   │   ├── screens/     # Screen composables
│   │   │   └── utils/       # Utility functions
│   │   ├── composeResources/ # Images and resources
│   │   └── sqldelight/      # Database schemas
│   ├── androidMain/         # Android-specific code
│   └── iosMain/             # iOS-specific code
└── iosApp/                  # iOS application wrapper
```

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

