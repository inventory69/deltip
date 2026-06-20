package dev.dettmer.deltip.logic

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionComparatorTest {
    @Test fun patchHigherIsNewer() {
        assertTrue(VersionComparator.isNewer("0.3.0", "0.2.9"))
    }

    @Test fun sameVersionIsNotNewer() {
        assertFalse(VersionComparator.isNewer("0.3.0", "0.3.0"))
    }

    @Test fun lowerVersionIsNotNewer() {
        assertFalse(VersionComparator.isNewer("0.2.9", "0.3.0"))
    }

    @Test fun multiDigitSegmentsCompareNumerically() {
        // 0.10.0 > 0.9.0 (numerisch, nicht lexikografisch)
        assertTrue(VersionComparator.isNewer("0.10.0", "0.9.0"))
        assertFalse(VersionComparator.isNewer("0.9.0", "0.10.0"))
    }

    @Test fun majorBeatsMinorAndPatch() {
        assertTrue(VersionComparator.isNewer("1.0.0", "0.99.99"))
    }

    @Test fun leadingVIsIgnored() {
        assertTrue(VersionComparator.isNewer("v0.3.0", "0.2.9"))
        assertFalse(VersionComparator.isNewer("v0.3.0", "v0.3.0"))
    }

    @Test fun differentLengthsArePaddedWithZero() {
        assertFalse(VersionComparator.isNewer("1.2", "1.2.0"))
        assertTrue(VersionComparator.isNewer("1.2.1", "1.2"))
    }
}
