# Deltip

Mini tool for calculating discounts (and, since v0.2.0, for splitting gross
amounts into net and VAT). Runs on Linux and Windows desktop as well as on
Android. Offline, no ads, no tracking.

> **Status:** Beta. The current version is tracked in
> [`gradle.properties`](gradle.properties) (`app.version`). Public APIs and the
> persisted settings schema may still change.

## Modes

- **Discount mode (default):** enter a price → the discounted final price is
  shown immediately and copied to the clipboard a moment later.
- **VAT mode:** enter a gross amount → the net amount and the contained VAT
  share are shown. Auto-copy target: the VAT amount (Anteil).

Both modes share the same input rules (`12,50` and `12.50` are treated
identically) and the same auto-copy behaviour (300 ms debounce after the last
keystroke).

## Requirements

- **JDK 21** (Eclipse Temurin or compatible)
- Git
- Optional, for native Linux packages: `fakeroot`, `rpm`
- Optional, for the Windows `.exe`: WiX Toolset 3.x

## Build and run

```bash
git clone https://github.com/inventory69/deltip.git
cd deltip

./gradlew :composeApp:run                              # launch desktop app
./gradlew :composeApp:assembleDebug                    # build Android APK
./gradlew :composeApp:packageDistributionForCurrentOS  # build native packages
./gradlew :composeApp:allTests                         # run unit tests
```

Native packages are written to `composeApp/build/compose/binaries/main/`.

## Usage

1. Open the app — the input cursor is already focused in the price (or gross)
   field.
2. Pick the mode using the **Rabatt** / **MwSt** toggle above the input field.
3. Type a value, e.g. `49,99`.
4. Read the result. After a short pause the relevant value is in the clipboard.
5. Click into the input field again to clear it — the cursor stays in the
   field, ready for the next value.

The settings bar at the top (discount % or VAT %, currency symbol, "Vorne",
"Autostart") persists its values across restarts. Since v0.2.0 the desktop
window also remembers its last on-screen position.

## Platform notes

- **Linux desktop:** autostart is wired up via
  `~/.config/autostart/deltip.desktop`.
- **Windows desktop:** autostart is wired up via the registry key
  `HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run`. Since
  v0.2.0 the window title bar follows the system dark mode.
- **Android:** autostart and "always on top" are not available; the
  corresponding toggles are hidden.

## Project documentation

Architecture, version matrix, constraints and implementation plans live in a
separate repository:

- `project-docs/deltip/` (private) — `developer_constraints.md`,
  `architektur.md`, `versions.md`, `impl-plan.md` (v0.1.0),
  `impl-plan-v0.2.0.md`, `offene-fragen-v0.2.0.md`.

## Contributing

Suggestions and pull requests are welcome. Before opening a PR, please:

1. Make sure `./gradlew :composeApp:allTests` is green locally.
2. Write commit messages in
   [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
   style — **English**, max. 72 characters in the subject, imperative mood.
3. For platform-specific changes use the existing `expect`/`actual` bridges
   under `commonMain/platform/` + `<target>Main/platform/`.

> Note: only commit messages and this README are English. The user-facing UI
> remains German (DACH-only).

## Upcoming (v0.3.0)

These features are planned but **not yet scheduled or decided**. No ETA.

- **English UI (i18n):** Add English as a second language alongside the current
  German-only UI, using Compose Multiplatform resource strings. The UX for
  language selection is an open question (automatic system locale, or an in-app
  toggle — to be decided).
- **Signed Android release APK:** Set up Android keystore signing in CI so that
  the GitHub release includes a signed `.apk` alongside the Linux and Windows
  packages.

## License

[GPL-3.0-or-later](LICENSE) — Fabian Dettmer, 2026.
