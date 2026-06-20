package dev.dettmer.deltip.logic

object VersionComparator {
    /**
     * Vergleicht zwei Versionsstrings semver-artig (numerischer Vergleich der
     * `.`-Segmente). Gibt true zurück, wenn [latest] echt neuer ist als [current].
     *
     * Beispiele: isNewer("0.3.0", "0.2.9") == true; isNewer("0.3.0", "0.3.0") == false;
     * isNewer("0.2.9", "0.3.0") == false. Führende `v` werden ignoriert,
     * unterschiedlich lange Versionen werden mit 0 aufgefüllt ("1.2" == "1.2.0").
     */
    fun isNewer(latest: String, current: String): Boolean {
        val l = segments(latest)
        val c = segments(current)
        val max = maxOf(l.size, c.size)
        for (i in 0 until max) {
            val lv = l.getOrElse(i) { 0 }
            val cv = c.getOrElse(i) { 0 }
            if (lv != cv) return lv > cv
        }
        return false
    }

    private fun segments(version: String): List<Int> =
        version.trim().trimStart('v', 'V')
            .split('.')
            .map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }
}
