package org.craftedsw.bank.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.craftedsw.contracts.RegisterBankRequest
import org.craftedsw.contracts.RegisterBankResponse
import org.craftedsw.contracts.TransferRequest
import org.craftedsw.contracts.TransferResult
import org.craftedsw.contracts.TransferStatus

class SwiftClient(
    private val httpClient: HttpClient = defaultHttpClient()
) {

    suspend fun registerBank(swiftHubUrl: String, request: RegisterBankRequest): RegisterBankResponse {
        val cleanUrl = swiftHubUrl.trimEnd('/')
        return httpClient.post("$cleanUrl/swift/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun sendTransfer(swiftHubUrl: String, request: TransferRequest): TransferResult {
        val cleanUrl = swiftHubUrl.trimEnd('/')
        return try {
            httpClient.post("$cleanUrl/swift/transfers") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            TransferResult(
                transactionId = request.transactionId,
                status = TransferStatus.FAILED,
                message = "Failed to communicate with SWIFT Hub: ${e.message}"
            )
        }
    }

    companion object {
        fun defaultHttpClient(): HttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }
}
