package dev.dettmer.deltip.platform

actual fun applyDarkTitleBarIfSupported(windowHandle: Any?, dark: Boolean) {
    // No-op on Android: system chrome is controlled by the OS/theme.
}
