package jp.co.soramitsu.oauth.network

import kotlinx.serialization.DeserializationStrategy
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

interface SoraCardNetworkClient {

    @Singleton
    class Adapter @Inject constructor(
        val soraCardNetworkClient: SoraCardNetworkClient
    ) {

        suspend inline fun <reified T: Any> post(
            header: String?,
            bearerToken: String?,
            url: String,
            body: Any,
            deserializer: DeserializationStrategy<T>
        ) = soraCardNetworkClient.post(
            header = header,
            bearerToken = bearerToken,
            url = url,
            body = body,
            deserializer = deserializer,
            deserializationClazz = T::class
        )

        suspend inline fun <reified T: Any> get(
            header: String?,
            bearerToken: String?,
            url: String,
            deserializer: DeserializationStrategy<T>,
        ) = soraCardNetworkClient.get(
            header = header,
            bearerToken = bearerToken,
            url = url,
            deserializer = deserializer,
            deserializationClazz = T::class
        )

    }

    suspend fun <T: Any> post(
        header: String?,
        bearerToken: String?,
        url: String,
        body: Any,
        deserializer: DeserializationStrategy<T>,
        deserializationClazz: KClass<T>
    ): SoraCardNetworkResponse<T>

    suspend fun <T: Any> get(
        header: String?,
        bearerToken: String?,
        url: String,
        deserializer: DeserializationStrategy<T>,
        deserializationClazz: KClass<T>
    ): SoraCardNetworkResponse<T>
}
