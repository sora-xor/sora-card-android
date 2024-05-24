package jp.co.soramitsu.oauth.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.xnetworking.basic.networkclient.SoramitsuNetworkClient

enum class NetworkRequest(val url: String) {
    CARD_STATUS("card-status"),
    COUNTRY_CODES("country-codes"),
    GATEWAY_GET_IFRAME("cryptogateway/get-user-iframe"),
    GATEWAY_ONBOARD("cryptogateway/onboard-user"),
    GATEWAY_ONBOARDED("cryptogateway/onboarded"),
    FEES("fees"),
    GET_REFERENCE_NUMBER("get-reference-number"),
    HEALTH("health"),
    GET_IBAN_DESC("ibans"),
    GET_KYC_FREE_ATTEMPT_INFO("kyc-attempt-count"),
    GET_KYC_LAST_STATUS("kyc-last-status"),
    GET_KYC_STATUS_N("kyc-status"),

    /**
     * please check availability, might be removed from backend
     */
    GET_CURRENT_XOR_EURO_PRICE("prices/xor_euro"),
    VERSION("version"),
}

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideSoraCardNetworkClient(
        inMemoryRepo: InMemoryRepo,
        client: SoramitsuNetworkClient,
    ): SoraCardNetworkClient = SoraCardNetworkClient(
        client = client,
        inMemoryRepo = inMemoryRepo,
    )
}
