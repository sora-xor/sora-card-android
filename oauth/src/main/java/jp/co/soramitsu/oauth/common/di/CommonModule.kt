package jp.co.soramitsu.oauth.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.oauth.common.data.KycRepositoryImpl
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient

@InstallIn(SingletonComponent::class)
@Module
class CommonModule {

    @Singleton
    @Provides
    fun provideKycRepository(
        apiClient: SoraCardNetworkClient
    ): KycRepository = KycRepositoryImpl(apiClient)
}
