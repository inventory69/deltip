package dev.dettmer.deltip.platform

import dev.dettmer.deltip.model.UpdateInfo

actual val supportsAutoUpdate: Boolean = false
actual suspend fun checkForUpdate(currentVersion: String): UpdateInfo? = null
actual suspend fun installUpdate(info: UpdateInfo): Unit = Unit
