package jp.co.soramitsu.sora.communitytesting

import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.oauth.network.SoraCardNetworkResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonGetRequestNonReified
import jp.co.soramitsu.xnetworking.lib.engines.utils.JsonPostRequestNonReified
import kotlinx.serialization.DeserializationStrategy
import kotlin.reflect.KClass

class SoraCardNetworkClientImpl(
    private val restClient: RestClient
) : SoraCardNetworkClient {

    private companion object {
        const val SUCCESS_STATUS_CODE = 200
    }

    override suspend fun <T: Any> get(
        header: String?,
        bearerToken: String?,
        url: String,
        deserializer: DeserializationStrategy<T>,
        deserializationClazz: KClass<T>
    ): SoraCardNetworkResponse<T> {
        return try {
            val result = restClient.get(
                JsonGetRequestNonReified(
                    userAgent = header,
                    bearerToken = bearerToken,
                    url = url,
                    responseDeserializer = deserializer,
                    responseClazz = deserializationClazz
                )
            )

            SoraCardNetworkResponse(
                value = result,
                statusCode = SUCCESS_STATUS_CODE
            )
        } catch (exception: RestClientException) {
            if (exception !is RestClientException.WithCode)
                throw exception

            SoraCardNetworkResponse(
                value = null,
                statusCode = exception.code
            )
        }
    }

    override suspend fun <T: Any> post(
        header: String?,
        bearerToken: String?,
        url: String,
        body: Any,
        deserializer: DeserializationStrategy<T>,
        deserializationClazz: KClass<T>
    ): SoraCardNetworkResponse<T> {
        return try {
            val result = restClient.post(
                JsonPostRequestNonReified(
                    userAgent = header,
                    bearerToken = bearerToken,
                    url = url,
                    responseDeserializer = deserializer,
                    body = body,
                    responseClazz = deserializationClazz
                )
            )

            SoraCardNetworkResponse(
                value = result,
                statusCode = SUCCESS_STATUS_CODE
            )
        } catch (exception: RestClientException) {
            if (exception !is RestClientException.WithCode)
                throw exception

            SoraCardNetworkResponse(
                value = null,
                statusCode = exception.code
            )
        }
    }
}