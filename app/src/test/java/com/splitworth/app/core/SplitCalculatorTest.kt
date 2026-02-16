package com.splitworth.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitCalculatorTest {

    @Test
    fun calculate_returnsExpectedValues_withoutRoundUp() {
        val input = SplitInput(
            subtotal = 100.0,
            taxPercent = 8.5,
            tipPercent = 15.0,
            people = 3,
            roundUpPerPerson = false
        )

        val result = SplitCalculator.calculate(input).getOrThrow()

        assertEquals(108.50, result.total - result.tipAmount, 0.001)
        assertEquals(16.28, result.tipAmount, 0.001)
        assertEquals(124.78, result.total, 0.001)
        assertEquals(41.59, result.perPerson, 0.001)
    }

    @Test
    fun calculate_roundsUpPerPerson_whenEnabled() {
        val input = SplitInput(
            subtotal = 25.0,
            taxPercent = 0.0,
            tipPercent = 0.0,
            people = 3,
            roundUpPerPerson = true
        )

        val result = SplitCalculator.calculate(input).getOrThrow()

        assertEquals(25.0, result.total, 0.001)
        assertEquals(8.34, result.perPerson, 0.001)
    }

    @Test
    fun calculate_rejectsInvalidPeopleCount() {
        val input = SplitInput(
            subtotal = 10.0,
            taxPercent = 5.0,
            tipPercent = 10.0,
            people = 0,
            roundUpPerPerson = false
        )

        val result = SplitCalculator.calculate(input)

        assertTrue(result.isFailure)
        assertEquals("People must be at least 1.", result.exceptionOrNull()?.message)
    }
}
