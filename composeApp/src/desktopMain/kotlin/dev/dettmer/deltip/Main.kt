package dev.dettmer.deltip

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.dettmer.deltip.state.AppViewModel
import dev.dettmer.deltip.state.SettingsRepository

fun main() = application {
    val repo = remember { SettingsRepository() }
    val vm = remember { AppViewModel(repo) }
    val settings by vm.appSettings.collectAsState()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Deltip",
        state = rememberWindowState(width = 360.dp, height = 420.dp),
        alwaysOnTop = settings.alwaysOnTop,
    ) {
        App(vm)
    }
}
