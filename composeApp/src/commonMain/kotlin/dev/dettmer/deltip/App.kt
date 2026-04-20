package dev.dettmer.deltip

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.dettmer.deltip.state.AppViewModel
import dev.dettmer.deltip.state.SettingsRepository
import dev.dettmer.deltip.ui.SettingsBar
import dev.dettmer.deltip.ui.SinglePriceScreen

@Composable
fun App(viewModel: AppViewModel = remember { AppViewModel(SettingsRepository()) }) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors) {
        Scaffold { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(12.dp)
            ) {
                SettingsBar(viewModel)
                Spacer(Modifier.height(12.dp))
                SinglePriceScreen(viewModel)
            }
        }
    }
}
