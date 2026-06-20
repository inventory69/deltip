package dev.dettmer.deltip.platform

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import java.awt.EventQueue
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

private const val DWMWA_USE_IMMERSIVE_DARK_MODE = 20
private const val DWMWA_USE_IMMERSIVE_DARK_MODE_LEGACY = 19

private interface IDwmApi : Library {
    fun DwmSetWindowAttribute(hwnd: Long, dwAttribute: Int, pvAttribute: Pointer, cbAttribute: Int): Int
}

actual fun applyDarkTitleBarIfSupported(windowHandle: Any?, dark: Boolean) {
    if (!System.getProperty("os.name").lowercase().contains("win")) return
    val frame = windowHandle as? Frame ?: return

    fun apply() {
        val hwnd = tryGetHwnd(frame)
        if (hwnd == 0L) {
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowOpened(e: WindowEvent) {
                    frame.removeWindowListener(this)
                    EventQueue.invokeLater { applyOnce(frame, dark) }
                }
            })
            return
        }
        applyOnce(frame, dark)
    }

    if (EventQueue.isDispatchThread()) apply() else EventQueue.invokeLater { apply() }
}

private fun applyOnce(frame: Frame, dark: Boolean) {
    val hwnd = tryGetHwnd(frame).takeIf { it != 0L } ?: return
    try {
        val dwmApi: IDwmApi = Native.load("dwmapi", IDwmApi::class.java)
        val value = Memory(4)
        value.setInt(0, if (dark) 1 else 0)
        var hr = dwmApi.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, value, 4)
        if (hr != 0) {
            hr = dwmApi.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE_LEGACY, value, 4)
        }
        if (hr == 0) forceTitleBarRepaint(frame)
    } catch (e: Throwable) {
        // TODO: replace with central Logger once introduced (developer_constraints §8.4)
        e.printStackTrace()
    }
}

private fun tryGetHwnd(frame: Frame): Long = try {
    Native.getWindowID(frame)
} catch (e: Throwable) {
    e.printStackTrace()
    0L
}

private fun forceTitleBarRepaint(frame: Frame) {
    val resizable = frame.isResizable
    frame.isResizable = !resizable
    frame.isResizable = resizable
}
