package org.craftedsw.contracts

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ModelsTest {

    private val json = Json { prettyPrint = true }

    @Test
    fun `should extract BIC from standard workshop IBAN`() {
        val iban = Iban("BE68BANKA0001234567")
        val bic = iban.extractBic()

        assertThat(bic).isNotNull
        assertThat(bic?.value).isEqualTo("BANKA000")
    }

    @Test
    fun `should serialize and deserialize TransferRequest correctly`() {
        val request = TransferRequest(
            transactionId = "tx-12345",
            fromIban = "BE68BANKA0001234567",
            toIban = "BE68BANKB0007654321",
            amountCents = 150000,
            timestamp = "2026-10-01T14:30:00Z",
            reference = "Invoice 42"
        )

        val jsonString = json.encodeToString(request)
        val deserialized = json.decodeFromString<TransferRequest>(jsonString)

        assertThat(deserialized).isEqualTo(request)
    }

    @Test
    fun `should serialize and deserialize ScoreboardState correctly`() {
        val state = ScoreboardState(
            banks = listOf(
                BankNodeInfo(
                    bic = "BANKAXXX",
                    name = "Bank Alpha",
                    webhookUrl = "https://bank-alpha.loca.lt",
                    status = "HEALTHY",
                    totalTransactions = 10,
                    successfulTransactions = 9,
                    failedTransactions = 1,
                    score = 90
                )
            ),
            metrics = NetworkMetrics(
                activeBanksCount = 1,
                totalVolumeCents = 500000,
                totalTransactions = 10,
                throughputTps = 2.5,
                errorRatePercentage = 10.0
            )
        )

        val jsonString = json.encodeToString(state)
        val deserialized = json.decodeFromString<ScoreboardState>(jsonString)

        assertThat(deserialized).isEqualTo(state)
    }
}
