package dev.dettmer.deltip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.dettmer.deltip.platform.supportsAlwaysOnTop
import dev.dettmer.deltip.platform.supportsAutostart
import dev.dettmer.deltip.state.AppViewModel

@Composable
fun SettingsBar(viewModel: AppViewModel) {
    val settings by viewModel.appSettings.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        OutlinedTextField(
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

        OutlinedTextField(
            value = settings.currencySymbol,
            onValueChange = { if (it.length <= 3) viewModel.updateCurrencySymbol(it) },
            label = { Text("Symbol") },
            singleLine = true,
            modifier = Modifier.width(72.dp),
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
