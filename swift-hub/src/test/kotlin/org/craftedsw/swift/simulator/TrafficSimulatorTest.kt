package org.craftedsw.swift.simulator

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.craftedsw.contracts.RegisterBankRequest
import org.craftedsw.contracts.TransferResult
import org.craftedsw.contracts.TransferStatus
import org.craftedsw.swift.router.BankRegistry
import org.craftedsw.swift.router.TransferRouter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrafficSimulatorTest {

    private lateinit var bankRegistry: BankRegistry
    private lateinit var transferRouter: TransferRouter
    private lateinit var simulator: TrafficSimulator

    @BeforeEach
    fun setUp() {
        bankRegistry = BankRegistry()
        transferRouter = mockk(relaxed = true)
        simulator = TrafficSimulator(bankRegistry, transferRouter)
    }

    @Test
    fun `should not simulate transfer if no banks registered`() = runTest {
        val result = simulator.simulateSingleTransfer()
        assertThat(result).isFalse()
        coVerify(exactly = 0) { transferRouter.routeTransfer(any()) }
    }

    @Test
    fun `should simulate transfer between registered banks`() = runTest {
        bankRegistry.register(RegisterBankRequest(bic = "BANKAXXX", name = "Bank A", webhookUrl = "http://a:8080"))
        bankRegistry.register(RegisterBankRequest(bic = "BANKBXXX", name = "Bank B", webhookUrl = "http://b:8080"))

        coEvery { transferRouter.routeTransfer(any()) } returns TransferResult("tx-1", TransferStatus.ACCEPTED, "OK")

        val result = simulator.simulateSingleTransfer()
        assertThat(result).isTrue()
        coVerify(atLeast = 1) { transferRouter.routeTransfer(any()) }
    }

    @Test
    fun `should start and stop simulator`() {
        simulator.start(100)
        assertThat(simulator.isSimulatorRunning()).isTrue()

        simulator.stop()
        assertThat(simulator.isSimulatorRunning()).isFalse()
    }
}
