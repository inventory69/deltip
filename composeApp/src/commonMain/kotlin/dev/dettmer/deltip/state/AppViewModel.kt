package dev.dettmer.deltip.state

import dev.dettmer.deltip.AppInfo
import dev.dettmer.deltip.logic.DiscountCalculator
import dev.dettmer.deltip.logic.PriceFormatter
import dev.dettmer.deltip.logic.PriceParser
import dev.dettmer.deltip.logic.VatCalculator
import dev.dettmer.deltip.model.AppMode
import dev.dettmer.deltip.model.AppSettings
import dev.dettmer.deltip.model.CalculationResult
import dev.dettmer.deltip.model.UpdateInfo
import dev.dettmer.deltip.model.UpdateState
import dev.dettmer.deltip.model.VatDirection
import dev.dettmer.deltip.model.VatResult
import dev.dettmer.deltip.platform.Autostart
import dev.dettmer.deltip.platform.checkForUpdate
import dev.dettmer.deltip.platform.copyToClipboard
import dev.dettmer.deltip.platform.installUpdate as platformInstallUpdate
import dev.dettmer.deltip.platform.supportsAutoUpdate
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
import kotlinx.coroutines.launch

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

    fun updateVatPercent(p: Double) =
        settingsRepo.update(appSettings.value.copy(vatPercent = p))

    fun updateMode(m: AppMode) =
        settingsRepo.update(appSettings.value.copy(mode = m))

    fun toggleVatDirection() {
        val next = if (appSettings.value.vatDirection == VatDirection.GROSS_TO_NET)
            VatDirection.NET_TO_GROSS else VatDirection.GROSS_TO_NET
        settingsRepo.update(appSettings.value.copy(vatDirection = next))
    }

    // ----- Input (shared across modes) -----
    private val _singlePriceInput = MutableStateFlow("")
    val singlePriceInput: StateFlow<String> = _singlePriceInput.asStateFlow()

    fun updateSinglePrice(s: String) { _singlePriceInput.value = s }

    fun clearInput() { _singlePriceInput.value = "" }

    // ----- Discount result -----
    val singleResult: StateFlow<CalculationResult?> =
        combine(_singlePriceInput, appSettings) { input, settings ->
            if (settings.mode != AppMode.RABATT) return@combine null
            val price = PriceParser.parse(input) ?: return@combine null
            DiscountCalculator.calculate(price, settings.discountPercent)
        }.stateIn(scope, SharingStarted.Eagerly, null)

    // ----- VAT result (direction-aware) -----
    val vatResult: StateFlow<VatResult?> =
        combine(_singlePriceInput, appSettings) { input, settings ->
            if (settings.mode != AppMode.MWST) return@combine null
            val amount = PriceParser.parse(input) ?: return@combine null
            when (settings.vatDirection) {
                VatDirection.GROSS_TO_NET -> VatCalculator.calculate(amount, settings.vatPercent)
                VatDirection.NET_TO_GROSS -> VatCalculator.fromNet(amount, settings.vatPercent)
            }
        }.stateIn(scope, SharingStarted.Eagerly, null)

    // ----- Update state -----
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun checkForUpdates() {
        if (!supportsAutoUpdate) return
        _updateState.value = UpdateState.Checking
        scope.launch {
            try {
                val info = checkForUpdate(AppInfo.VERSION)
                _updateState.value = if (info != null) UpdateState.Available(info) else UpdateState.UpToDate
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error
            }
        }
    }

    fun installUpdate(info: UpdateInfo) {
        _updateState.value = UpdateState.Downloading
        scope.launch {
            try {
                platformInstallUpdate(info)
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error
            }
        }
    }

    init {
        // Auto-copy: discount mode → final price
        combine(singleResult, appSettings) { result, settings -> result to settings }
            .debounce(300)
            .onEach { (result, settings) ->
                if (settings.mode == AppMode.RABATT && result != null) {
                    copyToClipboard(PriceFormatter.format(result.finalPrice, settings.currencySymbol))
                }
            }
            .launchIn(scope)

        // Auto-copy: VAT mode → copy the calculated target (net or gross depending on direction)
        combine(vatResult, appSettings) { result, settings -> result to settings }
            .debounce(300)
            .onEach { (result, settings) ->
                if (settings.mode == AppMode.MWST && result != null) {
                    val value = when (settings.vatDirection) {
                        VatDirection.GROSS_TO_NET -> result.net
                        VatDirection.NET_TO_GROSS -> result.gross
                    }
                    copyToClipboard(PriceFormatter.format(value, settings.currencySymbol))
                }
            }
            .launchIn(scope)
    }
}
