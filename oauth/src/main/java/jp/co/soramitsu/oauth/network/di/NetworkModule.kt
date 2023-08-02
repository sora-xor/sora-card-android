package jp.co.soramitsu.oauth.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.network.SoraCardClientProvider
import jp.co.soramitsu.oauth.network.SoraCardClientProviderImpl
import jp.co.soramitsu.oauth.network.SoraCardNetworkClient

@InstallIn(ActivityRetainedComponent::class)
@Module
class NetworkModule {

    @ActivityRetainedScoped
    @Provides
    fun provideSoraCardClientProvider(): SoraCardClientProvider = SoraCardClientProviderImpl()

    @ActivityRetainedScoped
    @Provides
    fun provideSoraCardNetworkClient(
        provider: SoraCardClientProvider,
        inMemoryRepo: InMemoryRepo,
    ): SoraCardNetworkClient =
        SoraCardNetworkClient(provider = provider, inMemoryRepo = inMemoryRepo)
}
