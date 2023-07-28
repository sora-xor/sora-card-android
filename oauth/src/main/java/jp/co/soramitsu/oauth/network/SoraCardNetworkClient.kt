package jp.co.soramitsu.oauth.network

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
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class SoraCardNetworkClient(
    timeout: Long = 10000,
    logging: Boolean = false,
    provider: SoraCardClientProvider,
    inMemoryRepo: InMemoryRepo,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val baseUrl = if (inMemoryRepo.environment == SoraCardEnvironmentType.PRODUCTION)
        BuildConfig.SORA_API_BASE_URL_PROD
    else
        BuildConfig.SORA_API_BASE_URL_TEST

    private val httpClient: HttpClient = provider.provide(logging, timeout, json)

    private val header: String by lazy {
        "${inMemoryRepo.client}/${Build.MANUFACTURER}/${Build.MODEL}/${Build.VERSION.SDK_INT}"
    }

    suspend fun post(bearerToken: String?, url: String, body: Any): HttpResponse =
        wrapInExceptionHandler {
            httpClient.post(baseUrl + url) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerToken?.let { bearerAuth(it) }
                userAgent(header)
                setBody(body)
            }
                .body()
        }

    suspend fun get(bearerToken: String?, url: String): HttpResponse =
        wrapInExceptionHandler {
            return httpClient.get(baseUrl + url) {
                bearerToken?.let { bearerAuth(it) }
                userAgent(header)
                accept(ContentType.Application.Json)
            }
                .body()
        }

    @Throws(SoraCardNetworkException::class)
    private inline fun <reified Type : Any> wrapInExceptionHandler(block: () -> Type): Type {
        return try {
            block.invoke()
        } catch (e: ResponseException) {
            val code: Int = when (e) {
                is RedirectResponseException -> 3
                is ClientRequestException -> 4
                is ServerResponseException -> 5
                else -> 0
            }
            throw CodeNetworkException(code, e.message.orEmpty(), e.cause)
        } catch (e: SerializationException) {
            throw SerializationNetworkException(e.message.orEmpty(), e.cause)
        } catch (e: Throwable) {
            throw GeneralNetworkException(e.message.orEmpty(), e.cause)
        }
    }
}
