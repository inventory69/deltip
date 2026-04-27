package dev.dettmer.deltip.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.dettmer.deltip.model.AppMode
import dev.dettmer.deltip.state.AppViewModel

@Composable
fun ModeToggle(viewModel: AppViewModel) {
    val settings by viewModel.appSettings.collectAsState()
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = settings.mode == AppMode.RABATT,
            onClick = { viewModel.updateMode(AppMode.RABATT) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) { Text("Rabatt") }
        SegmentedButton(
            selected = settings.mode == AppMode.MWST,
            onClick = { viewModel.updateMode(AppMode.MWST) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) { Text("MwSt") }
    }
}
