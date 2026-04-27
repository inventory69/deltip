package dev.dettmer.deltip.model

data class AppSettings(
    val discountPercent: Double = 20.0,
    val currencySymbol: String = "€",
    val alwaysOnTop: Boolean = false,
    val autostartEnabled: Boolean = false,
    // Window position (desktop only). -1f = not set → PlatformDefault.
    val windowX: Float = -1f,
    val windowY: Float = -1f,
    val vatPercent: Double = 19.0,
    val mode: AppMode = AppMode.RABATT,
)
