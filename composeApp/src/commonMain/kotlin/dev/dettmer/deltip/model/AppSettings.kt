package dev.dettmer.deltip.model

data class AppSettings(
    val discountPercent: Double = 20.0,
    val currencySymbol: String = "€",
    val alwaysOnTop: Boolean = false,
    val autostartEnabled: Boolean = false,
)
