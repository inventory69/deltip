package dev.dettmer.deltip.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PriceParserTest {
    @Test fun parsesGermanComma() = assertEquals(12.5, PriceParser.parse("12,50"))
    @Test fun parsesEnglishDot() = assertEquals(12.5, PriceParser.parse("12.50"))
    @Test fun parsesWithSurroundingWhitespace() = assertEquals(12.5, PriceParser.parse(" 12,50 "))
    @Test fun parsesSingleDecimal() = assertEquals(12.5, PriceParser.parse("12,5"))
    @Test fun parsesZero() = assertEquals(0.0, PriceParser.parse("0"))
    @Test fun parsesIntegerOnly() = assertEquals(50.0, PriceParser.parse("50"))
    @Test fun rejectsEmptyString() = assertNull(PriceParser.parse(""))
    @Test fun rejectsNonNumeric() = assertNull(PriceParser.parse("abc"))
    @Test fun rejectsNegative() = assertNull(PriceParser.parse("-5"))
}
