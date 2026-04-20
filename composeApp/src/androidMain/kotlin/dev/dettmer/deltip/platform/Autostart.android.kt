package dev.dettmer.deltip.platform

actual object Autostart {
    actual fun isEnabled(): Boolean = false
    actual fun setEnabled(enabled: Boolean) { /* no-op on Android */ }
}
