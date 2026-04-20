package dev.dettmer.deltip.logic

object PriceParser {
    // Akzeptiert: "12,50", "12.50", "1.234,56", "1,234.56" und einfache Integer-Werte.
    // Für Iteration 1: heuristische Normalisierung.
    fun parse(input: String): Double? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        val normalized = when {
            // Format "1.234,56" — Punkt als Tausender, Komma als Dezimal
            trimmed.contains(',') && trimmed.contains('.') && trimmed.lastIndexOf(',') > trimmed.lastIndexOf('.') -> {
                trimmed.replace(".", "").replace(',', '.')
            }
            // Format "1,234.56" — Komma als Tausender, Punkt als Dezimal
            trimmed.contains(',') && trimmed.contains('.') && trimmed.lastIndexOf('.') > trimmed.lastIndexOf(',') -> {
                trimmed.replace(",", "")
            }
            // Nur Komma — Komma als Dezimaltrenner (DE-Standard)
            trimmed.contains(',') && !trimmed.contains('.') -> {
                trimmed.replace(',', '.')
            }
            // Nur Punkt oder keine Trennzeichen — direkt parsen
            else -> trimmed
        }

        val parsed = normalized.toDoubleOrNull() ?: return null
        return if (parsed < 0.0) null else parsed
    }
}
