package jp.co.soramitsu.oauth.feature.session.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.base.data.SoraCardDataStore
import jp.co.soramitsu.oauth.feature.session.data.UserSessionRepositoryImpl
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class UserSessionModule {

    @Singleton
    @Provides
    fun provideUserSessionRepository(dataStore: SoraCardDataStore): UserSessionRepository =
        UserSessionRepositoryImpl(dataStore)
}
