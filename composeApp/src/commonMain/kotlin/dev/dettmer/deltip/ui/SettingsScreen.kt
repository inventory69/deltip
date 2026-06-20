package dev.dettmer.deltip.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import dev.dettmer.deltip.AppInfo
import dev.dettmer.deltip.model.UpdateState
import dev.dettmer.deltip.platform.supportsAlwaysOnTop
import dev.dettmer.deltip.platform.supportsAutoUpdate
import dev.dettmer.deltip.platform.supportsAutostart
import dev.dettmer.deltip.state.AppViewModel
import deltip.composeapp.generated.resources.Res
import deltip.composeapp.generated.resources.button_back
import deltip.composeapp.generated.resources.button_check_updates
import deltip.composeapp.generated.resources.button_install_update
import deltip.composeapp.generated.resources.label_always_on_top
import deltip.composeapp.generated.resources.label_autostart
import deltip.composeapp.generated.resources.label_vat_percent
import deltip.composeapp.generated.resources.label_version
import deltip.composeapp.generated.resources.title_settings
import deltip.composeapp.generated.resources.update_available
import deltip.composeapp.generated.resources.update_checking
import deltip.composeapp.generated.resources.update_downloading
import deltip.composeapp.generated.resources.update_error
import deltip.composeapp.generated.resources.update_section_title
import deltip.composeapp.generated.resources.update_up_to_date
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val settings by viewModel.appSettings.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.button_back))
            }
            Spacer(Modifier.width(4.dp))
            Text(stringResource(Res.string.title_settings), style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(12.dp))

        if (supportsAlwaysOnTop) {
            SettingRow(
                label = stringResource(Res.string.label_always_on_top),
                checked = settings.alwaysOnTop,
                onCheckedChange = viewModel::updateAlwaysOnTop,
            )
        }

        if (supportsAutostart) {
            SettingRow(
                label = stringResource(Res.string.label_autostart),
                checked = settings.autostartEnabled,
                onCheckedChange = viewModel::updateAutostart,
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = if (settings.vatPercent == settings.vatPercent.toLong().toDouble())
                settings.vatPercent.toLong().toString()
            else
                settings.vatPercent.toString(),
            onValueChange = { raw ->
                raw.replace(",", ".").toDoubleOrNull()?.let { viewModel.updateVatPercent(it) }
            },
            label = { Text(stringResource(Res.string.label_vat_percent)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.width(120.dp),
        )

        if (supportsAutoUpdate) {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Text(stringResource(Res.string.update_section_title), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(Res.string.label_version, AppInfo.VERSION), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))

            val statusText = when (val s = updateState) {
                is UpdateState.Idle -> null
                is UpdateState.Checking -> stringResource(Res.string.update_checking)
                is UpdateState.Available -> stringResource(Res.string.update_available, s.info.version)
                is UpdateState.Downloading -> stringResource(Res.string.update_downloading)
                is UpdateState.UpToDate -> stringResource(Res.string.update_up_to_date)
                is UpdateState.Error -> stringResource(Res.string.update_error)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = viewModel::checkForUpdates,
                    enabled = updateState !is UpdateState.Checking &&
                        updateState !is UpdateState.Downloading,
                ) {
                    Text(stringResource(Res.string.button_check_updates))
                }

                if (updateState is UpdateState.Available) {
                    Button(onClick = { viewModel.installUpdate((updateState as UpdateState.Available).info) }) {
                        Text(stringResource(Res.string.button_install_update))
                    }
                }
            }

            if (statusText != null) {
                Spacer(Modifier.height(4.dp))
                Text(statusText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
