package dev.dettmer.deltip.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import dev.dettmer.deltip.appContext

actual fun copyToClipboard(text: String) {
    val cm = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("Deltip", text))
}
