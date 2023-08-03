package jp.co.soramitsu.oauth.base.navigation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.MainRouterImpl
import javax.inject.Singleton

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
