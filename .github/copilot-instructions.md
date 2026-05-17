# Calculator Plus - Android Calculator App

Calculator Plus is a feature-rich Android calculator application written in Kotlin and Java using modern Android development practices. The app provides basic and advanced mathematical functions with smart calculations, error correction, and supports both light and dark themes.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites
- **REQUIRED**: Java 17 (OpenJDK 17.0.16+ recommended) - already installed in most development environments
- Android SDK API level 35 (automatically downloaded via Gradle)
- **NO manual SDK installation required** - Gradle handles all Android dependencies

### Build System Overview
- **Build Tool**: Gradle 8.13 with Gradle Wrapper (use `./gradlew` not `gradle`)
- **Languages**: Kotlin 2.1.20, Java 17
- **Android**: API 35 target, API 23 minimum
- **Architecture**: MVVM with Hilt dependency injection, Room database, Firebase integration

### Essential Build Commands
Bootstrap and build the project:
```bash
# Clean build from scratch - NEVER CANCEL: takes 98 seconds. Set timeout to 120+ seconds
./gradlew clean build --no-daemon

# Incremental build - takes 70 seconds. Set timeout to 90+ seconds  
./gradlew build --no-daemon

# Compile only (faster for development) - takes 28 seconds
./gradlew compileDebugSources --no-daemon
```

### Testing Commands
```bash
# Run all unit tests - NEVER CANCEL: takes 34 seconds. Set timeout to 60+ seconds
./gradlew test --no-daemon

# Run specific test variants
./gradlew testDebugUnitTest --no-daemon
./gradlew testReleaseUnitTest --no-daemon
```

### Code Quality Commands
```bash
# Run lint analysis - NEVER CANCEL: takes 52 seconds. Set timeout to 90+ seconds
./gradlew lint --no-daemon

# Generate lint reports (HTML output in app/build/reports/lint-results-debug.html)
./gradlew lintDebug --no-daemon
```

### APK/Bundle Generation
```bash
# Generate debug APK - takes 28 seconds. Set timeout to 45+ seconds
./gradlew assembleDebug --no-daemon

# Generate release APK (unsigned) - takes similar time
./gradlew assembleRelease --no-daemon

# Generate app bundle (AAB) for Play Store - takes 40 seconds. Set timeout to 60+ seconds
./gradlew bundleDebug --no-daemon
./gradlew bundleRelease --no-daemon
```

### Utility Commands
```bash
# Clean build directory - takes 10 seconds
./gradlew clean --no-daemon

# Show current app version
./gradlew printVersionName --no-daemon

# Bump version code (used in CI)
./gradlew bumpVersionCode --no-daemon

# List all available tasks
./gradlew tasks --no-daemon
```

## Critical Timing Information
- **NEVER CANCEL builds or long-running commands**
- **Clean + Build**: 98 seconds (1m 38s) - always set timeout to 120+ seconds
- **Incremental Build**: 70 seconds (1m 10s) - always set timeout to 90+ seconds  
- **Unit Tests**: 34 seconds - always set timeout to 60+ seconds
- **Lint Analysis**: 52 seconds - always set timeout to 90+ seconds
- **APK Assembly**: 28 seconds - always set timeout to 45+ seconds
- **AAB Bundle**: 40 seconds - always set timeout to 60+ seconds

## Validation Requirements

### Build Validation
After making code changes, ALWAYS run these validation steps:
1. **Clean build**: `./gradlew clean build --no-daemon` (98 seconds)
2. **Unit tests**: `./gradlew test --no-daemon` (34 seconds)  
3. **Lint check**: `./gradlew lint --no-daemon` (52 seconds)
4. **APK generation**: `./gradlew assembleDebug --no-daemon` (28 seconds)

### Manual Functionality Testing
**CRITICAL**: After code changes, you MUST test actual functionality:

1. **Build and locate APK**:
   ```bash
   ./gradlew assembleDebug --no-daemon
   find app/build/outputs -name "*.apk" -type f
   ```

2. **Verify APK contents** (basic validation):
   ```bash
   # Check APK was created and has reasonable size (should be 5-15 MB)
   ls -lh app/build/outputs/apk/debug/app-debug.apk
   ```

**Note**: You cannot run the Android app directly in this environment (no emulator), but you MUST verify the APK builds successfully and passes all tests.

## Project Structure

### Key Source Directories
- `app/src/main/java/com/gigaworks/tech/calculator/` - Main application code
  - `ui/` - User interface components (Activities, Fragments)  
  - `domain/` - Business logic and use cases
  - `repository/` - Data access layer
  - `di/` - Dependency injection modules (Hilt)
  - `util/` - Utility classes
  - `cache/` - Local data caching

### Key Test Directories  
- `app/src/test/java/com/gigaworks/tech/calculator/ui/main/helper/` - Unit tests
  - `CalculationUnitTest.kt` - Calculator logic tests
  - `EvaluateUnitTest.kt` - Expression evaluation tests
  - `ExtensionUnitTest.kt` - Utility extension tests
  - `HandleButtonClickTest.kt` - UI interaction tests

### Key Configuration Files
- `build.gradle` (root) - Project-level build configuration
- `app/build.gradle` - App module build configuration  
- `gradle.properties` - Gradle build properties
- `version.properties` - App version configuration (VERSION_CODE=66)

## Common Development Tasks

### Adding New Features
1. Write tests first in appropriate test directory
2. Implement feature in main source
3. Run validation commands: `build` → `test` → `lint` → `assembleDebug`
4. Always ensure APK builds successfully

### Dependency Management  
- **Add dependencies** in `app/build.gradle` dependencies block
- **Key dependencies**: Hilt, Room, Firebase, Navigation Components, Material Design
- Run `./gradlew build` after adding dependencies

### Code Style Requirements
- Follow existing Kotlin coding conventions
- Use 2 spaces for indentation, no tabs
- Follow style guide in `docs/en/style-guide.md`
- Lint will catch style violations automatically

## CI/CD Information
- GitHub Actions workflows in `.github/workflows/`
- `build-release.yml` - Handles Play Store releases
- Uses Java 17, matches local development environment
- Signing requires secrets (not available in dev environment)

## Troubleshooting

### Build Failures
1. **Clean first**: `./gradlew clean --no-daemon` 
2. **Check Java version**: Should be Java 17
3. **Gradle version**: 8.13 (use `./gradlew --version`)
4. **Common warning**: Deprecated Gradle features warning is normal and non-blocking

### Test Failures
- Unit tests are located in calculator logic helpers
- Test failures indicate actual logic errors that must be fixed
- **Never modify tests to pass** - fix the underlying code instead

### Lint Issues  
- Lint reports are generated in `app/build/reports/lint-results-debug.html`
- Some deprecation warnings (like `overridePendingTransition`) are known and acceptable
- Critical lint issues will fail the build

## Quick Reference Commands

```bash
# Essential development cycle (3-4 minutes total)
./gradlew clean build test lint assembleDebug --no-daemon

# Fast development cycle (for small changes)
./gradlew compileDebugSources test --no-daemon

# Pre-commit validation
./gradlew lint test --no-daemon
```

Always use `--no-daemon` flag to avoid Gradle daemon issues in CI environments.