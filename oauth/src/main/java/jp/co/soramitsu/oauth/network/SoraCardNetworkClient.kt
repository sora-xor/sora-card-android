package jp.co.soramitsu.oauth.network

import kotlinx.serialization.DeserializationStrategy

interface SoraCardNetworkClient {

    suspend fun <T> post(
        header: String?,
        bearerToken: String?,
        url: String,
        body: String,
        deserializer: DeserializationStrategy<T>,
    ): SoraCardNetworkResponse<T>

    suspend fun <T> get(
        header: String?,
        bearerToken: String?,
        url: String,
        deserializer: DeserializationStrategy<T>,
    ): SoraCardNetworkResponse<T>
}

/**
 * [SoraCardNetworkResponse] is a data holder class and should contain
 * either non null [value] and status code 200, or null [value] and status code
 *
 * If status code is not available due to serialization, or other kind of throwable issue,
 * the issue must be thrown without this class to be generated
 */
class SoraCardNetworkResponse<T>(
    private val value: T?,
    private val statusCode: Int,
    private val message: String? = null,
) {
    fun <S> parse(block: (value: T?, statusCode: Int, message: String?) -> S): S =
        block(value, statusCode, message)
}
