package dev.dettmer.deltip.platform

import dev.dettmer.deltip.model.UpdateInfo

expect val supportsAutoUpdate: Boolean
expect suspend fun checkForUpdate(currentVersion: String): UpdateInfo?
expect suspend fun installUpdate(info: UpdateInfo)
