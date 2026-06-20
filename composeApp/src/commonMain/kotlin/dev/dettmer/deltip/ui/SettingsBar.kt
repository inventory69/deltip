package dev.dettmer.deltip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.OutlinedTextField
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
import dev.dettmer.deltip.state.AppViewModel
import deltip.composeapp.generated.resources.Res
import deltip.composeapp.generated.resources.label_discount_percent
import deltip.composeapp.generated.resources.label_symbol
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsBar(viewModel: AppViewModel) {
    val settings by viewModel.appSettings.collectAsState()

    val symbolState = remember { TextFieldState(initialText = settings.currencySymbol) }

    LaunchedEffect(Unit) {
        snapshotFlow { settings.currencySymbol }
            .drop(1)
            .collect { newSymbol ->
                if (symbolState.text.toString() != newSymbol) {
                    symbolState.edit { replace(0, length, newSymbol) }
                }
            }
    }

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
        // Rabatt mode: show discount % field. MwSt mode: quick 19/7 toggle lives in the screen.
        if (settings.mode == AppMode.RABATT) {
            OutlinedTextField(
                value = if (settings.discountPercent == settings.discountPercent.toLong().toDouble())
                    settings.discountPercent.toLong().toString()
                else
                    settings.discountPercent.toString(),
                onValueChange = { raw ->
                    raw.replace(",", ".").toDoubleOrNull()?.let { viewModel.updateDiscountPercent(it) }
                },
                label = { Text(stringResource(Res.string.label_discount_percent)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.width(90.dp),
            )
        }

        OutlinedTextField(
            state = symbolState,
            label = { Text(stringResource(Res.string.label_symbol)) },
            labelPosition = TextFieldLabelPosition.Attached(alwaysMinimize = true),
            lineLimits = androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine,
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            modifier = Modifier.width(80.dp),
        )
    }
}
