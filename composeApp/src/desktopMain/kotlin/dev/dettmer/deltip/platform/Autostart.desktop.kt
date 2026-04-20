package dev.dettmer.deltip.platform

import java.io.File
import java.nio.file.Files

actual object Autostart {
    private val os = System.getProperty("os.name").lowercase()
    private val isWindows = "win" in os

    private val appPath: String
        get() = ProcessHandle.current().info().command()
            .orElse(System.getProperty("java.home") + "/bin/java")

    actual fun isEnabled(): Boolean {
        return if (isWindows) {
            isEnabledWindows()
        } else {
            isEnabledLinux()
        }
    }

    actual fun setEnabled(enabled: Boolean) {
        if (isWindows) {
            setEnabledWindows(enabled)
        } else {
            setEnabledLinux(enabled)
        }
    }

    // ── Windows ──────────────────────────────────────────────────────────────

    private fun isEnabledWindows(): Boolean {
        val proc = ProcessBuilder(
            "reg", "query",
            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
            "/v", "Deltip"
        ).redirectErrorStream(true).start()
        return proc.waitFor() == 0
    }

    private fun setEnabledWindows(enabled: Boolean) {
        if (enabled) {
            ProcessBuilder(
                "reg", "add",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
                "/v", "Deltip",
                "/t", "REG_SZ",
                "/d", "\"$appPath\"",
                "/f"
            ).inheritIO().start().waitFor()
        } else {
            ProcessBuilder(
                "reg", "delete",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
                "/v", "Deltip",
                "/f"
            ).inheritIO().start().waitFor()
        }
    }

    // ── Linux ─────────────────────────────────────────────────────────────────

    private val desktopFile: File
        get() = File(System.getProperty("user.home") + "/.config/autostart/deltip.desktop")

    private fun isEnabledLinux(): Boolean = desktopFile.exists()

    private fun setEnabledLinux(enabled: Boolean) {
        if (enabled) {
            desktopFile.parentFile?.mkdirs()
            desktopFile.writeText(
                """
                [Desktop Entry]
                Type=Application
                Name=Deltip
                Exec=$appPath
                Hidden=false
                X-GNOME-Autostart-enabled=true
                """.trimIndent()
            )
        } else {
            Files.deleteIfExists(desktopFile.toPath())
        }
    }
}
