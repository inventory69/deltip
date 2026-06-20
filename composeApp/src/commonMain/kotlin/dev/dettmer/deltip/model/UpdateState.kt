package dev.dettmer.deltip.model

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class Available(val info: UpdateInfo) : UpdateState()
    object Downloading : UpdateState()
    object UpToDate : UpdateState()
    object Error : UpdateState()
}
