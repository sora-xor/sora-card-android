package jp.co.soramitsu.oauth.common.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.common.data.CurrentActivityRetrieverImpl
import javax.inject.Singleton
import jp.co.soramitsu.oauth.common.data.KycRepositoryImpl
import jp.co.soramitsu.oauth.common.domain.CurrentActivityRetriever
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Singleton
    @Provides
    fun provideKycRepository(
        apiClient: SoraCardNetworkClient
    ): KycRepository = KycRepositoryImpl(apiClient)

    @Provides
    @Singleton
    fun provideCurrentActivityRetriever(): CurrentActivityRetriever =
        CurrentActivityRetrieverImpl()
}
