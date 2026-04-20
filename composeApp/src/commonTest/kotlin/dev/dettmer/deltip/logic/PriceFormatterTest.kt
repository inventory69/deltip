package dev.dettmer.deltip.logic

import kotlin.test.Test
import kotlin.test.assertEquals

class PriceFormatterTest {
    @Test fun formatsWithCurrency() = assertEquals("11,25 €", PriceFormatter.format(11.25, "€"))
    @Test fun padsSingleDecimal() = assertEquals("0,50 €", PriceFormatter.format(0.5, "€"))
    @Test fun padsZeroDecimals() = assertEquals("12,00 €", PriceFormatter.format(12.0, "€"))
    @Test fun noThousandsSeparator() = assertEquals("1234,50 €", PriceFormatter.format(1234.5, "€"))
    @Test fun noTrailingSpaceWhenSymbolEmpty() = assertEquals("11,25", PriceFormatter.format(11.25, ""))
    @Test fun handlesNegative() = assertEquals("-5,00 €", PriceFormatter.format(-5.0, "€"))
    @Test fun roundsCorrectly() = assertEquals("10,57 €", PriceFormatter.format(10.5678, "€"))
}
