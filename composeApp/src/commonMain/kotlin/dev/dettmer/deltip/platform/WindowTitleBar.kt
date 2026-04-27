package dev.dettmer.deltip.platform

/** Platform bridge: applies dark/light title-bar chrome to match the app theme. */
expect fun applyDarkTitleBarIfSupported(windowHandle: Any?, dark: Boolean)
