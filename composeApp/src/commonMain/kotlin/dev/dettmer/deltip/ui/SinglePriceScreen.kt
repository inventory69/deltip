package dev.dettmer.deltip.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.dettmer.deltip.logic.PriceFormatter
import dev.dettmer.deltip.state.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun SinglePriceScreen(viewModel: AppViewModel) {
    val input by viewModel.singlePriceInput.collectAsState()
    val result by viewModel.singleResult.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // ----- Clear-on-Click when field is already focused -----
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }
    var wasFocusedBeforePress by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    // Snapshot focus status BEFORE the click potentially changes it.
                    wasFocusedBeforePress = isFocused
                }
                is PressInteraction.Release -> {
                    if (wasFocusedBeforePress && input.isNotEmpty()) {
                        viewModel.clearInput()
                    }
                }
                else -> Unit
            }
        }
    }

    var showCopiedHint by remember { mutableStateOf(false) }
    LaunchedEffect(result) {
        if (result != null) {
            delay(350)
            showCopiedHint = true
            delay(1500)
            showCopiedHint = false
        } else {
            showCopiedHint = false
        }
    }

    Column {
        OutlinedTextField(
            value = input,
            onValueChange = viewModel::updateSinglePrice,
            label = { Text("Preis") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
        )

        Spacer(Modifier.height(16.dp))

        if (result != null) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Originalpreis: ${PriceFormatter.format(result!!.original, settings.currencySymbol)}")
                    Text(
                        "Rabatt (${
                            if (settings.discountPercent == settings.discountPercent.toLong().toDouble())
                                settings.discountPercent.toLong().toString()
                            else
                                settings.discountPercent.toString()
                        }%): ${PriceFormatter.format(result!!.discount, settings.currencySymbol)}"
                    )
                    Text(
                        "Endpreis: ${PriceFormatter.format(result!!.finalPrice, settings.currencySymbol)}",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (showCopiedHint) {
                Spacer(Modifier.height(4.dp))
                Text("Automatisch kopiert ✓", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
