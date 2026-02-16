package com.splitworth.app.core

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

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

        val subtotal = input.subtotal.bd()
        val taxPercent = input.taxPercent.bd()
        val tipPercent = input.tipPercent.bd()
        val hundred = BigDecimal("100")

        val taxAmount = subtotal.multiply(taxPercent).divide(hundred, 10, RoundingMode.HALF_UP)
        val tipBase = subtotal.add(taxAmount)
        val tipAmount = tipBase.multiply(tipPercent).divide(hundred, 10, RoundingMode.HALF_UP)
        val total = subtotal.add(taxAmount).add(tipAmount)

        val basePerPerson = total.divide(input.people.bd(), 10, RoundingMode.HALF_UP)
        val perPerson = if (input.roundUpPerPerson) {
            ceil(basePerPerson.toDouble() * 100.0) / 100.0
        } else {
            round2(basePerPerson.toDouble())
        }

        return Result.success(
            SplitResult(
                total = round2(total.toDouble()),
                perPerson = perPerson,
                taxAmount = round2(taxAmount.toDouble()),
                tipAmount = round2(tipAmount.toDouble())
            )
        )
    }

    fun formatCurrency(value: Double): String = "%.2f".format(round2(value))

    private fun round2(value: Double): Double = value.bd().setScale(2, RoundingMode.HALF_UP).toDouble()
    private fun Double.bd(): BigDecimal = BigDecimal.valueOf(this)
    private fun Int.bd(): BigDecimal = BigDecimal.valueOf(this.toLong())
}
