package jp.co.soramitsu.oauth.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.common.data.CurrentActivityRetrieverImpl
import jp.co.soramitsu.oauth.common.data.KycRepositoryImpl
import jp.co.soramitsu.oauth.common.data.PriceInteractorImpl
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxyImpl
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Singleton
    @Provides
    fun providePWOAuthClientProxy(): PWOAuthClientProxy = PWOAuthClientProxyImpl()

    @Singleton
    @Provides
    fun provideKycRepository(
        apiClient: SoraCardNetworkClient
    ): KycRepository = KycRepositoryImpl(apiClient)

    @Provides
    @Singleton
    fun provideCurrentActivityRetriever(): CurrentActivityRetriever =
        CurrentActivityRetrieverImpl()

    @Provides
    @Singleton
    fun providePriceInteractor(
        userSessionRepository: UserSessionRepository,
        inMemoryRepo: InMemoryRepo,
        kycRepository: KycRepository
    ): PriceInteractor = PriceInteractorImpl(
        userSessionRepository = userSessionRepository,
        inMemoryCache = inMemoryRepo,
        kycRepository = kycRepository
    )
}
