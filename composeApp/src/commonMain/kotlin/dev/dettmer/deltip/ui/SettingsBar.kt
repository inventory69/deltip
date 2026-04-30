package dev.dettmer.deltip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop
import dev.dettmer.deltip.model.AppMode
import dev.dettmer.deltip.platform.supportsAlwaysOnTop
import dev.dettmer.deltip.platform.supportsAutostart
import dev.dettmer.deltip.state.AppViewModel

@Composable
fun SettingsBar(viewModel: AppViewModel) {
    val settings by viewModel.appSettings.collectAsState()

    // TextFieldState for the Symbol field — allows TextFieldLabelPosition.Attached(alwaysMinimize=true)
    // so the label always sits in the outline cutout at the top, even when the field is empty.
    val symbolState = remember { TextFieldState(initialText = settings.currencySymbol) }

    // Seed symbolState when the settings flow delivers a new value from outside
    // (e.g. initial load). Skip the first emission to avoid fighting the user's own input.
    LaunchedEffect(Unit) {
        snapshotFlow { settings.currencySymbol }
            .drop(1)
            .collect { newSymbol ->
                if (symbolState.text.toString() != newSymbol) {
                    symbolState.edit { replace(0, length, newSymbol) }
                }
            }
    }

    // Push symbol changes back to the ViewModel.
    LaunchedEffect(symbolState) {
        snapshotFlow { symbolState.text.toString() }
            .drop(1)
            .collect { raw ->
                if (raw.length <= 3) viewModel.updateCurrencySymbol(raw)
            }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        // Show only the percent field for the active mode.
        when (settings.mode) {
            AppMode.RABATT -> OutlinedTextField(
                value = if (settings.discountPercent == settings.discountPercent.toLong().toDouble())
                    settings.discountPercent.toLong().toString()
                else
                    settings.discountPercent.toString(),
                onValueChange = { raw ->
                    raw.replace(",", ".").toDoubleOrNull()?.let { viewModel.updateDiscountPercent(it) }
                },
                label = { Text("Rabatt %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.width(90.dp),
            )
            AppMode.MWST -> OutlinedTextField(
                value = if (settings.vatPercent == settings.vatPercent.toLong().toDouble())
                    settings.vatPercent.toLong().toString()
                else
                    settings.vatPercent.toString(),
                onValueChange = { raw ->
                    raw.replace(",", ".").toDoubleOrNull()?.let { viewModel.updateVatPercent(it) }
                },
                label = { Text("MwSt %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.width(90.dp),
            )
        }

        OutlinedTextField(
            state = symbolState,
            label = { Text("Symbol") },
            labelPosition = TextFieldLabelPosition.Attached(alwaysMinimize = true),
            lineLimits = androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine,
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            modifier = Modifier.width(80.dp),
        )

        if (supportsAlwaysOnTop) {
            ToggleColumn(
                label = "Vorne",
                checked = settings.alwaysOnTop,
                onCheckedChange = viewModel::updateAlwaysOnTop,
            )
        }

        if (supportsAutostart) {
            ToggleColumn(
                label = "Autostart",
                checked = settings.autostartEnabled,
                onCheckedChange = viewModel::updateAutostart,
            )
        }
    }
}

@Composable
private fun ToggleColumn(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.width(70.dp),               // guarantees room for "Autostart"
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,                                // guard against line-wrap
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
