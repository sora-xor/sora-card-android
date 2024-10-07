package jp.co.soramitsu.sora.communitytesting

import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.oauth.network.SoraCardNetworkResponse
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.RestClientException
import kotlinx.serialization.DeserializationStrategy

class SoraCardNetworkClientImpl(
    private val restClient: RestClient,
) : SoraCardNetworkClient {

    private companion object {
        const val SUCCESS_STATUS_CODE = 200
    }

    private class RestfulGetRequest<Deserializer>(
        override val userAgent: String?,
        override val bearerToken: String?,
        override val url: String,
        override val responseDeserializer: DeserializationStrategy<Deserializer>,
    ) : AbstractRestServerRequest<Deserializer>()

    override suspend fun <T> get(
        header: String?,
        bearerToken: String?,
        url: String,
        deserializer: DeserializationStrategy<T>,
    ): SoraCardNetworkResponse<T> {
        return try {
            val result = restClient.get(
                RestfulGetRequest(
                    userAgent = header,
                    bearerToken = bearerToken,
                    url = url,
                    responseDeserializer = deserializer,
                ),
            )

            SoraCardNetworkResponse(
                value = result,
                statusCode = SUCCESS_STATUS_CODE,
            )
        } catch (exception: RestClientException) {
            if (exception !is RestClientException.WithCode) {
                throw exception
            }

            SoraCardNetworkResponse(
                value = null,
                statusCode = exception.code,
                message = exception.message,
            )
        }
    }

    private class RestfulPostRequest<Deserializer>(
        override val userAgent: String?,
        override val bearerToken: String?,
        override val url: String,
        override val responseDeserializer: DeserializationStrategy<Deserializer>,
        override val body: Any,
        override val requestContentType: RestClient.ContentType = RestClient.ContentType.JSON,
    ) : AbstractRestServerRequest.WithBody<Deserializer>()

    override suspend fun <T> post(
        header: String?,
        bearerToken: String?,
        url: String,
        body: Any,
        deserializer: DeserializationStrategy<T>,
    ): SoraCardNetworkResponse<T> {
        return try {
            val result = restClient.post(
                RestfulPostRequest(
                    userAgent = header,
                    bearerToken = bearerToken,
                    url = url,
                    responseDeserializer = deserializer,
                    body = body,
                ),
            )

            SoraCardNetworkResponse(
                value = result,
                statusCode = SUCCESS_STATUS_CODE,
            )
        } catch (exception: RestClientException) {
            if (exception !is RestClientException.WithCode) {
                throw exception
            }

            SoraCardNetworkResponse(
                value = null,
                statusCode = exception.code,
                message = exception.message,
            )
        }
    }
}
