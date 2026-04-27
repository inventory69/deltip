package dev.dettmer.deltip.logic

import kotlin.test.Test
import kotlin.test.assertEquals

class VatCalculatorTest {
    @Test fun standardCase19Percent() {
        val r = VatCalculator.calculate(119.0, 19.0)
        assertEquals(100.0, r.net)
        assertEquals(19.0, r.vatAmount)
    }

    @Test fun reducedRate7Percent() {
        val r = VatCalculator.calculate(107.0, 7.0)
        assertEquals(100.0, r.net)
        assertEquals(7.0, r.vatAmount)
    }

    @Test fun zeroGross() {
        val r = VatCalculator.calculate(0.0, 19.0)
        assertEquals(0.0, r.net)
        assertEquals(0.0, r.vatAmount)
    }

    @Test fun zeroPercent() {
        val r = VatCalculator.calculate(50.0, 0.0)
        assertEquals(50.0, r.net)
        assertEquals(0.0, r.vatAmount)
    }

    @Test fun roundingToTwoDecimals() {
        // 33.33 / 1.19 = 28.008... → rounded 28.01; vat = 5.32
        val r = VatCalculator.calculate(33.33, 19.0)
        assertEquals(28.01, r.net)
        assertEquals(5.32, r.vatAmount)
    }
}
