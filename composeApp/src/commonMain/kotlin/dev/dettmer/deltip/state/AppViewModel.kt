package dev.dettmer.deltip.state

import dev.dettmer.deltip.logic.DiscountCalculator
import dev.dettmer.deltip.logic.PriceFormatter
import dev.dettmer.deltip.logic.PriceParser
import dev.dettmer.deltip.model.AppSettings
import dev.dettmer.deltip.model.CalculationResult
import dev.dettmer.deltip.platform.Autostart
import dev.dettmer.deltip.platform.copyToClipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@OptIn(kotlinx.coroutines.FlowPreview::class)
class AppViewModel(
    private val settingsRepo: SettingsRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    // ----- Settings-Proxy -----
    val appSettings: StateFlow<AppSettings> = settingsRepo.state

    fun updateDiscountPercent(d: Double) =
        settingsRepo.update(appSettings.value.copy(discountPercent = d))

    fun updateCurrencySymbol(s: String) =
        settingsRepo.update(appSettings.value.copy(currencySymbol = s))

    fun updateAlwaysOnTop(b: Boolean) =
        settingsRepo.update(appSettings.value.copy(alwaysOnTop = b))

    fun updateAutostart(b: Boolean) {
        Autostart.setEnabled(b)
        settingsRepo.update(appSettings.value.copy(autostartEnabled = b))
    }

    fun updateWindowPosition(x: Float, y: Float) {
        settingsRepo.update(appSettings.value.copy(windowX = x, windowY = y))
    }

    // ----- Single-Modus (reaktiv) -----
    private val _singlePriceInput = MutableStateFlow("")
    val singlePriceInput: StateFlow<String> = _singlePriceInput.asStateFlow()

    fun updateSinglePrice(s: String) { _singlePriceInput.value = s }

    fun clearInput() { _singlePriceInput.value = "" }

    /**
     * Abgeleiteter Flow: reagiert auf Eingabe-Änderung UND auf Settings-Änderung
     * (Rabatt-% + Währungssymbol). Bei ungültiger Eingabe oder leerem String → null.
     */
    val singleResult: StateFlow<CalculationResult?> =
        combine(_singlePriceInput, appSettings) { input, settings ->
            val price = PriceParser.parse(input) ?: return@combine null
            DiscountCalculator.calculate(price, settings.discountPercent)
        }.stateIn(scope, SharingStarted.Eagerly, null)

    init {
        // ----- Auto-Copy mit Debounce -----
        // Erst 300 ms Pause nach der letzten Eingabe → kopieren.
        // Leerer Input / Parse-Fehler → kein Copy (if-Guard statt filterNotNull,
        // damit das Settings-Objekt nicht verloren geht).
        combine(singleResult, appSettings) { result, settings -> result to settings }
            .debounce(300)
            .onEach { (result, settings) ->
                if (result != null) {
                    copyToClipboard(PriceFormatter.format(result.finalPrice, settings.currencySymbol))
                }
            }
            .launchIn(scope)
    }
}
