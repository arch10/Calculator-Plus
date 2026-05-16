# Changelog

## v2.7.0

### Build & Toolchain
- Upgraded Gradle wrapper 8.13 → 9.4.1 (required by AGP 9.2)
- Upgraded AGP 8.11.1 → 9.2.0 → 9.2.1
- Upgraded Kotlin 2.1.20 → 2.3.21 (now managed via `android.kotlin.version`, KGP classpath removed)
- Upgraded KSP to independent versioning: 2.3.8
- Upgraded Hilt 2.57 → 2.59.2 (required for AGP 9 — drops `BaseExtension` dependency)
- Upgraded Firebase perf-plugin 2.0.0 → 2.0.2 (fixes AGP 9 Transform API removal)
- Migrated from `kotlinOptions { jvmTarget }` to `kotlin { jvmToolchain(17) }`
- Bumped `compileSdk` 35 → 36

### Android 16 (API 36) Support
- Bumped `targetSdkVersion` 35 → 36
- Added `android:enableOnBackInvokedCallback="true"` to `AndroidManifest.xml` to enable predictive back gesture animations (all activities already use `OnBackPressedCallback`)

### R8 Minification & Resource Shrinking
- Enabled `minifyEnabled true` and `shrinkResources true` in the release build type
- Added ProGuard keep rules for: BigMath, custom XML-inflated views, enums, Firebase Crashlytics stack traces, TapTargetView, Play Review API
- Enabled source file and line number attributes so Crashlytics stack traces remain readable after obfuscation
- Release APK size reduced ~68%: 14.85 MB → 4.72 MB (~10 MB saved)

### Bug Fixes
- **Theme crash on Android 16**: Migrated all `BaseTheme` variants from `Theme.MaterialComponents` (M2) to `Theme.Material3.DayNight.NoActionBar` — `MaterialToolbar` in `material:1.12.0` requires `colorSurfaceContainer` which M2 does not define, causing an `InflateException` on API 36
- **Release build crash (Firebase Perf)**: Disabled Firebase Perf bytecode instrumentation (`instrumentationEnabled = false`) — the AGP 9.x transform was corrupting the themed `Context` passed into `MaterialToolbar`'s constructor; runtime data collection (network, custom traces, app start) is unaffected
- **ProGuard stripping**: Added keep rules for `MainActivity.changeAngleType` (referenced by name in `main_menu.xml`) and About fragments (instantiated via `Navigation`'s `Class.forName`)

### UI
- Added padding and `maxLines` constraint to NumPad button styles (`res/values/themes.xml`)

### Dependencies
- `androidx.core:core-ktx` 1.16.0 → 1.18.0
- `androidx.lifecycle` (viewmodel + livedata) 2.9.2 → 2.10.0
- `firebase-bom` 34.0.0 → 34.13.0
- `play-services-ads` 24.5.0 → 24.8.0
- `google-services` plugin 4.4.3 → 4.4.4
- `firebase-crashlytics-gradle` 3.0.5 → 3.0.7

### CI / Tooling
- Added Fastlane configuration for version bumping and release management
- Added GitHub Copilot environment setup workflow (`copilot-setup-steps.yml`)
- Refined CI workflow triggers, removed redundant dependency downloads
- Added `CLAUDE.md` with agent guidance for the project
- Removed volatile `.idea/` files from version control (added to `.gitignore`)

---

## v2.6.0

### New Features
- **Calculation History**: Complete history management system - view, clear, and reuse your previous calculations
- **Theme Customization**: Multiple app themes including system default, light, and dark modes
- **Accent Colors**: Choose from 6 different accent colors (Blue, Green, Purple, Pink, Red, Grey) to personalize your experience
- **Smart Calculation**: Auto-complete equations for faster input
- **Memory Functions**: Store and recall values with memory buttons (MS/MR)

### Improvements  
- **Android 14 Support**: Updated target SDK to Android 14 for better compatibility
- **Themed Icons**: Added support for Android's themed icon system
- **Enhanced Performance**: Updated core libraries for improved calculation speed
- **Better Settings**: Organized settings with number separators, precision control, and auto-delete history options
- **Multi-language Support**: Available in English, German, Spanish, French, Portuguese, and Hindi

### Technical Updates
- Updated all library dependencies for security and performance
- Improved error handling for invalid expressions and edge cases
- Enhanced UI responsiveness across different screen sizes