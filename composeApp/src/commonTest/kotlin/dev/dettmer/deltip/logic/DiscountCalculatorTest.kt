package dev.dettmer.deltip.logic

import dev.dettmer.deltip.model.CalculationResult
import kotlin.test.Test
import kotlin.test.assertEquals

class DiscountCalculatorTest {
    @Test fun standardCase() =
        assertEquals(CalculationResult(100.0, 10.0, 90.0), DiscountCalculator.calculate(100.0, 10.0))

    @Test fun zeroPrice() =
        assertEquals(CalculationResult(0.0, 0.0, 0.0), DiscountCalculator.calculate(0.0, 20.0))

    @Test fun zeroPercent() =
        assertEquals(CalculationResult(50.0, 0.0, 50.0), DiscountCalculator.calculate(50.0, 0.0))

    @Test fun fullDiscount() =
        assertEquals(CalculationResult(50.0, 50.0, 0.0), DiscountCalculator.calculate(50.0, 100.0))

    @Test fun roundsToTwoDecimals() {
        val r = DiscountCalculator.calculate(33.33, 7.5)
        assertEquals(33.33, r.original)
        assertEquals(2.5, r.discount)
        assertEquals(30.83, r.finalPrice)
    }

    @Test fun twentyPercentDefault() =
        assertEquals(CalculationResult(50.0, 10.0, 40.0), DiscountCalculator.calculate(50.0, 20.0))
}
