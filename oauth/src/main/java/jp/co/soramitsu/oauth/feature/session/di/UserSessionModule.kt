package jp.co.soramitsu.oauth.feature.session.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.base.data.SoraCardDataStore
import jp.co.soramitsu.oauth.feature.session.data.UserSessionRepositoryImpl
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository

@InstallIn(ActivityRetainedComponent::class)
@Module
class UserSessionModule {

    @ActivityRetainedScoped
    @Provides
    fun provideUserSessionRepository(dataStore: SoraCardDataStore): UserSessionRepository =
        UserSessionRepositoryImpl(dataStore)
}
