package jp.co.soramitsu.oauth.core.engines.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface SoraCardClientProvider {

    fun provide(logging: Boolean, timeout: Long, json: Json): HttpClient
}

class SoraCardClientProviderImpl : SoraCardClientProvider {

    override fun provide(logging: Boolean, timeout: Long, json: Json): HttpClient {
        return HttpClient(OkHttp) {
            if (logging) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.SIMPLE
                }
            }
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    json,
                    contentType = ContentType.Any
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = timeout
                connectTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
        }
    }
}
