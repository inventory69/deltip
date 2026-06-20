package dev.dettmer.deltip.state

import com.russhwolf.settings.Settings
import dev.dettmer.deltip.model.AppMode
import dev.dettmer.deltip.model.AppSettings
import dev.dettmer.deltip.model.VatDirection
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
        settings.putFloat("windowX", newSettings.windowX)
        settings.putFloat("windowY", newSettings.windowY)
        settings.putDouble("vatPercent", newSettings.vatPercent)
        settings.putString("mode", newSettings.mode.name)
        settings.putString("vatDirection", newSettings.vatDirection.name)
        _state.value = newSettings
    }

    private fun load(): AppSettings = AppSettings(
        discountPercent = settings.getDouble("discountPercent", 20.0),
        currencySymbol = settings.getString("currencySymbol", "€"),
        alwaysOnTop = settings.getBoolean("alwaysOnTop", false),
        autostartEnabled = settings.getBoolean("autostartEnabled", false),
        windowX = settings.getFloat("windowX", -1f),
        windowY = settings.getFloat("windowY", -1f),
        vatPercent = settings.getDouble("vatPercent", 19.0),
        mode = runCatching {
            AppMode.valueOf(settings.getString("mode", AppMode.RABATT.name))
        }.getOrDefault(AppMode.RABATT),
        vatDirection = runCatching {
            VatDirection.valueOf(settings.getString("vatDirection", VatDirection.GROSS_TO_NET.name))
        }.getOrDefault(VatDirection.GROSS_TO_NET),
    )
}
