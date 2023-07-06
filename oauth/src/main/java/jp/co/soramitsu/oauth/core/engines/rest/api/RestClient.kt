package jp.co.soramitsu.oauth.core.engines.rest.api

import io.ktor.client.statement.HttpResponse

interface RestClient {

    @Throws(RestException::class)
    suspend fun post(header: String, bearerToken: String?, url: String, body: Any): HttpResponse

    @Throws(RestException::class)
    suspend fun get(header: String, bearerToken: String?, url: String): HttpResponse
}