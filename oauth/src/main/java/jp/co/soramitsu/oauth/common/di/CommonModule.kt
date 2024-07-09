package jp.co.soramitsu.oauth.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.data.CurrentActivityRetrieverImpl
import jp.co.soramitsu.oauth.common.data.KycRepositoryImpl
import jp.co.soramitsu.oauth.common.data.PriceInteractorImpl
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxyImpl
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.feature.AccessTokenValidator
import jp.co.soramitsu.oauth.feature.gatehub.GateHubRepository
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Singleton
    @Provides
    fun providePWOAuthClientProxy(): PWOAuthClientProxy = PWOAuthClientProxyImpl()

    @Singleton
    @Provides
    fun provideKycRepository(
        apiClient: SoraCardNetworkClient.Adapter,
        inMemoryRepo: InMemoryRepo,
        userSessionRepository: UserSessionRepository,
    ): KycRepository = KycRepositoryImpl(apiClient, inMemoryRepo, userSessionRepository)

    @Singleton
    @Provides
    fun provideGateHubRepository(
        apiClient: SoraCardNetworkClient.Adapter,
        accessTokenValidator: AccessTokenValidator,
        inMemoryRepo: InMemoryRepo,
    ): GateHubRepository {
        return GateHubRepository(apiClient, accessTokenValidator, inMemoryRepo)
    }

    @Provides
    @Singleton
    fun provideCurrentActivityRetriever(): CurrentActivityRetriever = CurrentActivityRetrieverImpl()

    @Provides
    @Singleton
    fun providePriceInteractor(
        userSessionRepository: UserSessionRepository,
        inMemoryRepo: InMemoryRepo,
        kycRepository: KycRepository,
    ): PriceInteractor = PriceInteractorImpl(
        userSessionRepository = userSessionRepository,
        inMemoryCache = inMemoryRepo,
        kycRepository = kycRepository,
    )
}
