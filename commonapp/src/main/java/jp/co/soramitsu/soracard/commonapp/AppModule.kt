package jp.co.soramitsu.soracard.commonapp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.lib.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.lib.engines.rest.impl.RestClientImpl
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideRestClient(): RestClient = RestClientImpl(
        restClientConfig = object : AbstractRestClientConfig() {
            override fun getConnectTimeoutMillis(): Long = 30_000L
            override fun getOrCreateJsonConfig(): Json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
            override fun getRequestTimeoutMillis(): Long = 30_000L
            override fun getSocketTimeoutMillis(): Long = 30_000L
            override fun isLoggingEnabled(): Boolean = true
        },
    )

    @Provides
    @Singleton
    fun provideSoraCardNetworkClient(restClient: RestClient): SoraCardNetworkClient =
        SoraCardNetworkClientImpl(restClient)
}