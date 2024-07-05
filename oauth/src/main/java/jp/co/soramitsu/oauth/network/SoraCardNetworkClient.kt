package jp.co.soramitsu.oauth.network

import kotlinx.serialization.DeserializationStrategy

interface SoraCardNetworkClient {

    suspend fun <T> post(
        header: String?,
        bearerToken: String?,
        url: String,
        body: Any,
        deserializer: DeserializationStrategy<T>,
    ): SoraCardNetworkResponse<T>

    suspend fun <T> get(
        header: String?,
        bearerToken: String?,
        url: String,
        deserializer: DeserializationStrategy<T>,
    ): SoraCardNetworkResponse<T>
}
