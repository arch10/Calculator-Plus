# Changelog

## v3.1.2

### Bug Fixes
- **Accent colours lost in the clear animation**: the `clearView` reveal read `colorPrimaryContainer` directly in `MainActivity` and all six `activity_main.xml` variants, bypassing the tinted `@color/primary*Clear` values each static accent theme defines, so every accent animated in the same container colour. The layouts and `MainActivity.setAccentColor` now resolve `?attr/clearColor`, and `MaterialDynamicTheme` declares its own `clearColor` pointing at `colorPrimaryContainer` in both `values/themes.xml` and `values-night/themes.xml` so Dynamic Colors still resolves through the overlay

### Build & Toolchain
- Added the `com.google.android.gms.ads.flag.OPTIMIZE_AD_LOADING` manifest flag, letting the Mobile Ads SDK move part of its initialisation off the critical path — the banner loads during `MainActivity` startup

### CI & Tooling
- **Fastlane `bump` lane overwrote `versionName`**: the lane ran `bump_patch(read_version_name)` and wrote the result back to `app/build.gradle`. Because `auto-bump.yml` fires on every push to `main`, a release commit setting a new `versionName` was immediately overwritten with a patch bump of the old one, making any minor or major release impossible to ship — this is how 3.1.0 became 3.1.1. The lane now bumps `VERSION_CODE` only and commits `version.properties` alone; `versionName` stays human-owned. Removed the dead `write_version_name` and `bump_patch` helpers

---

## v3.1.1

### New Features
- **TapMind waterfall mediation**: integrated the TapMind custom event adapter as waterfall backfill beneath AdMob bidding on all three banner placements (main, history, settings). Three eCPM tiers are mapped in the AdMob console against `com.tapmind.mediation.TapMindAdmobAdapter`
- **Ad source logging**: new `ResponseInfo.logAdSource()` extension in `ExtensionFunctions.kt` logs the winning ad source plus the full waterfall attempt list (adapter class, latency, per-rung error) from `onAdLoaded` and `onAdFailedToLoad`. Debug builds only — `printLogD` is a no-op in release
- **Height-gated banner**: the banner is now gated on a `show_banner_ad` bool, `false` in `values/` and `true` in `values-h400dp/`. A 50dp banner needs roughly 387dp of height to coexist with the system chrome, expression and five numpad rows, so short configurations (landscape phones, small split-screen windows) drop it instead of clipping the pads. Only `MainActivity` is gated; History and Settings scroll

### Bug Fixes
- **Release crash on launch**: R8 could rename or inline Hilt-generated superclasses, breaking the `GeneratedComponentManagerHolder` instanceof check inside `ActivityComponentManager.createComponent`. Added a keep rule for `@HiltAndroidApp`-annotated classes (issue #72)
- **Live result hidden on tall, narrow screens**: every layout showing the result view was gated behind an `sw` qualifier, so a device whose smallestWidth falls under 360dp dropped to base `layout/` and got compact mode regardless of available height. A OnePlus 11R (1240x2772 @ 560dpi) resolves to sw354dp/h792dp — it misses the floor by 6dp. Added an unqualified `layout-h500dp/result_pad.xml` so the height qualifier can apply on its own
- **Landscape rendered the portrait layout**: `-land` variants existed only for sw360dp, sw400dp and sw480dp, so devices below the sw360dp floor fell through to the portrait base layout with the numpad crushed into unreadable slivers. Added `layout-land/`
- **Numpad clipped in landscape**: `Widget.CalculatorPlus.NumPad.*` inherits AppCompat's 48dp/88dp `minHeight`/`minWidth` via `Widget.Material3.Button`, but landscape allocates roughly 32dp per row, so buttons overflowed their rows and glyphs were sliced. Cleared both minimums and set `includeFontPadding=false` on `NumPad.Primary`, `NumPad.Operator` and `NumPad.Secondary`

### UI
- Qualified layouts realigned with the v3.0.0 Material You migration, which had only updated the base `layout/` files — most phones (sw>=360dp) were still getting the pre-3.0 design. Deleted `layout-sw360dp/num_pad.xml` and `layout-sw400dp/num_pad.xml` (byte-identical to base apart from 5 stale divider Views and the pre-M3 operator styling) and `layout-sw360dp/activity_main.xml` and `layout-sw400dp/activity_main.xml` (a single stale `clearView` background)
- Ported M3 tokens into the variants carrying real differences: `operatorBtnColor` to `colorPrimary`, `clearColor` to `colorPrimaryContainer`, and the missing `colorSurface` root background on `scientific_pad`
- `values-land/themes.xml` drops the numpad type scale to `TitleLarge`, restored to the full portrait scale for tablets in `values-sw480dp-land/themes.xml`
- The result view is hidden in the landscape result pads and the expression takes the whole area; pressing equals writes the result into the expression via the existing compact path

### Dependencies
- `com.google.android.material:material` 1.12.0 → 1.13.0
- `com.google.gms:google-services` 4.4.4 → 4.5.0
- Added `io.github.tapmind-tech:customadapter-admob:3.0.2` and the JitPack repository
- `play-services-ads` is declared as 24.8.0 but now resolves to 25.0.0, pulled up transitively by the TapMind SDK

---

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