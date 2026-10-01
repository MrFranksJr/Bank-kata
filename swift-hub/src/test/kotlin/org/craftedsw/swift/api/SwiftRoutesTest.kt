package org.craftedsw.swift.api

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.craftedsw.contracts.BankNodeInfo
import org.craftedsw.contracts.HealthResponse
import org.craftedsw.contracts.RegisterBankRequest
import org.craftedsw.contracts.RegisterBankResponse
import org.craftedsw.contracts.ScoreboardState
import org.craftedsw.contracts.TransferRequest
import org.craftedsw.contracts.TransferResult
import org.craftedsw.contracts.TransferStatus
import org.craftedsw.swift.router.AuditLedger
import org.craftedsw.swift.router.BankRegistry
import org.craftedsw.swift.router.TransferRouter
import org.junit.jupiter.api.Test

class SwiftRoutesTest {

    @Test
    fun `health endpoint should return UP and SWIFTHUB bic`() = testApplication {
        application {
            swiftHubModule()
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.get("/health")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        val body = response.body<HealthResponse>()
        assertThat(body.status).isEqualTo("UP")
        assertThat(body.bic).isEqualTo("SWIFTHUB")
    }

    @Test
    fun `register bank should add bank to registry and return confirmation`() = testApplication {
        val registry = BankRegistry()
        application {
            swiftHubModule(bankRegistry = registry)
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/swift/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterBankRequest(bic = "BANKAXXX", name = "Bank Alpha", webhookUrl = "https://alpha.loca.lt"))
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        val body = response.body<RegisterBankResponse>()
        assertThat(body.status).isEqualTo("REGISTERED")
        assertThat(body.bic).isEqualTo("BANKAXXX")

        val banksResponse = client.get("/swift/banks")
        val banks = banksResponse.body<List<BankNodeInfo>>()
        assertThat(banks).hasSize(1)
        assertThat(banks.first().name).isEqualTo("Bank Alpha")
    }

    @Test
    fun `transfer should reject if destination bank not registered`() = testApplication {
        val registry = BankRegistry()
        val ledger = AuditLedger()
        application {
            swiftHubModule(bankRegistry = registry, auditLedger = ledger)
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val transfer = TransferRequest(
            transactionId = "tx-123",
            fromIban = "BE68BANKA0001111111",
            toIban = "BE68UNKN0002222222",
            amountCents = 10000
        )

        val response = client.post("/swift/transfers") {
            contentType(ContentType.Application.Json)
            setBody(transfer)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        val result = response.body<TransferResult>()
        assertThat(result.status).isEqualTo(TransferStatus.REJECTED)
        assertThat(result.message).contains("not registered")
    }

    @Test
    fun `metrics endpoint should return network summary`() = testApplication {
        val registry = BankRegistry()
        registry.register(RegisterBankRequest(bic = "BANKAXXX", name = "Bank Alpha", webhookUrl = "http://localhost:8080"))
        application {
            swiftHubModule(bankRegistry = registry)
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.get("/swift/metrics")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        val scoreboard = response.body<ScoreboardState>()
        assertThat(scoreboard.banks).hasSize(1)
        assertThat(scoreboard.metrics.activeBanksCount).isEqualTo(1)
    }
}
