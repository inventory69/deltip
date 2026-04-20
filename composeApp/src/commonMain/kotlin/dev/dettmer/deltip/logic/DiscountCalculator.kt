package dev.dettmer.deltip.logic

import dev.dettmer.deltip.model.CalculationResult
import kotlin.math.round

object DiscountCalculator {
    fun calculate(price: Double, percent: Double): CalculationResult {
        val discount = round(price * percent / 100 * 100) / 100
        val finalPrice = round((price - discount) * 100) / 100
        return CalculationResult(original = price, discount = discount, finalPrice = finalPrice)
    }
}
