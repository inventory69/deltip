package dev.dettmer.deltip.platform

import dev.dettmer.deltip.logic.VersionComparator
import dev.dettmer.deltip.model.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

actual val supportsAutoUpdate: Boolean =
    System.getProperty("os.name").lowercase().contains("win")

private const val MANIFEST_URL = "https://inventory69.github.io/deltip/latest.json"
private const val RELEASES_API = "https://api.github.com/repos/inventory69/deltip/releases/latest"

actual suspend fun checkForUpdate(currentVersion: String): UpdateInfo? {
    if (!supportsAutoUpdate) return null
    return withContext(Dispatchers.IO) {
        // Primärquelle: Pages-Manifest. Fallback: GitHub-Releases-API,
        // damit der Client auch vor dem ersten Pages-Deploy funktioniert.
        fromManifest(currentVersion) ?: fromReleasesApi(currentVersion)
    }
}

private fun fromManifest(currentVersion: String): UpdateInfo? {
    val body = httpGet(MANIFEST_URL) ?: return null
    val version = Regex(""""version"\s*:\s*"([^"]+)"""")
        .find(body)?.groupValues?.get(1)?.trimStart('v') ?: return null
    val exeUrl = Regex(""""exeUrl"\s*:\s*"([^"]+)"""")
        .find(body)?.groupValues?.get(1) ?: return null
    if (!VersionComparator.isNewer(version, currentVersion)) return null
    return UpdateInfo(version = version, downloadUrl = exeUrl)
}

private fun fromReleasesApi(currentVersion: String): UpdateInfo? {
    val body = httpGet(RELEASES_API, accept = "application/vnd.github+json") ?: return null
    val tagName = Regex(""""tag_name"\s*:\s*"([^"]+)"""")
        .find(body)?.groupValues?.get(1) ?: return null
    val latestVersion = tagName.trimStart('v')
    if (!VersionComparator.isNewer(latestVersion, currentVersion)) return null
    val downloadUrl = Regex(""""browser_download_url"\s*:\s*"([^"]+\.exe)"""")
        .find(body)?.groupValues?.get(1) ?: return null
    return UpdateInfo(version = latestVersion, downloadUrl = downloadUrl)
}

private fun httpGet(url: String, accept: String? = null): String? = try {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.setRequestProperty("User-Agent", "deltip-updater")
    accept?.let { conn.setRequestProperty("Accept", it) }
    conn.instanceFollowRedirects = true
    conn.connectTimeout = 5_000
    conn.readTimeout = 5_000
    if (conn.responseCode in 200..299) {
        conn.inputStream.bufferedReader().use { it.readText() }
    } else {
        null
    }.also { conn.disconnect() }
} catch (e: Exception) {
    e.printStackTrace()
    null
}

actual suspend fun installUpdate(info: UpdateInfo): Unit = withContext(Dispatchers.IO) {
    val tmpFile = Files.createTempFile("deltip-update-", ".exe")
    val conn = URL(info.downloadUrl).openConnection() as HttpURLConnection
    conn.setRequestProperty("User-Agent", "deltip-updater")
    conn.instanceFollowRedirects = true
    conn.inputStream.use { input ->
        tmpFile.toFile().outputStream().use { output -> input.copyTo(output) }
    }
    conn.disconnect()

    val installer = tmpFile.toAbsolutePath().toString()
    // Pfad der laufenden App (jpackage-Launcher), um sie nach dem Update neu zu
    // starten. Bleibt nach dem Major-Upgrade gleich (selbes Install-Verzeichnis).
    val appExe = ProcessHandle.current().info().command().orElse(null)

    // Eigenständiger Helfer-Batch, der die App überlebt (Windows killt
    // ProcessBuilder-Kinder beim JVM-Exit nicht):
    //  1. kurz warten, bis sich die App beendet hat (sonst sind Programmdateien
    //     gesperrt) — `ping` statt `timeout`, da letzteres bei umgeleitetem
    //     stdin abbricht.
    //  2. /passive-Installer synchron durchlaufen lassen (cmd blockiert, bis er
    //     fertig ist; /norestart unterdrückt nur einen System-Neustart).
    //  3. die frisch installierte App wieder starten.
    val batch = Files.createTempFile("deltip-update-", ".bat")
    batch.toFile().writeText(
        buildString {
            appendLine("@echo off")
            appendLine("ping -n 3 127.0.0.1 >nul")
            appendLine("\"$installer\" /passive /norestart")
            if (appExe != null) {
                appendLine("ping -n 2 127.0.0.1 >nul")
                appendLine("start \"\" \"$appExe\"")
            }
        },
    )
    ProcessBuilder("cmd", "/c", batch.toAbsolutePath().toString()).start()
    kotlin.system.exitProcess(0)
}
