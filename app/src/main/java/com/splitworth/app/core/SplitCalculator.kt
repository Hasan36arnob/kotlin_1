package com.splitworth.app.core

import kotlin.math.ceil
import kotlin.math.round

data class SplitInput(
    val subtotal: Double,
    val taxPercent: Double,
    val tipPercent: Double,
    val people: Int,
    val roundUpPerPerson: Boolean
)

data class SplitResult(
    val total: Double,
    val perPerson: Double,
    val taxAmount: Double,
    val tipAmount: Double
)

object SplitCalculator {
    fun calculate(input: SplitInput): Result<SplitResult> {
        if (input.subtotal < 0) return Result.failure(IllegalArgumentException("Subtotal must be 0 or higher."))
        if (input.taxPercent < 0) return Result.failure(IllegalArgumentException("Tax must be 0 or higher."))
        if (input.tipPercent < 0) return Result.failure(IllegalArgumentException("Tip must be 0 or higher."))
        if (input.people <= 0) return Result.failure(IllegalArgumentException("People must be at least 1."))

        val taxAmount = input.subtotal * (input.taxPercent / 100.0)
        val tipBase = input.subtotal + taxAmount
        val tipAmount = tipBase * (input.tipPercent / 100.0)
        val total = input.subtotal + taxAmount + tipAmount

        val basePerPerson = total / input.people
        val perPerson = if (input.roundUpPerPerson) {
            ceil(basePerPerson * 100.0) / 100.0
        } else {
            round2(basePerPerson)
        }

        return Result.success(
            SplitResult(
                total = round2(total),
                perPerson = perPerson,
                taxAmount = round2(taxAmount),
                tipAmount = round2(tipAmount)
            )
        )
    }

    fun formatCurrency(value: Double): String = "%.2f".format(round2(value))

    private fun round2(value: Double): Double = round(value * 100.0) / 100.0
}
