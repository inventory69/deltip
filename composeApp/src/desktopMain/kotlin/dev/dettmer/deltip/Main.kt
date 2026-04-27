package dev.dettmer.deltip

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.dettmer.deltip.platform.applyDarkTitleBarIfSupported
import dev.dettmer.deltip.state.AppViewModel
import dev.dettmer.deltip.state.SettingsRepository

fun main() = application {
    val repo = remember { SettingsRepository() }
    val vm = remember { AppViewModel(repo) }
    val settings by vm.appSettings.collectAsState()

    // Read initial position once (non-reactive) before creating window state.
    val initial = remember { repo.state.value }
    val initialPosition = if (initial.windowX >= 0f && initial.windowY >= 0f) {
        WindowPosition.Absolute(initial.windowX.dp, initial.windowY.dp)
    } else {
        WindowPosition.PlatformDefault
    }

    val windowState = rememberWindowState(
        width = 360.dp,
        height = 420.dp,
        position = initialPosition,
    )

    Window(
        onCloseRequest = {
            val pos = windowState.position
            if (pos is WindowPosition.Absolute) {
                vm.updateWindowPosition(pos.x.value, pos.y.value)
            }
            exitApplication()
        },
        title = "Deltip",
        state = windowState,
        alwaysOnTop = settings.alwaysOnTop,
    ) {
        val dark = isSystemInDarkTheme()
        LaunchedEffect(dark) {
            applyDarkTitleBarIfSupported(window, dark)
        }
        App(vm)
    }
}
