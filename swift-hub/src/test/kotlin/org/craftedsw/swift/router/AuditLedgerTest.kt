package org.craftedsw.swift.router

import org.assertj.core.api.Assertions.assertThat
import org.craftedsw.contracts.TransferRequest
import org.craftedsw.contracts.TransferResult
import org.craftedsw.contracts.TransferStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditLedgerTest {

    private lateinit var auditLedger: AuditLedger

    @BeforeEach
    fun setUp() {
        auditLedger = AuditLedger()
    }

    @Test
    fun `should record transactions and update volume and count`() {
        val transfer1 = TransferRequest(
            transactionId = "tx-1",
            fromIban = "BE68BANKA0001234567",
            toIban = "BE68BANKB0007654321",
            amountCents = 50000
        )
        val result1 = TransferResult("tx-1", TransferStatus.ACCEPTED, "OK")

        val transfer2 = TransferRequest(
            transactionId = "tx-2",
            fromIban = "BE68BANKB0007654321",
            toIban = "BE68BANKA0001234567",
            amountCents = 20000
        )
        val result2 = TransferResult("tx-2", TransferStatus.FAILED, "Timeout")

        auditLedger.record(transfer1, result1)
        auditLedger.record(transfer2, result2)

        assertThat(auditLedger.getTotalTransactionsCount()).isEqualTo(2)
        assertThat(auditLedger.getTotalSuccessfulCount()).isEqualTo(1)
        assertThat(auditLedger.getTotalFailedCount()).isEqualTo(1)
        assertThat(auditLedger.getTotalSettledVolumeCents()).isEqualTo(50000)
        assertThat(auditLedger.verifyConservationOfMoney()).isTrue()
    }
}
