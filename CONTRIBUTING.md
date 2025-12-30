# Contributing to Taskaroo

Thank you for your interest in contributing to Taskaroo! This document provides guidelines and instructions for contributing to the project.

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue on GitHub with the following information:

- **Clear title and description**: Explain the bug in detail
- **Steps to reproduce**: Provide step-by-step instructions
- **Expected behavior**: What you expected to happen
- **Actual behavior**: What actually happened
- **Platform**: Android or iOS, and version
- **Screenshots**: If applicable
- **Environment**: Device model, OS version, app version

### Suggesting Features

Feature suggestions are welcome! Please create an issue with:

- **Clear description**: Explain the feature and its benefits
- **Use case**: Describe when and why this feature would be useful
- **Mockups**: If applicable, include UI mockups or diagrams

### Submitting Pull Requests

1. **Fork the repository** and create a new branch from `main`
2. **Make your changes** following the code style guidelines below
3. **Test your changes** on both Android and iOS if possible
4. **Commit your changes** with clear, descriptive commit messages
5. **Push to your fork** and submit a pull request

#### Pull Request Guidelines

- Provide a clear description of the changes
- Reference any related issues
- Ensure all tests pass (if applicable)
- Update documentation as needed
- Follow the existing code style

## Code Style Guidelines

### Kotlin Code Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused on a single responsibility
- Add KDoc comments for public APIs and complex logic
- Use the existing code as a reference for style

### File Organization

- Place shared code in `commonMain`
- Use `androidMain` and `iosMain` for platform-specific implementations
- Keep screens in the `screens/` package
- Place reusable components in `common/` package
- Put data models in `modal/` package
- Utility functions go in `utils/` package

### Composable Functions

- Use PascalCase for composable function names
- Document parameters with `@param` tags
- Hoist state when appropriate
- Keep composables focused and reusable

### Commit Message Conventions

Follow conventional commits format:

```
<type>: <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```
feat: Add task sorting by due date
fix: Resolve calendar date selection issue
docs: Update README with new features
```

## Development Setup

### Prerequisites

- JDK 17 or higher
- Android Studio (latest stable version recommended)
- Xcode (for iOS development, macOS only)
- CocoaPods (for iOS dependencies)

### Setup Steps

1. **Clone your fork:**

```bash
git clone https://github.com/your-username/Taskaroo.git
cd Taskaroo
```

2. **iOS Configuration:**

```bash
cp iosApp/Configuration/Config.xcconfig.template iosApp/Configuration/Config.xcconfig
# Add your Apple Developer Team ID to Config.xcconfig
```

3. **Install iOS dependencies:**

```bash
cd iosApp
pod install
cd ..
```

4. **Open in your IDE:**
   - Android Studio for Android/multiplatform development
   - Xcode for iOS-specific work

## Testing Guidelines

- Test your changes on both Android and iOS when possible
- Verify the app builds successfully
- Test on different screen sizes if UI changes are involved
- Check for memory leaks and performance issues
- Ensure offline functionality works correctly

## Project Structure

```
Taskaroo/
├── composeApp/
│   ├── commonMain/          # Shared code
│   │   ├── kotlin/
│   │   │   ├── common/      # UI components
│   │   │   ├── database/    # Database layer
│   │   │   ├── modal/       # Data models
│   │   │   ├── screens/     # Screen composables
│   │   │   └── utils/       # Utilities
│   │   └── sqldelight/      # Database schemas
│   ├── androidMain/         # Android-specific
│   └── iosMain/             # iOS-specific
└── iosApp/                  # iOS app wrapper
```

## Questions?

If you have questions about contributing, feel free to:

- Open an issue for discussion
- Contact the maintainer: Muhammad Ali ([Portfolio](https://muhammadali0092.netlify.app/))

## Code of Conduct

Please be respectful and considerate in all interactions. We aim to maintain a welcoming and inclusive community.

## License

By contributing to Taskaroo, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Taskaroo!
