package dev.dettmer.deltip.logic

import kotlin.math.abs
import kotlin.math.roundToLong

object PriceFormatter {
    /**
     * Formatiert einen Double-Wert als deutschen Preis mit Komma als Dezimaltrenner.
     * Beispiel: format(11.25, "€") → "11,25 €"
     *
     * Nutzt keine Locale-abhängige String.format, um KMP-Kompatibilität zu gewährleisten.
     */
    fun format(value: Double, currencySymbol: String): String {
        val rounded = (value * 100).roundToLong()
        val intPart = abs(rounded / 100)
        val fracPart = abs(rounded % 100)
        val sign = if (value < 0) "-" else ""
        val number = "${sign}${intPart},${fracPart.toString().padStart(2, '0')}"
        return if (currencySymbol.isEmpty()) number else "$number $currencySymbol"
    }
}
