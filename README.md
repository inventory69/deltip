# deltip

> Discount calculator. VAT splitter. Clipboard-first. Personal tool - built for one person's workflow.

![Version](https://img.shields.io/badge/version-0.4.1-blue?style=flat-square)
![License](https://img.shields.io/badge/license-GPL--3.0--or--later-green?style=flat-square)
![Platform](https://img.shields.io/badge/platform-Linux%20%7C%20Windows%20%7C%20Android-lightgrey?style=flat-square)

> [!WARNING]
> **Beta.** The current version is tracked in `gradle.properties` (`app.version`). Public APIs and the settings schema may still change between releases.

> [!NOTE]
> **The UI ships German and English strings** (since v0.3.0, via Compose Resources); German is the base locale. There is **no in-app language switcher** — the displayed language is whatever the system locale resolves to.

---

## What it does

Two modes, one field, zero friction.

| Mode | Input | Output | Auto-copied |
|---|---|---|---|
| **Rabatt** (default) | gross price | discounted final price | final price |
| **MwSt** | gross amount | net + VAT share | VAT amount |

Both modes accept `12,50` and `12.50` interchangeably. Auto-copy fires 300 ms after the last keystroke - no button needed.

---

## Usage

1. App opens - input field is already focused.
2. Switch mode with the **Rabatt / MwSt** toggle if needed.
3. Type a value, e.g. `49,99`.
4. Read the result. Shortly after, it lands in your clipboard.
5. Click the field to clear it and start over.

Settings (discount %, VAT %, currency symbol, Vorne, Autostart) persist across restarts.  
Since v0.2.0: the desktop window remembers its last position.

---

## Build

**Requires:** JDK 21 (Eclipse Temurin or compatible), Git.

```bash
git clone https://github.com/inventory69/deltip.git
cd deltip

./gradlew :composeApp:run                              # desktop
./gradlew :composeApp:assembleDebug                    # Android APK
./gradlew :composeApp:packageDistributionForCurrentOS  # native packages
./gradlew :composeApp:allTests                         # tests
```

Native packages land in `composeApp/build/compose/binaries/main/`.

<details>
<summary>Optional dependencies for native packages</summary>

- Linux (`.deb` / `.rpm`): `fakeroot`, `rpm`
- Windows (`.exe`): WiX Toolset 3.x

</details>

---

## Platform notes

| | Linux | Windows | Android |
|---|---|---|---|
| Autostart | `~/.config/autostart/deltip.desktop` | `HKCU\...\CurrentVersion\Run` | - |
| Always on top | ✓ | ✓ | - |
| Dark mode title bar | - | ✓ (v0.2.0+) | follows system |

---

## New in v0.4.0

- **Bidirectional VAT** - split gross→net or gross up net→gross, with a quick 19/7 % selector and a swap button.
- **Dedicated settings screen** - always-on-top, autostart, VAT %, version and update controls moved off the main bar.
- **Windows auto-update** - the desktop app polls a GitHub Pages `latest.json` manifest (with a GitHub Releases API fallback) and installs new versions in place.

## Shipped in v0.3.0

- **i18n** - English as a second language alongside German, via Compose Multiplatform resource strings, following the system locale.
- **Signed Android APK** - keystore signing in CI so GitHub releases ship a ready-to-install `.apk`.

---

## Contributing

> [!IMPORTANT]
> The UI is bilingual (German base + English). Commit messages and this README are English; code comments are German. When adding user-facing text, add the key to **both** `values/strings.xml` and `values-en/strings.xml`.

Before opening a PR:

- `./gradlew :composeApp:allTests` must be green.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) - imperative, English, ≤ 72 chars.
- Platform-specific changes belong in the `expect`/`actual` bridges under `commonMain/platform/` + `<target>Main/platform/`.

---

## Docs

Architecture, version matrix, constraints and implementation plans live in the private `project-docs/deltip/` repo (`developer_constraints.md`, `architektur.md`, `versions.md`, `impl-plan*.md`).

---

*GPL-3.0-or-later - Fabian Dettmer, 2026.*
