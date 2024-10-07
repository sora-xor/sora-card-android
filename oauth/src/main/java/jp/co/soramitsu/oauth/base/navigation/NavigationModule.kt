package jp.co.soramitsu.oauth.base.navigation

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NavigationModule {

    @Singleton
    @Provides
    fun provideMainRouter(): MainRouter {
        return MainRouterImpl()
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface SetActivityResultModule {

    @Binds
    @Singleton
    fun bindSetActivityResult(setActivityResultImpl: SetActivityResultImpl): SetActivityResult
}
