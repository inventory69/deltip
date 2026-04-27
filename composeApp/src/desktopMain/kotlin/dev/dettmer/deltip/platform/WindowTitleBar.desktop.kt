package dev.dettmer.deltip.platform

import java.awt.Frame
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout

private const val DWMWA_USE_IMMERSIVE_DARK_MODE = 20

actual fun applyDarkTitleBarIfSupported(windowHandle: Any?, dark: Boolean) {
    if (!System.getProperty("os.name").lowercase().contains("win")) return
    val frame = windowHandle as? Frame ?: return
    try {
        val hwnd = nativeHwnd(frame) ?: return
        val linker = Linker.nativeLinker()
        val lookup = SymbolLookup.libraryLookup("dwmapi", Arena.global())
        val symbol = lookup.find("DwmSetWindowAttribute").orElse(null) ?: return
        val handle = linker.downcallHandle(
            symbol,
            FunctionDescriptor.of(
                ValueLayout.JAVA_INT,   // HRESULT
                ValueLayout.JAVA_LONG,  // HWND
                ValueLayout.JAVA_INT,   // DWORD attribute
                ValueLayout.ADDRESS,    // LPCVOID pvAttribute
                ValueLayout.JAVA_INT,   // DWORD cbAttribute
            ),
        )
        Arena.ofConfined().use { arena ->
            val flag: MemorySegment = arena.allocate(ValueLayout.JAVA_INT)
            flag.set(ValueLayout.JAVA_INT, 0, if (dark) 1 else 0)
            @Suppress("UNCHECKED_CAST")
            handle.invokeExact(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, flag, 4)
        }
    } catch (e: Throwable) {
        // Fail-soft: no-op on Linux dev builds, older Windows, or if the
        // sun.awt.windows package is unavailable.
        e.printStackTrace()
    }
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
    e.printStackTrace()
    null
}
