package org.craftedsw.swift

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.craftedsw.swift.api.swiftHubModule
import org.craftedsw.swift.router.AuditLedger
import org.craftedsw.swift.router.BankRegistry
import org.craftedsw.swift.router.TransferRouter
import org.craftedsw.swift.simulator.TrafficSimulator
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("SwiftHubApplication")

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 9000
    logger.info("==================================================")
    logger.info("🚀 Starting SWIFT Hub & Scoreboard on port $port")
    logger.info("👉 Open Dashboard at: http://localhost:$port")
    logger.info("==================================================")

    val bankRegistry = BankRegistry()
    val auditLedger = AuditLedger()
    val transferRouter = TransferRouter(bankRegistry, auditLedger)
    val trafficSimulator = TrafficSimulator(bankRegistry, transferRouter)

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        swiftHubModule(
            bankRegistry = bankRegistry,
            auditLedger = auditLedger,
            transferRouter = transferRouter,
            trafficSimulator = trafficSimulator
        )
    }.start(wait = true)
}
