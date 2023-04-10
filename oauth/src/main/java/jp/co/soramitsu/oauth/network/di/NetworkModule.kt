package jp.co.soramitsu.oauth.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.network.SoraCardClientProvider
import jp.co.soramitsu.oauth.network.SoraCardClientProviderImpl
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideSoraCardClientProvider(): SoraCardClientProvider = SoraCardClientProviderImpl()

    @Singleton
    @Provides
    fun provideSoraCardNetworkClient(
        provider: SoraCardClientProvider,
        inMemoryRepo: InMemoryRepo,
    ): SoraCardNetworkClient =
        SoraCardNetworkClient(provider = provider, inMemoryRepo = inMemoryRepo)
}
