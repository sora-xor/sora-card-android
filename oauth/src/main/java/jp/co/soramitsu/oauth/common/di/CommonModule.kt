package jp.co.soramitsu.oauth.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
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

@Module
@InstallIn(ActivityRetainedComponent::class)
class CommonModule {

    @ActivityRetainedScoped
    @Provides
    fun providePWOAuthClientProxy(): PWOAuthClientProxy = PWOAuthClientProxyImpl()

    @ActivityRetainedScoped
    @Provides
    fun provideKycRepository(
        apiClient: SoraCardNetworkClient
    ): KycRepository = KycRepositoryImpl(apiClient)

    @Provides
    @ActivityRetainedScoped
    fun provideCurrentActivityRetriever(): CurrentActivityRetriever =
        CurrentActivityRetrieverImpl()

    @Provides
    @ActivityRetainedScoped
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
