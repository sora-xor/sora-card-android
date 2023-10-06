package jp.co.soramitsu.oauth.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient
import javax.inject.Singleton

enum class NetworkRequest(val url: String) {
    GET_REFERENCE_NUMBER("get-reference-number"),
    GET_KYC_STATUS("kyc-last-status"),
    GET_KYC_FREE_ATTEMPT_INFO("kyc-attempt-count"),
    GET_CURRENT_XOR_EURO_PRICE("prices/xor_euro"),
    GET_IBAN_DESC("ibans"),
    FEES("fees")
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideSoraCardNetworkClient(
        inMemoryRepo: InMemoryRepo,
        client: SoramitsuNetworkClient,
    ): SoraCardNetworkClient =
        SoraCardNetworkClient(
            client = client,
            inMemoryRepo = inMemoryRepo,
        )
}
