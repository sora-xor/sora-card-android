package jp.co.soramitsu.oauth.core.engines.rest.impl

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import jp.co.soramitsu.oauth.BuildConfig
import jp.co.soramitsu.oauth.core.engines.rest.api.RestClient
import jp.co.soramitsu.oauth.core.engines.rest.api.RestException
import kotlinx.serialization.SerializationException
import javax.inject.Inject

class RestClientImpl @Inject constructor(
    private val httpClient: HttpClient
): RestClient {

    override suspend fun post(header: String, bearerToken: String?, url: String, body: Any): HttpResponse =
        wrapInExceptionHandler {
            httpClient.post(BuildConfig.API_BASE_URL + url) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerToken?.let { bearerAuth(it) }
                userAgent(header)
                setBody(body)
            }.body()
        }

    override suspend fun get(header: String, bearerToken: String?, url: String): HttpResponse =
        wrapInExceptionHandler {
            return httpClient.get(BuildConfig.API_BASE_URL + url) {
                bearerToken?.let { bearerAuth(it) }
                userAgent(header)
                accept(ContentType.Application.Json)
            }.body()
        }

    private inline fun <reified Type : Any> wrapInExceptionHandler(block: () -> Type): Type =
        try {
            block.invoke()
        } catch (e: ResponseException) {
            val code: Int = when (e) {
                is RedirectResponseException -> 3
                is ClientRequestException -> 4
                is ServerResponseException -> 5
                else -> 0
            }
            throw RestException.WithCode(code, e.message.orEmpty(), e.cause)
        } catch (e: SerializationException) {
            throw RestException.WhileSerialization(e.message.orEmpty(), e.cause)
        } catch (e: Throwable) {
            throw RestException.SimpleException(e.message.orEmpty(), e.cause)
        }

}