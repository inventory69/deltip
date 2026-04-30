package dev.dettmer.deltip.platform

import java.awt.EventQueue
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout

// Windows 10 build >= 18985 / Windows 11
private const val DWMWA_USE_IMMERSIVE_DARK_MODE = 20

// Windows 10 builds 17763 – 18985 (legacy)
private const val DWMWA_USE_IMMERSIVE_DARK_MODE_LEGACY = 19

actual fun applyDarkTitleBarIfSupported(windowHandle: Any?, dark: Boolean) {
    if (!System.getProperty("os.name").lowercase().contains("win")) return
    val frame = windowHandle as? Frame ?: return

    fun apply() {
        val hwnd = nativeHwnd(frame)
        if (hwnd == null || hwnd == 0L) {
            // Peer not realized yet — re-apply after the window opens.
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
    val hwnd = nativeHwnd(frame) ?: return
    if (hwnd == 0L) return
    try {
        val linker = Linker.nativeLinker()
        val lookup = SymbolLookup.libraryLookup("dwmapi", Arena.global())
        val symbol = lookup.find("DwmSetWindowAttribute").orElse(null) ?: return
        val handle = linker.downcallHandle(
            symbol,
            FunctionDescriptor.of(
                ValueLayout.JAVA_INT,   // HRESULT
                ValueLayout.ADDRESS,    // HWND
                ValueLayout.JAVA_INT,   // DWORD attribute
                ValueLayout.ADDRESS,    // LPCVOID pvAttribute
                ValueLayout.JAVA_INT,   // DWORD cbAttribute
            ),
        )
        Arena.ofConfined().use { arena ->
            val flag = arena.allocate(ValueLayout.JAVA_INT)
            flag.set(ValueLayout.JAVA_INT, 0, if (dark) 1 else 0)
            val hwndSeg = MemorySegment.ofAddress(hwnd)
            // Try modern attribute first (Win10 >= 18985 / Win11).
            var hr = handle.invokeExact(hwndSeg, DWMWA_USE_IMMERSIVE_DARK_MODE, flag, 4) as Int
            if (hr != 0) {
                // Fallback for legacy Win10 builds 17763–18985.
                hr = handle.invokeExact(hwndSeg, DWMWA_USE_IMMERSIVE_DARK_MODE_LEGACY, flag, 4) as Int
            }
            if (hr == 0) {
                forceTitleBarRepaint(frame)
            }
            // On failure hr is a non-zero HRESULT; app continues with default title bar color.
        }
    } catch (e: Throwable) {
        // TODO: replace with central Logger once introduced (developer_constraints §8.4)
        e.printStackTrace()
    }
}

private fun forceTitleBarRepaint(frame: Frame) {
    // Toggling resizable forces DWM to repaint the non-client area immediately.
    val resizable = frame.isResizable
    frame.isResizable = !resizable
    frame.isResizable = resizable
}

private fun nativeHwnd(frame: Frame): Long? = try {
    val accessorClass = Class.forName("sun.awt.AWTAccessor")
    val accessor = accessorClass.getMethod("getComponentAccessor").invoke(null)
    val peer = accessor.javaClass
        .getMethod("getPeer", java.awt.Component::class.java)
        .invoke(accessor, frame)
        ?: return null
    val getHwnd = Class.forName("sun.awt.windows.WComponentPeer")
        .getDeclaredMethod("getHWnd")
    (getHwnd.invoke(peer) as Long).takeIf { it != 0L }
} catch (e: Throwable) {
    // TODO: replace with central Logger once introduced (developer_constraints §8.4)
    e.printStackTrace()
    null
}
