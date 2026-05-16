---
name: release
description: >
  Automates release preparation for the Calculator Plus Android app. Use this
  skill whenever the user says the app is ready for a release, asks to prepare
  a release, wants to bump the version, or says anything like "let's release",
  "prepare v2.x", "update version for release", or "generate what's new".
  This skill handles everything: finding what changed since the last release,
  asking for the new version number, updating app/build.gradle, writing the
  CHANGELOG.md entry, and writing docs/whatsnew/whatsnew-en-GB.
---

# Calculator Plus — Release Preparation

You are preparing a new release of the Calculator Plus Android app. Follow
every step below in order. Do not skip steps or reorder them.

**Project root:** `C:\Users\pc\Documents\github\Calculator-Plus`

---

## Step 1 — Find the last released version

Read `CHANGELOG.md`. The first `## vX.Y.Z` heading is the last released
version. Note this version string (e.g. `2.7.0`).

---

## Step 2 — Find the baseline commit

You need the commit that represents the start of work *after* that last
release so you can collect only the new changes.

Try in order until one works:

1. Check for a git tag matching the version:
   ```
   git tag --list "v{last_version}"
   ```
   If it exists, use it as the baseline: `git log v{last_version}..HEAD --oneline`

2. If no tag, search the git log for the CI version-bump commit for that
   version:
   ```
   git log --oneline --grep="bump version to {last_version}"
   ```
   Use the hash of that commit as the baseline:
   `git log {hash}..HEAD --oneline`

3. If still nothing, use the full `git log --oneline` and visually find the
   last release boundary (look for a "bump version to {last_version}" commit).

---

## Step 3 — Collect all commits since the baseline

Run:
```
git log {baseline}..HEAD --oneline
```

Filter out these — they carry no useful changelog information:
- `ci: bump version to X.Y.Z` commits
- Merge commits (`Merge branch ...` / `Merge pull request ...`)

For every remaining commit (feat, fix, build, refactor, chore, docs, UI),
get the full detail:
```
git show {hash} --stat
```
For commits that touch source files (not just docs/config), also read the
full commit body:
```
git log -1 {hash} --format="%B"
```

Build a map of:
- What user-visible things changed (new features, bug fixes, UI tweaks)
- What internal/build things changed (toolchain, ProGuard, CI)
- Exact version numbers of upgraded dependencies

---

## Step 4 — Determine the new version

Look at the current `versionName` in `app/build.gradle`
(`defaultConfig.versionName`). This may already be ahead of the last
release if the user bumped it manually.

Apply this logic to suggest the next version:
- Any `feat:` commit (new user-visible feature) → bump the **minor** version
  (e.g. 2.6.x → 2.7.0)
- Only `fix:`, `build:`, `chore:`, `ci:` commits → bump the **patch** version
  (e.g. 2.6.0 → 2.6.1)

Show the user:
- Last released version: `vX.Y.Z`
- Current versionName in build.gradle: `X.Y.Z`
- Suggested new version: `X.Y.Z` (with one-line reason)

Ask: **"What should the new version number be?"**

Wait for the user's answer before proceeding.

---

## Step 5 — Update versionName in app/build.gradle

Edit `app/build.gradle`. Find the line inside `defaultConfig {}`:
```
versionName "X.Y.Z"
```
Change it to the version the user confirmed.

**Never touch `version.properties`** — `VERSION_CODE` is managed exclusively
by CI via the `bumpVersionCode` Gradle task.

---

## Step 6 — Write the CHANGELOG.md entry

Insert a new section at the top of `CHANGELOG.md`, immediately after the
`# Changelog` heading and before the previous `## vX.Y.Z` section.

### Format rules
- Plain technical markdown — **no emojis**
- Group under these headings (omit any with no entries):
  - `### New Features`
  - `### Bug Fixes`
  - `### UI`
  - `### Build & Toolchain`
  - `### Dependencies`
  - `### CI & Tooling`
- Be specific: include file names, class names, method names, and exact
  version numbers (e.g. `AGP 9.2.0 → 9.2.1`, not just "upgraded AGP")
- For bug fixes, include the root cause in one sentence (commit bodies
  usually have this — use them verbatim if clear)
- Skip all `ci: bump version to X.Y.Z` commits entirely
- Skip merge commits
- Add a `---` divider after the new section to separate it from the previous one

### Example shape
```markdown
## v2.8.0

### New Features
- Added scientific notation toggle in Settings (`SettingsActivity`,
  `AppPreference.SCIENTIFIC_NOTATION`)

### Bug Fixes
- **Theme crash on Android 16**: Migrated `BaseTheme` from
  `Theme.MaterialComponents` to `Theme.Material3.DayNight.NoActionBar` —
  `MaterialToolbar` requires `colorSurfaceContainer` which M2 does not define

### Build & Toolchain
- Upgraded AGP 8.11.1 → 9.2.0; Gradle wrapper 8.13 → 9.4.1
- Enabled R8 minification and resource shrinking; APK size 14.85 MB → 4.72 MB

### Dependencies
- `firebase-bom` 34.0.0 → 34.13.0
- `play-services-ads` 24.5.0 → 24.8.0

---
```

---

## Step 7 — Write docs/whatsnew/whatsnew-en-GB

This file is published to the Google Play Store and read by end users.

### Rules
- **4 to 6 bullet points maximum**
- One emoji at the start of each line
- First line is always: `🎉 Calculator Plus vX.Y.Z is here!`
- Then a blank line, then the bullets
- Each bullet: `emoji Short label — one plain sentence`
- **No technical jargon** — no class names, no library names, no build terms
- **Do not mention** toolchain upgrades, ProGuard, Gradle, Kotlin, AGP,
  dependency version bumps, CI changes, or anything a non-developer would
  not recognise
- Focus on what a user would notice: new features, visible UI changes,
  performance improvements, crash fixes, new Android version support
- Read the existing file first and match its tone and brevity exactly

### Example
```
🎉 Calculator Plus v2.8.0 is here!

✨ Scientific notation — results now display in scientific format when needed
📱 Android 16 support — fully compatible with the latest Android
⚡ Faster & lighter — app loads quicker and takes less storage
🔧 Under the hood improvements for better stability and performance
```

Overwrite the entire file with the new content.

---

## Step 8 — Summary and commit offer

Show the user a concise summary of every file changed:

1. `app/build.gradle` — `versionName` updated to `X.Y.Z`
2. `CHANGELOG.md` — new `## vX.Y.Z` section added at the top
3. `docs/whatsnew/whatsnew-en-GB` — updated for the new release

Then ask: **"Do you want me to commit these changes?"**

If yes, stage only these three files and commit:
```
chore: prepare release vX.Y.Z
```
Do **not** push — the user triggers the GitHub Actions release workflow
manually from the Actions tab.

---

## Hard rules

- Never edit `version.properties` — CI owns `VERSION_CODE`
- Never run `./gradlew bumpVersionCode` — CI does this during the release workflow
- Never push to remote
- No emojis in `CHANGELOG.md`
- No class/library/version names in `whatsnew-en-GB`
- No build tooling changes in `whatsnew-en-GB`
