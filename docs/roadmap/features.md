# Feature Roadmap & Improvements

This document outlines planned features and potential improvements for **Calculator Plus**.

## 1. New Features
- [ ] **Unit Converter:** Support for Length, Weight, Temperature, Speed, Area, and Volume.
- [ ] **Currency Converter:** Real-time conversion using an external API.
- [ ] **Floating Calculator:** Overlay mode to use the calculator on top of other applications.
- [ ] **Date Calculator:** Calculate duration between dates or add/subtract time from a date.
- [ ] **Equation Solver:** Solving linear and quadratic equations.
- [ ] **Scientific Constants:** A library of constants (e.g., Planck's, Speed of Light) for physics and chemistry.
- [ ] **Programmer Mode:** A dedicated pad for bitwise operations (AND, OR, XOR, NOT, shifts) with base conversion between Binary, Octal, Decimal, and Hexadecimal.
- [ ] **Statistical Functions:** Add `mean`, `median`, `mode`, `std dev`, and `variance` for a comma-separated list of numbers — useful for students and analysts.
- [ ] **Expression Templates / Snippets:** Let users save frequently-used expressions (e.g., compound interest formula) and recall them with a single tap.
- [ ] **Home Screen Widget:** A compact widget showing the last result and a minimal input field so users can calculate without opening the app.
- [ ] **Wear OS Companion:** A basic calculator face/tile for Android smartwatch users.
- [ ] **Voice Input:** Accept spoken expressions via the device microphone and parse them into the expression field.

## 2. UI/UX Improvements
- [ ] **Jetpack Compose Migration:** Modernize the UI layer for better performance and maintainability.
- [ ] **Material You (Dynamic Colors):** Support for Android 12+ dynamic color themes.
- [ ] **Graphing Tool:** Ability to plot mathematical functions.
- [ ] **Advanced History:**
    - [ ] Add notes to history items.
    - [ ] Export history to CSV/Text.
    - [ ] Search/filter history by expression or result.
    - [ ] Pin favourite calculations to the top of the history list.
- [ ] **Customizable Haptics:** Settings to adjust haptic feedback intensity.
- [ ] **Subtle Animations:** Enhance UI transitions and button press animations for a more fluid feel.
- [ ] **Optimized Landscape Layout:** A side-by-side expression + scientific pad layout when the device is in landscape orientation, instead of the current ViewPager scroll.
- [ ] **Calculation Steps Panel:** Show intermediate steps for multi-operation expressions so users can verify their working (especially useful for students).
- [ ] **Gesture Shortcuts:**
    - [ ] Swipe left on the expression field to clear the last character (alternative to backspace).
    - [ ] Shake device to clear the current expression (re-enable the disabled shake sensor).
- [ ] **Clipboard Smart Paste:** When the clipboard contains a number, show a paste chip above the keyboard so users can inject it into the expression in one tap.

## 3. Technical Enhancements
- [ ] **Robust Math Engine:** Explore formal parsers for more complex symbolic mathematics.
- [ ] **Improved Tablet Support:** Optimize layouts for larger screens and foldables.
- [ ] **Localization:** Expand translations for more languages.
- [ ] **History Cloud Backup:** Optional Google Drive / account-based backup and restore of calculation history, so users don't lose data on device switches.
- [ ] **Crashlytics Breadcrumbs:** Log key user actions (button presses, mode switches) as Crashlytics breadcrumbs to make crash reports more actionable.
- [ ] **Baseline Profiles:** Ship a Baseline Profile (`baseline-prof.txt`) to speed up first-launch JIT compilation and reduce startup time.
- [ ] **Accessibility Improvements:** Ensure all buttons have `contentDescription`, support TalkBack navigation order, and respect system font-scale for the result display.
