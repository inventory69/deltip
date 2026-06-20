package dev.dettmer.deltip

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.dettmer.deltip.model.UpdateState
import dev.dettmer.deltip.state.AppViewModel
import dev.dettmer.deltip.state.SettingsRepository
import dev.dettmer.deltip.ui.ModeToggle
import dev.dettmer.deltip.ui.SettingsBar
import dev.dettmer.deltip.ui.SettingsScreen
import dev.dettmer.deltip.ui.SinglePriceScreen
import deltip.composeapp.generated.resources.Res
import deltip.composeapp.generated.resources.button_settings
import deltip.composeapp.generated.resources.update_available
import org.jetbrains.compose.resources.stringResource

private enum class Screen { MAIN, SETTINGS }

@Composable
fun App(viewModel: AppViewModel = remember { AppViewModel(SettingsRepository()) }) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    var screen by remember { mutableStateOf(Screen.MAIN) }
    val updateState by viewModel.updateState.collectAsState()

    MaterialTheme(colorScheme = colors) {
        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                when (screen) {
                    Screen.MAIN -> {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(12.dp),
                        ) {
                            SettingsBar(viewModel)
                            Spacer(Modifier.height(8.dp))
                            ModeToggle(viewModel)
                            Spacer(Modifier.height(12.dp))
                            SinglePriceScreen(viewModel)

                            if (updateState is UpdateState.Available) {
                                Spacer(Modifier.height(8.dp))
                                val info = (updateState as UpdateState.Available).info
                                androidx.compose.material3.Text(
                                    stringResource(Res.string.update_available, info.version),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        IconButton(
                            onClick = { screen = Screen.SETTINGS },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp),
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(Res.string.button_settings))
                        }
                    }
                    Screen.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onBack = { screen = Screen.MAIN },
                    )
                }
            }
        }
    }
}
