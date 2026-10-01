package org.craftedsw.bank.domain

import org.assertj.core.api.Assertions.assertThat
import org.craftedsw.bank.domain.Amount.Companion.amountOf
import org.junit.jupiter.api.Test

class AmountTest {

    @Test
    fun `should be equal to another instance containing same amount`() {
        val oneHundred = Amount(100)
        val anotherOneHundred = Amount(100)

        assertThat(oneHundred).isEqualTo(anotherOneHundred)
    }

    @Test
    fun `should be different from another instance containing different amount`() {
        val ten = Amount(10)
        val five = Amount(5)

        assertThat(ten).isNotEqualTo(five)
    }

    @Test
    fun `should statically initialise an amount`() {
        assertThat(Amount(10)).isEqualTo(amountOf(10))
    }

    @Test
    fun `should sum up amounts`() {
        val ten = amountOf(10)
        val five = amountOf(5)
        val fifteen = amountOf(15)

        assertThat(ten.plus(five)).isEqualTo(fifteen)
    }

    @Test
    fun `should indicate when it is greater than other amount`() {
        val ten = amountOf(10)
        val five = amountOf(5)

        assertThat(ten.isGreaterThan(five)).isTrue()
    }

    @Test
    fun `should indicate when it is not greater than other amount`() {
        val ten = amountOf(10)
        val five = amountOf(5)

        assertThat(five.isGreaterThan(ten)).isFalse()
    }

    @Test
    fun `should return the absolute value`() {
        val minusFive = amountOf(-5)

        assertThat(minusFive.absoluteValue()).isEqualTo(amountOf(5))
    }

    @Test
    fun `should return the negative value`() {
        val five = amountOf(5)

        assertThat(five.negative()).isEqualTo(amountOf(-5))
    }

    @Test
    fun `should return money representation`() {
        val oneThousand = amountOf(1000)

        assertThat(oneThousand.moneyRepresentation()).isEqualTo("1000.00")
    }
}
