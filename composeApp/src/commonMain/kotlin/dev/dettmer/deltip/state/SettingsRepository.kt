package dev.dettmer.deltip.state

import com.russhwolf.settings.Settings
import dev.dettmer.deltip.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(private val settings: Settings = Settings()) {
    private val _state = MutableStateFlow(load())
    val state: StateFlow<AppSettings> = _state.asStateFlow()

    fun update(newSettings: AppSettings) {
        settings.putDouble("discountPercent", newSettings.discountPercent)
        settings.putString("currencySymbol", newSettings.currencySymbol)
        settings.putBoolean("alwaysOnTop", newSettings.alwaysOnTop)
        settings.putBoolean("autostartEnabled", newSettings.autostartEnabled)
        _state.value = newSettings
    }

    private fun load(): AppSettings = AppSettings(
        discountPercent = settings.getDouble("discountPercent", 20.0),
        currencySymbol = settings.getString("currencySymbol", "€"),
        alwaysOnTop = settings.getBoolean("alwaysOnTop", false),
        autostartEnabled = settings.getBoolean("autostartEnabled", false),
    )
}
