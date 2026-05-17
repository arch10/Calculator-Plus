# Changelog

## v3.0.0

### New Features
- **Haptic feedback**: optional vibration on every button tap; toggled in Settings (`AppPreference.HAPTIC_FEEDBACK`, `HapticFeedback.kt`)
- **Compact mode**: result display area compresses automatically on small or landscape screens; layout breakpoints defined in `result_pad.xml` for sw360dp, sw400dp, sw480dp

### Material You Design Overhaul
- Migrated all `TextAppearance` styles from `TextAppearance.MaterialComponents.*` to `TextAppearance.Material3.*` parents
- Re-pointed all custom theme attrs (`operatorBtnColor`, `numPadPrimary`, `textPrimary`, etc.) to M3 semantic tokens so Dynamic Colors works automatically
- Added `MaterialDynamicTheme` style — activates `DynamicColors.applyToActivityIfAvailable()` on Android 12+ to colour the UI from the device wallpaper
- Added `DYNAMIC` to `AccentTheme` enum; Dynamic swatch appears in the colour picker only on API 31+ devices
- Centralised accent theme application in `BaseActivity` — `HistoryActivity` and `AboutActivity` now correctly inherit the selected accent theme
- Replaced Open Sans with Google Sans across all `TextAppearance` styles

### Bug Fixes
- **DynamicColors broken on API 27+ devices**: `values-v27/themes.xml` and `values-night-v27/themes.xml` were not updated during the Material You migration — custom attrs (`textPrimary`, `textSecondary`, `border`, `operatorBtnColor`, etc.) were pointing to static colour resources instead of M3 semantic tokens, so Dynamic Colors had no effect on the vast majority of active devices

### Splash Screen
- Replaced static mipmap launcher icon with `ic_splash_animated.xml` — an `AnimatedVectorDrawable` containing the four calculator symbols (+, −, ×, =); on API 31+ they cascade-pulse with a 150 ms stagger; the first frame is fully rendered so the compat layer displays correctly on Android 7+
- Icon uses `@color/splashIcon` (dark in light mode, white in dark mode) against `@color/background` — no forced brand colour that clashes with accent themes

### Numpad & Buttons
- `button_corner_radius` raised `0dp` → `16dp` — all buttons now have M3 medium-shape rounded corners
- Added `3dp` margins to each button and `4dp` outer padding to numpad container — creates visible gaps between buttons
- Operator buttons (÷, ×, +, −) switched to `NumPad.Secondary` style — accent-colour fill with white text, clearly distinct from number keys
- Removed flat border dividers between number and operator columns

### Settings Screen
- Each settings section (General, User Interface, App) wrapped in a `MaterialCardView` with `12dp` corner radius and `colorSurfaceContainerHigh` background
- Internal row dividers use `?attr/colorOutlineVariant`
- Added Haptic Feedback toggle row with vibrate icon

### About Screen
- Items (Join Beta, Open Source, Privacy Policy, Terms of Use) regrouped into a single `MaterialCardView` matching the settings page pattern — 72dp rows with two-line title/subtitle layout (`Body1`/`Body2`) and `colorOutlineVariant` dividers between items
- All `MaterialCardView` corner radii bumped from `0dp` to `12dp`

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