package jp.co.soramitsu.oauth.base.navigation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.MainRouterImpl

@InstallIn(ActivityRetainedComponent::class)
@Module
class NavigationModule {

    @ActivityRetainedScoped
    @Provides
    fun provideNavigator(): MainRouterImpl = MainRouterImpl()

    @ActivityRetainedScoped
    @Provides
    fun provideMainRouter(impl: MainRouterImpl): MainRouter = impl
}
