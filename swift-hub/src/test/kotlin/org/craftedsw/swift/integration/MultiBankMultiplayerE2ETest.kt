package org.craftedsw.swift.integration

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.craftedsw.bank.service.BankService
import org.craftedsw.contracts.Bic
import org.craftedsw.contracts.DepositRequest
import org.craftedsw.contracts.RegisterBankRequest
import org.craftedsw.contracts.TransferRequest
import org.craftedsw.contracts.TransferResult
import org.craftedsw.contracts.TransferStatus
import org.craftedsw.contracts.WithdrawRequest
import org.craftedsw.swift.router.AuditLedger
import org.craftedsw.swift.router.BankRegistry
import org.craftedsw.swift.router.TransferRouter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MultiBankMultiplayerE2ETest {

    private lateinit var bankRegistry: BankRegistry
    private lateinit var auditLedger: AuditLedger
    private lateinit var bankServiceAlpha: BankService
    private lateinit var bankServiceBeta: BankService

    @BeforeEach
    fun setUp() {
        bankRegistry = BankRegistry()
        auditLedger = AuditLedger()
        bankServiceAlpha = BankService()
        bankServiceBeta = BankService()
    }

    @Test
    fun `should execute end-to-end multi-bank transfer and maintain conservation of money in SWIFT ledger`() = runTest {
        val alphaIban = "BE68BANKA0001111111"
        val betaIban = "BE68BANKB0002222222"

        // 1. Initial Deposit at Bank Alpha
        bankServiceAlpha.deposit(DepositRequest(iban = alphaIban, amountCents = 100000)) // 1000.00 EUR
        assertThat(bankServiceAlpha.getBalanceCents(alphaIban)).isEqualTo(100000)
        assertThat(bankServiceBeta.getBalanceCents(betaIban)).isEqualTo(0)

        // 2. Register both banks on SWIFT Hub
        bankRegistry.register(
            RegisterBankRequest(bic = "BANKAXXX", name = "Bank Alpha", webhookUrl = "http://alpha-bank:8080")
        )
        bankRegistry.register(
            RegisterBankRequest(bic = "BANKBXXX", name = "Bank Beta", webhookUrl = "http://beta-bank:8080")
        )

        // 3. Mock the HTTP dispatch from TransferRouter to forward directly to Bank Beta's service
        val transferRouter = mockk<TransferRouter>()
        val transfer = TransferRequest(
            transactionId = "tx-multiplayer-001",
            fromIban = alphaIban,
            toIban = betaIban,
            amountCents = 40000, // 400.00 EUR
            reference = "Inter-bank settle"
        )

        coEvery { transferRouter.routeTransfer(transfer) } answers {
            // Debit sender
            val withdrawResult = bankServiceAlpha.withdraw(
                WithdrawRequest(iban = transfer.fromIban, amountCents = transfer.amountCents)
            )
            if (withdrawResult.isSuccess) {
                val creditResult = bankServiceBeta.processIncomingTransfer(transfer)
                bankRegistry.recordTransaction(Bic("BANKBXXX"), true, 15)
                auditLedger.record(transfer, creditResult)
                creditResult
            } else {
                val fail = TransferResult(transfer.transactionId, TransferStatus.REJECTED, "Insufficient funds")
                auditLedger.record(transfer, fail)
                fail
            }
        }

        // 4. Execute the transfer through the SWIFT Network
        val result = transferRouter.routeTransfer(transfer)

        // 5. Verify balances at both nodes
        assertThat(result.status).isEqualTo(TransferStatus.ACCEPTED)
        assertThat(bankServiceAlpha.getBalanceCents(alphaIban)).isEqualTo(60000) // 600.00 EUR remaining
        assertThat(bankServiceBeta.getBalanceCents(betaIban)).isEqualTo(40000)  // 400.00 EUR received

        // 6. Verify SWIFT Hub Audit Ledger
        assertThat(auditLedger.getTotalTransactionsCount()).isEqualTo(1)
        assertThat(auditLedger.getTotalSuccessfulCount()).isEqualTo(1)
        assertThat(auditLedger.getTotalSettledVolumeCents()).isEqualTo(40000)
        assertThat(auditLedger.verifyConservationOfMoney()).isTrue()

        // 7. Verify statement on Bank Beta
        val betaStatement = bankServiceBeta.getStatement(betaIban)
        assertThat(betaStatement.currentBalanceCents).isEqualTo(40000)
        assertThat(betaStatement.lines).hasSize(1)
        assertThat(betaStatement.lines[0].credit).isEqualTo("400.00")
    }
}
