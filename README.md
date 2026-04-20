# Deltip

Ein kleines Werkzeug, das Rabatte ausrechnet — ohne Klickorgie, ohne Werbung,
ohne Internet. Preis eingeben, fertig. Der Endpreis steht sofort da und liegt
nach kurzem Innehalten in der Zwischenablage.

Gebaut für den Schreibtisch (Linux, Windows) und für unterwegs (Android), mit
denselben Eingabe- und Anzeigeregeln auf allen Geräten.

---

## Was es kann

- **Sofortige Berechnung.** Sobald eine Zahl im Feld steht, erscheint der
  Endpreis. Es gibt keinen „Berechnen"-Knopf.
- **Automatisches Kopieren.** Kurz nach der letzten Tastenanschlag-Pause liegt
  der Endpreis als Text in der Zwischenablage und kann in der Kasse, im
  Browser oder in einer E-Mail eingefügt werden.
- **Frei einstellbarer Rabatt.** Standard sind 20 %. Der Wert lässt sich
  jederzeit ändern und bleibt nach dem nächsten Start erhalten.
- **Komma oder Punkt.** `12,50` und `12.50` werden gleich verstanden.
- **Eigenes Währungszeichen.** Standard ist `€`. Wer mag, schreibt `$`, `CHF`
  oder lässt das Feld leer.
- **Hell und dunkel.** Folgt dem Systemthema des Betriebssystems.
- **Optional immer im Vordergrund** (nur Desktop) und **Autostart** mit dem
  Rechner (Linux per `.desktop`-Datei, Windows per Registry).

## Was es nicht macht

- Keine Berechnungen mit mehreren Positionen oder Summen — bewusst ein
  Werkzeug für einen Preis nach dem anderen.
- Keine Steuern, keine Währungsumrechnung, keine Verläufe.
- Keine Datenübertragung. Die App läuft vollständig offline.

---

## Installation

### Linux (Debian, Ubuntu, Mint)

```bash
sudo apt install ./deltip_1.0.0-1_amd64.deb
```

### Linux (Fedora, openSUSE, RHEL)

```bash
sudo rpm -i deltip-1.0.0-1.x86_64.rpm
```

### Linux (Arch, EndeavourOS, Manjaro)

Die `.rpm`-Datei lässt sich nicht direkt nutzen. Stattdessen die `.deb`
mit `dpkg-deb` entpacken oder direkt aus den Quellen bauen
(siehe Abschnitt „Selbst bauen").

### Windows

Die `Deltip-1.0.0.exe` aus dem Release-Bereich herunterladen und
ausführen. Beim ersten Start zeigt Windows eine SmartScreen-Warnung; über
„Weitere Informationen" → „Trotzdem ausführen" lässt sich die App starten.

### Android

Die `composeApp-debug.apk` aus dem Release-Bereich herunterladen. Vor der
Installation muss in den Android-Einstellungen die Installation aus
unbekannten Quellen für den verwendeten Browser oder Dateimanager freigegeben
werden.

---

## Bedienung

1. App öffnen — der Eingabe-Cursor steht bereits im Preisfeld.
2. Preis tippen, zum Beispiel `49,99`.
3. Endpreis ablesen. Nach einer kurzen Pause liegt er in der Zwischenablage.
4. Mit `Strg + V` (Windows/Linux) oder langem Druck und „Einfügen" (Android)
   in das Zielfeld einfügen.
5. Mit dem Knopf „Löschen" das Feld leeren — der Cursor springt automatisch
   zurück.

Standard-Rabatt und Währungszeichen lassen sich oben in der Einstellungs-Leiste
ändern. Die Werte werden automatisch gespeichert.

---

## Selbst bauen

Voraussetzung ist ein installiertes **JDK 21** (Eclipse Temurin oder
vergleichbar).

```bash
git clone https://github.com/inventory69/deltip.git
cd deltip

./gradlew :composeApp:run                              # Desktop direkt starten
./gradlew :composeApp:assembleDebug                    # Android-APK bauen
./gradlew :composeApp:packageDistributionForCurrentOS  # Native Pakete bauen
```

Die nativen Pakete entstehen unter
`composeApp/build/compose/binaries/main/`.

Auf Linux werden zusätzlich `fakeroot` und `rpm` benötigt:

```bash
# Debian/Ubuntu
sudo apt install fakeroot rpm

# Arch/EndeavourOS
sudo pacman -S fakeroot rpm-tools
```

Auf Windows wird das **WiX Toolset 3.x** benötigt, damit jpackage eine
`.exe` erzeugen kann.

---

## Tests

```bash
./gradlew :composeApp:allTests
```

Der Bericht liegt anschließend unter
`composeApp/build/reports/tests/`.

---

## Mitwirken

Vorschläge und Pull Requests sind willkommen. Vor einem PR bitte:

1. `./gradlew :composeApp:allTests` lokal grün bekommen
2. Commit-Nachrichten im
   [Conventional-Commits](https://www.conventionalcommits.org/de/v1.0.0/)-Stil
   verfassen

---

## Lizenz

[GPL-3.0-or-later](LICENSE) — Fabian Dettmer, 2026.
