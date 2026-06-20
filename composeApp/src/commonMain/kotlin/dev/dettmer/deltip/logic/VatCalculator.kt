package dev.dettmer.deltip.logic

import dev.dettmer.deltip.model.VatResult
import kotlin.math.round

object VatCalculator {
    /**
     * Splits a gross amount into net and contained VAT share.
     *
     * Example: gross=119, percent=19 → net=100.00; vatAmount=19.00
     */
    fun calculate(gross: Double, percent: Double): VatResult {
        val factor = 1.0 + percent / 100.0
        val net = round(gross / factor * 100) / 100
        val vatAmount = round((gross - net) * 100) / 100
        return VatResult(
            gross = gross,
            net = net,
            vatAmount = vatAmount,
            vatPercent = percent,
        )
    }

    /**
     * Grosses up a net amount by the given VAT percent.
     *
     * Example: net=100, percent=19 → gross=119.00; vatAmount=19.00
     */
    fun fromNet(net: Double, percent: Double): VatResult {
        val gross = round(net * (1.0 + percent / 100.0) * 100) / 100
        val vatAmount = round((gross - net) * 100) / 100
        return VatResult(
            gross = gross,
            net = net,
            vatAmount = vatAmount,
            vatPercent = percent,
        )
    }
}
