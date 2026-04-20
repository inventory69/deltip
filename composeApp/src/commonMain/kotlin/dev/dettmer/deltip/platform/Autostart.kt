package dev.dettmer.deltip.platform

expect object Autostart {
    fun isEnabled(): Boolean
    fun setEnabled(enabled: Boolean)
}
