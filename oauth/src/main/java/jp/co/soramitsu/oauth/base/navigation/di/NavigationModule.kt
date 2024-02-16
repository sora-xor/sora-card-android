package jp.co.soramitsu.oauth.base.navigation.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.MainRouterImpl
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.navigation.SetActivityResultImpl

@InstallIn(SingletonComponent::class)
@Module
class NavigationModule {

    @Singleton
    @Provides
    fun provideNavigator(): MainRouterImpl = MainRouterImpl()

    @Singleton
    @Provides
    fun provideMainRouter(impl: MainRouterImpl): MainRouter = impl
}

@Module
@InstallIn(SingletonComponent::class)
interface SetActivityResultModule {

    @Binds
    @Singleton
    fun bindSetActivityResult(setActivityResultImpl: SetActivityResultImpl): SetActivityResult
}
