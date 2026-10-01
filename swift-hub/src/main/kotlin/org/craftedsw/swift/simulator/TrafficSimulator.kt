package org.craftedsw.swift.simulator

import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.craftedsw.contracts.TransferRequest
import org.craftedsw.swift.router.BankRegistry
import org.craftedsw.swift.router.TransferRouter
import org.slf4j.LoggerFactory

class TrafficSimulator(
    private val bankRegistry: BankRegistry,
    private val transferRouter: TransferRouter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val logger = LoggerFactory.getLogger(TrafficSimulator::class.java)
    private val isRunning = AtomicBoolean(false)
    private val intervalMillis = AtomicLong(2000)
    private var job: Job? = null

    fun start(intervalMs: Long = 2000) {
        intervalMillis.set(intervalMs.coerceAtLeast(100))
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting Traffic Simulator with interval ${intervalMillis.get()}ms...")
            job = scope.launch {
                while (isActive && isRunning.get()) {
                    simulateSingleTransfer()
                    delay(intervalMillis.get())
                }
            }
        }
    }

    fun stop() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("Stopping Traffic Simulator...")
            job?.cancel()
            job = null
        }
    }

    fun isSimulatorRunning(): Boolean = isRunning.get()

    fun getIntervalMs(): Long = intervalMillis.get()

    suspend fun triggerBurst(count: Int): Int {
        val banks = bankRegistry.getAllBanks()
        if (banks.isEmpty()) return 0

        var executed = 0
        for (i in 1..count) {
            simulateSingleTransfer()
            executed++
            delay(50) // Micro-burst spacing
        }
        return executed
    }

    suspend fun simulateSingleTransfer(): Boolean {
        val banks = bankRegistry.getAllBanks()
        if (banks.isEmpty()) return false

        val senderBank = banks.random()
        val receiverBank = banks.random()

        val senderIban = "BE68${senderBank.bic}000${(1000000..9999999).random()}"
        val receiverIban = "BE68${receiverBank.bic}000${(1000000..9999999).random()}"
        val amountCents = (1000..50000).random().toLong() // 10.00 EUR to 500.00 EUR

        val transfer = TransferRequest(
            transactionId = "sim-${System.currentTimeMillis()}-${(100..999).random()}",
            fromIban = senderIban,
            toIban = receiverIban,
            amountCents = amountCents,
            timestamp = Instant.now().toString(),
            reference = "Simulated Transfer"
        )

        transferRouter.routeTransfer(transfer)
        return true
    }
}
