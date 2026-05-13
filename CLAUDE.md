# Calculator Plus — Agent Guidance

## Project Overview

**Calculator Plus** is a single-module Android calculator app (package `com.gigaworks.tech.calculator`) supporting both a standard numpad and a scientific pad. Current release is **v2.6.0** (versionCode 66, targetSdk 35, minSdk 23).

---

## Build System

| Tool | Version |
|---|---|
| Gradle | 9.4.1 |
| Kotlin | 2.3.21 |
| KSP | 2.3.8 |
| AGP | 9.2.0 |
| Java compatibility | VERSION_17 |

### Key Gradle Commands

```bash
./gradlew assembleDebug          # debug APK
./gradlew bundleRelease          # release AAB (for Play Store)
./gradlew test                   # run unit tests
./gradlew lint                   # lint check
./gradlew bumpVersionCode        # increment VERSION_CODE in version.properties
./gradlew printVersionName       # prints current versionName
./gradlew clean                  # clean build directory
```

### Version Management

- `versionCode` lives in `version.properties` as `VERSION_CODE` — **never edit this manually**, the `bumpVersionCode` Gradle task owns it.
- `versionName` is hardcoded in `app/build.gradle` `defaultConfig.versionName` — update this manually before a release.
- Release signing secrets (`SIGNING_KEY`, `ALIAS`, `KEY_STORE_PASSWORD`, `KEY_PASSWORD`) are GitHub Actions secrets — do not hardcode them.

---

## Architecture

Clean layered architecture within a single `app` module:

```
ui/           → Activities, Fragments, ViewModels, custom Views
├── base/     → BaseActivity<B: ViewBinding>, BaseFragment<B: ViewBinding>
├── main/     → MainActivity, MainViewModel, helper functions, CalculatorPadViewPager
├── history/  → HistoryActivity, HistoryViewModel, HistoryAdapter
├── settings/ → SettingsActivity, SettingsViewModel, SettingsHelper
└── about/    → AboutActivity, fragments for About/Changelog/OpenSource

domain/       → Pure data classes: History, HistoryAdapterItem, License
repository/   → HistoryRepository (suspending functions, wraps Room DAO)
cache/        → Room database, HistoryDao, HistoryEntity, CacheUtil, Response<T>
di/           → Hilt modules: AppModule (AppPreference), CacheModule (Room DB)
util/         → AppPreference, constants, extension functions, enums, Logger
```

**Dependency injection:** Hilt (`@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`). All ViewModels use `@HiltViewModel`.

**View binding:** Enabled project-wide. Every activity/fragment uses `getViewBinding(inflater)` via the generic `BaseActivity<B>` / `BaseFragment<B>` pattern. Never use `findViewById`.

**Navigation:** Activity-based (no single-activity nav graph). `AboutActivity` uses Jetpack Navigation internally for its three fragments.

---

## Key Files

| File | Purpose |
|---|---|
| `app/build.gradle` | App-level dependencies, build types, SDK versions |
| `build.gradle` | Root classpath, Kotlin/Hilt/Firebase plugin versions |
| `version.properties` | `VERSION_CODE` — auto-managed by CI |
| `app/src/main/res/xml/remote_config_defaults.xml` | Default values for Firebase Remote Config keys |
| `app/src/main/java/.../util/AppPreference.kt` | All SharedPreferences keys in one place |
| `app/src/main/java/.../util/Constants.kt` | Firebase Analytics event name constants |
| `app/src/main/java/.../ui/main/helper/Evaluate.kt` | Core expression evaluator using `ch.obermuhlner:big-math` |
| `app/src/main/java/.../ui/main/helper/HandleButtonClicks.kt` | Pure functions for expression string manipulation |
| `app/src/main/java/.../ui/main/helper/NumberFormat.kt` | Number separator, rounding, formatting helpers |
| `app/src/main/java/.../util/GoogleMobileAdsConsentManager.kt` | GDPR/UMP consent flow wrapper |

---

## Calculation Engine

The evaluator lives in `ui/main/helper/Evaluate.kt`. Key facts:

- Uses `ch.obermuhlner:big-math:2.3.2` for arbitrary-precision arithmetic — never swap this for `java.lang.Math`.
- Throws `CalculationException(msg: CalculationMessage)` for four error states: `INVALID_EXPRESSION`, `DIVIDE_BY_ZERO`, `VALUE_TOO_LARGE`, `DOMAIN_ERROR`.
- `prepareExpression(expr)` normalises the string before evaluation (converts display symbols to math operators).
- `tryBalancingBrackets(expr)` is called by `MainViewModel` when the expression is unbalanced and Smart Calculation is on.
- Trig functions respect the `AngleType` (DEG/RAD) preference passed into `getResult(expression, angleType)`.

**Never call the evaluator on the main thread for long expressions.** `MainViewModel.calculateExpression()` runs synchronously but is called from `afterTextChanged`; keep expression prep fast.

---

## Preferences (AppPreference)

All preference keys are `const val` in `AppPreference.companion`. The SharedPreferences file name equals `BuildConfig.APPLICATION_ID`. Known keys:

| Key constant | Default | Purpose |
|---|---|---|
| `ANGLE_TYPE` | `DEG` | Trig angle unit |
| `NUMBER_SEPARATOR` | `INTERNATIONAL` | Thousands separator style |
| `SMART_CALCULATION` | `true` | Auto-balance brackets |
| `ANSWER_PRECISION` | `6` | Decimal places in result |
| `MEMORY` | `""` | Stored memory value |
| `EXPRESSION` | `""` | Saved expression across sessions |
| `APP_THEME` | `SYSTEM_DEFAULT` | Light / Dark / System |
| `ACCENT_THEME` | `BLUE` | Accent color scheme |
| `HISTORY_AUTO_DELETE` | — | History retention policy |
| `LAUNCH_COUNT` | `0` | App launch counter |
| `LAST_LAUNCH_DAY` | `0L` | Timestamp of first launch |
| `DISABLE_ADS` | `false` | User-level ad disable flag |

---

## Firebase & Remote Config

Remote Config keys (with defaults in `res/xml/remote_config_defaults.xml`):

| Key | Type | Purpose |
|---|---|---|
| `enable_ads` | Boolean | Master switch to disable all ads remotely |
| `main_ad_id` | String | AdMob banner ad unit ID |
| `allow_disabling_ads` | Boolean | Feature flag for user-controlled ad disable (currently commented out) |

Remote Config is fetched in `MainActivity.setupRemoteConfig()` on every cold start. Defaults are applied first, then a live fetch is attempted.

**Do not hardcode ad unit IDs.** The production ID comes from Remote Config. The test ID `ca-app-pub-3940256099942544/6300978111` is in a comment in `MainActivity.enableAds()` — only uncomment it for local testing, never commit it enabled.

---

## Ads (AdMob + UMP)

Ad lifecycle is managed in `MainActivity`:

1. `enableAds()` — checks Remote Config, then calls `GoogleMobileAdsConsentManager.gatherConsent()`.
2. `initializeMobileAdsSdk(adUnitId)` — guarded by `AtomicBoolean isMobileAdsInitializeCalled` to prevent double-init.
3. Banner ad is added dynamically to `binding.adViewContainer`.

The Privacy Settings menu item is only visible when `googleMobileAdsConsentManager.isPrivacyOptionsRequired` is true (EEA users).

---

## Themes & Accent Colors

- **App theme** (Light/Dark/System): controlled by `AppTheme` enum, applied via `AppCompatDelegate.setDefaultNightMode()` in `MainActivity.setAppTheme()`.
- **Accent themes** (`AccentTheme` enum): applied before `super.onCreate()` in `MainActivity` using `setTheme(getAccentTheme(accentTheme))`. Available accents: `BLUE`, `GREEN`, `ORANGE`, `PURPLE`, `RED`, `TEAL`, `YELLOW`.
- Theme must be set **before** `super.onCreate()` — changing the order will break theme application.
- Accent theme styles are defined in `res/values/themes.xml` and night variants in `res/values-night/themes.xml`.

---

## Testing

Unit tests are in `app/src/test/java/com/gigaworks/tech/calculator/ui/main/helper/`:

| File | Coverage |
|---|---|
| `CalculationUnitTest.kt` | Number formatting, bracket balancing, number separator logic |
| `EvaluateUnitTest.kt` | Expression evaluation correctness |
| `ExtensionUnitTest.kt` | String extension helpers |
| `HandleButtonClickTest.kt` | Button click expression building |

Framework: **JUnit 4**. No instrumentation or UI tests exist yet.

**Run tests before any PR:** `./gradlew test`. All four test files must pass.

**When adding new calculation features**, add corresponding test cases to `EvaluateUnitTest.kt` and `CalculationUnitTest.kt`.

---

## CI/CD Workflows

| Workflow | Trigger | What it does |
|---|---|---|
| `build-release.yml` | Manual (`workflow_dispatch`) | Builds AAB → signs → uploads to Play Store beta → bumps VERSION_CODE → creates GitHub Release (debug APK) → posts to Slack |
| `build-prerelease.yml` | Push to `beta` branch | Builds debug APK → creates pre-release tag `v{VERSION}-rc{run_number}` → posts to Slack |
| `copilot-setup-steps.yml` | Copilot setup | Sets up JDK for GitHub Copilot workspace |

**Release track:** The CI deploys to the **beta** track on Play Store. Promoting to production is a manual step in the Play Console.

**What's new files:** Located at `docs/whatsnew/whatsnew-en-GB` (and other locales). This file is used as the release body for both GitHub Releases and Play Store release notes. Update it before triggering a release.

---

## Code Conventions

- **Language:** Kotlin only. No Java source files.
- **Null safety:** Avoid `!!` — use safe calls, `let`, or `Elvis`. `AppPreference.getStringPreference` uses `!!` on SharedPreferences which is safe since defaults are always provided.
- **Logging:** Use `logD(tag, message)` and `logE(tag, message)` from `util/Logger.kt`. These are no-ops in release builds. Do not use `Log.d`/`Log.e` directly.
- **Analytics:** Use `logEvent(CONSTANT_NAME)` or `logEvent(CONSTANT_NAME) { param(...) }` from `BaseActivity`. All event name constants live in `util/Constants.kt`.
- **Comments:** Only for non-obvious WHY (constraints, workarounds). No block comments describing what the code does.
- **No mock databases in tests** — prefer in-memory Room for integration tests if added.

---

## Known Issues / Things to Watch

1. **R8 minification + resource shrinking are enabled** in release builds (`minifyEnabled true`, `shrinkResources true`). Keep rules for BigMath, custom views, and enums are in `app/proguard-rules.pro`. If adding a new library that uses reflection, add a corresponding keep rule there.
2. **Commented-out ad-disable feature** in `MainActivity.enableAds()` lines 154–163 — the `allow_disabling_ads` Remote Config key and `DISABLE_ADS` pref key are wired up but the UI toggle is not yet surfaced. Do not remove without also removing `AppPreference.DISABLE_ADS` and `MainViewModel.getDisableAds()`.
3. **`MainActivity` is 824 lines** — ad management, animation, theme setup, and click handling are all in one class. New features should not add more responsibilities here; extract into helpers or managers instead.
4. **No Lint baseline** — `./gradlew lint` will report all issues. Do not suppress lint warnings globally; fix them or add targeted `@SuppressLint` annotations.

---

## Adding a New Feature Checklist

- [ ] Domain model (if new data) → `domain/`
- [ ] Room entity + DAO migration (if DB change) → `cache/`, bump `DATABASE_VERSION` in `Database.kt`
- [ ] Repository method → `repository/`
- [ ] ViewModel logic → appropriate ViewModel in `ui/<screen>/viewmodel/`
- [ ] UI wiring in Activity/Fragment using View Binding
- [ ] Preference key added to `AppPreference` companion if a setting is involved
- [ ] Analytics event constant added to `util/Constants.kt`, `logEvent()` called at the right moment
- [ ] Unit tests added or updated
- [ ] `docs/whatsnew/whatsnew-en-GB` updated if user-facing

---

## Release Checklist

1. Update `versionName` in `app/build.gradle` (e.g., `2.6.0` → `2.7.0`)
2. Update `docs/whatsnew/whatsnew-en-GB` and locale variants
3. Run `./gradlew test` — all tests must pass
4. Run `./gradlew lint` — resolve any new errors
5. Commit and push to `main`
6. Trigger `App Release` workflow manually from GitHub Actions
7. Verify the beta track upload in Play Console
8. Promote from beta to production in Play Console after soak period
