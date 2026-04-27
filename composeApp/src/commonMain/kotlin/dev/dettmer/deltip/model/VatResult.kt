package dev.dettmer.deltip.model

/**
 * Breakdown of a gross amount into net and contained VAT share.
 *
 * gross = net + vatAmount
 */
data class VatResult(
    val gross: Double,
    val net: Double,
    val vatAmount: Double,
    val vatPercent: Double,
)
